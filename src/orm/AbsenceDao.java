package orm;

import java.time.LocalDate;
import java.util.Iterator;

import domainModel.Absence;
import domainModel.SchoolClass;
import domainModel.Student;

public interface AbsenceDao {
	
	Absence getAbsenceByStudentInDate(Student studente, LocalDate date) throws AbsenceDaoException, DaoConnectionException;

	void addAbsence(Student student, LocalDate date) throws AbsenceDaoException, DaoConnectionException;

	void deleteAbsence(Student student, LocalDate date) throws AbsenceDaoException, DaoConnectionException;

	Iterator<Absence> getAbsencesByClassInDate(SchoolClass schoolClass, LocalDate date) throws AbsenceDaoException, DaoConnectionException;

	Iterator<Absence> getAbsencesByStudent(Student student) throws AbsenceDaoException, DaoConnectionException;

}