package orm;

import java.time.LocalDate;

import java.util.Iterator;

import domainModel.Grade;
import domainModel.Student;
import domainModel.TeachingAssignment;

public interface GradeDao {

	default void addNewGrade(double value, String description, TeachingAssignment teaching, Student student, LocalDate date) throws GradeDaoException, StudentDaoException {
		addNewGradeWithWeight(value, 1, description, teaching, student, date);
	}
	
	void addNewGradeWithWeight(double value, int weight, String description, TeachingAssignment teaching, Student student, LocalDate date) throws GradeDaoException, StudentDaoException;
	
	void deleteGrade(Grade grade) throws GradeDaoException;
	
	void editGradeValue(Grade grade, double value) throws GradeDaoException;
	
	void editGradeWeight(Grade oldGrade, int weigth) throws GradeDaoException;
	
	void editGradeDescription(Grade oldGrade, String description) throws GradeDaoException;
	
	Iterator<Grade> getAllStudentGrades(Student student) throws GradeDaoException, StudentDaoException;

	Iterator<Grade> getStudentGradesByTeaching(Student student, TeachingAssignment teaching) throws GradeDaoException, StudentDaoException, TeachingAssignmentDaoException;

}
