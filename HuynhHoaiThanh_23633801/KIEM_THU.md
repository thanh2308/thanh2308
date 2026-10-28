# Kết quả kiểm tra bản giao

- JDK: Eclipse Temurin 17.0.17; Maven: 3.9.9; Spring Boot: 3.5.7.
- `mvn clean verify`: BUILD SUCCESS; tạo executable JAR thành công.
- H2 MariaDB mode: 9 tests, 0 failures, 0 errors, 0 skipped.
- MariaDB 10.11.19: 9 tests, 0 failures, 0 errors, 0 skipped.
- Chạy `database/eShop.sql` trên database kiểm thử riêng: 15 category,
  50 product, 5 users. JPA `validate` thành công với schema SQL này.
- BCrypt trong SQL xác minh đúng mật khẩu mẫu 123456.

## Nội dung kiểm thử tích hợp

1. Xem/tìm kiếm sản phẩm và kiểm tra phân quyền guest/customer/seller.
2. Render danh sách và form quản trị, login/register, chi tiết sản phẩm.
3. Đăng ký luôn CUSTOMER, login phát cookie HttpOnly, POST thiếu CSRF bị chặn.
4. Đặt hàng BANKING, tổng tiền/tồn kho, kiểm tra chủ sở hữu đơn, thanh toán
   giả lập, hủy hai lần chỉ hoàn kho một lần, chặn xóa sản phẩm đã được mua.
5. Thiếu tồn kho không tạo đơn và giữ giỏ hàng.
6. CRUD sản phẩm bằng Bearer SELLER; CUSTOMER bị chặn và cookie web không
   được chấp nhận để ghi qua API.
7. Mật khẩu BCrypt tài khoản mẫu khớp tài liệu.
8. Hai dòng giỏ hàng: dòng đầu đủ, dòng sau thiếu kho → rollback cả dòng đầu.
9. Tạo danh mục, sản phẩm, tài khoản bằng form; chặn xóa danh mục còn sản phẩm
   và tài khoản seller đang đăng nhập.

## HTTP thực tế trên executable JAR + SQL mẫu + MariaDB

- Trang cửa hàng và GET API trả đúng dữ liệu mẫu 50 sản phẩm.
- seller1/customer1 đăng nhập bằng mật khẩu mẫu thành công.
- Bốn trang quản trị categories/products/orders/users trả HTTP 200.
- customer1 mua 2 iPhone 15: tổng 39.980.000; tồn từ 25 xuống 23.
- Seller hủy đơn: tồn trở lại 25.

Database kiểm thử nằm trong container riêng, không dùng database cá nhân.
Thanh toán BANKING là giả lập theo PDF, không kết nối ngân hàng thật.
