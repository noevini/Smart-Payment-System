create table businesses (
    id bigserial not null primary key,
    name varchar(140) not null,
    owner_id bigint not null references users(id) on delete restrict,
    created_at timestamp not null default now(),
    updated_at timestamp not null default now()
);

create index idx_businesses_owner_id on businesses(owner_id);
create unique index uq_businesses_owner_name on businesses(owner_id, name);