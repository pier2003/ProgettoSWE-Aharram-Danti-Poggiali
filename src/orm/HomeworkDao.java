package orm;


import java.time.LocalDate;

import java.util.Iterator;
import domainModel.Homework;
import domainModel.TeachingAssignment;

public interface HomeworkDao {
	
	void addHomework(TeachingAssignment teaching, LocalDate date, String description, LocalDate subissionDate) throws HomeworkDaoException;

	void editHomeworkDescription(int id, String description) throws HomeworkDaoException;
	
	void editHomeworkSubmissionDate(int id, LocalDate submissionDate) throws HomeworkDaoException;

	Iterator<Homework> getHomeworksInDay(LocalDate date, String className) throws DaoConnectionException, HomeworkDaoException;

	Homework getHomeworkById(int id) throws HomeworkDaoException, DaoConnectionException;

}
