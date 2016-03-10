-- currently not used automatically, run manually the first time to provision the user
-- note: the following commands work the same regardless of if the database exists

--  note: this next line will fail if run on the same RDS for a 2nd (or +) school -- fix by giving each school a different username/pass
CREATE USER 'warehouse' IDENTIFIED BY 't3achTh3Childr3n';
-- TODO in PROD: client can use SSL without this, but require it to be safe
-- GRANT USAGE ON *.* TO 'warehouse' REQUIRE SSL;
GRANT USAGE ON *.* TO 'warehouse';

-- use a default DB name, but allow this to be overridden if @databasename was set earlier 
set @default_database_name='scholar_warehouse';
set @databasename=if(@databasename is not null, @databasename, @default_database_name);

-- GRANT ALL PRIVILEGES ON scholar_warehouse.* TO 'warehouse';
SET @query = CONCAT('GRANT ALL PRIVILEGES ON ', @databasename, '.* TO ''warehouse'';');
PREPARE stmt FROM @query;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
set @query = NULL;

FLUSH PRIVILEGES;