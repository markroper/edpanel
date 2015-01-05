-- currently not used automatically, run manually the first time to provision the user
CREATE DATABASE scholar_warehouse;
CREATE USER 'warehouse'@'localhost' IDENTIFIED BY 't3achTh3Childr3n';
GRANT USAGE ON *.* TO 'warehouse'@'localhost';
GRANT ALL PRIVILEGES ON scholar_warehouse.* TO 'warehouse'@'localhost';