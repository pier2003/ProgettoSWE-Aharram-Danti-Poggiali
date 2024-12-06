package orm;

import java.time.LocalDate;

import java.time.LocalTime;
import java.util.Iterator;

import domainModel.Lesson;
import domainModel.TeachingAssignment;

public interface LessonDao {
	
	Lesson getLessonById(int id) throws LessonDaoException, DaoConnectionException;

	void addLesson(TeachingAssignment teaching, LocalDate date, String description, LocalTime startHour, LocalTime endHour) throws LessonDaoException, DaoConnectionException;

	void editLessonDescription(int idLesson, String description) throws LessonDaoException, DaoConnectionException;

	void editLessonDateTime(int idLesson, LocalDate date, LocalTime startHour, LocalTime endHour) throws LessonDaoException, DaoConnectionException;

	Iterator<Lesson> getLessonsInDay(LocalDate date, String schoolClassName) throws DaoConnectionException, LessonDaoException;

}
