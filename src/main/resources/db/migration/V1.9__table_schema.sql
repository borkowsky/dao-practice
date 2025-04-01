alter table tickets drop column passengerId;
alter table tickets add column userId int not null references users(id);