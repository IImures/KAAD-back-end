create table contact (
     id bigint generated by default as identity,
     uuid varchar(255) not null unique,
     created_at timestamp(6) with time zone not null,
     email varchar(255),
     full_name varchar(255) not null,
     phone_number varchar(255),
     resolved boolean not null default false,
     contact_type_id bigint,
     specialization_id bigint not null,
     language_id bigint not null,
     primary key (id),
     constraint fk_contact_type foreign key (contact_type_id) references contact_type,
     constraint fk_specialization foreign key (specialization_id) references specialization,
     constraint fk_language foreign key (language_id) references language
)