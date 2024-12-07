package businessLogic;

import static org.assertj.core.api.Assertions.assertThat;

import static org.easymock.EasyMock.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import daoFactory.DaoFactory;
import decorator.Component;
import domainModel.Annotation;
import domainModel.DisciplinaryReport;
import domainModel.Grade;
import domainModel.SchoolClass;
import domainModel.Student;
import domainModel.Teacher;
import domainModel.TeachingAssignment;
import orm.DaoConnectionException;
import orm.DisciplinaryReportDao;
import orm.DisciplinaryReportException;
import orm.GradeDao;
import orm.GradeDaoException;
import orm.HomeworkDao;
import orm.HomeworkDaoException;
import orm.LessonDao;
import orm.SchoolClassDao;
import orm.SchoolClassDaoException;
import orm.StudentDao;
import orm.StudentDaoException;
import orm.TeachingAssignmentDao;
import orm.TeachingAssignmentDaoException;
import strategyForGrade.ArithmeticGradeAverageStrategy;
import strategyForGrade.GeometricGradeAverageStrategy;
import strategyForGrade.WeightedGradeAverageStrategy;


public class StudentControllerTestConMock {

	private DaoFactory factoryMock;
	private StudentDao studentDaoMock;
	private SchoolClassDao schoolClassDaoMock;
	private StudentController studentController;
	private Student student;
	private SchoolClass schoolClass;
	private TeachingAssignmentDao teachingAssignmentDaoMock;
	private int studentId;
	private GradeDao gradeDaoMock;
	private DisciplinaryReportDao disciplinaryReportDaoMock;
	private HomeworkDao homeworkDaoMock;
	private LessonDao lessonDaoMock;
	private TeachingAssignment teaching1;
	private TeachingAssignment teachingAssignment;
	private Grade grade1;
	private Grade grade2;


	@Before 
	public void setup() throws DaoConnectionException, StudentDaoException, SchoolClassDaoException {
		factoryMock = createMock(DaoFactory.class);
		studentDaoMock = createMock(StudentDao.class);
		schoolClassDaoMock = createMock(SchoolClassDao.class);
		teachingAssignmentDaoMock = createMock(TeachingAssignmentDao.class);
		gradeDaoMock = createMock(GradeDao.class);
		disciplinaryReportDaoMock = createMock(DisciplinaryReportDao.class);
		homeworkDaoMock = createMock(HomeworkDao.class);
		lessonDaoMock = createMock(LessonDao.class);
		
		studentId = 1;
		schoolClass = new SchoolClass("1A");
		student = new Student(studentId, "Mario", "Rossi", schoolClass);
		
		expect(studentDaoMock.getStudentById(studentId)).andReturn(student).anyTimes();
		expect(schoolClassDaoMock.getSchoolClassByStudent(student)).andReturn(schoolClass).anyTimes();
		expect(factoryMock.createStudentDao()).andReturn(studentDaoMock).anyTimes();
		expect(factoryMock.createSchoolClassDao()).andReturn(schoolClassDaoMock).anyTimes();
		expect(factoryMock.createTeachingAssignmentDao()).andReturn(teachingAssignmentDaoMock).anyTimes();
		expect(factoryMock.createGradeDao()).andReturn(gradeDaoMock).anyTimes();
		expect(factoryMock.createDisciplinaryReportDao()).andReturn(disciplinaryReportDaoMock).anyTimes();
		expect(factoryMock.createHomeworkDao()).andReturn(homeworkDaoMock).anyTimes();
		expect(factoryMock.createLessonDao()).andReturn(lessonDaoMock).anyTimes();
		
		studentController = new StudentController(student, factoryMock);
		
		grade1 = new Grade(1, student, teachingAssignment, null, 8, 1, null);
		grade2 = new Grade(2, student, teachingAssignment, null, 4, 1, null);
	}
	
	@Test
	public void testGetStudent() throws StudentDaoException, SchoolClassDaoException, DaoConnectionException {
		replay(factoryMock, schoolClassDaoMock);
		
		assertThat(studentController.getStudent()).isEqualTo(student);
	
		verify(factoryMock, schoolClassDaoMock);
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
	public void testGetGradesByTeaching() throws StudentDaoException, SchoolClassDaoException, DaoConnectionException, GradeDaoException {
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

		assertThat(studentController.getAllStudentGrades()).toIterable().containsExactlyInAnyOrder(grade1);

		verify(factoryMock, gradeDaoMock);
	}
	
	@Test
	public void testGetDisciplinaryReports() throws StudentDaoException, SchoolClassDaoException, DaoConnectionException, DisciplinaryReportException {
		ArrayList<DisciplinaryReport> reports = new ArrayList<DisciplinaryReport>();
		DisciplinaryReport disciplinaryReport1 = new DisciplinaryReport(student.getId(),student, null, LocalDate.of(2023, 11, 21), "");
		DisciplinaryReport disciplinaryReport2 = new DisciplinaryReport(student.getId(), student, null, LocalDate.of(2023, 11, 21), "");

		reports.add(disciplinaryReport1);
		reports.add(disciplinaryReport2);
		Iterator<DisciplinaryReport> reportsIterator = reports.iterator();
		
		expect(disciplinaryReportDaoMock.getDisciplinaryReportsByStudent(student)).andReturn(reportsIterator).once();
		
		replay(factoryMock, studentDaoMock, schoolClassDaoMock, disciplinaryReportDaoMock);
		
		assertThat(studentController.getDisciplinaryReports()).isEqualTo(reportsIterator);
		
		verify(factoryMock, studentDaoMock, schoolClassDaoMock, disciplinaryReportDaoMock);
	}
	
	@Test
	public void testGetHomeworkInDate() throws StudentDaoException, SchoolClassDaoException, DaoConnectionException, HomeworkDaoException {
		LocalDate date = LocalDate.now();
		ArrayList<Component> homeworks = new ArrayList<Component>();
		homeworks.add(new Annotation(null, date, 1));
		homeworks.add(new Annotation(null, date, 1));
		Iterator<Component> homeworksIterator = homeworks.iterator();
		
		expect(homeworkDaoMock.getHomeworksInDay(date, schoolClass.getClassName())).andReturn(homeworksIterator).once();
		
		replay(factoryMock, studentDaoMock, schoolClassDaoMock, homeworkDaoMock);
		
		studentController = createStudentController();
		assertThat(studentController.getHomeworkInDate(date)).isEqualTo(homeworksIterator);
		
		verify(factoryMock, studentDaoMock, schoolClassDaoMock, homeworkDaoMock);
	}
	
	@Test
	public void testGetLessonInDate() throws StudentDaoException, SchoolClassDaoException, DaoConnectionException {
		LocalDate date = LocalDate.now();
		ArrayList<Component> lessons = new ArrayList<Component>();
		lessons.add(new Annotation(null, date, 1));
		lessons.add(new Annotation(null, date,1 ));
		Iterator<Component> lessonsIterator = lessons.iterator();
		
		expect(lessonDaoMock.getLessonsInDay(date, schoolClass.getClassName())).andReturn(lessonsIterator).once();
		
		replay(factoryMock, studentDaoMock, schoolClassDaoMock, lessonDaoMock);
		
		studentController = createStudentController();
		assertThat(studentController.getLessonInDate(date)).isEqualTo(lessonsIterator);
		
		verify(factoryMock, studentDaoMock, schoolClassDaoMock, lessonDaoMock);
	}
	
	@Test
	public void testGetComponentAnnotationInDate() throws StudentDaoException, SchoolClassDaoException, DaoConnectionException, HomeworkDaoException {
	    LocalDate date = LocalDate.now();
	    
	    ArrayList<Component> lessons = new ArrayList<Component>();
	    lessons.add(new Annotation(null, date, 1));
	    lessons.add(new Annotation(null, date, 1));
	    Iterator<Component> lessonsIterator = lessons.iterator();
	    
	    ArrayList<Component> homeworks = new ArrayList<Component>();
	    homeworks.add(new Annotation(null, date, 1));
	    homeworks.add(new Annotation(null, date, 1));
	    Iterator<Component> homeworksIterator = homeworks.iterator();
	    
	    expect(lessonDaoMock.getLessonsInDay(date, schoolClass.getClassName())).andReturn(lessonsIterator).once();
	    expect(homeworkDaoMock.getHomeworksInDay(date, schoolClass.getClassName())).andReturn(homeworksIterator).once();
	    
	    replay(factoryMock, studentDaoMock, schoolClassDaoMock, lessonDaoMock, homeworkDaoMock);
	    
	    studentController = createStudentController();
	    Iterator<Component> result = studentController.getComponentAnnotationInDate(date);
	    
	    assertThat(result).hasNext();
	    assertThat(result.next()).isEqualTo(lessons.get(0));
	    assertThat(result.next()).isEqualTo(lessons.get(1));
	    assertThat(result.next()).isEqualTo(homeworks.get(0));
	    assertThat(result.next()).isEqualTo(homeworks.get(1));
	    assertThat(result).isExhausted();
	    
	    verify(factoryMock, studentDaoMock, schoolClassDaoMock, lessonDaoMock, homeworkDaoMock);
	}

	@Test
	public void testCalculateTeachingGradesAverageWithArithmeticAverage() throws StudentDaoException, GradeDaoException, SchoolClassDaoException, DaoConnectionException {
		TeachingAssignment teachingAssignment = new TeachingAssignment(1, null, null, null);
		ArrayList<Grade> grades = new ArrayList<Grade>();
		Grade g1 = new Grade(0, student, teachingAssignment, null, 8.5, 1, null);
		Grade g2 = new Grade(0, student, teachingAssignment, null, 7.25, 1, null);
		Grade g3 = new Grade(0, student, teachingAssignment, null, 7, 1, null);
		grades.add(g1);
		grades.add(g2);
		grades.add(g3);
		Iterator<Grade> gradesIterator = grades.iterator();

		expect(gradeDaoMock.getStudentGradesByTeaching(studentId, 1)).andReturn(gradesIterator).once();
		
		
		double expectedAverage = (8.5+7.25+7)/3;
		
		replay(factoryMock, studentDaoMock, schoolClassDaoMock, gradeDaoMock);

		StudentController studentController = new StudentController(studentId, factoryMock);
		assertThat(studentController.calculateTeachingGradeAverage(teachingAssignment, new ArithmeticGradeAverageStrategy())).isEqualTo(expectedAverage);
		
		verify(factoryMock, studentDaoMock, schoolClassDaoMock, gradeDaoMock);
	}
	
	@Test
	public void testCalculateTeachingGradesAverageWithGeometricAverage() throws StudentDaoException, GradeDaoException, SchoolClassDaoException, DaoConnectionException {
		TeachingAssignment teachingAssignment = new TeachingAssignment(1, null, null, null);
		ArrayList<Grade> grades = new ArrayList<Grade>();
		Grade g1 = new Grade(0, student, teachingAssignment, null, 8.5, 1, null);
		Grade g2 = new Grade(0, student, teachingAssignment, null, 7.25, 1, null);
		Grade g3 = new Grade(0, student, teachingAssignment, null, 7, 1, null);
		grades.add(g1);
		grades.add(g2);
		grades.add(g3);
		Iterator<Grade> gradesIterator = grades.iterator();

		expect(gradeDaoMock.getStudentGradesByTeaching(studentId, 1)).andReturn(gradesIterator).once();
		
		double product = 8.5 * 7.25 * 7;
        double expectedAverage = Math.pow(product, 1.0 / 3);
		
		replay(factoryMock, studentDaoMock, schoolClassDaoMock, gradeDaoMock);

		StudentController studentController = new StudentController(studentId, factoryMock);
		assertThat(studentController.calculateTeachingGradeAverage(teachingAssignment, new GeometricGradeAverageStrategy())).isEqualTo(expectedAverage);
		
		verify(factoryMock, studentDaoMock, schoolClassDaoMock, gradeDaoMock);
	}
	
	@Test
	public void testCalculateTeachingGradesAverageWithWeightedAverage() throws StudentDaoException, GradeDaoException, SchoolClassDaoException, DaoConnectionException {
		TeachingAssignment teachingAssignment = new TeachingAssignment(1, null, null, null);
		ArrayList<Grade> grades = new ArrayList<Grade>();
		Grade g1 = new Grade(0, student, teachingAssignment, null, 8.5, 2, null);
		Grade g2 = new Grade(0, student, teachingAssignment, null, 7.25, 3, null);
		Grade g3 = new Grade(0, student, teachingAssignment, null, 7, 5, null);
		grades.add(g1);
		grades.add(g2);
		grades.add(g3);
		Iterator<Grade> gradesIterator = grades.iterator();

		expect(gradeDaoMock.getStudentGradesByTeaching(studentId, 1)).andReturn(gradesIterator).once();
		
		double weightedSum = (8.5 * 2) + (7.25 * 3) + (7 * 5);
     	double totalWeight = 2 + 3 + 5;
     	double expectedAverage = weightedSum / totalWeight;
		
		replay(factoryMock, studentDaoMock, schoolClassDaoMock, gradeDaoMock);

		StudentController studentController = new StudentController(studentId, factoryMock);
		assertThat(studentController.calculateTeachingGradeAverage(teachingAssignment, new WeightedGradeAverageStrategy())).isEqualTo(expectedAverage);
		
		verify(factoryMock, studentDaoMock, schoolClassDaoMock, gradeDaoMock);
	}
	
	@Test
	public void testCalculateTotalGradesAverageWithArithmeticAverage() throws StudentDaoException, GradeDaoException, SchoolClassDaoException, DaoConnectionException {
		TeachingAssignment teachingAssignment = new TeachingAssignment(1, null, null, null);
		ArrayList<Grade> grades = new ArrayList<Grade>();
		Grade g1 = new Grade(0, student, teachingAssignment, null, 8.5, 1, null);
		Grade g2 = new Grade(0, student, teachingAssignment, null, 7.25, 1, null);
		Grade g3 = new Grade(0, student, teachingAssignment, null, 7, 1, null);
		grades.add(g1);
		grades.add(g2);
		grades.add(g3);
		Iterator<Grade> gradesIterator = grades.iterator();

		expect(gradeDaoMock.getAllStudentGrades(studentId)).andReturn(gradesIterator).once();
		
		double expectedAverage = (8.5+7.25+7)/3;
		
		replay(factoryMock, studentDaoMock, schoolClassDaoMock, gradeDaoMock);

		StudentController studentController = new StudentController(studentId, factoryMock);
		assertThat(studentController.calculateTotalGradeAverage(new ArithmeticGradeAverageStrategy())).isEqualTo(expectedAverage);
		
		verify(factoryMock, studentDaoMock, schoolClassDaoMock, gradeDaoMock);
	}
	
	@Test
	public void testCalculateTotalGradesAverageWithGeometricAverage() throws StudentDaoException, GradeDaoException, SchoolClassDaoException, DaoConnectionException {
		TeachingAssignment teachingAssignment = new TeachingAssignment(1, null, null, null);
		ArrayList<Grade> grades = new ArrayList<Grade>();
		Grade g1 = new Grade(0, student, teachingAssignment, null, 8.5, 1, null);
		Grade g2 = new Grade(0, student, teachingAssignment, null, 7.25, 1, null);
		Grade g3 = new Grade(0, student, teachingAssignment, null, 7, 1, null);
		grades.add(g1);
		grades.add(g2);
		grades.add(g3);
		Iterator<Grade> gradesIterator = grades.iterator();

		expect(gradeDaoMock.getAllStudentGrades(studentId)).andReturn(gradesIterator).once();
		
		double product = 8.5 * 7.25 * 7;
        double expectedAverage = Math.pow(product, 1.0 / 3);
		
		replay(factoryMock, studentDaoMock, schoolClassDaoMock, gradeDaoMock);

		StudentController studentController = new StudentController(studentId, factoryMock);
		assertThat(studentController.calculateTotalGradeAverage(new GeometricGradeAverageStrategy())).isEqualTo(expectedAverage);
		
		verify(factoryMock, studentDaoMock, schoolClassDaoMock, gradeDaoMock);
	}
	
	@Test
	public void testCalculateTotalGradesAverageWithWeightedAverage() throws StudentDaoException, GradeDaoException, SchoolClassDaoException, DaoConnectionException {
		TeachingAssignment teachingAssignment = new TeachingAssignment(1, null, null, null);
		ArrayList<Grade> grades = new ArrayList<Grade>();
		Grade g1 = new Grade(0, student, teachingAssignment, null, 8.5, 2, null);
		Grade g2 = new Grade(0, student, teachingAssignment, null, 7.25, 3, null);
		Grade g3 = new Grade(0, student, teachingAssignment, null, 7, 5, null);
		grades.add(g1);
		grades.add(g2);
		grades.add(g3);
		Iterator<Grade> gradesIterator = grades.iterator();

		expect(gradeDaoMock.getAllStudentGrades(studentId)).andReturn(gradesIterator).once();
		
		double weightedSum = (8.5 * 2) + (7.25 * 3) + (7 * 5);
     	double totalWeight = 2 + 3 + 5;
     	double expectedAverage = weightedSum / totalWeight;
		
		replay(factoryMock, studentDaoMock, schoolClassDaoMock, gradeDaoMock);

		StudentController studentController = new StudentController(studentId, factoryMock);
		assertThat(studentController.calculateTotalGradeAverage(new WeightedGradeAverageStrategy())).isEqualTo(expectedAverage);
		
		verify(factoryMock, studentDaoMock, schoolClassDaoMock, gradeDaoMock);
	}
	
}

