DROP TABLE compound_pk_objects;
CREATE TABLE compound_pk_objects(
    id          bigint not null,
    id2         bigint not null,
    name        varchar(255),

    primary key(id, id2)
);

DROP TABLE persistent_object;
CREATE TABLE persistent_object (
  id            bigint not null,
  name          varchar(255),
  fieldValue    varchar(255),
  objectValue   varchar(255),
  autoColumn    timestamp,
  some_boolean  bigint,

  primary key (id)
);


DROP TABLE other_persistent_object;
CREATE TABLE other_persistent_object (
  id            bigint not null,
  name          varchar(255),
  fieldValue    varchar(255),
  objectValue   varchar(255),
  autoColumn    timestamp,
  some_boolean  bigint,

  primary key (id)
);




DROP TABLE ambiguous_table_name;
CREATE TABLE ambiguous_table_name (
  id        bigint not null,
  name      varchar(255),

  PRIMARY KEY  (id)
);


DROP TABLE AmbiguousTableName;
CREATE TABLE AmbiguousTableName (
  id        bigint not null,
  name      varchar(255),

  PRIMARY KEY  (id)
);


DROP TABLE Employees;
CREATE TABLE Employees(
  employee_id       varchar(10) NOT NULL,
  name              varchar(255) NOT NULL,
  job_title         varchar(255) NOT NULL,
  job_description   varchar(255),
  employed_since    date,
  monthly_salary    int,
  PRIMARY KEY (employee_id)
);



DROP TABLE table1;
CREATE TABLE table1(
    id   bigint,
    text varchar(255)
);

DROP TABLE table2;
CREATE TABLE table2(
    id   bigint,
    fid  bigint,
    text varchar(255)
);


