insert into `user` (username, password, enabled) values ('mattg', '$2a$12$QEJJY2BIGzObt/qXynvaHOYmbvvdlcJzUV7PlmDgs0St1C.m4bkrK', 1);  -- password: admin
insert into `authorities` (user_id, authority) values (1, 'ADMINISTRATOR');
insert into `administrator` (administrator_name, administrator_user_fk, school_fk) values ('Matt Greenwood', 1, null);
