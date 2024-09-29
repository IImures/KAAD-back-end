create table team_member (
     id bigint generated by default as identity,
     email varchar(255),
     first_name varchar(255) not null,
     image_data oid not null,
     img_name varchar(255) not null,
     img_type varchar(255) not null,
     last_name varchar(255) not null,
     phone varchar(255),
     priority bigint,
     primary key (id)
)