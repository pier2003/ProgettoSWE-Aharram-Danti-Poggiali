package orm;

import java.util.Iterator;

import domainModel.Student;
import domainModel.Teacher;
import domainModel.TeachingAssignment;

public interface TeachingAssignmentDao {

	Iterator<TeachingAssignment> getAllStudentTeachings(Student student) throws TeachingAssignmentDaoException, DaoConnectionException;
	
	Iterator<TeachingAssignment> getAllTeacherTeachings(Teacher teacher);


}
