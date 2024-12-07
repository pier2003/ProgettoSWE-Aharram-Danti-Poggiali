package businessLogic;

import static org.easymock.EasyMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.Before;
import org.junit.Test;

import businessLogic.TeacherController.IllegalHomeworkAccessException;
import businessLogic.TeacherController.IllegalLessonAccessException;
import daoFactory.DaoFactory;
import domainModel.Lesson;
import domainModel.SchoolClass;
import domainModel.Teacher;
import domainModel.TeachingAssignment;
import orm.DaoConnectionException;
import orm.LessonDao;
import orm.LessonDaoException;
import orm.StudentDaoException;
import orm.TeacherDaoException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;

public class TeacherControllerTest_Lesson {

	private DaoFactory factoryMock;
	private Teacher teacher;
	private TeacherController teacherController;
	private LessonDao lessonDaoMock;

	private TeachingAssignment teachingAssignment;
	private SchoolClass schoolClass;
	private LocalDate date;
	private LocalTime startHour;
	private LocalTime endHour;
	private ArrayList<Lesson> lessons;
	private Lesson lesson1;
	private Lesson lesson2;

	@Before
	public void setup() throws StudentDaoException, TeacherDaoException, DaoConnectionException {
		factoryMock = createMock(DaoFactory.class);
		lessonDaoMock = createMock(LessonDao.class);

		teacher = new Teacher(1, "Mario", "Rossi");

		expect(factoryMock.createLessonDao()).andReturn(lessonDaoMock).anyTimes();

		teacherController = new TeacherController(teacher, factoryMock);

		date = LocalDate.now();
		startHour = LocalTime.of(9, 0);
		endHour = LocalTime.of(11, 0);

		schoolClass = new SchoolClass("1A");
		teachingAssignment = new TeachingAssignment(1, "matematica", teacher, schoolClass);
	}

	@Test
	public void testAddNewLesson()
			throws LessonDaoException, DaoConnectionException, StudentDaoException, TeacherDaoException {
		String description = "Math Lesson";

		lessonDaoMock.addLesson(teachingAssignment, date, description, startHour, endHour);

		replay(factoryMock, lessonDaoMock);

		teacherController.addNewLesson(teachingAssignment, date, description, startHour, endHour);

		verify(factoryMock, lessonDaoMock);
	}

	@Test
	public void testEditLessonDescription()
			throws LessonDaoException, DaoConnectionException, IllegalLessonAccessException {
		Lesson lesson = new Lesson(1, teachingAssignment, date, "Old Description", endHour, startHour);
		String newDescription = "Updated Lesson Description";

		lessonDaoMock.editLessonDescription(lesson, newDescription);

		replay(factoryMock, lessonDaoMock);

		teacherController.editLessonDescription(lesson, newDescription);

		verify(factoryMock, lessonDaoMock);
	}

	@Test
	public void testEditLessonDescription_WithAnotherTeacher()
			throws LessonDaoException, DaoConnectionException, IllegalLessonAccessException {
		Teacher otherTeacher = new Teacher(-1, "Other", "Teacher");
		TeachingAssignment otherTeachingAssignment = new TeachingAssignment(0, "History", otherTeacher, schoolClass);
		Lesson lesson = new Lesson(1, otherTeachingAssignment, date, "Old Description", endHour, startHour);
		String newDescription = "Updated Lesson Description";

		replay(factoryMock);

		assertThatThrownBy(() -> teacherController.editLessonDescription(lesson, newDescription))
				.isInstanceOf(IllegalLessonAccessException.class);

		verify(factoryMock);
	}

	@Test
	public void testEditLessonDateTime()
			throws LessonDaoException, DaoConnectionException, IllegalLessonAccessException {
		Lesson lesson = new Lesson(1, teachingAssignment, date, "Old Description", endHour, startHour);
		LocalDate newDate = date.plusDays(1);
		LocalTime newStartHour = startHour.plusHours(1);
		LocalTime newEndHour = endHour.plusHours(1);

		lessonDaoMock.editLessonDateTime(lesson, newDate, newStartHour, newEndHour);

		replay(factoryMock, lessonDaoMock);

		teacherController.editLessonDateTime(lesson, newDate, newStartHour, newEndHour);

		verify(factoryMock, lessonDaoMock);
	}

	@Test
	public void testEditLessonDateTime_WithAnotherTeacher()
			throws LessonDaoException, DaoConnectionException, IllegalLessonAccessException {
		Teacher otherTeacher = new Teacher(-1, "Other", "Teacher");
		TeachingAssignment otherTeachingAssignment = new TeachingAssignment(0, "History", otherTeacher, schoolClass);
		Lesson lesson = new Lesson(1, otherTeachingAssignment, date, "Old Description", endHour, startHour);
		LocalDate newDate = date.plusDays(1);
		LocalTime newStartHour = startHour.plusHours(1);
		LocalTime newEndHour = endHour.plusHours(1);

		replay(factoryMock);

		assertThatThrownBy(() -> teacherController.editLessonDateTime(lesson, newDate, newStartHour, newEndHour))
				.isInstanceOf(IllegalLessonAccessException.class);

		verify(factoryMock);
	}

	@Test
	public void testGetClassLessonsInDay() throws LessonDaoException, DaoConnectionException {
		lessons = new ArrayList<>();
		lesson1 = new Lesson(1, teachingAssignment, date, "esercizi", startHour, endHour);
		lesson2 = new Lesson(2, teachingAssignment, date, "esercizi", startHour, endHour);
		lessons.add(lesson1);
		lessons.add(lesson2);
		Iterator<Lesson> lessonsIterator = lessons.iterator();

		expect(lessonDaoMock.getLessonsInDay(date, schoolClass)).andReturn(lessonsIterator).once();

		replay(factoryMock, lessonDaoMock);

		assertThat(teacherController.getClassLessonsInDay(date, schoolClass)).toIterable()
				.containsExactlyInAnyOrder(lesson1, lesson2);

		verify(factoryMock, lessonDaoMock);
	}

	@Test
	public void testDeleteLesson() throws IllegalHomeworkAccessException {
		Lesson lesson = new Lesson(1, teachingAssignment, date, "Description", endHour, startHour);

		lessonDaoMock.deleteLesson(lesson);
		replay(factoryMock, lessonDaoMock);

		teacherController.deleteLesson(lesson);

		verify(factoryMock, lessonDaoMock);

	}

	@Test
	public void testDeleteLesson_WithAnotherTeacher()
	        throws IllegalHomeworkAccessException {
	    Teacher otherTeacher = new Teacher(-1, "Other", "Teacher");
	    TeachingAssignment otherTeachingAssignment = new TeachingAssignment(0, "History", otherTeacher, schoolClass);
	    Lesson lesson = new Lesson(1, otherTeachingAssignment, date, "Description", endHour, startHour);

	    replay(factoryMock);

	    assertThatThrownBy(() -> teacherController.deleteLesson(lesson))
	            .isInstanceOf(IllegalHomeworkAccessException.class);

	    verify(factoryMock);
	}

}