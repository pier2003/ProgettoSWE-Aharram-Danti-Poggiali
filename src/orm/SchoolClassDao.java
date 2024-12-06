package orm;

import java.util.Iterator;

import domainModel.SchoolClass;
import domainModel.Student;
import domainModel.Teacher;

public interface SchoolClassDao {
	
	SchoolClass getSchoolClassByName(String name) throws SchoolClassDaoException, DaoConnectionException;
	
	Iterator<SchoolClass> getAllSchoolClassesByTeacher(Teacher teacher) throws SchoolClassDaoException, DaoConnectionException;

	SchoolClass getSchoolClassByStudent(Student student) throws SchoolClassDaoException, DaoConnectionException;

}
