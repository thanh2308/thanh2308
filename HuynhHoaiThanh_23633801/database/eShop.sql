-- eShop.sql - MariaDB 10.6+; CHÚ Ý: xóa database eshop hiện có.
DROP DATABASE IF EXISTS eshop;
CREATE DATABASE eshop CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE eshop;
CREATE TABLE category (id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,name VARCHAR(100) NOT NULL UNIQUE,description VARCHAR(255)) ENGINE=InnoDB;
CREATE TABLE product (id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,name VARCHAR(150) NOT NULL,description VARCHAR(500),price DECIMAL(15,2) NOT NULL,quantity INT NOT NULL DEFAULT 0,image_url VARCHAR(500),category_id BIGINT NOT NULL,created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,CONSTRAINT fk_product_category FOREIGN KEY(category_id) REFERENCES category(id),CONSTRAINT ck_product_price CHECK(price>=0),CONSTRAINT ck_product_quantity CHECK(quantity>=0)) ENGINE=InnoDB;
CREATE INDEX idx_product_name ON product(name);
INSERT INTO category (id,name,description) VALUES
(1,'Điện thoại','Danh mục Điện thoại'),
(2,'Máy tính bảng','Danh mục Máy tính bảng'),
(3,'Laptop','Danh mục Laptop'),
(4,'Đồng hồ thông minh','Danh mục Đồng hồ thông minh'),
(5,'Tai nghe','Danh mục Tai nghe'),
(6,'Loa','Danh mục Loa'),
(7,'Bàn phím','Danh mục Bàn phím'),
(8,'Chuột','Danh mục Chuột'),
(9,'Màn hình','Danh mục Màn hình'),
(10,'Máy ảnh','Danh mục Máy ảnh'),
(11,'Phụ kiện sạc','Danh mục Phụ kiện sạc'),
(12,'Thiết bị mạng','Danh mục Thiết bị mạng'),
(13,'Thiết bị lưu trữ','Danh mục Thiết bị lưu trữ'),
(14,'Máy in','Danh mục Máy in'),
(15,'Gia dụng thông minh','Danh mục Gia dụng thông minh');
INSERT INTO product(name,description,price,quantity,image_url,category_id) VALUES
('iPhone 15 128GB','Sản phẩm iPhone 15 128GB',19990000,25,'https://placehold.co/400x300?text=iPhone+15+128GB',1),
('Samsung Galaxy S24 256GB','Sản phẩm Samsung Galaxy S24 256GB',22990000,20,'https://placehold.co/400x300?text=Samsung+Galaxy+S24+256GB',1),
('Xiaomi Redmi Note 13 8GB/128GB','Sản phẩm Xiaomi Redmi Note 13 8GB/128GB',4890000,60,'https://placehold.co/400x300?text=Xiaomi+Redmi+Note+13+8GB/128GB',1),
('OPPO Reno11 F 5G','Sản phẩm OPPO Reno11 F 5G',8990000,35,'https://placehold.co/400x300?text=OPPO+Reno11+F+5G',1),
('iPad Air M2 11 inch WiFi 128GB','Sản phẩm iPad Air M2 11 inch WiFi 128GB',16990000,15,'https://placehold.co/400x300?text=iPad+Air+M2+11+inch+WiFi+128GB',2),
('Samsung Galaxy Tab S9 FE','Sản phẩm Samsung Galaxy Tab S9 FE',9490000,18,'https://placehold.co/400x300?text=Samsung+Galaxy+Tab+S9+FE',2),
('Xiaomi Pad 6 8GB/256GB','Sản phẩm Xiaomi Pad 6 8GB/256GB',7990000,22,'https://placehold.co/400x300?text=Xiaomi+Pad+6+8GB/256GB',2),
('MacBook Air M3 13 inch 16GB/256GB','Sản phẩm MacBook Air M3 13 inch 16GB/256GB',27990000,10,'https://placehold.co/400x300?text=MacBook+Air+M3+13+inch+16GB/256GB',3),
('Dell Inspiron 15 3530','Sản phẩm Dell Inspiron 15 3530',15990000,14,'https://placehold.co/400x300?text=Dell+Inspiron+15+3530',3),
('ASUS Vivobook 15 X1504VA','Sản phẩm ASUS Vivobook 15 X1504VA',12490000,20,'https://placehold.co/400x300?text=ASUS+Vivobook+15+X1504VA',3),
('Lenovo ThinkPad E14 Gen 5','Sản phẩm Lenovo ThinkPad E14 Gen 5',18990000,8,'https://placehold.co/400x300?text=Lenovo+ThinkPad+E14+Gen+5',3),
('Apple Watch SE 2 GPS 40mm','Sản phẩm Apple Watch SE 2 GPS 40mm',6490000,30,'https://placehold.co/400x300?text=Apple+Watch+SE+2+GPS+40mm',4),
('Samsung Galaxy Watch6 40mm','Sản phẩm Samsung Galaxy Watch6 40mm',5990000,25,'https://placehold.co/400x300?text=Samsung+Galaxy+Watch6+40mm',4),
('Xiaomi Smart Band 8','Sản phẩm Xiaomi Smart Band 8',890000,80,'https://placehold.co/400x300?text=Xiaomi+Smart+Band+8',4),
('AirPods Pro 2 USB-C','Sản phẩm AirPods Pro 2 USB-C',5990000,40,'https://placehold.co/400x300?text=AirPods+Pro+2+USB-C',5),
('Sony WH-1000XM5','Sản phẩm Sony WH-1000XM5',7990000,12,'https://placehold.co/400x300?text=Sony+WH-1000XM5',5),
('JBL Tune 520BT','Sản phẩm JBL Tune 520BT',1190000,50,'https://placehold.co/400x300?text=JBL+Tune+520BT',5),
('Soundcore Liberty 4 NC','Sản phẩm Soundcore Liberty 4 NC',1790000,45,'https://placehold.co/400x300?text=Soundcore+Liberty+4+NC',5),
('JBL Flip 6','Sản phẩm JBL Flip 6',2690000,30,'https://placehold.co/400x300?text=JBL+Flip+6',6),
('Marshall Emberton II','Sản phẩm Marshall Emberton II',3990000,15,'https://placehold.co/400x300?text=Marshall+Emberton+II',6),
('Sony SRS-XB100','Sản phẩm Sony SRS-XB100',1290000,40,'https://placehold.co/400x300?text=Sony+SRS-XB100',6),
('Akko 3087 Dragon Ball','Sản phẩm Akko 3087 Dragon Ball',1290000,25,'https://placehold.co/400x300?text=Akko+3087+Dragon+Ball',7),
('Logitech K380','Sản phẩm Logitech K380',790000,60,'https://placehold.co/400x300?text=Logitech+K380',7),
('Keychron K2 V2','Sản phẩm Keychron K2 V2',2190000,20,'https://placehold.co/400x300?text=Keychron+K2+V2',7),
('Logitech M331 Silent Plus','Sản phẩm Logitech M331 Silent Plus',350000,100,'https://placehold.co/400x300?text=Logitech+M331+Silent+Plus',8),
('Logitech MX Master 3S','Sản phẩm Logitech MX Master 3S',2490000,20,'https://placehold.co/400x300?text=Logitech+MX+Master+3S',8),
('Razer DeathAdder Essential','Sản phẩm Razer DeathAdder Essential',590000,45,'https://placehold.co/400x300?text=Razer+DeathAdder+Essential',8),
('LG 24MP60G 24 inch','Sản phẩm LG 24MP60G 24 inch',2890000,30,'https://placehold.co/400x300?text=LG+24MP60G+24+inch',9),
('Dell UltraSharp U2424H','Sản phẩm Dell UltraSharp U2424H',5990000,12,'https://placehold.co/400x300?text=Dell+UltraSharp+U2424H',9),
('Samsung Odyssey G5 27 inch','Sản phẩm Samsung Odyssey G5 27 inch',6490000,10,'https://placehold.co/400x300?text=Samsung+Odyssey+G5+27+inch',9),
('ASUS ProArt PA248QV','Sản phẩm ASUS ProArt PA248QV',5290000,8,'https://placehold.co/400x300?text=ASUS+ProArt+PA248QV',9),
('Canon EOS R50 Kit 18-45mm','Sản phẩm Canon EOS R50 Kit 18-45mm',18990000,6,'https://placehold.co/400x300?text=Canon+EOS+R50+Kit+18-45mm',10),
('Sony ZV-E10 Kit 16-50mm','Sản phẩm Sony ZV-E10 Kit 16-50mm',16490000,7,'https://placehold.co/400x300?text=Sony+ZV-E10+Kit+16-50mm',10),
('Fujifilm Instax Mini 12','Sản phẩm Fujifilm Instax Mini 12',1890000,35,'https://placehold.co/400x300?text=Fujifilm+Instax+Mini+12',10),
('Củ sạc Anker 20W USB-C','Sản phẩm Củ sạc Anker 20W USB-C',290000,120,'https://placehold.co/400x300?text=Củ+sạc+Anker+20W+USB-C',11),
('Pin dự phòng Xiaomi 20000mAh','Sản phẩm Pin dự phòng Xiaomi 20000mAh',590000,70,'https://placehold.co/400x300?text=Pin+dự+phòng+Xiaomi+20000mAh',11),
('Cáp Ugreen USB-C to USB-C 100W','Sản phẩm Cáp Ugreen USB-C to USB-C 100W',250000,150,'https://placehold.co/400x300?text=Cáp+Ugreen+USB-C+to+USB-C+100W',11),
('Củ sạc Baseus GaN 65W','Sản phẩm Củ sạc Baseus GaN 65W',790000,55,'https://placehold.co/400x300?text=Củ+sạc+Baseus+GaN+65W',11),
('Router TP-Link Archer AX23','Sản phẩm Router TP-Link Archer AX23',1290000,30,'https://placehold.co/400x300?text=Router+TP-Link+Archer+AX23',12),
('TP-Link Deco X20 (2-pack)','Sản phẩm TP-Link Deco X20 (2-pack)',3490000,15,'https://placehold.co/400x300?text=TP-Link+Deco+X20+(2-pack)',12),
('SSD di động Samsung T7 1TB','Sản phẩm SSD di động Samsung T7 1TB',2690000,25,'https://placehold.co/400x300?text=SSD+di+động+Samsung+T7+1TB',13),
('USB SanDisk Ultra 64GB','Sản phẩm USB SanDisk Ultra 64GB',190000,200,'https://placehold.co/400x300?text=USB+SanDisk+Ultra+64GB',13),
('SSD WD Blue SN580 1TB NVMe','Sản phẩm SSD WD Blue SN580 1TB NVMe',1890000,30,'https://placehold.co/400x300?text=SSD+WD+Blue+SN580+1TB+NVMe',13),
('HDD Seagate One Touch 2TB','Sản phẩm HDD Seagate One Touch 2TB',2190000,20,'https://placehold.co/400x300?text=HDD+Seagate+One+Touch+2TB',13),
('Canon LBP2900','Sản phẩm Canon LBP2900',3490000,10,'https://placehold.co/400x300?text=Canon+LBP2900',14),
('HP LaserJet M211dw','Sản phẩm HP LaserJet M211dw',4290000,8,'https://placehold.co/400x300?text=HP+LaserJet+M211dw',14),
('Epson EcoTank L3250','Sản phẩm Epson EcoTank L3250',4590000,9,'https://placehold.co/400x300?text=Epson+EcoTank+L3250',14),
('Robot hút bụi Xiaomi E10','Sản phẩm Robot hút bụi Xiaomi E10',3990000,12,'https://placehold.co/400x300?text=Robot+hút+bụi+Xiaomi+E10',15),
('Nồi chiên không dầu Philips HD9200','Sản phẩm Nồi chiên không dầu Philips HD9200',1990000,25,'https://placehold.co/400x300?text=Nồi+chiên+không+dầu+Philips+HD9200',15),
('Máy lọc không khí Xiaomi 4 Lite','Sản phẩm Máy lọc không khí Xiaomi 4 Lite',3290000,18,'https://placehold.co/400x300?text=Máy+lọc+không+khí+Xiaomi+4+Lite',15);
CREATE TABLE users(id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,username VARCHAR(50) NOT NULL UNIQUE,password VARCHAR(100) NOT NULL,full_name VARCHAR(100) NOT NULL,email VARCHAR(100) NOT NULL UNIQUE,phone VARCHAR(20),address VARCHAR(255),role VARCHAR(30) NOT NULL DEFAULT 'ROLE_CUSTOMER',enabled BOOLEAN NOT NULL DEFAULT TRUE,created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,CONSTRAINT ck_users_role CHECK(role IN ('ROLE_SELLER','ROLE_CUSTOMER'))) ENGINE=InnoDB;
INSERT INTO users(username,password,full_name,email,role,enabled) VALUES
('seller1','$2a$10$uPUNNrC1JUnzBYDZD6AQBe.swyBhdnARmWl.Ctpo737mZZF4hejyS','Trần Văn Bán','seller1@eshop.vn','ROLE_SELLER',1),
('seller2','$2a$10$uPUNNrC1JUnzBYDZD6AQBe.swyBhdnARmWl.Ctpo737mZZF4hejyS','Lê Thị Hàng','seller2@eshop.vn','ROLE_SELLER',1),
('customer1','$2a$10$uPUNNrC1JUnzBYDZD6AQBe.swyBhdnARmWl.Ctpo737mZZF4hejyS','Nguyễn Văn Mua','customer1@gmail.com','ROLE_CUSTOMER',1),
('customer2','$2a$10$uPUNNrC1JUnzBYDZD6AQBe.swyBhdnARmWl.Ctpo737mZZF4hejyS','Phạm Thị Sắm','customer2@gmail.com','ROLE_CUSTOMER',1),
('locked','$2a$10$uPUNNrC1JUnzBYDZD6AQBe.swyBhdnARmWl.Ctpo737mZZF4hejyS','Tài Khoản Khoá','locked@gmail.com','ROLE_CUSTOMER',0);
CREATE TABLE orders(id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,order_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,customer_name VARCHAR(100) NOT NULL,phone VARCHAR(20) NOT NULL,address VARCHAR(255) NOT NULL,note VARCHAR(500),payment_method VARCHAR(20) NOT NULL,status VARCHAR(20) NOT NULL DEFAULT 'NEW',total_amount DECIMAL(15,2) NOT NULL,user_id BIGINT NULL,CONSTRAINT fk_orders_user FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE SET NULL) ENGINE=InnoDB;
CREATE TABLE order_detail(id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,order_id BIGINT NOT NULL,product_id BIGINT NOT NULL,quantity INT NOT NULL,unit_price DECIMAL(15,2) NOT NULL,CONSTRAINT fk_od_order FOREIGN KEY(order_id) REFERENCES orders(id) ON DELETE CASCADE,CONSTRAINT fk_od_product FOREIGN KEY(product_id) REFERENCES product(id),CONSTRAINT ck_od_quantity CHECK(quantity>0)) ENGINE=InnoDB;
SELECT 'category' AS tbl,COUNT(*) AS total FROM category UNION ALL SELECT 'product',COUNT(*) FROM product UNION ALL SELECT 'users',COUNT(*) FROM users;
