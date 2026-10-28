package com.example.eshop;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.eshop.entity.*;
import com.example.eshop.entity.Order;
import com.example.eshop.repository.*;
import com.example.eshop.security.*;
import jakarta.servlet.http.Cookie;
import java.math.BigDecimal;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.*;

@SpringBootTest
@AutoConfigureMockMvc
class EshopIntegrationTest {
  @Autowired MockMvc mvc;
  @Autowired CategoryRepository categories;
  @Autowired ProductRepository products;
  @Autowired OrderRepository orders;
  @Autowired UserRepository users;
  @Autowired PasswordEncoder encoder;
  @Autowired JwtService jwt;
  @Autowired CustomUserDetailsService details;
  Long productId, categoryId;

  @BeforeEach
  void seed() {
    orders.deleteAll();
    products.deleteAll();
    categories.deleteAll();
    users.deleteAll();
    Category c = new Category();
    c.setName("Điện thoại");
    categoryId = categories.save(c).getId();
    Product p = new Product();
    p.setName("Phone Demo");
    p.setDescription("Test");
    p.setPrice(new BigDecimal("200000.00"));
    p.setQuantity(10);
    p.setCategory(c);
    p.setImageUrl("/images/demo.png");
    productId = products.save(p).getId();
    for (String name : new String[] {"seller1", "customer1", "customer2", "locked"}) {
      User u = new User();
      u.setUsername(name);
      u.setPassword(encoder.encode("123456"));
      u.setEmail(name + "@example.com");
      u.setFullName(name);
      u.setRole(name.startsWith("seller") ? Role.ROLE_SELLER : Role.ROLE_CUSTOMER);
      u.setEnabled(!name.equals("locked"));
      users.save(u);
    }
  }

  Cookie cookie(String name) {
    return new Cookie(JwtService.COOKIE_NAME, jwt.generateToken(details.loadUserByUsername(name)));
  }

  @Test
  void publicShopSearchAndSecurity() throws Exception {
    mvc.perform(get("/"))
        .andExpect(status().isOk())
        .andExpect(content().string(org.hamcrest.Matchers.containsString("Phone Demo")));
    mvc.perform(
            get("/products").param("keyword", "absent").param("categoryId", categoryId.toString()))
        .andExpect(status().isOk())
        .andExpect(
            content().string(org.hamcrest.Matchers.containsString("Không có sản phẩm phù hợp")));
    mvc.perform(get("/products/999999")).andExpect(status().isNotFound());
    mvc.perform(get("/cart")).andExpect(status().is3xxRedirection());
    mvc.perform(get("/admin/users").cookie(cookie("customer1"))).andExpect(status().isForbidden());
    mvc.perform(get("/cart").cookie(cookie("seller1"))).andExpect(status().isForbidden());
    mvc.perform(get("/register").cookie(cookie("customer1"))).andExpect(status().isForbidden());
    mvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"locked\",\"password\":\"123456\"}"))
        .andExpect(status().isUnauthorized());
    mvc.perform(get("/api/products"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].name").value("Phone Demo"));
    mvc.perform(delete("/api/products/" + productId).cookie(cookie("seller1")))
        .andExpect(status().isUnauthorized());
    mvc.perform(
            delete("/api/products/" + productId)
                .header("Authorization", "Bearer " + cookie("customer1").getValue()))
        .andExpect(status().isForbidden());
  }

  @Test
  void rendersEveryFormAndAdminList() throws Exception {
    for (String path :
        new String[] {
          "/admin/categories",
          "/admin/categories/new",
          "/admin/categories/" + categoryId + "/edit",
          "/admin/products",
          "/admin/products/new",
          "/admin/products/" + productId + "/edit",
          "/admin/users",
          "/admin/users/new",
          "/admin/users/" + users.findByUsername("customer1").orElseThrow().getId() + "/edit",
          "/admin/orders"
        }) mvc.perform(get(path).cookie(cookie("seller1"))).andExpect(status().isOk());
    for (String path : new String[] {"/login", "/register", "/products/" + productId, "/403"})
      mvc.perform(get(path)).andExpect(status().is(path.equals("/403") ? 403 : 200));
    mvc.perform(get("/orders").cookie(cookie("customer1"))).andExpect(status().isOk());
  }

  @Test
  void registrationLoginAndCsrf() throws Exception {
    mvc.perform(
            post("/register")
                .with(csrf())
                .param("username", "newcustomer")
                .param("password", "123456")
                .param("confirmPassword", "123456")
                .param("fullName", "Khách mới")
                .param("email", "new@example.com")
                .param("phone", "")
                .param("role", "ROLE_SELLER"))
        .andExpect(redirectedUrl("/login?registered"));
    assertThat(users.findByUsername("newcustomer").orElseThrow().getRole())
        .isEqualTo(Role.ROLE_CUSTOMER);
    mvc.perform(
            post("/login")
                .with(csrf())
                .param("username", "newcustomer")
                .param("password", "123456"))
        .andExpect(status().is3xxRedirection())
        .andExpect(header().string("Set-Cookie", org.hamcrest.Matchers.containsString("HttpOnly")));
    mvc.perform(
            post("/cart/add").cookie(cookie("customer1")).param("productId", productId.toString()))
        .andExpect(status().isForbidden());
    mvc.perform(post("/register").with(csrf()).param("username", "bad"))
        .andExpect(status().isOk())
        .andExpect(model().attributeHasErrors("registerForm"));
  }

  @Test
  void purchaseOwnershipPaymentAndCancellation() throws Exception {
    MockHttpSession session = new MockHttpSession();
    Cookie customer = cookie("customer1");
    Cookie seller = cookie("seller1");
    mvc.perform(
            post("/cart/add")
                .session(session)
                .cookie(customer)
                .with(csrf())
                .param("productId", productId.toString())
                .param("quantity", "2"))
        .andExpect(redirectedUrl("/cart"));
    mvc.perform(get("/cart").session(session).cookie(customer)).andExpect(status().isOk());
    mvc.perform(get("/checkout").session(session).cookie(customer)).andExpect(status().isOk());
    mvc.perform(
            post("/checkout")
                .session(session)
                .cookie(customer)
                .with(csrf())
                .param("customerName", "Thanh")
                .param("phone", "0901234567")
                .param("address", "TP HCM")
                .param("paymentMethod", "BANKING"))
        .andExpect(status().is3xxRedirection());
    Order order = orders.findAll().get(0);
    Long id = order.getId();
    assertThat(order.getTotalAmount()).isEqualByComparingTo("400000");
    assertThat(products.findById(productId).orElseThrow().getQuantity()).isEqualTo(8);
    mvc.perform(get("/orders/" + id).cookie(customer))
        .andExpect(status().isOk())
        .andExpect(content().string(org.hamcrest.Matchers.containsString("Phone Demo")));
    mvc.perform(get("/orders/" + id).cookie(cookie("customer2"))).andExpect(status().isForbidden());
    mvc.perform(get("/orders/" + id + "/payment").cookie(customer)).andExpect(status().isOk());
    mvc.perform(post("/orders/" + id + "/confirm-payment").cookie(customer).with(csrf()))
        .andExpect(status().is3xxRedirection());
    assertThat(orders.findById(id).orElseThrow().getStatus()).isEqualTo(OrderStatus.PAID);
    mvc.perform(get("/admin/orders/" + id).cookie(seller)).andExpect(status().isOk());
    for (int i = 0; i < 2; i++)
      mvc.perform(
              post("/admin/orders/" + id + "/status")
                  .cookie(seller)
                  .with(csrf())
                  .param("status", "CANCELLED"))
          .andExpect(status().is3xxRedirection());
    assertThat(products.findById(productId).orElseThrow().getQuantity()).isEqualTo(10);
    mvc.perform(post("/admin/products/" + productId + "/delete").cookie(seller).with(csrf()))
        .andExpect(flash().attributeExists("error"));
    assertThat(products.existsById(productId)).isTrue();
  }

  @Test
  void stockFailureRollsBackAndRetainsCart() throws Exception {
    MockHttpSession session = new MockHttpSession();
    Cookie customer = cookie("customer1");
    mvc.perform(
            post("/cart/add")
                .session(session)
                .cookie(customer)
                .with(csrf())
                .param("productId", productId.toString())
                .param("quantity", "3"))
        .andExpect(status().is3xxRedirection());
    Product p = products.findById(productId).orElseThrow();
    p.setQuantity(1);
    products.save(p);
    mvc.perform(
            post("/checkout")
                .session(session)
                .cookie(customer)
                .with(csrf())
                .param("customerName", "Thanh")
                .param("phone", "0901234567")
                .param("address", "TP HCM")
                .param("paymentMethod", "COD"))
        .andExpect(status().isOk())
        .andExpect(model().attributeHasErrors("checkoutForm"));
    assertThat(orders.count()).isZero();
    assertThat(products.findById(productId).orElseThrow().getQuantity()).isEqualTo(1);
    mvc.perform(get("/cart").session(session).cookie(customer))
        .andExpect(status().isOk())
        .andExpect(content().string(org.hamcrest.Matchers.containsString("Phone Demo")));
  }

  @Test
  void bearerProductCrud() throws Exception {
    String token = cookie("seller1").getValue();
    mvc.perform(
            post("/api/products")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"name\":\"API Product\",\"price\":1000,\"quantity\":5,\"categoryId\":"
                        + categoryId
                        + "}"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.categoryName").value("Điện thoại"));
    mvc.perform(
            put("/api/products/" + productId)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"name\":\"Updated\",\"price\":1500,\"quantity\":6,\"categoryId\":"
                        + categoryId
                        + "}"))
        .andExpect(status().isOk());
    mvc.perform(delete("/api/products/" + productId).header("Authorization", "Bearer " + token))
        .andExpect(status().isNoContent());
    mvc.perform(get("/api/products/" + productId)).andExpect(status().isNotFound());
  }

  @Test
  void suppliedSeedPasswordMatchesGuide() {
    assertThat(
            encoder.matches(
                "123456", "$2a$10$uPUNNrC1JUnzBYDZD6AQBe.swyBhdnARmWl.Ctpo737mZZF4hejyS"))
        .isTrue();
  }

  @Test
  void multipleLinesAreAtomic() throws Exception {
    MockHttpSession session = new MockHttpSession();
    Cookie customer = cookie("customer1");
    Product second = new Product();
    second.setName("Second product");
    second.setPrice(new BigDecimal("100"));
    second.setQuantity(5);
    second.setCategory(categories.findById(categoryId).orElseThrow());
    Long secondId = products.save(second).getId();
    for (Long id : new Long[] {productId, secondId})
      mvc.perform(
              post("/cart/add")
                  .session(session)
                  .cookie(customer)
                  .with(csrf())
                  .param("productId", id.toString())
                  .param("quantity", "2"))
          .andExpect(status().is3xxRedirection());
    second = products.findById(secondId).orElseThrow();
    second.setQuantity(0);
    products.save(second);
    mvc.perform(
            post("/checkout")
                .session(session)
                .cookie(customer)
                .with(csrf())
                .param("customerName", "Thanh")
                .param("phone", "0901234567")
                .param("address", "TP HCM")
                .param("paymentMethod", "COD"))
        .andExpect(status().isOk())
        .andExpect(model().attributeHasErrors("checkoutForm"));
    assertThat(orders.count()).isZero();
    assertThat(products.findById(productId).orElseThrow().getQuantity()).isEqualTo(10);
  }

  @Test
  void adminCategoryUserAndProductForms() throws Exception {
    Cookie seller = cookie("seller1");
    mvc.perform(
            post("/admin/categories/save")
                .cookie(seller)
                .with(csrf())
                .param("name", "New category")
                .param("description", "Demo"))
        .andExpect(redirectedUrl("/admin/categories"));
    mvc.perform(
            post("/admin/products/save")
                .cookie(seller)
                .with(csrf())
                .param("name", "New product")
                .param("price", "1000")
                .param("quantity", "5")
                .param("categoryId", categoryId.toString()))
        .andExpect(redirectedUrl("/admin/products"));
    mvc.perform(post("/admin/categories/" + categoryId + "/delete").cookie(seller).with(csrf()))
        .andExpect(flash().attributeExists("error"));
    mvc.perform(
            post("/admin/users/save")
                .cookie(seller)
                .with(csrf())
                .param("username", "createduser")
                .param("password", "123456")
                .param("fullName", "Created User")
                .param("email", "created@example.com")
                .param("phone", "")
                .param("role", "ROLE_CUSTOMER")
                .param("enabled", "true"))
        .andExpect(redirectedUrl("/admin/users"));
    assertThat(users.existsByUsername("createduser")).isTrue();
    mvc.perform(
            post("/admin/users/"
                    + users.findByUsername("seller1").orElseThrow().getId()
                    + "/delete")
                .cookie(seller)
                .with(csrf()))
        .andExpect(flash().attributeExists("error"));
    mvc.perform(
            post("/admin/users/"
                    + users.findByUsername("createduser").orElseThrow().getId()
                    + "/delete")
                .cookie(seller)
                .with(csrf()))
        .andExpect(redirectedUrl("/admin/users"));
    assertThat(users.existsByUsername("createduser")).isFalse();
  }
}
