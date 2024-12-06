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
	
	void deleteGrade(int id_grade) throws GradeDaoException, DaoConnectionException;
	
	void editGradeValue(int id_grade, double value) throws GradeDaoException, DaoConnectionException;
	
	void editGradeWeight(int id_grade, int weigth) throws GradeDaoException, DaoConnectionException;
	
	void editGradeDescription(int id_grade, String description) throws GradeDaoException, DaoConnectionException;
	
	Grade getGradeById(int id_grade) throws GradeDaoException, DaoConnectionException;
	
	Iterator<Grade> getAllStudentGrades(int id_student) throws GradeDaoException, DaoConnectionException;

	Iterator<Grade> getStudentGradesByTeaching(int id_student, int id_teaching) throws GradeDaoException, DaoConnectionException;

}
