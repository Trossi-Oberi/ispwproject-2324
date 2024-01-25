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

insert into EventImages (image_data) values (load_file('C:\Users\ianni\Desktop\ISPW\Progetto\repo\ispwproject-2324\src\main\resources\images\home\Capture-decran-2023-09-25-a-14.34.08.jpg'));
