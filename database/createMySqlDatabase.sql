-- currently not used automatically, run manually the first time to provision the user
-- note: the following commands work the same regardless of if the scholar_warehouse database exists
CREATE USER 'warehouse'@'localhost' IDENTIFIED BY 't3achTh3Childr3n';
-- TODO in PROD: this line should be: 
-- GRANT USAGE ON *.* TO 'warehouse'@'localhost REQUIRE SSL';
GRANT USAGE ON *.* TO 'warehouse'@'localhost';
GRANT ALL PRIVILEGES ON scholar_warehouse.* TO 'warehouse'@'localhost';
