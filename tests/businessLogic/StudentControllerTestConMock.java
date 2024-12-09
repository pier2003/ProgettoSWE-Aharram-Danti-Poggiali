package businessLogic;

import static org.assertj.core.api.Assertions.assertThat; 
import static org.easymock.EasyMock.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import daoFactory.DaoFactory;
import domainModel.Absence;
import domainModel.DisciplinaryReport;
import domainModel.Grade;
import domainModel.Homework;
import domainModel.Lesson;
import domainModel.SchoolClass;
import domainModel.Student;
import domainModel.TeachingAssignment;
import orm.AbsenceDao;
import orm.AbsenceDaoException;
import orm.DaoConnectionException;
import orm.DisciplinaryReportDao;
import orm.DisciplinaryReportException;
import orm.GradeDao;
import orm.GradeDaoException;
import orm.HomeworkDao;
import orm.HomeworkDaoException;
import orm.LessonDao;
import orm.LessonDaoException;
import orm.SchoolClassDaoException;
import orm.StudentDao;
import orm.StudentDaoException;
import orm.TeachingAssignmentDao;
import orm.TeachingAssignmentDaoException;

public class StudentControllerTestConMock {

	private DaoFactory factoryMock;
	private StudentDao studentDaoMock;
	private StudentController studentController;
	private Student student;
	private SchoolClass schoolClass;
	private TeachingAssignmentDao teachingAssignmentDaoMock;
	private AbsenceDao AbsenceDaoMock;
	private int studentId;
	private GradeDao gradeDaoMock;
	private DisciplinaryReportDao disciplinaryReportDaoMock;
	private HomeworkDao homeworkDaoMock;
	private LessonDao lessonDaoMock;
	private TeachingAssignment teaching1;
	private TeachingAssignment teachingAssignment;
	private Grade grade1;
	private Grade grade2;
	private DisciplinaryReport disciplinaryReport1;
	private DisciplinaryReport disciplinaryReport2;


	@Before 
	public void setup() throws DaoConnectionException, StudentDaoException, SchoolClassDaoException {
		factoryMock = createMock(DaoFactory.class);
		studentDaoMock = createMock(StudentDao.class);
		teachingAssignmentDaoMock = createMock(TeachingAssignmentDao.class);
		gradeDaoMock = createMock(GradeDao.class);
		disciplinaryReportDaoMock = createMock(DisciplinaryReportDao.class);
		homeworkDaoMock = createMock(HomeworkDao.class);
		lessonDaoMock = createMock(LessonDao.class);
		AbsenceDaoMock = createMock(AbsenceDao.class);
		
		studentId = 1;
		schoolClass = new SchoolClass("1A");
		student = new Student(studentId, "Mario", "Rossi", schoolClass);
		
		expect(factoryMock.createStudentDao()).andReturn(studentDaoMock).anyTimes();
		expect(factoryMock.createTeachingAssignmentDao()).andReturn(teachingAssignmentDaoMock).anyTimes();
		expect(factoryMock.createGradeDao()).andReturn(gradeDaoMock).anyTimes();
		expect(factoryMock.createDisciplinaryReportDao()).andReturn(disciplinaryReportDaoMock).anyTimes();
		expect(factoryMock.createHomeworkDao()).andReturn(homeworkDaoMock).anyTimes();
		expect(factoryMock.createLessonDao()).andReturn(lessonDaoMock).anyTimes();
		expect(factoryMock.createAbsenceDao()).andReturn(AbsenceDaoMock).anyTimes();
		
		studentController = new StudentController(student, factoryMock);
		
		grade1 = new Grade(1, student, teachingAssignment, null, 8, 1, null);
		grade2 = new Grade(2, student, teachingAssignment, null, 4, 1, null);
		disciplinaryReport1 = new DisciplinaryReport(student.getId(),student, null, LocalDate.of(2023, 11, 21), "");
		disciplinaryReport2 = new DisciplinaryReport(student.getId(), student, null, LocalDate.of(2023, 11, 21), "");

	}
	
	@Test
	public void testGetStudent() throws StudentDaoException, SchoolClassDaoException, DaoConnectionException {
		replay(factoryMock);
		
		assertThat(studentController.getStudent()).isEqualTo(student);
	
		verify(factoryMock);
	}
	
	@Test
	public void testGetTeaching() throws TeachingAssignmentDaoException, DaoConnectionException, StudentDaoException, SchoolClassDaoException {
		ArrayList<TeachingAssignment> teachings = new ArrayList<TeachingAssignment>();
		
		teaching1 = new TeachingAssignment(1, null, null, schoolClass);
		teachings.add(teaching1);
		TeachingAssignment teaching2 = new TeachingAssignment(2, null, null, schoolClass);
		teachings.add(teaching2);
		Iterator<TeachingAssignment> teachingsIterator = teachings.iterator();
		
		expect(teachingAssignmentDaoMock.getAllStudentTeachings(student)).andReturn(teachingsIterator).once();
		
		replay(factoryMock, teachingAssignmentDaoMock);
		
		assertThat(studentController.getTeachings()).toIterable().containsExactlyInAnyOrder(teaching1, teaching2);
		
		verify(factoryMock, teachingAssignmentDaoMock);
	}
	
	@Test
	public void testGetGradesByTeaching() throws StudentDaoException, SchoolClassDaoException, DaoConnectionException, GradeDaoException, TeachingAssignmentDaoException {
		TeachingAssignment teachingAssignment = new TeachingAssignment(1, "matematica", null, schoolClass);
		ArrayList<Grade> grades = new ArrayList<Grade>();
		grades.add(grade1);
		grades.add(grade2);
		Iterator<Grade> gradesIterator = grades.iterator();
		
		expect(gradeDaoMock.getStudentGradesByTeaching(student, teachingAssignment)).andReturn(gradesIterator).once();
		
		replay(factoryMock, gradeDaoMock);
		
		assertThat(studentController.getGradesByTeaching(teachingAssignment)).toIterable().containsExactlyInAnyOrder(grade1, grade2);
		
		verify(factoryMock, gradeDaoMock);
	}
	
	@Test
	public void testGetAllStudentGrades() throws GradeDaoException, DaoConnectionException, StudentDaoException, SchoolClassDaoException {
		ArrayList<Grade> grades = new ArrayList<Grade>();
		grades.add(grade1);
		grades.add(grade2);
		Iterator<Grade> gradesIterator = grades.iterator();
		
		expect(gradeDaoMock.getAllStudentGrades(student)).andReturn(gradesIterator).once();
		
		replay(factoryMock, gradeDaoMock);

		assertThat(studentController.getAllStudentGrades()).toIterable().containsExactlyInAnyOrder(grade1,grade2);

		verify(factoryMock, gradeDaoMock);
	}
	
	@Test
	public void testGetDisciplinaryReports() throws StudentDaoException, SchoolClassDaoException, DaoConnectionException, DisciplinaryReportException {
		ArrayList<DisciplinaryReport> reports = new ArrayList<DisciplinaryReport>();
		reports.add(disciplinaryReport1);
		reports.add(disciplinaryReport2);
		Iterator<DisciplinaryReport> reportsIterator = reports.iterator();
		
		expect(disciplinaryReportDaoMock.getDisciplinaryReportsByStudent(student)).andReturn(reportsIterator).once();
		
		replay(factoryMock, disciplinaryReportDaoMock);
		
		assertThat(studentController.getDisciplinaryReports()).toIterable().containsExactlyInAnyOrder(disciplinaryReport1, disciplinaryReport2);
		
		verify(factoryMock, disciplinaryReportDaoMock);
	}
	
	@Test
	public void testGetHomeworksBySubmissionDate() throws StudentDaoException, SchoolClassDaoException, DaoConnectionException, HomeworkDaoException {
		LocalDate date = LocalDate.now();
		ArrayList<Homework> homeworks = new ArrayList<Homework>();
		//int id, TeachingAssignment teaching, LocalDate date, String description, LocalDate submissionDate
		Homework homework1 = new Homework(1, teaching1, date ,"aDescription1", LocalDate.of(2024, 12, 2));
		Homework homework2 = new Homework(2, teaching1, date ,"aDescription2", LocalDate.of(2024, 10, 2));

		homeworks.add(homework1);
		homeworks.add(homework2);
		Iterator<Homework> homeworksIterator = homeworks.iterator();
		
		expect(homeworkDaoMock.getHomeworksBySubmissionDate(date, schoolClass)).andReturn(homeworksIterator).once();
		
		replay(factoryMock, homeworkDaoMock);
		
		assertThat(studentController.getHomeworksBySubmissionDate(date)).toIterable().containsExactlyInAnyOrder(homework1, homework2);
		
		verify(factoryMock, homeworkDaoMock);
	}
	
	@Test
	public void testGetLessonInDate() throws StudentDaoException, SchoolClassDaoException, DaoConnectionException, LessonDaoException {
		LocalDate date = LocalDate.now();
		ArrayList<Lesson> lessons = new ArrayList<Lesson>();
		Lesson lesson1 = new Lesson(1, teaching1, date ,"aDescription1", LocalTime.of(10, 20), LocalTime.of(11, 20));
		Lesson lesson2 = new Lesson(1, teaching1, date ,"aDescription1", LocalTime.of(11, 20), LocalTime.of(12, 20));
		lessons.add(lesson1);
		lessons.add(lesson2);
		Iterator<Lesson> lessonsIterator = lessons.iterator();
		
		expect(lessonDaoMock.getLessonsInDay(date, schoolClass)).andReturn(lessonsIterator).once();
		
		replay(factoryMock, lessonDaoMock);
		
		assertThat(studentController.getLessonInDate(date)).toIterable().containsExactlyInAnyOrder(lesson1,lesson2);
		
		verify(factoryMock, lessonDaoMock);
	}

	@Test
	public void testGetAllStudentAbsences() throws AbsenceDaoException, DaoConnectionException, StudentDaoException {
		ArrayList<Absence> absences = new ArrayList<Absence>();
		Absence absence1 = new Absence(student, LocalDate.of(2024, 06, 20), false);
		Absence absence2 = new Absence(student, LocalDate.of(2024, 05, 20),  false);
		absences.add(absence1);
		absences.add(absence2);
		Iterator<Absence> absencesIterator = absences.iterator();
		
		expect(AbsenceDaoMock.getAbsencesByStudent(student)).andReturn(absencesIterator).once();
		
		replay(factoryMock, AbsenceDaoMock);
		
		assertThat(studentController.getAllStudentAbsences()).toIterable().containsExactlyInAnyOrder(absence1, absence2);
		
		verify(factoryMock, AbsenceDaoMock);
	}
	
	@Test
	public void testCheckStudentAttendanceInDay() throws AbsenceDaoException, DaoConnectionException, StudentDaoException {
		expect(AbsenceDaoMock.checkStudentAttendanceInDay(student, LocalDate.of(2024, 12, 10))).andReturn(true).once();
		
		replay(factoryMock, AbsenceDaoMock);
		
		assertThat(studentController.checkStudentAttendanceInDay(LocalDate.of(2024, 12, 10))).isEqualTo(true);
		
		verify(factoryMock, AbsenceDaoMock);
	}
	
}