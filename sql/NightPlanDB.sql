flush binary logs;
create table Locations
(
	country VARCHAR(45) NOT NULL,
    city VARCHAR(45) NOT NULL primary key,
    tipeOfPersonality VARCHAR(20),
    photo1 LONGBLOB,
    descr VARCHAR(4096)
);

create table Usr
(
	username VARCHAR(20) NOT NULL unique,
    passw VARCHAR(20) NOT NULL,
    nome VARCHAR(20) NOT NULL,
    surname VARCHAR(20) NOT NULL,
    dateOfBirth VARCHAR(20) NOT NULL,
    gender VARCHAR(10),
    tipeOfUser VARCHAR(20),
    tipeOfPersonality VARCHAR(20),
    profilePicture LONGBLOB,
    userStatus VARCHAR(15),
    primary key(username)
);

create table TravelGroups
(
    travCity VARCHAR(45),
	groupOwner VARCHAR(20) not null,
	title VARCHAR(50) unique NOT NULL,
	foreign key (groupOwner)
			references Usr(username) on delete cascade,
	primary key(groupOwner, title)
);

create table Post
(
	ID int not null,
	photo LONGBLOB,
    utente VARCHAR(20),
	descr VARCHAR(4096),
    beds VARCHAR(5),
    city VARCHAR(30),
    address VARCHAR(40),
    services BLOB,
    squareMetres VARCHAR(10),
    tipologia VARCHAR(20),
    foreign key (utente) 
			references Usr(username) on delete cascade,
    primary key(ID, utente)
);

create table Chat
(
	ID int auto_increment primary key,
	sender VARCHAR(20),
	receiver VARCHAR(50),
	message VARCHAR(1000),
    foreign key (sender)
			references Usr(username) on delete cascade
);

create table Tickets
(
	ID int primary key,
	depCity VARCHAR(50),
    arrCity VARCHAR(50),
	dateOfDep date,
    dateOfArr date,
    cost float,
	numOfTick int
);

create table ParticipatesTo
(
	participant VARCHAR(20),
	grp VARCHAR(50),
    foreign key (participant)
			references Usr(username) on delete cascade,
	foreign key (grp)
			references travelgroups(title) on delete cascade
);

create table Buys
(
	ticket int, 
	passenger varchar(20),
    foreign key (ticket)
			references tickets(ID) on delete cascade,
	foreign key (passenger)
			references Usr(username) on delete cascade
);
