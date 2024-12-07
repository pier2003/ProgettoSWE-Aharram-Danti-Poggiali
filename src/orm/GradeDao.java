package orm;

import java.time.LocalDate;

import java.util.Iterator;

import domainModel.Grade;
import domainModel.Student;
import domainModel.TeachingAssignment;

public interface GradeDao {

	default void addNewGrade(double value, String description, TeachingAssignment teaching, Student student, LocalDate date) throws GradeDaoException, DaoConnectionException {
		addNewGradeWithWeight(value, 1, description, teaching, student, date);
	}
	
	void addNewGradeWithWeight(double value, int weight, String description, TeachingAssignment teaching, Student student, LocalDate date) throws GradeDaoException, DaoConnectionException;
	
	void deleteGrade(Grade grade) throws GradeDaoException, DaoConnectionException;
	
	void editGradeValue(Grade grade, double value) throws GradeDaoException, DaoConnectionException;
	
	void editGradeWeight(Grade oldGrade, int weigth) throws GradeDaoException, DaoConnectionException;
	
	void editGradeDescription(Grade oldGrade, String description) throws GradeDaoException, DaoConnectionException;
	
	Grade getGradeById(int id_grade) throws GradeDaoException, DaoConnectionException;
	
	Iterator<Grade> getAllStudentGrades(Student student) throws GradeDaoException, DaoConnectionException;

	Iterator<Grade> getStudentGradesByTeaching(Student student, TeachingAssignment teaching) throws GradeDaoException, DaoConnectionException;

}
