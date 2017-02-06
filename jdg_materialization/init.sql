CREATE TABLE status (VDBName varchar(50) not null, VDBVersion integer not null, SchemaName varchar(50) not null, Name varchar(256) not null, TargetSchemaName varchar(50), TargetName varchar(256) not null, Valid boolean not null, LoadState varchar(25) not null, Cardinality bigint, Updated timestamp not null, LoadNumber bigint not null, PRIMARY KEY (VDBName, VDBVersion, SchemaName, Name));
create table mytable(_id int primary key, _value varchar(64));
insert into mytable values (1, 'John');
insert into mytable values (2, 'Jonathan');
insert into mytable values (3, 'James');
insert into mytable values (4, 'Joe');
