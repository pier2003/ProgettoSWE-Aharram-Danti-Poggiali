PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS Teachers (
    id_teacher INTEGER PRIMARY KEY AUTOINCREMENT,
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
    id_student INTEGER PRIMARY KEY AUTOINCREMENT,
    username CHAR(6) NOT NULL,
    password VARCHAR(20) NOT NULL,
    name VARCHAR(50) NOT NULL,
    surname VARCHAR(50) NOT NULL,
    date_of_birth DATE NOT NULL,
    class CHAR(2) NOT NULL,
    FOREIGN KEY (class) REFERENCES Classes(name) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Parents (
    id_parent INTEGER PRIMARY KEY AUTOINCREMENT,
    username CHAR(6) NOT NULL,
    password VARCHAR(20) NOT NULL,
    name VARCHAR(50) NOT NULL,
    surname VARCHAR(50) NOT NULL,
    id_student INTEGER NOT NULL,
    FOREIGN KEY (id_student) REFERENCES Students(id_student) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Reports (
    id_report INTEGER PRIMARY KEY AUTOINCREMENT,
    description TEXT NOT NULL,
    date DATE NOT NULL,
    id_student INTEGER NOT NULL,
    id_teacher INTEGER NOT NULL,
    FOREIGN KEY (id_teacher) REFERENCES Teachers(id_teacher) ON DELETE CASCADE,
    FOREIGN KEY (id_student) REFERENCES Students(id_student) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Teachings (
    id_teaching INTEGER PRIMARY KEY AUTOINCREMENT,
    id_teacher INTEGER NOT NULL,
    class_name CHAR(2) NULL,
    subject VARCHAR(50) NOT NULL,
    FOREIGN KEY (id_teacher) REFERENCES Teachers(id_teacher) ON DELETE CASCADE,
    FOREIGN KEY (class_name) REFERENCES Classes(name) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Grades (
    id_grade INTEGER PRIMARY KEY AUTOINCREMENT,
    rating DOUBLE NOT NULL,
    weight INTEGER NOT NULL DEFAULT 1,
    description TEXT NOT NULL,
    date DATE NOT NULL,
    id_student INTEGER NOT NULL,
    id_teaching INTEGER NOT NULL,
    FOREIGN KEY (id_student) REFERENCES Students(id_student) ON DELETE CASCADE,
    FOREIGN KEY (id_teaching) REFERENCES Teachings(id_teaching) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Annotations (
    id_annotation INTEGER PRIMARY KEY AUTOINCREMENT,
    id_teaching INTEGER NOT NULL,
    type TEXT CHECK(type IN ('lesson', 'homework')) NOT NULL,
    description TEXT NOT NULL,
    date DATE NOT NULL,
    start_hour TEXT,
    end_hour TEXT,
    submission_date DATE,
    FOREIGN KEY (id_teaching) REFERENCES Teachings(id_teaching) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Absences (
    date DATE NOT NULL,
    justification INTEGER NOT NULL DEFAULT 0, -- 0=False, 1=True
    id_student INTEGER NOT NULL,
    PRIMARY KEY (date, id_student),
    FOREIGN KEY (id_student) REFERENCES Students(id_student) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS MeetingsAvailability (
    date DATE NOT NULL,
    hour TEXT NOT NULL,
    id_teacher INTEGER NOT NULL,
    isBooked INTEGER NOT NULL DEFAULT 0,
    PRIMARY KEY (date, hour, id_teacher),
    FOREIGN KEY (id_teacher) REFERENCES Teachers(id_teacher) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Meetings (
    id_meeting INTEGER PRIMARY KEY AUTOINCREMENT,
    date DATE NOT NULL,
    hour TEXT NOT NULL,
    id_teacher INTEGER NOT NULL,
    id_parent INTEGER NOT NULL,
    FOREIGN KEY (date, hour, id_teacher) REFERENCES MeetingsAvailability(date, hour, id_teacher) ON DELETE CASCADE,
    FOREIGN KEY (id_parent) REFERENCES Parents(id_parent) ON DELETE CASCADE
);
