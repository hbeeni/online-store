-- 카테고리
insert into category (id, created_at, modified_at, created_by, modified_by, description, name)
values (1, now(), now(), 'admin', 'admin', '상의', '상의'),
       (2, now(), now(), 'admin', 'admin', '하의', '하의'),
       (3, now(), now(), 'admin', 'admin', '음식', '음식');

-- 상품
insert into product (id, created_at, modified_at, created_by, modified_by, description, image_url, name, price, sale_status, sales_volume, stock_quantity, category_id)
values (1, now(), now(), 'seller', 'seller', '꽃무늬 바지', null, '꽃무늬 바지', 12000, 'SALE', 10, 100, 2),
       (2, now(), now(), 'seller', 'seller', '꽃무늬 셔츠', null, '꽃무늬 셔츠', 15500, 'SALE', 20, 121, 1),
       (3, now(), now(), 'seller', 'seller', '프렌치 코트', null, '프렌치 코트', 53000, 'WAIT', 0, 55, 1),
       (4, now(), now(), 'seller', 'seller', '아보카도', null, '아보카도', 2300, 'SALE', 120, 20, 3),
       (5, now(), now(), 'seller', 'seller', '요거트', null, '요거트', 3500, 'SALE', 22, 56, 3);
