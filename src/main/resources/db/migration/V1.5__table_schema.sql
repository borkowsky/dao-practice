alter table staff add column createdAt timestamp default now();
alter table staff add column updatedAt timestamp default now();