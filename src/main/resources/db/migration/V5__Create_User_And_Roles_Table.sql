create table user_tab (
      id bigint generated by default as identity,
      blog_image oid not null,
      email varchar(255) not null,
      first_name varchar(255) not null,
      is_enabled boolean not null,
      last_name varchar(255) not null,
      password varchar(255) not null,
      primary key (id)
);

create table roles (
                       id bigint generated by default as identity,
                       authority varchar(255) not null,
                       primary key (id)
);

create table user_roles (
        user_id bigint not null,
        role_id bigint not null,
        primary key (user_id, role_id),
    constraint fk_user foreign key (user_id) references user_tab,
    constraint fk_role foreign key (role_id) references roles
)