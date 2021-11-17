delete from schedules;
delete from classrooms;
delete from students;
delete from groups;
delete from subjects;
delete from teachers;
delete from users;
delete from group_subjects;

insert into classrooms values (1, 18, 'classroom1'), (2, 24, 'classroom2');

insert into groups values (1, 'group1'), (2, 'group2');

insert into students values (1, 'John', 1, 'Smith'), (2, 'Enthony', 2, 'McLovin');

insert into subjects values (1, 'description1', 'subject1'), (2, 'description2', 'subject2');

insert into teachers values (1, 'academicDegree1', 'Mr', 'Bean'), (2, 'academicDegree2', 'Uncle', 'Wassermann');

insert into schedules values (1, '2018-10-20', 1, 1, '8:00-9:20', 1, 1), (2, '2019-11-25', 2, 2, '9:35-10:55', 2, 2);

insert into users values (1, 'admin', 'admin4ik', 'images/user/admin_default.png', 'ADMIN', true),
                         (2, 'testuser', 'password', 'images/user/usr_default.png', 'USER', true);