package businessLogic;

import static org.easymock.EasyMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
import orm.StudentDaoException;
import orm.TeacherDaoException;
import orm.TeachingAssignmentDaoException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;

public class TeacherControllerTest_Grades {

	private DaoFactory factoryMock;
	private Teacher teacher;
	private TeacherController teacherController;
	private GradeDao gradeDaoMock;
	private Student student;
	private ArrayList<Grade> grades;
	private Grade grade1;
	private Grade grade2;
	private LocalDate date;
	private TeachingAssignment teachingAssignment;
	private SchoolClass schoolClass;

	@Before
	public void setup() throws StudentDaoException, TeacherDaoException, DaoConnectionException {
		factoryMock = createMock(DaoFactory.class);
		gradeDaoMock = createMock(GradeDao.class);

		teacher = new Teacher(1, "Mario", "Rossi");

		expect(factoryMock.createGradeDao()).andReturn(gradeDaoMock).anyTimes();

		teacherController = new TeacherController(teacher, factoryMock);
		student = new Student(1, "Mario", "Bianchi", schoolClass);
		date = LocalDate.now();
		schoolClass = new SchoolClass("1A");
		teachingAssignment = new TeachingAssignment(1, "matematica", teacher, schoolClass);
	}
	
	@Test
	public void testGetAllStudentGradesByTeaching() throws GradeDaoException, DaoConnectionException, StudentDaoException, TeachingAssignmentDaoException {
		grades = new ArrayList<>();
		grade1 = new Grade(1, student, teachingAssignment, date, 8, 1, "Ottimo lavoro");
		grade2 = new Grade(2, student, teachingAssignment, date, 4, 1, "Non ha studiato abbastanza");
		grades.add(grade1);
		grades.add(grade2);
		Iterator<Grade> gradesIterator = grades.iterator();

		expect(gradeDaoMock.getStudentGradesByTeaching(student, teachingAssignment)).andReturn(gradesIterator).once();

		replay(factoryMock, gradeDaoMock);

		assertThat(teacherController.getAllStudentGradesByTeaching(student, teachingAssignment)).toIterable()
				.containsExactlyInAnyOrder(grade1, grade2);

		verify(factoryMock, gradeDaoMock);
	}

	@Test
	public void testAssignGradeToStudentInDate_ValidValue() throws Exception {
		double gradeValue = 8.5;
		String description = "Math Test";

		gradeDaoMock.addNewGrade(gradeValue, description, teachingAssignment, student, date);

		replay(factoryMock, gradeDaoMock);

		teacherController.assignGradeToStudentInDate(gradeValue, description, teachingAssignment, student, date);

		verify(factoryMock, gradeDaoMock);
	}

	@Test
	public void testAssignGradeToStudentInDate_InvalidValue() throws Exception {
		double invalideGradeValue = -1;
		String description = "Math Test";

		replay(factoryMock, gradeDaoMock);

		assertThatThrownBy(() -> teacherController.assignGradeToStudentInDate(invalideGradeValue, description,
				teachingAssignment, student, date)).isInstanceOf(InvalidGradeValueException.class);

		verify(factoryMock, gradeDaoMock);
	}

	@Test
	public void testAssignGradeToStudentInDateWithWeight_ValidValue() throws Exception {
		double gradeValue = 8.5;
		int weight = 2;
		String description = "Math Test";

		gradeDaoMock.addNewGradeWithWeight(gradeValue, weight, description, teachingAssignment, student, date);
		expectLastCall().once();

		replay(factoryMock, gradeDaoMock);

		teacherController.assignGradeToStudentInDateWithWeight(gradeValue, weight, description, teachingAssignment,
				student, date);

		verify(factoryMock, gradeDaoMock);
	}

	@Test
	public void testAssignGradeToStudentInDateWithWeight_InvalidGradeValue() throws Exception {
		double invalidGradeValue = -1;
		int weight = 2;
		String description = "Math Test";

		replay(factoryMock);

		assertThatThrownBy(() -> teacherController.assignGradeToStudentInDateWithWeight(invalidGradeValue, weight,
				description, teachingAssignment, student, date)).isInstanceOf(InvalidGradeValueException.class);

		verify(factoryMock);
	}

	@Test
	public void testAssignGradeToStudentInDateWithWeight_NegativeWeight() throws Exception {
		double gradeValue = 8.5;
		int negativeWeight = -2;
		String description = "Math Test";

		replay(factoryMock, gradeDaoMock);

		assertThatThrownBy(() -> teacherController.assignGradeToStudentInDateWithWeight(gradeValue, negativeWeight,
				description, teachingAssignment, student, date)).isInstanceOf(NegativeWeightException.class);

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
	
}