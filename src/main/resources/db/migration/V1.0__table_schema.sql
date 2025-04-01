create type users_gender as enum ('male', 'female');
create table if not exists users (
    id serial primary key,
    username character varying(255) not null unique,
    password character varying(255) not null,
    name character varying(255) default null,
    age integer default null,
    gender users_gender not null
);