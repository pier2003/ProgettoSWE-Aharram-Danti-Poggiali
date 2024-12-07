package businessLogic;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.fail;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.Before;
import org.junit.Test;

import businessLogic.TeacherController.IllegalHomeworkAccessException;
import businessLogic.TeacherController.IllegalLessonAccessException;
import businessLogic.TeacherController.IllegalReportAccessException;
import businessLogic.TeacherController.MeetingAlreadyBookedException;
import daoFactory.DaoFactory;
import domainModel.Absence;
import domainModel.DisciplinaryReport;
import domainModel.Grade;
import domainModel.Homework;
import domainModel.Lesson;
import domainModel.Meeting;
import domainModel.MeetingAvailability;
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
import orm.MeetingAvailabilityDao;
import orm.MeetingDao;
import orm.SchoolClassDao;
import orm.SchoolClassDaoException;
import orm.StudentDao;
import orm.StudentDaoException;
import orm.TeacherDao;
import orm.TeacherDaoException;
import orm.TeachingAssignmentDao;
import strategyForGrade.ArithmeticGradeAverageStrategy;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class TeacherControllerTestConMock {

	private DaoFactory factoryMock;
	private TeacherDao teacherDaoMock;
	private StudentDao studentDaoMock;
	private Teacher teacher;
	private SchoolClassDao schoolClassDaoMock;
	private TeacherController teacherController;
	private GradeDao gradeDaoMock;
	private DisciplinaryReportDao disciplinaryReportDaoMock;
	private AbsenceDao absenceDaoMock;
	private HomeworkDao homeworkDaoMock;
	private LessonDao lessonDaoMock;
	private TeachingAssignmentDao teachingAssignmentDaoMock;

	// Variabili condivise tra i test
	private ArrayList<TeachingAssignment> teachingAssignments;
	private TeachingAssignment teachingAssignment1;
	private TeachingAssignment teachingAssignment2;
	private ArrayList<SchoolClass> classes;
	private SchoolClass schoolClass1;
	private SchoolClass schoolClass2;
	private ArrayList<Student> students;
	private Student student1;
	private Student student2;
	private SchoolClass schoolClassForStudents;
	private ArrayList<Grade> grades;
	private Grade grade1;
	private Grade grade2;
	private LocalDate date;
	private ArrayList<DisciplinaryReport> reports;
	private DisciplinaryReport report1;
	private DisciplinaryReport report2;
	private ArrayList<Absence> absences;
	private Absence absence1;
	private Absence absence2;
	private Homework homework1;
	private Homework homework2;
	private ArrayList<Homework> homeworks;
	private LocalTime startHour;
	private LocalTime endHour;
	private ArrayList<Lesson> lessons;
	private Lesson lesson1;
	private Lesson lesson2;
	private MeetingAvailabilityDao meetingAvailabilityDaoMock;
	private MeetingDao meetingDaoMock;

	@Before
	public void setup() throws StudentDaoException, TeacherDaoException, DaoConnectionException {
		// Mock e oggetti principali
		factoryMock = createMock(DaoFactory.class);
		teacherDaoMock = createMock(TeacherDao.class);
		studentDaoMock = createMock(StudentDao.class);
		schoolClassDaoMock = createMock(SchoolClassDao.class);
		gradeDaoMock = createMock(GradeDao.class);
		disciplinaryReportDaoMock = createMock(DisciplinaryReportDao.class);
		absenceDaoMock = createMock(AbsenceDao.class);
		homeworkDaoMock = createMock(HomeworkDao.class);
		lessonDaoMock = createMock(LessonDao.class);
		teachingAssignmentDaoMock = createMock(TeachingAssignmentDao.class);
		meetingAvailabilityDaoMock = createMock(MeetingAvailabilityDao.class);
		meetingDaoMock = createMock(MeetingDao.class);

		teacher = new Teacher(1, "Mario", "Rossi");

		// Aspettative comuni per DaoFactory
		expect(factoryMock.createSchoolClassDao()).andReturn(schoolClassDaoMock).anyTimes();
		expect(factoryMock.createStudentDao()).andReturn(studentDaoMock).anyTimes();
		expect(factoryMock.createGradeDao()).andReturn(gradeDaoMock).anyTimes();
		expect(factoryMock.createDisciplinaryReportDao()).andReturn(disciplinaryReportDaoMock).anyTimes();
		expect(factoryMock.creatTeacherDao()).andReturn(teacherDaoMock).anyTimes();
		expect(factoryMock.createAbsenceDao()).andReturn(absenceDaoMock).anyTimes();
		expect(factoryMock.createHomeworkDao()).andReturn(homeworkDaoMock).anyTimes();
		expect(factoryMock.createLessonDao()).andReturn(lessonDaoMock).anyTimes();
		expect(factoryMock.createTeachingAssignmentDao()).andReturn(teachingAssignmentDaoMock).anyTimes();
		expect(factoryMock.createMeetingAvailabilityDao()).andReturn(meetingAvailabilityDaoMock).anyTimes();
		expect(factoryMock.createMeetingDao()).andReturn(meetingDaoMock).anyTimes();
		
		teacherController = new TeacherController(teacher, factoryMock);
				
		date = LocalDate.now();
		startHour = LocalTime.of(9, 0);
		endHour = LocalTime.of(11, 0);

		classes = new ArrayList<>();
		schoolClass1 = new SchoolClass("1A");
		schoolClass2 = new SchoolClass("2A");
		classes.add(schoolClass1);
		classes.add(schoolClass2);

		// Dati condivisi per i test
		teachingAssignments = new ArrayList<>();
		teachingAssignment1 = new TeachingAssignment(1, "matematica", teacher, schoolClass1);
		teachingAssignment2 = new TeachingAssignment(2, "scienze", teacher, schoolClass2);
		teachingAssignments.add(teachingAssignment1);
		teachingAssignments.add(teachingAssignment2);

		students = new ArrayList<>();
		schoolClassForStudents = new SchoolClass("1A");
		student1 = new Student(1, "Fabio", "Rossi", schoolClassForStudents);
		student2 = new Student(2, "Gino", "Rossi", schoolClassForStudents);
		students.add(student1);
		students.add(student2);

		grades = new ArrayList<>();
		grade1 = new Grade(1, student1, teachingAssignment1, date, 8, 1, "Ottimo lavoro");
		grade2 = new Grade(2, student1, teachingAssignment1, date, 4, 1, "Non ha studiato abbastanza");
		grades.add(grade1);
		grades.add(grade2);

		reports = new ArrayList<>();
		report1 = new DisciplinaryReport(1, student1, teacher, date, "Mangia in classe durante la lezione");
		report2 = new DisciplinaryReport(2, student1, teacher, date, "Parla col compagno in classe durante la lezione");
		reports.add(report1);
		reports.add(report2);

		absences = new ArrayList<>();
		absence1 = new Absence(student1, date, false);
		absence2 = new Absence(student1, date, false);
		absences.add(absence1);
		absences.add(absence2);

		homeworks = new ArrayList<>();
		homework1 = new Homework(0, teachingAssignment1, date, "Fare esercizi", date);
		homework2 = new Homework(1, teachingAssignment1, date, "Fare esercizi", date);
		homeworks.add(homework1);
		homeworks.add(homework2);

		lessons = new ArrayList<>();
		lesson1 = new Lesson(1, teachingAssignment1, date, "esercizi", startHour, endHour);
		lesson2 = new Lesson(2, teachingAssignment1, date, "esercizi", startHour, endHour);
		lessons.add(lesson1);
		lessons.add(lesson2);

	}

	@Test
	public void testGetTeacher() throws StudentDaoException, TeacherDaoException, DaoConnectionException {
		assertThat(teacherController.getTeacher()).isEqualTo(teacher);
	}

	@Test
	public void testGetAllMyTeachings() {
		Iterator<TeachingAssignment> teachingsIterator = teachingAssignments.iterator();

		expect(teachingAssignmentDaoMock.getAllTeacherTeachings(teacher)).andReturn(teachingsIterator).once();

		replay(factoryMock, teachingAssignmentDaoMock);

		assertThat(teacherController.getAllMyTeachings()).toIterable().containsExactlyInAnyOrder(teachingAssignment1,
				teachingAssignment2);

		verify(factoryMock, teachingAssignmentDaoMock);
	}

	@Test
	public void testGetClassByTeaching()
			throws SchoolClassDaoException, DaoConnectionException, StudentDaoException, TeacherDaoException {
		Iterator<SchoolClass> classesIterator = classes.iterator();
		TeachingAssignment teachingAssignment = teachingAssignment1;

		expect(schoolClassDaoMock.getAllSchoolClassesByTeaching(teachingAssignment)).andReturn(classesIterator).once();

		replay(factoryMock, schoolClassDaoMock);

		assertThat(teacherController.getClassByTeaching(teachingAssignment)).isEqualTo(classesIterator);

		verify(factoryMock, schoolClassDaoMock);
	}

	@Test
	public void testGetStudentByClass() throws DaoConnectionException, StudentDaoException, TeacherDaoException {
		Iterator<Student> studentsIterator = students.iterator();

		expect(studentDaoMock.getStudentsByClass(schoolClassForStudents)).andReturn(studentsIterator).once();

		replay(factoryMock, studentDaoMock);

		assertThat(teacherController.getStudentsByClass(schoolClassForStudents)).isEqualTo(studentsIterator);

		verify(factoryMock, studentDaoMock);
	}

	@Test
	public void testGetAllStudentGradesByTeaching() throws GradeDaoException, DaoConnectionException {
		Iterator<Grade> gradesIterator = grades.iterator();

		expect(gradeDaoMock.getStudentGradesByTeaching(student1, teachingAssignment1)).andReturn(gradesIterator).once();

		replay(factoryMock, gradeDaoMock);

		assertThat(teacherController.getAllStudentGradesByTeaching(student1, teachingAssignment1)).toIterable()
				.containsExactlyInAnyOrder(grade1, grade2);

		verify(factoryMock, gradeDaoMock);
	}

	@Test
	public void testAssignGradeToStudentInDate_ValidValue() throws Exception {
		double gradeValue = 8.5;
		String description = "Math Test";

		gradeDaoMock.addNewGrade(gradeValue, description, teachingAssignment1, student1, date);

		replay(factoryMock, gradeDaoMock);

		teacherController.assignGradeToStudentInDate(gradeValue, description, teachingAssignment1, student1, date);

		verify(factoryMock, gradeDaoMock);
	}

	@Test
	public void testAssignGradeToStudentInDate_InvalidValue() throws Exception {
		double invalideGradeValue = -1;
		String description = "Math Test";

		replay(factoryMock, gradeDaoMock);

		assertThatThrownBy(() -> teacherController.assignGradeToStudentInDate(invalideGradeValue, description,
				teachingAssignment1, student1, date)).isInstanceOf(InvalidGradeValueException.class);

		verify(factoryMock, gradeDaoMock);
	}

	@Test
	public void testAssignGradeToStudentInDateWithWeight_ValidValue() throws Exception {
		double gradeValue = 8.5;
		int weight = 2;
		String description = "Math Test";

		gradeDaoMock.addNewGradeWithWeight(gradeValue, weight, description, teachingAssignment1, student1, date);
		expectLastCall().once();

		replay(factoryMock, gradeDaoMock);

		teacherController.assignGradeToStudentInDateWithWeight(gradeValue, weight, description, teachingAssignment1,
				student1, date);

		verify(factoryMock, gradeDaoMock);
	}

	@Test
	public void testAssignGradeToStudentInDateWithWeight_InvalidGradeValue() throws Exception {
		double invalidGradeValue = -1;
		int weight = 2;
		String description = "Math Test";

		replay(factoryMock, teacherDaoMock);

		assertThatThrownBy(() -> teacherController.assignGradeToStudentInDateWithWeight(invalidGradeValue, weight,
				description, teachingAssignment1, student1, date)).isInstanceOf(InvalidGradeValueException.class);

		verify(factoryMock, teacherDaoMock);
	}

	@Test
	public void testAssignGradeToStudentInDateWithWeight_NegativeWeight() throws Exception {
		double gradeValue = 8.5;
		int negativeWeight = -2;
		String description = "Math Test";

		replay(factoryMock, gradeDaoMock);

		assertThatThrownBy(() -> teacherController.assignGradeToStudentInDateWithWeight(gradeValue, negativeWeight,
				description, teachingAssignment1, student1, date)).isInstanceOf(NegativeWeightException.class);

		verify(factoryMock, gradeDaoMock);
	}

	@Test
	public void testEditGradeValue()
			throws GradeDaoException, DaoConnectionException, StudentDaoException, TeacherDaoException {
		double value = 8;

		gradeDaoMock.editGradeValue(grade1, value);

		replay(factoryMock, gradeDaoMock);

		teacherController.editGradeValue(grade1, value);

		verify(factoryMock, gradeDaoMock);
	}

	@Test
	public void testEditGradeWeightById()
			throws GradeDaoException, DaoConnectionException, StudentDaoException, TeacherDaoException {
		int weight = 10;

		gradeDaoMock.editGradeWeight(grade1, weight);

		replay(factoryMock, gradeDaoMock);

		teacherController.editGradeWeight(grade1, weight);

		verify(factoryMock, gradeDaoMock);
	}

	@Test
	public void testEditGradeDescription()
			throws GradeDaoException, DaoConnectionException, StudentDaoException, TeacherDaoException {
		String description = "Updated description";

		gradeDaoMock.editGradeDescription(grade1, description);

		replay(factoryMock, gradeDaoMock);

		teacherController.editGradeDescription(grade1, description);

		verify(factoryMock, gradeDaoMock);
	}

	@Test
	public void testDeleteGrade() throws GradeDaoException, DaoConnectionException {
		gradeDaoMock.deleteGrade(grade1);

		replay(factoryMock, gradeDaoMock);

		teacherController.deleteGrade(grade1);

		verify(factoryMock, gradeDaoMock);
	}

	@Test
	public void testCalculateStudentTeachingAvarage() {
		ArithmeticGradeAverageStrategy strategy = new ArithmeticGradeAverageStrategy();
		// a senso che lo testi qui?
		fail();
	}

	@Test
	public void testGetStudentDisciplinaryReports() throws DisciplinaryReportException, DaoConnectionException {
		Iterator<DisciplinaryReport> reportsIterator = reports.iterator();

		expect(disciplinaryReportDaoMock.getDisciplinaryReportsByStudent(student1)).andReturn(reportsIterator).once();

		replay(factoryMock, disciplinaryReportDaoMock);

		assertThat(teacherController.getStudentDisciplinaryReports(student1)).toIterable()
				.containsExactlyInAnyOrder(report1, report2);

		verify(factoryMock, disciplinaryReportDaoMock);
	}

	@Test
	public void testAssignDisciplinaryReportToStudentInDate() throws StudentDaoException, TeacherDaoException,
			DaoConnectionException, AbsenceDaoException, DisciplinaryReportException {
		String description = "Arriva in ritardo.";

		disciplinaryReportDaoMock.addNewReport(teacher, student1, description, date);

		replay(factoryMock, disciplinaryReportDaoMock);

		teacherController.assignDisciplinaryReportToStudentInDate(student1, description, date);

		verify(factoryMock, disciplinaryReportDaoMock);
	}

	@Test
	public void testdeleteDisciplinaryReport() throws IllegalReportAccessException {
		disciplinaryReportDaoMock.deleteReport(report1);

		replay(factoryMock, disciplinaryReportDaoMock);

		teacherController.deleteDisciplinaryReport(report1);

		verify(factoryMock, disciplinaryReportDaoMock);
	}

	@Test
	public void testdeleteDisciplinaryReport_WithAnotherTeacher() {
		Teacher otherTeacher = new Teacher(-1, "", "");
		DisciplinaryReport report = new DisciplinaryReport(0, student1, otherTeacher, date, "");

		replay(factoryMock);

		assertThatThrownBy(() -> teacherController.deleteDisciplinaryReport(report))
				.isInstanceOf(IllegalReportAccessException.class);

		verify(factoryMock);
	}

	@Test
	public void testGetAbsencesByClassInDate()
			throws AbsenceDaoException, DaoConnectionException, StudentDaoException, TeacherDaoException {
		Iterator<Absence> absencesIterator = absences.iterator();

		expect(absenceDaoMock.getAbsencesByClassInDate(schoolClass1, date)).andReturn(absencesIterator).once();

		replay(factoryMock, absenceDaoMock);

		assertThat(teacherController.getAbsencesByClassInDate(schoolClass1, date)).isEqualTo(absencesIterator);

		verify(factoryMock, absenceDaoMock);
	}

	@Test
	public void testGetAbsencesByStudent()
			throws AbsenceDaoException, DaoConnectionException, StudentDaoException, TeacherDaoException {
		Iterator<Absence> absencesIterator = absences.iterator();

		expect(absenceDaoMock.getAbsencesByStudent(student1)).andReturn(absencesIterator).once();
		replay(factoryMock, absenceDaoMock);

		Iterator<Absence> result = teacherController.getAbsencesByStudent(student1);

		assertThat(result).isEqualTo(absencesIterator);

		verify(factoryMock, absenceDaoMock);
	}

	@Test
	public void testAssignAbsenceToStudentOnDate()
			throws AbsenceDaoException, DaoConnectionException, StudentDaoException, TeacherDaoException {
		absenceDaoMock.addAbsence(student1, date);

		replay(factoryMock, absenceDaoMock);

		teacherController.assignAbsenceToStudentInDate(student1, date);

		verify(factoryMock, absenceDaoMock);
	}

	@Test
	public void testDeleteAbsence()
			throws AbsenceDaoException, DaoConnectionException, StudentDaoException, TeacherDaoException {
		absenceDaoMock.deleteAbsence(student1, date);

		replay(factoryMock, absenceDaoMock);

		teacherController.deleteAbsence(student1, date);

		verify(factoryMock, absenceDaoMock);
	}

	@Test
	public void testAssignNewHomework()
			throws HomeworkDaoException, StudentDaoException, TeacherDaoException, DaoConnectionException {
		String description = "Math Homework";
		LocalDate submissionDate = LocalDate.now().plusDays(7);

		homeworkDaoMock.addHomework(teachingAssignment1, date, description, submissionDate);

		replay(factoryMock, homeworkDaoMock);

		teacherController.assignNewHomework(teachingAssignment1, date, description, submissionDate);

		verify(factoryMock, homeworkDaoMock);
	}

	@Test
	public void tesEditHomeworkDescription() throws HomeworkDaoException, StudentDaoException, TeacherDaoException,
			DaoConnectionException, IllegalHomeworkAccessException {
		Homework oldHomework = new Homework(1, teachingAssignment1, date, "Old descriptoin", date);
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
		TeachingAssignment otherTeachingAssignment = new TeachingAssignment(0, "italiano", otherTeacher, schoolClass1);
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
		Homework oldHomework = new Homework(1, teachingAssignment1, date, "Homework Description", date);
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
		TeachingAssignment otherTeachingAssignment = new TeachingAssignment(0, "italiano", otherTeacher, schoolClass1);
		Homework oldHomework = new Homework(1, otherTeachingAssignment, date, "Homework Description", date);
		LocalDate newSubmissionDate = LocalDate.now().plusDays(5);

		replay(factoryMock);

		assertThatThrownBy(() -> teacherController.editHomeworkSubmissionDate(oldHomework, newSubmissionDate))
				.isInstanceOf(IllegalHomeworkAccessException.class);

		verify(factoryMock);
	}

	@Test
	public void testGetClassHomeworksSubmissionDate()
			throws DaoConnectionException, HomeworkDaoException, StudentDaoException, TeacherDaoException {
		LocalDate submissionDate = LocalDate.now();
		Iterator<Homework> homeworksIterator = homeworks.iterator();

		expect(homeworkDaoMock.getHomeworksBySubmissionDate(submissionDate, schoolClass1)).andReturn(homeworksIterator)
				.once();

		replay(factoryMock, homeworkDaoMock);

		assertThat(teacherController.getClassHomeworksSubmissionDate(submissionDate, schoolClass1)).toIterable()
				.containsExactlyInAnyOrder(homework1, homework2);

		verify(factoryMock, homeworkDaoMock);
	}

	@Test
	public void testDeleteHomework() throws HomeworkDaoException, StudentDaoException, TeacherDaoException,
			DaoConnectionException, IllegalHomeworkAccessException {
		Homework homework = new Homework(1, teachingAssignment1, date, "Homework Description", date);

		homeworkDaoMock.deleteHomework(homework);

		replay(factoryMock, homeworkDaoMock);

		teacherController.deleteHomework(homework);

		verify(factoryMock, homeworkDaoMock);
	}

	@Test
	public void testDeleteHomework_WithAnotherTeacher() throws HomeworkDaoException, StudentDaoException,
			TeacherDaoException, DaoConnectionException, IllegalHomeworkAccessException {
		Teacher otherTeacher = new Teacher(-1, "Other", "Teacher");
		TeachingAssignment otherTeachingAssignment = new TeachingAssignment(0, "italiano", otherTeacher, schoolClass1);
		Homework homework = new Homework(1, otherTeachingAssignment, date, "Homework Description", date);

		replay(factoryMock);

		assertThatThrownBy(() -> teacherController.deleteHomework(homework))
				.isInstanceOf(IllegalHomeworkAccessException.class);

		verify(factoryMock);
	}

	@Test
	public void testAddNewLesson()
			throws LessonDaoException, DaoConnectionException, StudentDaoException, TeacherDaoException {
		String description = "Math Lesson";

		lessonDaoMock.addLesson(teachingAssignment1, date, description, startHour, endHour);

		replay(factoryMock, lessonDaoMock);

		teacherController.addNewLesson(teachingAssignment1, date, description, startHour, endHour);

		verify(factoryMock, lessonDaoMock);
	}

	@Test
	public void testEditLessonDescription()
			throws LessonDaoException, DaoConnectionException, IllegalLessonAccessException {
		Lesson lesson = new Lesson(1, teachingAssignment1, date, "Old Description", endHour, startHour);
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
		TeachingAssignment otherTeachingAssignment = new TeachingAssignment(0, "History", otherTeacher, schoolClass1);
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
		Lesson lesson = new Lesson(1, teachingAssignment1, date, "Old Description", endHour, startHour);
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
		TeachingAssignment otherTeachingAssignment = new TeachingAssignment(0, "History", otherTeacher, schoolClass1);
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
		Iterator<Lesson> lessonsIterator = lessons.iterator();

		expect(lessonDaoMock.getLessonsInDay(date, schoolClass1)).andReturn(lessonsIterator).once();
		
		replay(factoryMock, lessonDaoMock);

	    assertThat(teacherController.getClassLessonsInDay(date, schoolClass1)).toIterable().containsExactlyInAnyOrder(lesson1, lesson2);

		verify(factoryMock, lessonDaoMock);
	}
	
	@Test
	public void testDeleteLesson()
	        throws IllegalHomeworkAccessException {
	    Lesson lesson = new Lesson(1, teachingAssignment1, date, "Description", endHour, startHour);

	    lessonDaoMock.deleteLesson(lesson);
	    replay(factoryMock, lessonDaoMock);

	    teacherController.deleteLesson(lesson);

	    verify(factoryMock, lessonDaoMock);
	}

	@Test
	public void testDeleteLesson_WithAnotherTeacher()
	        throws IllegalHomeworkAccessException {
	    Teacher otherTeacher = new Teacher(-1, "Other", "Teacher");
	    TeachingAssignment otherTeachingAssignment = new TeachingAssignment(0, "History", otherTeacher, schoolClass1);
	    Lesson lesson = new Lesson(1, otherTeachingAssignment, date, "Description", endHour, startHour);

	    replay(factoryMock);

	    assertThatThrownBy(() -> teacherController.deleteLesson(lesson))
	            .isInstanceOf(IllegalHomeworkAccessException.class);

	    verify(factoryMock);
	}
	
	@Test
	public void testAddNewMeetingAvailabilityInDate() {
	    LocalDate meetingDate = date;

	    meetingAvailabilityDaoMock.addMeetingAvailabilityInDate(teacher, meetingDate);
	    replay(factoryMock, meetingAvailabilityDaoMock);

	    teacherController.addNewMeetingAvailabilityInDate(meetingDate);

	    verify(factoryMock, meetingAvailabilityDaoMock);
	}
	
	@Test
	public void testGetMeetingAvailabilities() {
	    MeetingAvailability unbookedMeeting1 = new MeetingAvailability(teacher, date, endHour, false);
	    MeetingAvailability unbookedMeeting2 = new MeetingAvailability(teacher, date, endHour, true);
	    List<MeetingAvailability> allMeetings = Arrays.asList(unbookedMeeting1, unbookedMeeting2);
	    Iterator<MeetingAvailability> meetingsIterator = allMeetings.iterator();

	    expect(meetingAvailabilityDaoMock.getAllMeetingsAvaialabilityByTeacher(teacher))
	            .andReturn(meetingsIterator).once();
	    replay(factoryMock, meetingAvailabilityDaoMock);

	    Iterator<MeetingAvailability> result = teacherController.getMeetingAvailabilities();

	    assertThat(result).toIterable().containsExactlyInAnyOrder(unbookedMeeting1);

	    verify(factoryMock, meetingAvailabilityDaoMock);
	}

	@Test
	public void testGetBookedMeetings() {
		Meeting meeting1 = new Meeting(null, null);
	    Meeting meeting2 = new Meeting(null, null);
		List<Meeting> meetingList = Arrays.asList(meeting1, meeting2);
	    Iterator<Meeting> meetingsIterator = meetingList.iterator();

	    expect(meetingDaoMock.getMeetingsByTeacher(teacher)).andReturn(meetingsIterator).once();
	    replay(factoryMock, meetingDaoMock);

	    Iterator<Meeting> result = teacherController.getBookedMeetings();

	    assertThat(result).isEqualTo(meetingsIterator);

	    verify(factoryMock, meetingDaoMock);
	}


	@Test
	public void testDeleteMeetingAvailability() throws MeetingAlreadyBookedException {
	    MeetingAvailability unbookedMeeting = new MeetingAvailability(teacher, date, endHour, false);

	    meetingAvailabilityDaoMock.deleteMeetingAvailability(unbookedMeeting);
	    replay(factoryMock, meetingAvailabilityDaoMock);

	    teacherController.deleteMeetingAvailability(unbookedMeeting);

	    verify(factoryMock, meetingAvailabilityDaoMock);
	}

	@Test
	public void testDeleteMeetingAvailability_BookedMeeting() throws MeetingAlreadyBookedException {
	    MeetingAvailability bookedMeeting = new MeetingAvailability(teacher, date, endHour, true);

	    replay(factoryMock);

	    assertThatThrownBy(() -> teacherController.deleteMeetingAvailability(bookedMeeting))
	            .isInstanceOf(MeetingAlreadyBookedException.class);

	    verify(factoryMock);
	}


}