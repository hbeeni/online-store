-- 회원: 비밀번호 test12
insert into users (created_at, modified_at, email, name, nickname, password, phone, role_type, uid)
values (now(), now(), 'soo@mail.com', '김철수', '철수',
        '$2a$10$wcVfFiEQnqu3WjgyiIsPzuqdYKV9WJ08Wx.4aac0e08CLFpUjvoW6', '01011111111', 'USER', 'soo'),
       (now(), now(), 'hee@mail.com', '김영희', '영희',
        '$2a$10$wcVfFiEQnqu3WjgyiIsPzuqdYKV9WJ08Wx.4aac0e08CLFpUjvoW6', '01022222222', 'USER', 'hee'),
       (now(), now(), 'admin@mail.com', '관리자', '관리자',
        '$2a$10$wcVfFiEQnqu3WjgyiIsPzuqdYKV9WJ08Wx.4aac0e08CLFpUjvoW6', '01012341234', 'ADMIN', 'admin');

-- 카테고리
insert into category (created_at, modified_at, created_by, modified_by, description, name)
values (now(), now(), 'admin', 'admin', '채소', '채소'),       -- 1
       (now(), now(), 'admin', 'admin', '과일', '과일'),       -- 2
       (now(), now(), 'admin', 'admin', '수산', '수산'),       -- 3
       (now(), now(), 'admin', 'admin', '정육', '정육'),       -- 4
       (now(), now(), 'admin', 'admin', '국·반찬', '국·반찬'),   -- 5
       (now(), now(), 'admin', 'admin', '밀키트', '밀키트'),     -- 6
       (now(), now(), 'admin', 'admin', '면', '면'),         -- 7
       (now(), now(), 'admin', 'admin', '음료·커피', '음료·커피'), -- 8
       (now(), now(), 'admin', 'admin', '간식·과자', '간식·과자'), -- 9
       (now(), now(), 'admin', 'admin', '유제품', '유제품') -- 10
;

-- 상품
insert into product (created_at, modified_at, created_by, modified_by, delivery_fee, name, image_name, description,
                     price, sale_status, sales_volume, stock_quantity, category_id)
values (now(), now(), 'admin', 'admin', 3000, '깐대파 500g', 'c1b2f2a2-f0b8-403a-b03b-351d1ee0bd05.jpg',
        '시원한 국물 맛의 비밀', 4500, 'SALE', 1000, 10000, 1),                        -- 1
       (now(), now(), 'admin', 'admin', 0, '양파 1.5kg', 'f33104ba-2e81-4b2e-91f7-658d45ec2d6d.jpg',
        '단단하고 아삭한 양파의 매력', 4290, 'SALE', 2132, 999, 1),                       -- 2
       (now(), now(), 'admin', 'admin', 3000, '칠레산 생 블루베리', 'f5a2b73b-ffe7-4831-94e1-e962996a103f.jpg',
        '톡톡 번져가는 상큼함 가득', 6980, 'SALE', 0, 23452, 2),                         -- 3
       (now(), now(), 'admin', 'admin', 3000, '남해안 대용량 생굴 1kg', '77c91724-7aff-4781-a040-6f9b477ded71.jpg',
        '넉넉하게 담은 대용량 굴', 25600, 'SALE', 120, 200, 3),                         -- 4
       (now(), now(), 'admin', 'admin', 3000, '모듬회 (냉장)', '4f1adad1-f619-4d06-9fb6-1672334e9594.jpg',
        '희고 깨끗한 감칠맛 한 점', 11900, 'SALE', 87, 56, 3),                          -- 5
       (now(), now(), 'admin', 'admin', 3000, '불고기 1kg', 'd6e89ff1-8e66-4344-966f-6b8c31f53ba2.jpg',
        '뚝배기 불고기로 먹으면 더 맛있는 불고기. 100g당 : 1,869원', 18690, 'SALE', 120, 20, 4), -- 6
       (now(), now(), 'admin', 'admin', 3000, '육개장 (2인분)', 'b5cadc9c-4868-46cb-a06c-2faa2fcc90dc.jpg',
        '사골 육수와 대파로 완성한', 7840, 'OUT_OF_STOCK', 552, 0, 5),                   -- 7
       (now(), now(), 'admin', 'admin', 3000, '영양보강 선식', '96d2286f-bd65-4916-9625-e651080e0366.jpg',
        '33가지 재료로 가뿐하게 시작하는 하루', 13900, 'SALE', 1333, 22, 6),                 -- 8
       (now(), now(), 'admin', 'admin', 3000, '돈코츠 라멘', '4432a267-6387-401c-8004-ecfc545068e4.jpg',
        '얼큰한 라면의 대명사', 8700, 'OUT_OF_STOCK', 1540, 0, 7),                     -- 9
       (now(), now(), 'admin', 'admin', 0, '에스프레소 커피 캡슐 5종', 'e8f36d1f-49fa-47ff-8a6d-e2a2ea1179a5.jpg',
        '풍성한 크래마가 지켜낸 깊은 커피 맛', 8020, 'CLOSE', 3423, 22, 8),                  -- 10
       (now(), now(), 'admin', 'admin', 0, '통밀 도너츠', 'd2b5da20-a5d6-4e5f-85dc-003b3292d166.jpg',
        '부드럽고 푹신푹신한 통밀 도너츠!', 3900, 'SALE', 28563, 7452, 9),                  -- 11
       (now(), now(), 'admin', 'admin', 0, '미니 초콜릿믹스 9P', '628b5fe8-0e0c-4978-957c-d39372e22574.jpg',
        '다양하게 경험하는 미니 초콜릿', 6980, 'SALE', 199, 26663, 9),                     -- 12
       (now(), now(), 'admin', 'admin', 0, '소화가 잘되는 우유 930mL', '6d78dfad-12c4-4e1d-ab26-687ed4071d07.jpg',
        '유당은 제거하고 칼슘은 높인', 8020, 'SALE', 7567345, 4477777, 10) -- 13
;

-- 배송 요청
insert into delivery_request (delivery_address, receiver_name, receiver_phone)
values ('주소 1', '수령인 1', '01011111111'), -- 1
       ('주소 2', '수령인 2', '01022222222'), -- 2
       ('주소 3', '수령인 3', '01022222222'), -- 3
       ('주소 4', '수령인 4', '01011111111'), -- 4
       ('주소 5', '수령인 5', '01011111111'), -- 5
       ('주소 6', '수령인 6', '01066666666'), -- 6
       ('주소 7', '수령인 7', '01077777777'), -- 7
       ('주소 8', '수령인 8', '01088888888'), -- 8
       ('주소 9', '수령인 9', '01011111111'), -- 9
       ('주소 10', '수령인 10', '01022222222') -- 10
;

-- 배송
insert into delivery (delivered_at, delivery_fee, delivery_status)
values (NULL, 0, 'PREPARING'),    -- 1
       (NULL, 3000, 'PREPARING'), -- 2
       (NULL, 3000, 'ACCEPT'),    -- 3
       (NULL, 0, 'ACCEPT'),       -- 4
       (NULL, 0, 'DELIVERING'),   -- 5
       (NULL, 0, 'DELIVERING'),   -- 6
       (NULL, 3000, 'ACCEPT'),    -- 7
       (NULL, 3000, 'PREPARING'), -- 8
       (NULL, 3000, 'ACCEPT'),    -- 9
       (NULL, 0, 'COMPLETED') -- 10
;

-- 주문
insert into orders (created_at, modified_at, order_status, orderer_phone, delivery_request_id, delivery_id, orderer_id)
values (now(), now(), 'ORDER', '01011111111', 1, 1, 1), -- 1 [1-1, 2-3] user1 PREPARING
       (now(), now(), 'ORDER', '01022222222', 2, 2, 2), -- 2 [4-1, 6-1] user2 PREPARING
       (now(), now(), 'ORDER', '01022222222', 3, 3, 2), -- 3 [9-2] user2 ACCEPT
       (now(), now(), 'ORDER', '01011111111', 4, 4, 1), -- 4 [10-5, 11-2] user1 ACCEPT
       (now(), now(), 'ORDER', '01011111111', 5, 5, 1), -- 5 [12-12] user1 DELIVERING
       (now(), now(), 'ORDER', '01022222222', 6, 6, 2), -- 6 [3-2, 13-1] user2 DELIVERING
       (now(), now(), 'ORDER', '01011111111', 7, 7, 1), -- 7 [6-5] user1 ACCEPT
       (now(), now(), 'ORDER', '01022222222', 8, 8, 2), -- 8 [5-1] user2 PREPARING
       (now(), now(), 'ORDER', '01011111111', 9, 9, 1), -- 9 [8-11] user1 ACCEPT
       (now(), now(), 'ORDER', '01022222222', 10, 10, 2) -- 10 [11-2, 12-2] user2 COMPLETED
;

-- 주문 상품
insert into order_product (order_id, product_id, price, quantity)
values (1, 1, 4500, 1),   -- 1
       (1, 2, 4290, 3),   -- 2
       (2, 4, 25600, 1),  -- 3
       (2, 6, 18690, 1),  -- 4
       (3, 9, 3900, 2),   -- 5
       (4, 10, 8020, 5),  -- 6
       (4, 11, 3900, 2),  -- 7
       (5, 12, 6980, 12), -- 8
       (6, 3, 6980, 2),   -- 9
       (6, 13, 8020, 1),  -- 10
       (7, 6, 18690, 5),  -- 11
       (8, 5, 11900, 1),  -- 12
       (9, 8, 13900, 11), -- 13
       (10, 11, 3900, 2), -- 14
       (10, 12, 6980, 2) -- 15
;
