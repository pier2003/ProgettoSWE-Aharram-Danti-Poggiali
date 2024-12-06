package businessLogic;

import static org.easymock.EasyMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.Before;
import org.junit.Test;

import daoFactory.DaoFactory;
import decorator.Component;
import domainModel.Absence;
import domainModel.SchoolClass;
import domainModel.Student;
import domainModel.Teacher;
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
import orm.SchoolClassDao;
import orm.SchoolClassDaoException;
import orm.StudentDao;
import orm.StudentDaoException;
import orm.TeacherDao;
import orm.TeacherDaoException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;

public class TeacherControllerTestConMock {

	private DaoFactory factoryMock;
	private TeacherDao teacherDaoMock;
	private StudentDao studentDaoMock;
	private Teacher teacher;
	private SchoolClassDao schoolClassDaoMock;
	private TeacherController teacherController;
	private GradeDao gradeDaoMock;
	private DisciplinaryReportDao disciplinaryReportMock;
	private AbsenceDao absenceDaoMock;
	private HomeworkDao homeworkDaoMock;
	private LessonDao lessonDaoMock;

	@Before
	public void setup() throws StudentDaoException, TeacherDaoException, DaoConnectionException {
		factoryMock = createMock(DaoFactory.class);
		teacherDaoMock = createMock(TeacherDao.class);
		studentDaoMock = createMock(StudentDao.class);
		schoolClassDaoMock = createMock(SchoolClassDao.class);
		gradeDaoMock = createMock(GradeDao.class);
		disciplinaryReportMock = createMock(DisciplinaryReportDao.class);
		absenceDaoMock = createMock(AbsenceDao.class);
		homeworkDaoMock = createMock(HomeworkDao.class);
		lessonDaoMock = createMock(LessonDao.class);

		teacher = new Teacher("Mario", "Rossi", 1);

		expect(teacherDaoMock.getTeacherById(1)).andReturn(teacher).anyTimes();
		expect(factoryMock.createSchoolClassDao()).andReturn(schoolClassDaoMock).anyTimes();
		expect(factoryMock.createStudentDao()).andReturn(studentDaoMock).anyTimes();
		expect(factoryMock.createGradeDao()).andReturn(gradeDaoMock).anyTimes();
		expect(factoryMock.createDisciplinaryReportDao()).andReturn(disciplinaryReportMock).anyTimes();
		expect(factoryMock.creatTeacherDao()).andReturn(teacherDaoMock).anyTimes();
		expect(factoryMock.createAbsenceDao()).andReturn(absenceDaoMock).anyTimes();
		expect(factoryMock.createHomeworkDao()).andReturn(homeworkDaoMock).anyTimes();
		expect(factoryMock.createLessonDao()).andReturn(lessonDaoMock).anyTimes();
	}

	private TeacherController createTeacherController() throws StudentDaoException, TeacherDaoException, DaoConnectionException {
		return new TeacherController(1, factoryMock);
	}
	
	@Test
	public void testViewTeacher() throws StudentDaoException, TeacherDaoException, DaoConnectionException {
		replay(factoryMock, teacherDaoMock);

		teacherController = createTeacherController();
		assertThat(teacherController.viewTeacher()).isEqualTo(teacher);

		verify(factoryMock, teacherDaoMock);
	}

	@Test
	public void testGetAllMyClass()
			throws SchoolClassDaoException, DaoConnectionException, StudentDaoException, TeacherDaoException {
		ArrayList<SchoolClass> classes = new ArrayList<SchoolClass>();
		classes.add(new SchoolClass("1A"));
		classes.add(new SchoolClass("2A"));
		Iterator<SchoolClass> classesIterator = classes.iterator();

		expect(schoolClassDaoMock.getAllSchoolClassesByTeacher(teacher)).andReturn(classesIterator).once();

		replay(factoryMock, teacherDaoMock, schoolClassDaoMock);

		teacherController = createTeacherController();
		assertThat(teacherController.getAllMyClasses()).isEqualTo(classesIterator);

		verify(factoryMock, teacherDaoMock, schoolClassDaoMock);
	}

	@Test
	public void testGetStudentByClass() throws DaoConnectionException, StudentDaoException, TeacherDaoException {
		SchoolClass schoolClass = new SchoolClass("1A");
		ArrayList<Student> students = new ArrayList<Student>();
		students.add(new Student("Fabio", "Rossi", 1, schoolClass));
		students.add(new Student("Gino", "Rossi", 2, schoolClass));
		Iterator<Student> studentsIterator = students.iterator();

		expect(studentDaoMock.getStudentsByClass(schoolClass.getClassName())).andReturn(studentsIterator).once();

		replay(factoryMock, teacherDaoMock, studentDaoMock);

		teacherController = createTeacherController();
		assertThat(teacherController.getStudentsByClass(schoolClass.getClassName())).isEqualTo(studentsIterator);

		verify(factoryMock, teacherDaoMock, studentDaoMock);
	}

	@Test
	public void testStudentById() throws StudentDaoException, DaoConnectionException, TeacherDaoException {
		Student student = new Student("Fabio", "Rossi", 1, null);

		expect(studentDaoMock.getStudentById(student.getId())).andReturn(student).once();

		replay(factoryMock, teacherDaoMock, studentDaoMock);

		teacherController = createTeacherController();
		assertThat(teacherController.getStudentById(student.getId())).isEqualTo(student);

		verify(factoryMock, teacherDaoMock, studentDaoMock);
	}

	@Test
	public void testAssignGradeToStudentInDate_ValidValue() throws Exception {
		double gradeValue = 8.5;
		String description = "Math Test";
		TeachingAssignment teaching = new TeachingAssignment(1, description, teacher, null);
		Student student = new Student("Fabio", "Rossi", 1, null);
		LocalDate date = LocalDate.now();

		gradeDaoMock.addNewGrade(gradeValue, description, teaching, student, date);

		replay(factoryMock, teacherDaoMock, gradeDaoMock);

		teacherController = createTeacherController();
		teacherController.assignGradeToStudentInDate(gradeValue, description, teaching, student, date);

		verify(factoryMock, teacherDaoMock, gradeDaoMock);
	}

	@Test
	public void testAssignGradeToStudentInDate_InvalidValue() throws Exception {
		double invalideGradeValue = -1;
		String description = "Math Test";
		TeachingAssignment teaching = new TeachingAssignment(1, description, teacher, null);
		Student student = new Student("Fabio", "Rossi", 1, null);
		LocalDate date = LocalDate.now();

		replay(factoryMock, teacherDaoMock, gradeDaoMock);

		teacherController = createTeacherController();
		assertThatThrownBy(() -> teacherController.assignGradeToStudentInDate(invalideGradeValue, description, teaching,
				student, date)).isInstanceOf(InvalidGradeValueException.class);

		verify(factoryMock, teacherDaoMock, gradeDaoMock);
	}

	@Test
	public void testAssignGradeToStudentInDateWithWeight_ValidValue() throws Exception {
		double gradeValue = 8.5;
		int weight = 2;
		String description = "Math Test";
		TeachingAssignment teaching = new TeachingAssignment(1, description, teacher, null);
		Student student = new Student("Fabio", "Rossi", 1, null);
		LocalDate date = LocalDate.now();

		gradeDaoMock.addNewGradeWithWeight(gradeValue, weight, description, teaching, student, date);
		expectLastCall().once();

		replay(factoryMock, teacherDaoMock, gradeDaoMock);

		teacherController = createTeacherController();
		teacherController.assignGradeToStudentInDateWithWeight(gradeValue, weight, description, teaching, student,
				date);

		verify(factoryMock, teacherDaoMock, gradeDaoMock);
	}

	@Test
	public void testAssignGradeToStudentInDateWithWeight_InvalidGradeValue() throws Exception {
		double invalidGradeValue = -1;
		int weight = 2;
		String description = "Math Test";
		TeachingAssignment teaching = new TeachingAssignment(1, description, teacher, null);
		Student student = new Student("Fabio", "Rossi", 1, null);
		LocalDate date = LocalDate.now();

		replay(factoryMock, teacherDaoMock);

		teacherController = createTeacherController();
		assertThatThrownBy(() -> teacherController.assignGradeToStudentInDateWithWeight(invalidGradeValue, weight,
				description, teaching, student, date)).isInstanceOf(InvalidGradeValueException.class);

		verify(factoryMock, teacherDaoMock);
	}

	@Test
	public void testAssignGradeToStudentInDateWithWeight_NegativeWeight() throws Exception {
		double gradeValue = 8.5;
		int negativeWeight = -2;
		String description = "Math Test";
		TeachingAssignment teaching = new TeachingAssignment(1, description, teacher, null);
		Student student = new Student("Fabio", "Rossi", 1, null);
		LocalDate date = LocalDate.now();

		replay(factoryMock, teacherDaoMock, gradeDaoMock);

		teacherController = createTeacherController();
		assertThatThrownBy(() -> teacherController.assignGradeToStudentInDateWithWeight(gradeValue, negativeWeight,
				description, teaching, student, date)).isInstanceOf(NegativeWeightException.class);

		verify(factoryMock, teacherDaoMock, gradeDaoMock);
	}

	@Test
	public void testEditGradeValueById()
			throws GradeDaoException, DaoConnectionException, StudentDaoException, TeacherDaoException {
		int id = 1;
		double value = 8;

		gradeDaoMock.editGradeValue(id, value);

		replay(factoryMock, teacherDaoMock, gradeDaoMock);

		teacherController = createTeacherController();
		teacherController.editGradeValueById(id, value);

		verify(factoryMock, teacherDaoMock, gradeDaoMock);
	}

	@Test
	public void testEditGradeWeightById()
			throws GradeDaoException, DaoConnectionException, StudentDaoException, TeacherDaoException {
		int id = 1;
		int weight = 10;

		gradeDaoMock.editGradeWeight(id, weight);

		replay(factoryMock, teacherDaoMock, gradeDaoMock);

		teacherController = createTeacherController();
		teacherController.editGradeWeightById(id, weight);

		verify(factoryMock, teacherDaoMock, gradeDaoMock);
	}

	@Test
	public void testEditGradeDescriptionById()
			throws GradeDaoException, DaoConnectionException, StudentDaoException, TeacherDaoException {
		int id = 1;
		String description = "Updated description";

		gradeDaoMock.editGradeDescription(id, description);

		replay(factoryMock, teacherDaoMock, gradeDaoMock);

		teacherController = createTeacherController();
		teacherController.editGradeDescriptionById(id, description);

		verify(factoryMock, teacherDaoMock, gradeDaoMock);
	}

	@Test
	public void testAssignDisciplinaryReportToStudentInDate()
			throws StudentDaoException, TeacherDaoException, DaoConnectionException, AbsenceDaoException, DisciplinaryReportException {
		Student student = new Student("Mario", "Rossi", 1, null);
		String description = "Arriva in ritardo.";
		LocalDate date = LocalDate.now();

		disciplinaryReportMock.addNewReport(teacher, student, description, date);

		replay(factoryMock, teacherDaoMock, disciplinaryReportMock);

		teacherController = createTeacherController();
		teacherController.assignDisciplinaryReportToStudentInDate(student, description, date);

		verify(factoryMock, teacherDaoMock, disciplinaryReportMock);
	}

	@Test
	public void testGetAbsencesByClassInDate()
			throws AbsenceDaoException, DaoConnectionException, StudentDaoException, TeacherDaoException {
		SchoolClass schoolClass = new SchoolClass("1A");
		LocalDate date = LocalDate.now();
		ArrayList<Absence> absences = new ArrayList<Absence>();
		absences.add(new Absence(null, date, false));
		absences.add(new Absence(null, date, false));
		Iterator<Absence> absencesIterator = absences.iterator();

		expect(absenceDaoMock.getAbsencesByClassInDate(schoolClass, date)).andReturn(absencesIterator).once();

		replay(factoryMock, teacherDaoMock, absenceDaoMock);

		teacherController = createTeacherController();
		assertThat(teacherController.getAbsencesByClassInDate(schoolClass, date)).isEqualTo(absencesIterator);

		verify(factoryMock, teacherDaoMock, absenceDaoMock);
	}

	@Test
	public void testGetNumberOfAbsencesByClassInDate()
			throws AbsenceDaoException, DaoConnectionException, StudentDaoException, TeacherDaoException {
		SchoolClass schoolClass = new SchoolClass("1A");
		LocalDate date = LocalDate.now();
		ArrayList<Absence> absences = new ArrayList<>();
		absences.add(new Absence(null, date, false));
		absences.add(new Absence(null, date, false));
		absences.add(new Absence(null, date, false));
		Iterator<Absence> absencesIterator = absences.iterator();

		expect(absenceDaoMock.getAbsencesByClassInDate(schoolClass, date)).andReturn(absencesIterator).once();

		replay(factoryMock, teacherDaoMock, absenceDaoMock);

		teacherController = createTeacherController();
		int numberOfAbsences = teacherController.getNumberOfAbsencesByClassInDate(schoolClass, date);

		assertThat(numberOfAbsences).isEqualTo(3);

		verify(factoryMock, teacherDaoMock, absenceDaoMock);
	}

	@Test
	public void testAssignAbsenceToStudentOnDate()
			throws AbsenceDaoException, DaoConnectionException, StudentDaoException, TeacherDaoException {
		Student student = new Student("Fabio", "Rossi", 1, null);
		LocalDate date = LocalDate.now();

		absenceDaoMock.addAbsence(student, date);
		replay(factoryMock, absenceDaoMock);

		teacherController = createTeacherController();
		teacherController.assignAbsenceToStudentOnDate(student, date);

		verify(factoryMock, absenceDaoMock);
	}

	@Test
	public void testDeleteAbsence()
			throws AbsenceDaoException, DaoConnectionException, StudentDaoException, TeacherDaoException {
		Student student = new Student("Fabio", "Rossi", 1, null);
		LocalDate date = LocalDate.now();

		absenceDaoMock.deleteAbsence(student, date);
		replay(factoryMock, absenceDaoMock);

		teacherController = createTeacherController();
		teacherController.deleteAbsence(student, date);

		verify(factoryMock, absenceDaoMock);
	}

	@Test
	public void testAssignNewHomework()
			throws HomeworkDaoException, StudentDaoException, TeacherDaoException, DaoConnectionException {
		TeachingAssignment teaching = new TeachingAssignment(1, "Math", teacher, null);
		LocalDate date = LocalDate.now();
		String description = "Math Homework";
		LocalDate submissionDate = LocalDate.now().plusDays(7);

		homeworkDaoMock.addHomework(teaching, date, description, submissionDate);
		replay(factoryMock, homeworkDaoMock);

		teacherController = createTeacherController();
		teacherController.assignNewHomework(teaching, date, description, submissionDate);

		verify(factoryMock, homeworkDaoMock);
	}

	@Test
	public void testEditHomework()
			throws HomeworkDaoException, StudentDaoException, TeacherDaoException, DaoConnectionException {
		TeachingAssignment teaching = new TeachingAssignment(1, "Math", teacher, null);
		LocalDate date = LocalDate.now();
		String description = "Updated Math Homework";
		LocalDate submissionDate = LocalDate.now().plusDays(7);

		homeworkDaoMock.editHomework(0, teaching, date, description, submissionDate);
		replay(factoryMock, homeworkDaoMock);

		teacherController = createTeacherController();
		teacherController.editHomework(0, teaching, date, description, submissionDate);

		verify(factoryMock, homeworkDaoMock);
	}

	@Test
	public void testAddNewLesson()
			throws LessonDaoException, DaoConnectionException, StudentDaoException, TeacherDaoException {
		TeachingAssignment teaching = new TeachingAssignment(1, "Math", teacher, null);
		LocalDate date = LocalDate.now();
		String description = "Math Lesson";
		LocalTime startHour = LocalTime.of(9, 0);
		LocalTime endHour = LocalTime.of(11, 0);

		lessonDaoMock.addLesson(teaching, date, description, startHour, endHour);
		replay(factoryMock, lessonDaoMock);

		teacherController = createTeacherController();
		teacherController.addNewLesson(teaching, date, description, startHour, endHour);

		verify(factoryMock, lessonDaoMock);
	}

	@Test
	public void testEditLesson()
			throws LessonDaoException, DaoConnectionException, StudentDaoException, TeacherDaoException {
		TeachingAssignment teaching = new TeachingAssignment(1, "Math", teacher, null);
		LocalDate date = LocalDate.now();
		String description = "Updated Math Lesson";
		LocalTime startHour = LocalTime.of(9, 0);
		LocalTime endHour = LocalTime.of(11, 0);

		lessonDaoMock.editLesson(teaching, date, description, startHour, endHour);
		replay(factoryMock, lessonDaoMock);

		teacherController = createTeacherController();
		teacherController.editLesson(teaching, date, description, startHour, endHour);

		verify(factoryMock, lessonDaoMock);
	}

	@Test
	public void testGetAbsencesByStudent()
			throws AbsenceDaoException, DaoConnectionException, StudentDaoException, TeacherDaoException {
		Student student = new Student("Fabio", "Rossi", 1, null);
		ArrayList<Absence> absences = new ArrayList<Absence>();
		absences.add(new Absence(student, LocalDate.now(), false));
		Iterator<Absence> absencesIterator = absences.iterator();

		expect(absenceDaoMock.getAbsencesByStudent(student)).andReturn(absencesIterator).once();
		replay(factoryMock, absenceDaoMock);

		teacherController = createTeacherController();
		Iterator<Absence> result = teacherController.getAbsencesByStudent(student);

		assertThat(result).isEqualTo(absencesIterator);

		verify(factoryMock, absenceDaoMock);
	}

	@Test
	public void testGetComponentAnnotationsInDay()
			throws StudentDaoException, TeacherDaoException, DaoConnectionException, HomeworkDaoException {
		LocalDate date = LocalDate.now();
		SchoolClass schoolClass = new SchoolClass("1A");

		Iterator<Component> lessons = createMock(Iterator.class);
		Iterator<Component> homeworks = createMock(Iterator.class);

		expect(lessonDaoMock.getLessonsInDay(date, schoolClass.getClassName())).andReturn(lessons)
				.once();
		expect(homeworkDaoMock.getHomeworksInDay(date, schoolClass.getClassName())).andReturn(homeworks).once();

		replay(factoryMock, lessonDaoMock, homeworkDaoMock ,lessons, homeworks);

		teacherController = createTeacherController();
		Iterator<Component> result = teacherController.getComponentAnnotationsInDay(date, schoolClass);

		assertThat(result).isNotNull();

		verify(factoryMock, lessonDaoMock, homeworkDaoMock ,lessons, homeworks);
	}

}