insert into `user` (username, password, enabled) values ('mattg', '$2a$12$QEJJY2BIGzObt/qXynvaHOYmbvvdlcJzUV7PlmDgs0St1C.m4bkrK', 1);  -- password: admin
insert into `authorities` (user_id, authority) values (1, 'ADMINISTRATOR');
insert into `staff` (staff_name, staff_user_fk, school_fk, admin, teacher) values ('Matt Greenwood', 1, null, TRUE, FALSE);

