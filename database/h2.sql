DROP TABLE IF EXISTS compound_pk_objects;
CREATE TABLE compound_pk_objects(
    id          bigint,
    id2         bigint,
    name        varchar(255),

    primary key(id, id2)
);

DROP TABLE IF EXISTS persistent_object;
CREATE TABLE persistent_object (
  id            bigint,
  name          varchar,
  fieldValue    varchar,
  objectValue   varchar,
  autoColumn    timestamp,
  some_boolean  int,

  primary key (id)
);


DROP TABLE IF EXISTS other_persistent_object;
CREATE TABLE other_persistent_object (
  id            bigint,
  name          varchar,
  fieldValue    varchar,
  objectValue   varchar,
  autoColumn    timestamp,
  some_boolean  int,

  primary key (id)
);




DROP TABLE IF EXISTS ambiguous_table_name;
CREATE TABLE ambiguous_table_name (
  id        bigint,
  name      varchar(255),

  PRIMARY KEY  (id)
);


DROP TABLE IF EXISTS AmbiguousTableName;
CREATE TABLE AmbiguousTableName (
  id        bigint,
  name      varchar(255),

  PRIMARY KEY  (id)
);


DROP TABLE IF EXISTS Employees;
CREATE TABLE Employees(
  employee_id       varchar(255) NOT NULL,
  name              varchar(255) NOT NULL,
  job_title         varchar(255) NOT NULL,
  job_description   varchar(255),
  employed_since    datetime,
  monthly_salary    int,
  PRIMARY KEY (employee_id)
);



DROP TABLE IF EXISTS table1;
CREATE TABLE table1(
    id   bigint,
    text varchar(255)
);

DROP TABLE IF EXISTS table2;
CREATE TABLE table2(
    id   bigint,
    fid  bigint,
    text varchar(255)
);


DROP TABLE IF EXISTS PrimaryObjects;
CREATE TABLE PrimaryObjects (
    id   identity,
    text varchar(255)
);

DROP TABLE IF EXISTS RelatedObjects;
CREATE TABLE RelatedObjects (
    id identity,
    primary_id bigint,
    a_value varchar(255)
);


DROP TABLE IF EXISTS TableWithAutoIncrement;
CREATE TABLE TableWithAutoIncrement (
   id identity
);
