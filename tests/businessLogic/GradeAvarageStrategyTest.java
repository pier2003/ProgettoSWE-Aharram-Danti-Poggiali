package businessLogic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import daoFactory.DaoFactory;
import domainModel.Grade;
import domainModel.SchoolClass;
import domainModel.Student;
import domainModel.TeachingAssignment;
import orm.DaoConnectionException;
import orm.DisciplinaryReportDao;
import orm.GradeDao;
import orm.GradeDaoException;
import orm.HomeworkDao;
import orm.LessonDao;
import orm.SchoolClassDao;
import orm.SchoolClassDaoException;
import orm.StudentDao;
import orm.StudentDaoException;
import orm.TeachingAssignmentDao;
import strategyForGrade.ArithmeticGradeAverageStrategy;
import strategyForGrade.GeometricGradeAverageStrategy;
import strategyForGrade.WeightedGradeAverageStrategy;

public class GradeAvarageStrategyTest {
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
		
		schoolClass = new SchoolClass("1A");
		student = new Student(1, "Mario", "Rossi", schoolClass);
		studentController = new StudentController(student, factoryMock);
		
		expect(studentDaoMock.getStudentById(studentId)).andReturn(student).anyTimes();
		expect(schoolClassDaoMock.getSchoolClassByStudent(student)).andReturn(schoolClass).anyTimes();
		expect(factoryMock.createStudentDao()).andReturn(studentDaoMock).anyTimes();
		expect(factoryMock.createSchoolClassDao()).andReturn(schoolClassDaoMock).anyTimes();
		expect(factoryMock.createTeachingAssignmentDao()).andReturn(teachingAssignmentDaoMock).anyTimes();
		expect(factoryMock.createGradeDao()).andReturn(gradeDaoMock).anyTimes();
		expect(factoryMock.createDisciplinaryReportDao()).andReturn(disciplinaryReportDaoMock).anyTimes();
		expect(factoryMock.createHomeworkDao()).andReturn(homeworkDaoMock).anyTimes();
		expect(factoryMock.createLessonDao()).andReturn(lessonDaoMock).anyTimes();
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

		expect(gradeDaoMock.getStudentGradesByTeaching(student, teachingAssignment)).andReturn(gradesIterator).once();
		
		
		double expectedAverage = (8.5+7.25+7)/3;
		
		replay(factoryMock, studentDaoMock, schoolClassDaoMock, gradeDaoMock);

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

		expect(gradeDaoMock.getStudentGradesByTeaching(student, teachingAssignment)).andReturn(gradesIterator).once();
		
		double product = 8.5 * 7.25 * 7;
        double expectedAverage = Math.pow(product, 1.0 / 3);
		
		replay(factoryMock, studentDaoMock, schoolClassDaoMock, gradeDaoMock);

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

		expect(gradeDaoMock.getStudentGradesByTeaching(student, teachingAssignment)).andReturn(gradesIterator).once();
		
		double weightedSum = (8.5 * 2) + (7.25 * 3) + (7 * 5);
     	double totalWeight = 2 + 3 + 5;
     	double expectedAverage = weightedSum / totalWeight;
		
		replay(factoryMock, studentDaoMock, schoolClassDaoMock, gradeDaoMock);

		assertThat(studentController.calculateTeachingGradeAverage(teachingAssignment, new WeightedGradeAverageStrategy())).isEqualTo(expectedAverage);
		
		verify(factoryMock, studentDaoMock, schoolClassDaoMock, gradeDaoMock);
	}
	

}
