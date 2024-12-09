package orm;

import java.util.Iterator;

import domainModel.SchoolClass;
import domainModel.Student;

public interface StudentDao {
		
	Iterator<Student> getStudentsByClass(SchoolClass schoolClass) throws StudentDaoException, DaoConnectionException, SchoolClassDaoException;

}
