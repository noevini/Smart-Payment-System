create table users (
  id bigserial not null primary key,
  name varchar(60) not null,
  email varchar(120) not null unique,
  phone varchar(30) not null,
  password_hash varchar(255) not null,
  role varchar(30) not null
    check (role in ('OWNER', 'STAFF')),
  created_at timestamp not null default now(),
  updated_at timestamp not null default now()
);