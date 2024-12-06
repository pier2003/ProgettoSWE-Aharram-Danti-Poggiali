package orm;

import java.util.Iterator;

import domainModel.Student;

public interface StudentDao {
	
	Iterator<Student> getAllStudents() throws StudentDaoException, DaoConnectionException;
	
	Student getStudentById(int id) throws StudentDaoException, DaoConnectionException;

	Iterator<Student> getStudentsByClass(String schoolClass_name) throws StudentDaoException, DaoConnectionException;

}
