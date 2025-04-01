alter table users add column createdAt timestamp default now();
alter table users add column updatedAt timestamp default now();

alter table locations add column createdAt timestamp default now();
alter table locations add column updatedAt timestamp default now();

alter table airplanes add column createdAt timestamp default now();
alter table airplanes add column updatedAt timestamp default now();

alter table luggage add column createdAt timestamp default now();
alter table luggage add column updatedAt timestamp default now();

alter table passengers add column createdAt timestamp default now();
alter table passengers add column updatedAt timestamp default now();

alter table staff_roles add column createdAt timestamp default now();
alter table staff_roles add column updatedAt timestamp default now();

alter table airplane_staff add column createdAt timestamp default now();
alter table airplane_staff add column updatedAt timestamp default now();

alter table routes add column createdAt timestamp default now();
alter table routes add column updatedAt timestamp default now();

alter table tickets add column createdAt timestamp default now();
alter table tickets add column updatedAt timestamp default now();