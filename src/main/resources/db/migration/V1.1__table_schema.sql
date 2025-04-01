create table if not exists locations
(
    id      serial primary key,
    address text                   not null,
    country character varying(255) not null
);

create table if not exists airplanes
(
    id     serial primary key,
    name   character varying(255) not null,
    number character varying(255) not null unique
);

create table if not exists luggage
(
    id     serial primary key,
    weight integer not null
);

create table if not exists passengers
(
    id       serial primary key,
    passport character varying(255) not null,
    name     character varying(255) not null,
    email    character varying(255) not null
);

create table if not exists staff_roles
(
    id   serial primary key,
    name character varying(255) not null unique
);

create table if not exists staff
(
    id     serial primary key,
    roleId integer not null references staff_roles (id)
);

create table if not exists airplane_staff
(
    id         serial primary key,
    airplaneId integer not null references airplanes (id),
    staffId    integer not null references staff (id)
);
create index if not exists airplane_personal_idx on airplane_staff (airplaneId, staffId);

create table if not exists routes
(
    id                  serial primary key,
    airplaneId          integer                not null references airplanes (id),
    departureTime       character varying(255) not null,
    arrivalTime         character varying(255) not null,
    departureLocationId integer                not null references locations (id),
    arrivalLocationId   integer                not null references locations (id)
);

create type tickets_class as enum ('economy', 'business', 'first');
create table if not exists tickets
(
    id          serial primary key,
    routeId     integer                not null references routes (id),
    passengerId integer                not null references passengers (id),
    passport    character varying(255) not null,
    staffId     integer                not null references staff (id),
    class       tickets_class          not null,
    luggageId   integer default null references luggage (id)
);