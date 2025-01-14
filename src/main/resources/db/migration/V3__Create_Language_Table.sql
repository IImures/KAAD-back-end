create table language (
      id bigint generated by default as identity,
      code varchar(255) not null,
      language varchar(255) not null,
      image_data oid,
      default_language boolean not null,
      primary key (id)
);

insert into language (code, language, default_language) values ('pl','polish', True);
insert into language (code, language, default_language) values ('en','english', False);
