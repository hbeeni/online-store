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
insert into product (created_at, modified_at, created_by, modified_by, delivery_fee, description, image_url, name, price, sale_status, sales_volume, stock_quantity, category_id, seller_id)
values (now(), now(), 'seller', 'seller', 3000, '꽃무늬 바지', null, '꽃무늬 바지', 12000, 'SALE', 10, 100, 1, 2),
       (now(), now(), 'seller', 'seller', 3000, '꽃무늬 셔츠', null, '꽃무늬 셔츠', 15500, 'SALE', 20, 121, 1, 2),
       (now(), now(), 'seller', 'seller', 0, '프렌치 코트', null, '프렌치 코트', 53000, 'WAIT', 0, 55, 1, 2),
       (now(), now(), 'seller', 'seller', 3000, '아보카도', null, '아보카도', 2300, 'SALE', 120, 20, 3, 2),
       (now(), now(), 'seller', 'seller', 3000, '요거트', null, '요거트', 3500, 'SALE', 22, 56, 3, 2);

-- 배송 요청
insert into delivery_request (delivery_address, receiver_name, receiver_phone)
values ('주소 1', '수령인 1', '01011111111'),
       ('주소 2', '수령인 2', '01022222222'),
       ('주소 3', '수령인 3', '01033333333'),
       ('주소 4', '수령인 4', '01044444444'),
       ('주소 5', '수령인 5', '01055555555'),
       ('주소 6', '수령인 6', '01066666666');

-- 주문
insert into orders (created_at, modified_at, order_status, orderer_phone, delivery_request_id, orderer_id)
values (now(), now(), 'ORDER', '01012341234', 1, 1),
       (now(), now(), 'ORDER', '01012341234', 2, 2),
       (now(), now(), 'ORDER', '01012341234', 3, 1),
       (now(), now(), 'ORDER', '01012341234', 4, 1),
       (now(), now(), 'ORDER', '01012341234', 5, 1),
       (now(), now(), 'ORDER', '01012341234', 6, 2);

-- 배송
insert into delivery (delivered_at, delivery_fee, delivery_status)
values (now(), 3000, 'ACCEPT'),
       (now(), 3000, 'ACCEPT'),
       (now(), 3000, 'ACCEPT'),
       (now(), 3000, 'ACCEPT'),
       (now(), 3000, 'ACCEPT'),
       (now(), 3000, 'ACCEPT'),
       (now(), 3000, 'ACCEPT'),
       (now(), 3000, 'ACCEPT'),
       (now(), 3000, 'ACCEPT');

-- 주문 상품
insert into order_product (price, quantity, delivery_id, order_id, product_id)
values (12000, 10, 1, 1, 1),
       (15500, 20, 2, 1, 2),
       (53000, 30, 3, 1, 3),
       (12000, 10, 4, 2, 1),
       (15500, 20, 5, 2, 2),
       (15500, 20, 6, 3, 2),
       (15500, 20, 7, 4, 2),
       (15500, 20, 8, 5, 2),
       (15500, 20, 9, 6, 2);
