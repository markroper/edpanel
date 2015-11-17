-- currently not used automatically, run manually the first time to provision the user
-- note: the following commands work the same regardless of if the scholar_warehouse database exists
CREATE USER 'warehouse' IDENTIFIED BY 't3achTh3Childr3n';
-- TODO in PROD: client can use SSL without this, but require it to be safe
-- GRANT USAGE ON *.* TO 'warehouse' REQUIRE SSL;
GRANT USAGE ON *.* TO 'warehouse';
GRANT ALL PRIVILEGES ON scholar_warehouse.* TO 'warehouse';
