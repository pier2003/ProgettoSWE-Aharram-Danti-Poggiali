DROP DATABASE IF EXISTS School;
CREATE DATABASE IF NOT EXISTS School;
USE School;

CREATE TABLE IF NOT EXISTS Teachers (
    id_teacher INT AUTO_INCREMENT PRIMARY KEY,
    username CHAR(6) NOT NULL,
    password VARCHAR(20) NOT NULL,
    name VARCHAR(50) NOT NULL,
    surname VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS Classes (
	name CHAR(2) PRIMARY KEY,
    classroom VARCHAR(3) NOT NULL
);

CREATE TABLE IF NOT EXISTS Students (
    id_student INT AUTO_INCREMENT PRIMARY KEY,
    username CHAR(6) NOT NULL,
    password VARCHAR(20) NOT NULL,
    name VARCHAR(50) NOT NULL,
    surname VARCHAR(50) NOT NULL,
    date_of_birth DATE NOT NULL,
    class CHAR (2) NOT NULL,
    FOREIGN KEY (class) REFERENCES Classes(name)
);

CREATE TABLE IF NOT EXISTS Parents (
	id_parent INT AUTO_INCREMENT PRIMARY KEY,
    username CHAR(6) NOT NULL,
    password VARCHAR(20) NOT NULL,
    name VARCHAR(50) NOT NULL,
    surname VARCHAR(50) NOT NULL,
    id_student INT NOT NULL,
    FOREIGN KEY (id_student) REFERENCES Students(id_student) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Reports (
    id_report INT AUTO_INCREMENT PRIMARY KEY,
    description TEXT NOT NULL,
    date DATE NOT NULL,
    id_student INT NOT NULL,
    id_teacher INT NOT NULL,
    FOREIGN KEY (id_teacher) REFERENCES Teachers(id_teacher) ON DELETE CASCADE,
    FOREIGN KEY (id_student) REFERENCES Students(id_student) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Teachings (
	id_teaching INT AUTO_INCREMENT PRIMARY KEY,
    id_teacher INT NOT NULL,
    class_name CHAR(2) NULL,
    subject VARCHAR(50) NOT NULL,
    FOREIGN KEY (id_teacher) REFERENCES Teachers(id_teacher) ON DELETE CASCADE,
    FOREIGN KEY (class_name) REFERENCES Classes(name) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Grades (
    id_grade INT AUTO_INCREMENT PRIMARY KEY,
    rating DOUBLE NOT NULL,
    weight INT NOT NULL DEFAULT 1,
    description TEXT NOT NULL,
    date DATE NOT NULL,
    id_student INT NOT NULL,
    id_teaching INT NOT NULL,
    FOREIGN KEY (id_student) REFERENCES Students(id_student) ON DELETE CASCADE,
    FOREIGN KEY (id_teaching) REFERENCES Teachings(id_teaching) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Annotations (
    id_annotation INT AUTO_INCREMENT PRIMARY KEY,
    id_teaching INT NOT NULL,
    type ENUM('lesson', 'homework') NOT NULL,
    description TEXT NOT NULL,
    date DATE NOT NULL,
    start_hour TIME,
    end_hour TIME,
    submission_date DATE,
    FOREIGN KEY (id_teaching) REFERENCES Teachings(id_teaching) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Absences (
    date DATE NOT NULL,
    justification BOOL NOT NULL DEFAULT FALSE,
    id_student INT NOT NULL,
    FOREIGN KEY (id_student) REFERENCES Students(id_student) ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS MeetingsAvailability (
    date DATE NOT NULL,
    hour text NOT NULL,
    isBooked BOOL NOT NULL DEFAULT FALSE,
    id_teacher INT NOT NULL,
    PRIMARY KEY (date, hour, id_teacher),
    FOREIGN KEY (id_teacher) REFERENCES Teachers(id_teacher) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Meetings (
	id_meeting INT PRIMARY KEY AUTO_INCREMENT,
    date DATE NOT NULL,
    hour text NOT NULL,
    id_teacher INT NOT NULL,
    id_parent INT NOT NULL,
    FOREIGN KEY (date, hour, id_teacher) REFERENCES MeetingsAvailability(date, hour, id_teacher) ON DELETE CASCADE,
    FOREIGN KEY (id_parent) REFERENCES Parents(id_parent) ON DELETE CASCADE
);

INSERT INTO Teachers (username, password, name, surname) 
VALUES 
('T00001', 'secure123', 'Alessandro', 'Ferrari'),
('T00002', 'pass2023', 'Maria', 'Bianchi'),
('T00003', 'abcXYZ789', 'Luca', 'Parigi'),
('T00004', 'teach789', 'Francesca', 'Rossi'),
('T00005', 'alpha456', 'Li', 'Wei');

INSERT INTO Classes (name, classroom) 
VALUES 
('1A', 'A01'),
('2B', 'B02'),
('3C', 'C03'),
('4D', 'D04');

INSERT INTO Students (username, password, name, surname, date_of_birth, class) 
VALUES 
('S10001', 'stud12345', 'Marco', 'Rossi', '2005-04-15', '1A'),
('S10002', 'alpha4567', 'Giulia', 'Neri', '2006-09-21', '2B'),
('S10003', 'beta12345', 'Luca', 'Verdi', '2007-11-10', '1A'),
('S10004', 'gamma9876', 'Anna', 'Esposito', '2005-06-18', '3C'),
('S10005', 'delta6543', 'Youness', 'Mohammed', '2006-05-22', '2B');

INSERT INTO Parents (username, password, name, surname, id_student) 
VALUES 
('P20001', 'parent123', 'Francesco', 'Rossi', 1),
('P20002', 'guardian456', 'Lucia', 'Neri', 2),
('P20003', 'fam98765', 'Paolo', 'Verdi', 3),
('P10004', 'gamma9876', 'Giulio', 'Esposito', 4),
('P20004', 'caretaker', 'Abir', 'Mohammed', 5);

INSERT INTO Reports (description, date, id_student, id_teacher) 
VALUES 
('Disturbo in classe durante la lezione.', '2023-10-11', 1, 1),
('Non ha portato i compiti.', '2023-11-04', 2, 2),
('Discussione accesa col docente.', '2023-11-02', 3, 3),
('Problemi di disciplina segnalati.', '2023-11-22', 4, 4);

INSERT INTO Teachings (id_teacher, class_name, subject) 
VALUES 
(1, '1A', 'Matematica'),
(2, '2B', 'Storia'),
(3, '3C', 'Geografia'),
(4, '1A', 'Inglese'),
(5, '4D', 'Italiano');

INSERT INTO Grades (rating, description, date, id_student, id_teaching) 
VALUES 
(8.5, 'Buona comprensione del capitolo 3', '2023-11-11', 1, 1),
(5.75,  'Miglioramento necessario', '2023-11-12', 2, 2),
(7, 'Verifica scritta sul nord Italia', '2023-11-14', 4, 3),
(9.25, 'Eccellente prova scritta', '2023-11-15', 3, 4);

INSERT INTO Annotations (id_teaching, type, description, date, start_hour, end_hour, submission_date) 
VALUES 
(1,  'lesson', 'Introduzione agli integrali', '2023-11-25', '09:00:00', '10:00:00', NULL),
(2, 'homework', 'Leggere capitolo 4 del libro di testo', '2023-11-22', NULL, NULL, '2023-11-28'),
(4, 'homework', 'Performer Blu. Exercises pages 3 - 45', '2023-11-12', NULL, NULL, '2023-11-28'),
(5, 'lesson', 'Pirandello: la Carriola', '2023-11-25', '08:00:00', '09:00:00', NULL);

INSERT INTO Absences (date, justification, id_student) 
VALUES 
('2023-11-20', FALSE, 1),
('2023-11-21', TRUE, 2),
('2023-11-22', FALSE, 3);

INSERT INTO MeetingsAvailability (date, hour, id_teacher)
VALUES
('2023-12-01', '15:00:00', 1),
('2023-12-01', '16:00:00', 2),
('2023-12-02', '14:00:00', 3),
('2023-12-02', '15:00:00', 4),
('2023-12-03', '09:00:00', 1),
('2023-12-03', '10:00:00', 2);

INSERT INTO Meetings (date, hour, id_teacher, id_parent)
VALUES
('2023-12-01', '15:00:00', 1, 1),
('2023-12-01', '16:00:00', 2, 2),
('2023-12-02', '14:00:00', 3, 3),
('2023-12-03', '09:00:00', 1, 4);