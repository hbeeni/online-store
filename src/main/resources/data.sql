-- 회원: 비밀번호 test12
insert into users (created_at, modified_at, email, name, nickname, password, phone, role_type, uid)
values (now(), now(), 'user@mail.com', 'user', 'test user', '$2a$10$wcVfFiEQnqu3WjgyiIsPzuqdYKV9WJ08Wx.4aac0e08CLFpUjvoW6', '01012341234', 'USER','user'),
       (now(), now(), 'seller@mail.com', 'seller', 'test seller', '$2a$10$wcVfFiEQnqu3WjgyiIsPzuqdYKV9WJ08Wx.4aac0e08CLFpUjvoW6', '01012341234', 'SELLER', 'seller'),
       (now(), now(), 'admin@mail.com', 'admin', 'test admin', '$2a$10$wcVfFiEQnqu3WjgyiIsPzuqdYKV9WJ08Wx.4aac0e08CLFpUjvoW6', '01012341234', 'ADMIN', 'admin');

-- 카테고리
insert into category (created_at, modified_at, created_by, modified_by, description, name)
values (now(), now(), 'admin', 'admin', '패션의류', '패션의류'),
       (now(), now(), 'admin', 'admin', '뷰티', '뷰티'),
       (now(), now(), 'admin', 'admin', '식품', '식품'),
       (now(), now(), 'admin', 'admin', '도서', '도서'),
       (now(), now(), 'admin', 'admin', '가전디지털', '가전디지털'),
       (now(), now(), 'admin', 'admin', '문구', '문구'),
       (now(), now(), 'admin', 'admin', '헬스', '헬스'),
       (now(), now(), 'admin', 'admin', '반려동물용품', '반려동물용품'),
       (now(), now(), 'admin', 'admin', '완구', '완구'),
       (now(), now(), 'admin', 'admin', '스포츠', '스포츠');

-- 상품
insert into product (created_at, modified_at, created_by, modified_by, description, image_url, name, price, sale_status, sales_volume, stock_quantity, category_id, seller_id)
values (now(), now(), 'seller', 'seller', '꽃무늬 바지', null, '꽃무늬 바지', 12000, 'SALE', 10, 100, 1, 1),
       (now(), now(), 'seller', 'seller', '꽃무늬 셔츠', null, '꽃무늬 셔츠', 15500, 'SALE', 20, 121, 1, 1),
       (now(), now(), 'seller', 'seller', '프렌치 코트', null, '프렌치 코트', 53000, 'WAIT', 0, 55, 1, 1),
       (now(), now(), 'seller', 'seller', '아보카도', null, '아보카도', 2300, 'SALE', 120, 20, 3, 1),
       (now(), now(), 'seller', 'seller', '요거트', null, '요거트', 3500, 'SALE', 22, 56, 3, 1);
