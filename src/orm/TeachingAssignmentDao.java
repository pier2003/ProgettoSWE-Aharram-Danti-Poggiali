package orm;

import java.util.Iterator;

import domainModel.TeachingAssignment;

public interface TeachingAssignmentDao {

	Iterator<TeachingAssignment> getAllStudentTeachings(int id) throws TeachingAssignmentDaoException, DaoConnectionException;
	
	TeachingAssignment getTeachingById(int id) throws TeachingAssignmentDaoException, DaoConnectionException;


}
