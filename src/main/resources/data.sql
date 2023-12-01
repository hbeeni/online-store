-- 회원: 비밀번호 test12
insert into users (created_at, modified_at, email, name, nickname, password, phone, role_type, uid)
values (now(), now(), 'user1@mail.com', 'user1', 'test user1', '$2a$10$wcVfFiEQnqu3WjgyiIsPzuqdYKV9WJ08Wx.4aac0e08CLFpUjvoW6', '01012341234', 'USER','user1'),
       (now(), now(), 'seller1@mail.com', 'seller1', 'test seller1', '$2a$10$wcVfFiEQnqu3WjgyiIsPzuqdYKV9WJ08Wx.4aac0e08CLFpUjvoW6', '01012341234', 'SELLER', 'seller1'),
       (now(), now(), 'admin@mail.com', 'admin', 'test admin', '$2a$10$wcVfFiEQnqu3WjgyiIsPzuqdYKV9WJ08Wx.4aac0e08CLFpUjvoW6', '01012341234', 'ADMIN', 'admin'),
       (now(), now(), 'user2@mail.com', 'user2', 'test user2', '$2a$10$wcVfFiEQnqu3WjgyiIsPzuqdYKV9WJ08Wx.4aac0e08CLFpUjvoW6', '01012341234', 'USER','user2'),
       (now(), now(), 'seller2@mail.com', 'seller2', 'test seller2', '$2a$10$wcVfFiEQnqu3WjgyiIsPzuqdYKV9WJ08Wx.4aac0e08CLFpUjvoW6', '01012341234', 'SELLER', 'seller2');

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
insert into product (created_at, modified_at, created_by, modified_by, delivery_fee, name, image_name, description, price, sale_status, sales_volume, stock_quantity, category_id, seller_id)
values (now(), now(), 'seller', 'seller', 3000, '부츠컷 바지', 'c07b62ee-14a7-4555-8f38-95eed42304cd.jpg', '부츠컷 바지', 12000, 'SALE', 10, 100, 1, 5),
       (now(), now(), 'seller', 'seller', 0, '빨간색 셔츠', '02b817ac-f7db-44ee-b291-4f6f48a2f7f6.jpg', '여성용 빨간 셔츠', 15500, 'SALE', 20, 121, 1, 2),
       (now(), now(), 'seller', 'seller', 10000, '컴퓨터', '29be2acd-a54b-4259-b7ed-51541604c412.jpg', '아이맥', 1240000, 'SALE', 0, 55, 5, 2),
       (now(), now(), 'seller', 'seller', 3000, '아보카도', 'c1cf8d7a-be86-4f00-8b20-8d8f1d7d26ba.jpg', '아보카도', 2300, 'SALE', 120, 20, 3, 5),
       (now(), now(), 'seller', 'seller', 3000, '요거트', '53b3fcc9-1be6-4671-b3a8-7af81cfe205d.jpg', '요거트', 3500, 'SALE', 22, 56, 3, 2),
       (now(), now(), 'seller', 'seller', 2500, '런닝화', '13200308-8faa-480c-a467-b8024014e498.jpg', '분홍색 여성용 런닝화', 57000, 'WAIT', 0, 199, 1, 2),
       (now(), now(), 'seller', 'seller', 7000, '카메라', 'b819c1c9-05e7-406a-bf12-df597daff3cc.jpg', '캐논 카메라', 770000, 'OUT_OF_STOCK', 55, 0, 5, 5),
       (now(), now(), 'seller', 'seller', 2500, '강아지 옷', 'b19b76dd-c442-4383-bbc2-8e6743bf1cdd.jpg', '겨울에 입힐 수 있는 강아지 니트', 3500, 'SALE', 1333, 22, 8, 2),
       (now(), now(), 'seller', 'seller', 3000, '인라인 스케이트', '4413ce8c-f236-4c42-8452-6cc7158b7d85.jpg', '아동용 인라인 스케이트', 135000, 'OUT_OF_STOCK', 1540, 0, 10, 2),
       (now(), now(), 'seller', 'seller', 0, '레드 립스틱', 'c78e0088-3686-4f25-946f-9937bdf77074.jpg', '진한 빨간색이 돋보이는 립스틱', 12000, 'CLOSE', 3423, 22, 2, 5);

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
       (now(), now(), 'ORDER', '01012341234', 2, 4),
       (now(), now(), 'ORDER', '01012341234', 3, 4),
       (now(), now(), 'ORDER', '01012341234', 4, 1),
       (now(), now(), 'ORDER', '01012341234', 5, 1),
       (now(), now(), 'ORDER', '01012341234', 6, 4);

-- 배송
insert into delivery (delivered_at, delivery_fee, delivery_status)
values (NULL, 3000, 'ACCEPT'),
       (NULL, 3000, 'ACCEPT'),
       (NULL, 3000, 'ACCEPT'),
       (NULL, 3000, 'ACCEPT'),
       (NULL, 3000, 'ACCEPT'),
       (NULL, 3000, 'ACCEPT'),
       (NULL, 3000, 'ACCEPT'),
       (NULL, 3000, 'ACCEPT'),
       (NULL, 3000, 'ACCEPT');

-- 주문 상품
insert into order_product (price, quantity, delivery_id, order_id, product_id)
values (12000, 10, 1, 1, 1),
       (15500, 20, 2, 1, 2),
       (53000, 30, 3, 1, 3),
       (12000, 10, 4, 2, 1),
       (15500, 20, 5, 2, 2),
       (15500, 20, 6, 3, 4),
       (15500, 20, 7, 4, 2),
       (15500, 20, 8, 5, 5),
       (15500, 20, 9, 6, 4);
