DROP TABLE IF EXISTS compound_pk_objects;
CREATE TABLE compound_pk_objects(
    id          bigint(20),
    id2         bigint(20),
    name        varchar(255),

    primary key(id, id2)
) TYPE=InnoDB ;

DROP TABLE IF EXISTS persistent_object;
CREATE TABLE persistent_object (
  id        bigint(20) NOT NULL default '0',
  name      longtext NOT NULL,
  fieldValue     longtext,
  objectValue longtext,
  autoColumn timestamp,
  some_boolean int,
  
  PRIMARY KEY  (id)
) TYPE=InnoDB ;


DROP TABLE IF EXISTS other_persistent_object;
CREATE TABLE other_persistent_object (
  id        bigint(20) NOT NULL default '0',
  name      longtext NOT NULL,
  fieldValue     longtext,
  objectValue longtext,
  autoColumn timestamp,
  some_boolean int,

  PRIMARY KEY  (id)
) TYPE=InnoDB ;



DROP TABLE IF EXISTS ambiguous_table_name;
CREATE TABLE ambiguous_table_name (
  id        bigint(20) NOT NULL default '0',
  name      longtext NOT NULL,

  PRIMARY KEY  (id)
) TYPE=InnoDB ;


DROP TABLE IF EXISTS AmbiguousTableName;
CREATE TABLE AmbiguousTableName (
  id        bigint(20) NOT NULL default '0',
  name      longtext NOT NULL,

  PRIMARY KEY  (id)
) TYPE=InnoDB ;


DROP TABLE IF EXISTS Employees;
CREATE TABLE Employees(
  employee_id       varchar(255) NOT NULL,
  name              varchar(255) NOT NULL,
  job_title         varchar(255) NOT NULL,
  job_description   longtext,
  employed_since    datetime,
  monthly_salary    int,
  PRIMARY KEY (employee_id)
) TYPE= InnoDB ;


DROP TABLE IF EXISTS table1;
CREATE TABLE table1(
    id    bigint(20),
    text  varchar(255)
) TYPE= InnoDB ;

DROP TABLE IF EXISTS table2;
CREATE TABLE table2(
    id    bigint(20),
    fid   bigint(20),
    text  varchar(255)
) TYPE= InnoDB;

