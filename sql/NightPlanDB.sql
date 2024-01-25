flush binary logs;
use nightplan;
create table Events
(
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(25) NOT NULL,
    music_type VARCHAR(25) NOT NULL,
    location VARCHAR(50) NOT NULL,
    logo LONGBLOB
);

create table EventImages
(
    id INT AUTO_INCREMENT PRIMARY KEY,
    image_data LONGBLOB
);

create table Users
(
    id INT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    username VARCHAR(20) NOT NULL unique,
    password VARCHAR(20) NOT NULL,
    name VARCHAR(20) NOT NULL,
    surname VARCHAR(20) NOT NULL,
    dateOfBirth VARCHAR(20) NOT NULL,
    gender VARCHAR(10),
    typeOfUser VARCHAR(20),
    userStatus VARCHAR(15)
);

insert into Users (id, username, password, name, surname, dateOfBirth, gender, typeOfuser, userStatus) values (NULL, 'Traveler','Traveler','Traveler','Traveler','16-03-1998','Female','User','online');

insert into EventImages (image_data) values (load_file('C:\Users\ianni\Desktop\ISPW\Progetto\repo\ispwproject-2324\src\main\resources\images\home\Capture-decran-2023-09-25-a-14.34.08.jpg'));
