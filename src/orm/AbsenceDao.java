package orm;

import java.time.LocalDate;
import java.util.Iterator;

import domainModel.Absence;
import domainModel.SchoolClass;
import domainModel.Student;

public interface AbsenceDao {
	
	void addAbsence(Student student, LocalDate date) throws AbsenceDaoException;

	void deleteAbsence(Student student, LocalDate date) throws AbsenceDaoException;

	Iterator<Absence> getAbsencesByClassInDate(SchoolClass schoolClass, LocalDate date) throws AbsenceDaoException;

	Iterator<Absence> getAbsencesByStudent(Student student) throws AbsenceDaoException;

	boolean checkStudentAttendanceInDay(Student student, LocalDate date) throws AbsenceDaoException;

	void justifyAbsence(Absence absence) throws AbsenceDaoException;

}