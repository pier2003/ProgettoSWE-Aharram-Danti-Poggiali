package businessLogic;

import static org.easymock.EasyMock.*;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

import daoFactory.DaoFactory;
import domainModel.SchoolClass;
import domainModel.Student;
import domainModel.Teacher;
import domainModel.TeachingAssignment;
import orm.DaoConnectionException;
import orm.SchoolClassDao;
import orm.SchoolClassDaoException;
import orm.StudentDao;
import orm.StudentDaoException;
import orm.TeacherDaoException;
import orm.TeachingAssignmentDao;

import java.util.ArrayList;
import java.util.Iterator;

public class TeacherControllerTest_Teacher {

	private DaoFactory factoryMock;
	private StudentDao studentDaoMock;
	private Teacher teacher;
	private SchoolClassDao schoolClassDaoMock;
	private TeacherController teacherController;
	private TeachingAssignmentDao teachingAssignmentDaoMock;

	private ArrayList<TeachingAssignment> teachingAssignments;
	private TeachingAssignment teachingAssignment1;
	private TeachingAssignment teachingAssignment2;
	private ArrayList<SchoolClass> classes;
	private SchoolClass schoolClass1;
	private SchoolClass schoolClass2;
	private ArrayList<Student> students;
	private SchoolClass schoolClassForStudents;
	private Student student2;
	private Student student1;

	@Before
	public void setup() throws StudentDaoException, TeacherDaoException, DaoConnectionException {
		factoryMock = createMock(DaoFactory.class);
		studentDaoMock = createMock(StudentDao.class);
		schoolClassDaoMock = createMock(SchoolClassDao.class);
		teachingAssignmentDaoMock = createMock(TeachingAssignmentDao.class);

		teacher = new Teacher(1, "Mario", "Rossi");

		expect(factoryMock.createSchoolClassDao()).andReturn(schoolClassDaoMock).anyTimes();
		expect(factoryMock.createStudentDao()).andReturn(studentDaoMock).anyTimes();
		expect(factoryMock.createTeachingAssignmentDao()).andReturn(teachingAssignmentDaoMock).anyTimes();
		
		teacherController = new TeacherController(teacher, factoryMock);

		classes = new ArrayList<>();
		schoolClass1 = new SchoolClass("1A");
		schoolClass2 = new SchoolClass("2A");
		classes.add(schoolClass1);
		classes.add(schoolClass2);

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
}