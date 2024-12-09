package orm;

import java.time.LocalDate;

import java.time.LocalTime;
import java.util.Iterator;

import domainModel.Lesson;
import domainModel.SchoolClass;
import domainModel.TeachingAssignment;

public interface LessonDao {
	
	void addLesson(TeachingAssignment teaching, LocalDate date, String description, LocalTime startHour, LocalTime endHour) throws LessonDaoException, DaoConnectionException;

	void editLessonDescription(Lesson lesson, String description) throws LessonDaoException, DaoConnectionException;

	void editLessonDateTime(Lesson lesson, LocalDate date, LocalTime startHour, LocalTime endHour) throws LessonDaoException, DaoConnectionException;

	Iterator<Lesson> getLessonsInDay(LocalDate date, SchoolClass schoolClass) throws DaoConnectionException, LessonDaoException, SchoolClassDaoException;

	void deleteLesson(Lesson lesson) throws LessonDaoException;

}
