# eShop — HuynhHoaiThanh_23633801

Project được tổ chức theo mục 2.6 của `huong-dan-eshop-spring-boot-maven.pdf`:
package `com.example.eshop`, entity / repository / exception / dto / cart /
service / security / controller / controller.admin / api; templates được chia
shop, admin/category, admin/product, admin/order, admin/user, cart, checkout,
order, auth, error và fragments/layout.html.

## Chạy trên máy cá nhân

1. Cài **JDK 17**, **Maven 3.9+**, **MariaDB 10.6+**.
2. Mở `database/eShop.sql` và kiểm tra trước khi chạy: script **DROP DATABASE
   eshop**, xóa dữ liệu cũ. Chỉ chạy khi muốn khởi tạo lại database của bài tập.
3. Cấu hình biến môi trường `DB_USERNAME`, `DB_PASSWORD`. Mặc định username=root,
   mật khẩu rỗng. Có thể đặt `DB_URL` nếu không dùng localhost:3306/eshop.
4. Đặt `APP_JWT_SECRET` bằng khóa Base64 tối thiểu 32 byte, tự sinh bằng
   `openssl rand -base64 48`. Không đưa khóa thật vào Git.
   Nếu chưa đặt, ứng dụng dùng khóa ngẫu nhiên cho mỗi lần chạy để phục vụ học
   tập; token cũ sẽ hết hiệu lực sau khi restart.
5. Trong thư mục chứa `pom.xml`, chạy:

   ```sh
   mvn clean verify
   mvn spring-boot:run
   ```

6. Mở trình duyệt tại cổng 8080 của máy đang chạy ứng dụng.
   Khi triển khai HTTPS, đặt `COOKIE_SECURE=true`.

Trong IntelliJ: Open thư mục project → Load Maven → chọn Project SDK 17 và Maven
Runner JDK 17 → chạy `com.example.eshop.EshopApplication`.

## Tài khoản và phân quyền

Dữ liệu mẫu giữ từ project gốc: seller1, seller2, customer1, customer2, locked.
Mật khẩu theo hướng dẫn: `123456`; tài khoản locked bị khóa.

- GUEST: xem/tìm kiếm/lọc/phân trang, xem chi tiết, đăng ký, đăng nhập.
- CUSTOMER: giỏ hàng session, đặt hàng COD/BANKING giả lập, xem đơn của mình.
- SELLER: `/admin/categories`, `/admin/products`, `/admin/orders`, `/admin/users`.
- SELLER không sử dụng giỏ hàng/checkout; CUSTOMER không được mở quản trị.
- GUEST chuyển sang login khi mở chức năng mua hàng.

## API theo PDF

- `POST /api/auth/login`, JSON `{"username":"seller1","password":"123456"}`.
- Response: `accessToken`, `tokenType=Bearer`, `expiresIn`.
- `GET /api/products?keyword=...&categoryId=...&page=0&size=10`: công khai.
- `GET /api/products/{id}`: công khai.
- `POST /api/products`, `PUT /api/products/{id}`, `DELETE /api/products/{id}`:
  cần `Authorization: Bearer <accessToken>` của SELLER.
- JSON tạo/sửa: `name`, `description`, `price`, `quantity`, `imageUrl`, `categoryId`.
- API trả 400 dữ liệu sai, 401 chưa xác thực, 403 thiếu quyền, 404 không tìm thấy,
  409 xung đột dữ liệu. API chỉ đọc Bearer header, không nhận cookie web.

## Hoàn thiện so với project ban đầu

- Chuyển `vn.edu.iuh.eshop` sang `com.example.eshop`, `model` sang `entity`.
- Chuyển trang seller cũ thành các controller `/admin/...` như PDF.
- Thêm CategoryService, ProductService, UserService, OrderService; Bean Validation
  và DTO cho đăng ký, thanh toán, quản trị người dùng.
- Thêm JWT HttpOnly/SameSite cookie cho web và Bearer cho API; hai filter chain;
  CSRF bật trên web, tắt riêng cho API chỉ dùng header; method security.
- Bổ sung CRUD danh mục, sản phẩm, tài khoản và quản lý đơn, trang chuyển khoản
  giả lập, lịch sử và chi tiết đơn có kiểm tra chủ sở hữu.
- Tìm kiếm kết hợp lọc danh mục và phân trang, template Bootstrap theo mẫu PDF.
- Tồn kho được khóa khi checkout, giá tính lại từ database; transaction rollback
  khi lỗi và chỉ xóa giỏ sau commit; hủy đơn hoàn kho một lần.
- Không cho mở lại đơn đã hủy/hoàn thành; chỉ xóa đơn đã hủy/hoàn thành; không cho
  xóa sản phẩm đã có trong đơn hoặc danh mục còn sản phẩm.
- Không cho seller tự xóa, tự khóa hoặc tự hạ quyền tài khoản đang sử dụng.
- Giữ Spring Boot 3.5.7 của project gốc; PDF cho phép dùng 3.5.x thay 3.2.12.

## Kiểm thử

`mvn test` dùng H2 ở chế độ MariaDB, database riêng trong bộ nhớ, không tác động
MariaDB thật. Test gồm render template, phân quyền, CSRF, đăng ký/login, Bearer
API CRUD, checkout, sở hữu đơn, chuyển khoản giả lập, hủy/hoàn kho và thiếu tồn.
Bản giao đã được chạy cùng bộ 9 kiểm thử trên cả H2 và MariaDB 10.11.19;
đều không có lỗi, thất bại hay test bị bỏ qua. Ngoài ra đã kiểm tra HTTP thực tế
với database SQL mẫu. Xem `KIEM_THU.md`.

Chuyển khoản là **giả lập theo tài liệu**, không kết nối ngân hàng, không dùng
cho thanh toán thực tế. Ảnh seed và Bootstrap sử dụng URL ngoài; cần Internet
để trình duyệt tải tài nguyên này. CSS cục bộ nằm ở `static/css/style.css`.
