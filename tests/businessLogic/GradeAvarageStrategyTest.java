package businessLogic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.ArrayList;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import daoFactory.DaoFactory;
import domainModel.Grade;
import domainModel.SchoolClass;
import domainModel.Student;
import domainModel.Teacher;
import domainModel.TeachingAssignment;
import orm.DaoConnectionException;
import orm.GradeDao;
import orm.GradeDaoException;
import orm.SchoolClassDaoException;
import orm.StudentDaoException;
import orm.TeacherDaoException;
import orm.TeachingAssignmentDaoException;
import strategyForGrade.ArithmeticGradeAverageStrategy;
import strategyForGrade.GeometricGradeAverageStrategy;
import strategyForGrade.GradeAverageStrategy;
import strategyForGrade.WeightedGradeAverageStrategy;

public class GradeAvarageStrategyTest {
	private DaoFactory factoryMock;
	private StudentController studentController;
	private Student student;
	private SchoolClass schoolClass;
	private GradeDao gradeDaoMock;
	private TeachingAssignment teachingAssignment;
	private Teacher teacher;
	private TeacherController teacherController;

	@Before
	public void setup() throws DaoConnectionException, StudentDaoException, SchoolClassDaoException, TeacherDaoException {
		factoryMock = createMock(DaoFactory.class);
		gradeDaoMock = createMock(GradeDao.class);
		
		schoolClass = new SchoolClass("1A");
		student = new Student(1, "Mario", "Rossi", schoolClass);
		studentController = new StudentController(student, factoryMock);
		teachingAssignment = new TeachingAssignment(1, null, null, schoolClass);
		
		teacher = new Teacher(1, "Atif", "Mohamed");
		teacherController = new TeacherController(teacher, factoryMock);
		
		expect(factoryMock.createGradeDao()).andReturn(gradeDaoMock).anyTimes();        
	}
	
	@Test
	public void testCalculateTeachingGradesAverageWithArithmeticAverage_StudentController() throws StudentDaoException, GradeDaoException, SchoolClassDaoException, DaoConnectionException, TeachingAssignmentDaoException {
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
		
		replay(factoryMock, gradeDaoMock);

		assertThat(studentController.calculateTeachingGradeAverage(teachingAssignment, new ArithmeticGradeAverageStrategy())).isEqualTo(expectedAverage);
		
		verify(factoryMock, gradeDaoMock);
	}
	
	@Test
	public void testCalculateTeachingGradesAverageWithGeometricAverage_StudentController() throws StudentDaoException, GradeDaoException, SchoolClassDaoException, DaoConnectionException, TeachingAssignmentDaoException {
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
		
		replay(factoryMock, gradeDaoMock);

		assertThat(studentController.calculateTeachingGradeAverage(teachingAssignment, new GeometricGradeAverageStrategy())).isEqualTo(expectedAverage);
		
		verify(factoryMock, gradeDaoMock);
	}
	
	@Test
	public void testCalculateTeachingGradesAverageWithWeightedAverage_StudentController() throws StudentDaoException, GradeDaoException, SchoolClassDaoException, DaoConnectionException, TeachingAssignmentDaoException {
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
		
		replay(factoryMock, gradeDaoMock);

		assertThat(studentController.calculateTeachingGradeAverage(teachingAssignment, new WeightedGradeAverageStrategy())).isEqualTo(expectedAverage);
		
		verify(factoryMock, gradeDaoMock);
	}
	
	@Test
	public void testCalculateTotalGradesAverageWithArithmeticAverage_StudentController() throws StudentDaoException, GradeDaoException, SchoolClassDaoException, DaoConnectionException {
		TeachingAssignment teachingAssignment = new TeachingAssignment(1, null, null, null);
		ArrayList<Grade> grades = new ArrayList<Grade>();
		Grade g1 = new Grade(0, student, teachingAssignment, null, 8.5, 1, null);
		Grade g2 = new Grade(0, student, teachingAssignment, null, 7.25, 1, null);
		Grade g3 = new Grade(0, student, teachingAssignment, null, 7, 1, null);
		grades.add(g1);
		grades.add(g2);
		grades.add(g3);
		Iterator<Grade> gradesIterator = grades.iterator();

		expect(gradeDaoMock.getAllStudentGrades(student)).andReturn(gradesIterator).once();
		
		double expectedAverage = (8.5+7.25+7)/3;
		
		replay(factoryMock, gradeDaoMock);

		StudentController studentController = new StudentController(student, factoryMock);
		assertThat(studentController.calculateTotalGradeAverage(new ArithmeticGradeAverageStrategy())).isEqualTo(expectedAverage);
		
		verify(factoryMock, gradeDaoMock);
	}
	
	@Test
	public void testCalculateTotalGradesAverageWithGeometricAverage_StudentController() throws StudentDaoException, GradeDaoException, SchoolClassDaoException, DaoConnectionException {
		TeachingAssignment teachingAssignment = new TeachingAssignment(1, null, null, null);
		ArrayList<Grade> grades = new ArrayList<Grade>();
		Grade g1 = new Grade(0, student, teachingAssignment, null, 8.5, 1, null);
		Grade g2 = new Grade(0, student, teachingAssignment, null, 7.25, 1, null);
		Grade g3 = new Grade(0, student, teachingAssignment, null, 7, 1, null);
		grades.add(g1);
		grades.add(g2);
		grades.add(g3);
		Iterator<Grade> gradesIterator = grades.iterator();

		expect(gradeDaoMock.getAllStudentGrades(student)).andReturn(gradesIterator).once();
		
		double product = 8.5 * 7.25 * 7;
        double expectedAverage = Math.pow(product, 1.0 / 3);
		
		replay(factoryMock, gradeDaoMock);

		StudentController studentController = new StudentController(student, factoryMock);
		assertThat(studentController.calculateTotalGradeAverage(new GeometricGradeAverageStrategy())).isEqualTo(expectedAverage);
		
		verify(factoryMock, gradeDaoMock);
	}
	
	@Test
	public void testCalculateTotalGradesAverageWithWeightedAverage_StudentController() throws StudentDaoException, GradeDaoException, SchoolClassDaoException, DaoConnectionException {
		TeachingAssignment teachingAssignment = new TeachingAssignment(1, null, null, null);
		ArrayList<Grade> grades = new ArrayList<Grade>();
		Grade g1 = new Grade(0, student, teachingAssignment, null, 8.5, 2, null);
		Grade g2 = new Grade(0, student, teachingAssignment, null, 7.25, 3, null);
		Grade g3 = new Grade(0, student, teachingAssignment, null, 7, 5, null);
		grades.add(g1);
		grades.add(g2);
		grades.add(g3);
		Iterator<Grade> gradesIterator = grades.iterator();

		expect(gradeDaoMock.getAllStudentGrades(student)).andReturn(gradesIterator).once();
		
		double weightedSum = (8.5 * 2) + (7.25 * 3) + (7 * 5);
     	double totalWeight = 2 + 3 + 5;
     	double expectedAverage = weightedSum / totalWeight;
		
		replay(factoryMock, gradeDaoMock);

		StudentController studentController = new StudentController(student, factoryMock);
		assertThat(studentController.calculateTotalGradeAverage(new WeightedGradeAverageStrategy())).isEqualTo(expectedAverage);
		
		verify(factoryMock, gradeDaoMock);
	}
	
	@Test
    public void testCalculateStudentTeachingGradeAverage_TeacherController() throws GradeDaoException, DaoConnectionException, StudentDaoException, TeachingAssignmentDaoException {
        ArrayList<Grade> grades = new ArrayList<>();
		Grade g1 = new Grade(0, student, teachingAssignment, null, 8, 1, null);
		Grade g2 = new Grade(0, student, teachingAssignment, null, 7, 1, null);
		Grade g3 = new Grade(0, student, teachingAssignment, null, 9, 1, null);
		grades.add(g1);
		grades.add(g2);
		grades.add(g3);
        Iterator<Grade> gradesIterator = grades.iterator();

        expect(gradeDaoMock.getStudentGradesByTeaching(student, teachingAssignment)).andReturn(gradesIterator).once();
        
        replay(factoryMock, gradeDaoMock);

		double average = teacherController.calculateStudentTeachingGradeAverage(student, teachingAssignment, new ArithmeticGradeAverageStrategy());

        assertThat(average).isEqualTo(8.0);

        verify(factoryMock, gradeDaoMock);
    }
}
