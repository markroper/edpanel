-- use a default DB name, but allow this to be overridden if @databasename was set earlier 
set @default_database_name='scholar_warehouse';
set @databasename=if(@databasename is not null, @databasename, @default_database_name);

SET @query = CONCAT('DROP DATABASE IF EXISTS ', @databasename, ';');
PREPARE stmt FROM @query;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
set @query = NULL;

SET @query = CONCAT('CREATE DATABASE ', @databasename, ';');
PREPARE stmt FROM @query;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
set @query = NULL;
