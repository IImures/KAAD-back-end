create table post (
      id bigint generated by default as identity,
      content TEXT not null,
      created_at timestamp(6) with time zone not null,
      author_id bigint,
      primary key (id),
    constraint fk_user foreign key (author_id) references user_tab
)