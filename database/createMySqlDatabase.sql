CREATE DATABASE scholar_warehouse;
CREATE USER 'warehouse'@'localhost' IDENTIFIED BY 't3achTh3Childr3n';
GRANT USAGE on *.* to warehouse@localhost identified by 't3achTh3Childr3n';
GRANT ALL PRIVILEGES ON scholar_warehouse.* to warehouse@localhost;