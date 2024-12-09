package businessLogic;

import static org.easymock.EasyMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.Before;
import org.junit.Test;

import businessLogic.TeacherController.IllegalHomeworkAccessException;
import daoFactory.DaoFactory;
import domainModel.Homework;
import domainModel.SchoolClass;
import domainModel.Teacher;
import domainModel.TeachingAssignment;
import orm.DaoConnectionException;
import orm.HomeworkDao;
import orm.HomeworkDaoException;
import orm.SchoolClassDaoException;
import orm.StudentDaoException;
import orm.TeacherDaoException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;

public class TeacherControllerTest_Homework {

	private DaoFactory factoryMock;
	private Teacher teacher;
	private TeacherController teacherController;
	private HomeworkDao homeworkDaoMock;
	private TeachingAssignment teachingAssignment;
	private SchoolClass schoolClass;
	private LocalDate date;
	private Homework homework1;
	private Homework homework2;
	private ArrayList<Homework> homeworks;

	@Before
	public void setup() throws StudentDaoException, TeacherDaoException, DaoConnectionException {
		factoryMock = createMock(DaoFactory.class);
		homeworkDaoMock = createMock(HomeworkDao.class);

		teacher = new Teacher(1, "Mario", "Rossi");

		expect(factoryMock.createHomeworkDao()).andReturn(homeworkDaoMock).anyTimes();
		
		teacherController = new TeacherController(teacher, factoryMock);
				
		date = LocalDate.now();
		schoolClass = new SchoolClass("1A");
		teachingAssignment = new TeachingAssignment(1, "matematica", teacher, schoolClass);
	}

	@Test
	public void testAssignNewHomework()
			throws HomeworkDaoException, StudentDaoException, TeacherDaoException, DaoConnectionException {
		String description = "Math Homework";
		LocalDate submissionDate = LocalDate.now().plusDays(7);

		homeworkDaoMock.addHomework(teachingAssignment, date, description, submissionDate);

		replay(factoryMock, homeworkDaoMock);

		teacherController.assignNewHomework(teachingAssignment, date, description, submissionDate);

		verify(factoryMock, homeworkDaoMock);
	}

	@Test
	public void tesEditHomeworkDescription() throws HomeworkDaoException, StudentDaoException, TeacherDaoException,
			DaoConnectionException, IllegalHomeworkAccessException {
		Homework oldHomework = new Homework(1, teachingAssignment, date, "Old descriptoin", date);
		String newDescription = "Updated Math Homework";

		homeworkDaoMock.editHomeworkDescription(oldHomework, newDescription);

		replay(factoryMock, homeworkDaoMock);

		teacherController.editHomeworkDescription(oldHomework, newDescription);

		verify(factoryMock, homeworkDaoMock);
	}

	@Test
	public void tesEditHomeworkDescription_WithAnotherTeacher() throws HomeworkDaoException, StudentDaoException,
			TeacherDaoException, DaoConnectionException, IllegalHomeworkAccessException {
		Teacher otherTeacher = new Teacher(-1, "", "");
		TeachingAssignment otherTeachingAssignment = new TeachingAssignment(0, "italiano", otherTeacher, schoolClass);
		Homework oldHomework = new Homework(1, otherTeachingAssignment, date, "Old descriptoin", date);
		String newDescription = "Updated Math Homework";

		replay(factoryMock);

		assertThatThrownBy(() -> teacherController.editHomeworkDescription(oldHomework, newDescription))
				.isInstanceOf(IllegalHomeworkAccessException.class);

		verify(factoryMock);
	}

	@Test
	public void testEditHomeworkSubmissionDate() throws HomeworkDaoException, StudentDaoException, TeacherDaoException,
			DaoConnectionException, IllegalHomeworkAccessException {
		Homework oldHomework = new Homework(1, teachingAssignment, date, "Homework Description", date);
		LocalDate newSubmissionDate = LocalDate.now().plusDays(5);

		homeworkDaoMock.editHomeworkSubmissionDate(oldHomework, newSubmissionDate);

		replay(factoryMock, homeworkDaoMock);

		teacherController.editHomeworkSubmissionDate(oldHomework, newSubmissionDate);

		verify(factoryMock, homeworkDaoMock);
	}

	@Test
	public void testEditHomeworkSubmissionDate_WithAnotherTeacher() throws HomeworkDaoException, StudentDaoException,
			TeacherDaoException, DaoConnectionException, IllegalHomeworkAccessException {
		Teacher otherTeacher = new Teacher(-1, "Other", "Teacher");
		TeachingAssignment otherTeachingAssignment = new TeachingAssignment(0, "italiano", otherTeacher, schoolClass);
		Homework oldHomework = new Homework(1, otherTeachingAssignment, date, "Homework Description", date);
		LocalDate newSubmissionDate = LocalDate.now().plusDays(5);

		replay(factoryMock);

		assertThatThrownBy(() -> teacherController.editHomeworkSubmissionDate(oldHomework, newSubmissionDate))
				.isInstanceOf(IllegalHomeworkAccessException.class);

		verify(factoryMock);
	}

	@Test
	public void testGetClassHomeworksSubmissionDate()
			throws DaoConnectionException, HomeworkDaoException, StudentDaoException, TeacherDaoException, SchoolClassDaoException {
		homeworks = new ArrayList<>();
		homework1 = new Homework(0, teachingAssignment, date, "Fare esercizi", date);
		homework2 = new Homework(1, teachingAssignment, date, "Fare esercizi", date);
		homeworks.add(homework1);
		homeworks.add(homework2);
		LocalDate submissionDate = LocalDate.now();
		Iterator<Homework> homeworksIterator = homeworks.iterator();

		expect(homeworkDaoMock.getHomeworksBySubmissionDate(submissionDate, schoolClass)).andReturn(homeworksIterator)
				.once();

		replay(factoryMock, homeworkDaoMock);

		assertThat(teacherController.getClassHomeworksSubmissionDate(submissionDate, schoolClass)).toIterable()
				.containsExactlyInAnyOrder(homework1, homework2);

		verify(factoryMock, homeworkDaoMock);
	}

	@Test
	public void testDeleteHomework() throws HomeworkDaoException, StudentDaoException, TeacherDaoException,
			DaoConnectionException, IllegalHomeworkAccessException {
		Homework homework = new Homework(1, teachingAssignment, date, "Homework Description", date);

		homeworkDaoMock.deleteHomework(homework);

		replay(factoryMock, homeworkDaoMock);

		teacherController.deleteHomework(homework);

		verify(factoryMock, homeworkDaoMock);
	}

	@Test
	public void testDeleteHomework_WithAnotherTeacher() throws HomeworkDaoException, StudentDaoException,
			TeacherDaoException, DaoConnectionException, IllegalHomeworkAccessException {
		Teacher otherTeacher = new Teacher(-1, "Other", "Teacher");
		TeachingAssignment otherTeachingAssignment = new TeachingAssignment(0, "italiano", otherTeacher, schoolClass);
		Homework homework = new Homework(1, otherTeachingAssignment, date, "Homework Description", date);

		replay(factoryMock);

		assertThatThrownBy(() -> teacherController.deleteHomework(homework))
				.isInstanceOf(IllegalHomeworkAccessException.class);

		verify(factoryMock);
	}

}
