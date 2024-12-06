package orm;

import java.util.Iterator;

import domainModel.SchoolClass;
import domainModel.Student;
import domainModel.Teacher;
import domainModel.TeachingAssignment;

public interface SchoolClassDao {
	
	SchoolClass getSchoolClassByName(String name) throws SchoolClassDaoException, DaoConnectionException;
	
	Iterator<SchoolClass> getAllSchoolClassesByTeaching(TeachingAssignment teachingAssignment) throws SchoolClassDaoException, DaoConnectionException;

	SchoolClass getSchoolClassByStudent(Student student) throws SchoolClassDaoException, DaoConnectionException;

}
