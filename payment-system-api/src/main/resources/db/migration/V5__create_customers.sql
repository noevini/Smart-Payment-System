create table customers (
                           id bigserial primary key,
                           business_id bigint not null,

                           name varchar(120) not null,
                           email varchar(255),
                           phone varchar(30),
                           notes varchar(500),

                           created_at timestamp not null,
                           updated_at timestamp not null,

                           constraint fk_customers_business
                               foreign key (business_id) references businesses(id)
);

create unique index ux_customers_business_email
    on customers (business_id, email)
    where email is not null;
