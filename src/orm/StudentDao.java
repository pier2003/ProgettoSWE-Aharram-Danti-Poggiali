package orm;

import java.util.Iterator;

import domainModel.SchoolClass;
import domainModel.Student;

public interface StudentDao {
	
	Iterator<Student> getAllStudents() throws StudentDaoException, DaoConnectionException;
	
	Student getStudentById(int id) throws StudentDaoException, DaoConnectionException;

	Iterator<Student> getStudentsByClass(SchoolClass schoolClass) throws StudentDaoException, DaoConnectionException;

}
