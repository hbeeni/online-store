set foreign_key_checks = 0;

/* alter table address drop foreign key FK_address_users;
alter table cart drop foreign key FK_cart_users;
alter table cart_product drop foreign key FK_cart_product_cart;
alter table cart_product drop foreign key FK_cart_product_product;
alter table order_product drop foreign key FK_order_product_orders;
alter table order_product drop foreign key FK_order_product_product;
alter table orders drop foreign key FK_orders_delivery;
alter table orders drop foreign key FK_orders_users;
alter table product drop foreign key FK_product_category; */

drop table if exists address;
drop table if exists cart;
drop table if exists cart_product;
drop table if exists category;
drop table if exists delivery;
drop table if exists order_product;
drop table if exists orders;
drop table if exists product;
drop table if exists users;

set foreign_key_checks = 1;

create table address
(
    id              bigint      not null auto_increment,
    user_id         bigint      not null,
    detail          varchar(50) not null,
    zipcode         varchar(20) not null,
    default_address char(1)     not null default 'N',
    created_at      datetime(6) not null,
    modified_at     datetime(6) not null,
    constraint PK_address PRIMARY KEY (id)
) engine = InnoDB;

create table cart_product
(
    id          bigint      not null auto_increment,
    user_id     bigint      not null,
    product_id  bigint      not null,
    quantity    integer     not null,
    created_at  datetime(6) not null,
    modified_at datetime(6) not null,
    constraint PK_cart_product PRIMARY KEY (id)
) engine = InnoDB;

create table category
(
    id          bigint      not null auto_increment,
    name        varchar(20) not null unique,
    description varchar(100),
    created_at  datetime(6) not null,
    created_by  varchar(50) not null,
    modified_at datetime(6) not null,
    modified_by varchar(50) not null,
    constraint PK_category PRIMARY KEY (id)
) engine = InnoDB;

create table delivery
(
    id              bigint      not null auto_increment,
    delivery_status varchar(20) not null default 'ACCEPT',
    delivery_fee    integer     not null,
    delivered_at    datetime(6),
    constraint PK_delivery PRIMARY KEY (id)
) engine = InnoDB;

create table delivery_request
(
    id               bigint      not null auto_increment,
    delivery_address varchar(50) not null,
    receiver_name    varchar(20) not null,
    receiver_phone   varchar(20) not null,
    constraint PK_delivery_request PRIMARY KEY (id)
) engine = InnoDB;

create table order_product
(
    id         bigint  not null auto_increment,
    order_id   bigint  not null,
    product_id bigint  not null,
    price      integer not null,
    quantity   integer not null,
    constraint PK_order_product PRIMARY KEY (id)
) engine = InnoDB;

create table orders
(
    id                  bigint      not null auto_increment,
    orderer_id          bigint      not null,
    delivery_id         bigint      not null,
    delivery_request_id bigint      not null,
    orderer_phone       varchar(20) not null,
    order_status        varchar(20) not null default 'ORDER',
    created_at          datetime(6) not null,
    modified_at         datetime(6) not null,
    constraint PK_orders PRIMARY KEY (id)
) engine = InnoDB;

create table product
(
    id             bigint       not null auto_increment,
    category_id    bigint       not null,
    name           varchar(100) not null,
    price          integer      not null,
    description    varchar(255),
    stock_quantity integer      not null,
    sales_volume   integer      not null default 0,
    sale_status    varchar(20)  not null default 'WAIT',
    delivery_fee   integer      not null default 3000,
    image_name     varchar(200),
    created_at     datetime(6)  not null,
    created_by     varchar(50)  not null,
    modified_at    datetime(6)  not null,
    modified_by    varchar(50)  not null,
    constraint PK_product PRIMARY KEY (id)
) engine = InnoDB;

create table users
(
    id          bigint       not null auto_increment,
    uid         varchar(50)  not null unique,
    password    varchar(255) not null,
    name        varchar(20)  not null,
    email       varchar(100) not null unique,
    nickname    varchar(20),
    phone       varchar(20)  not null,
    role_type   varchar(20)  not null default 'USER',
    created_at  datetime(6)  not null,
    modified_at datetime(6)  not null,
    constraint PK_users PRIMARY KEY (id)
) engine = InnoDB;

alter table address
    add constraint FK_address_users
        foreign key (user_id) references users (id);

alter table cart_product
    add constraint FK_cart_product_product
        foreign key (product_id) references product (id);

alter table cart_product
    add constraint FK_cart_product_users
        foreign key (user_id) references users (id);

alter table order_product
    add constraint FK_order_product_orders
        foreign key (order_id) references orders (id);

alter table order_product
    add constraint FK_order_product_product
        foreign key (product_id) references product (id);

alter table orders
    add constraint FK_orders_delivery
        foreign key (delivery_id) references delivery (id);

alter table orders
    add constraint FK_orders_delivery_request
        foreign key (delivery_request_id) references delivery_request (id);

alter table orders
    add constraint FK_orders_users
        foreign key (orderer_id) references users (id);

alter table product
    add constraint FK_product_category
        foreign key (category_id) references category (id);
