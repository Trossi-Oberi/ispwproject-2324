flush binary logs;
use nightplan;
create table Events
(
    id INT AUTO_INCREMENT PRIMARY KEY,
    organizer VARCHAR(50) NOT NULL,
    name VARCHAR(50) NOT NULL,
    city VARCHAR(50) NOT NULL,
    address VARCHAR(50) NOT NULL,
    music_genre VARCHAR(25) NOT NULL,
    date VARCHAR(15) NOT NULL,
    time VARCHAR(15) NOT NULL,
    image LONGBLOB
);

/*create table EventImages
(
    id INT AUTO_INCREMENT PRIMARY KEY,
    image_data LONGBLOB
);
*/
create table Users
(
    id INT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    username VARCHAR(50) NOT NULL unique,
    password VARCHAR(50) NOT NULL,
    firstName VARCHAR(50) NOT NULL,
    lastName VARCHAR(50) NOT NULL,
    dateOfBirth VARCHAR(20) NOT NULL,
    gender VARCHAR(10) NOT NULL,
    city VARCHAR(30) NOT NULL,
    userType VARCHAR(20),
    userStatus VARCHAR(15)
);

insert into Users (id, username, password, firstName, lastName, dateOfBirth, gender, city, userType, userStatus)values (NULL, 'Matteo', 'Matteo', 'Matteo', 'Trossi', '16/03/1998', 'Male', 'Anagni', 'User', 'Online');

insert into Users (id, username, password, firstName, lastName, dateOfBirth, gender, city, userType, userStatus)values (NULL, 'Nicolas', 'Nicolas', 'Nicolas', 'Oberi', '16/03/1998', 'Male', 'Cave', 'Organizer', 'Online');

# insert into EventImages (image_data) values (load_file('C:\Users\ianni\Desktop\ISPW\Progetto\repo\ispwproject-2324\src\main\resources\images\home\Capture-decran-2023-09-25-a-14.34.08.jpg'));
