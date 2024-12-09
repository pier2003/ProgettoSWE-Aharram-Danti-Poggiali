package businessLogic;

import static org.easymock.EasyMock.*;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

import daoFactory.DaoFactory;
import domainModel.Absence;
import domainModel.SchoolClass;
import domainModel.Student;
import domainModel.Teacher;
import orm.AbsenceDao;
import orm.AbsenceDaoException;
import orm.DaoConnectionException;
import orm.SchoolClassDaoException;
import orm.StudentDaoException;
import orm.TeacherDaoException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;

public class TeacherControllerTest_Absences {

	private DaoFactory factoryMock;
	private Teacher teacher;
	private TeacherController teacherController;
	private AbsenceDao absenceDaoMock;
	private SchoolClass schoolClass;
	private Student student;
	private LocalDate date;
	private ArrayList<Absence> absences;
	private Absence absence1;
	private Absence absence2;

	@Before
	public void setup() throws StudentDaoException, TeacherDaoException, DaoConnectionException {
		factoryMock = createMock(DaoFactory.class);
		absenceDaoMock = createMock(AbsenceDao.class);

		teacher = new Teacher(1, "Mario", "Rossi");

		expect(factoryMock.createAbsenceDao()).andReturn(absenceDaoMock).anyTimes();
		
		teacherController = new TeacherController(teacher, factoryMock);
				
		date = LocalDate.now();
		
		schoolClass = new SchoolClass("1A");
		student = new Student(1, "Mario", "Bianchi", schoolClass);
		absences = new ArrayList<>();
		absence1 = new Absence(student, date, false);
		absence2 = new Absence(student, date, false);
		absences.add(absence1);
		absences.add(absence2);
	}
	
	@Test
	public void testGetAbsencesByClassInDate()
			throws AbsenceDaoException, DaoConnectionException, StudentDaoException, TeacherDaoException, SchoolClassDaoException {
		Iterator<Absence> absencesIterator = absences.iterator();

		expect(absenceDaoMock.getAbsencesByClassInDate(schoolClass, date)).andReturn(absencesIterator).once();

		replay(factoryMock, absenceDaoMock);

		assertThat(teacherController.getAbsencesByClassInDate(schoolClass, date)).toIterable().containsExactlyInAnyOrder(absence1, absence2);

		verify(factoryMock, absenceDaoMock);
	}

	@Test
	public void testGetAbsencesByStudent()
			throws AbsenceDaoException, DaoConnectionException, StudentDaoException, TeacherDaoException {
		Iterator<Absence> absencesIterator = absences.iterator();

		expect(absenceDaoMock.getAbsencesByStudent(student)).andReturn(absencesIterator).once();
		
		replay(factoryMock, absenceDaoMock);

		assertThat(teacherController.getAbsencesByStudent(student)).toIterable().containsExactlyInAnyOrder(absence1, absence2);

		verify(factoryMock, absenceDaoMock);
	}

	@Test
	public void testAssignAbsenceToStudentOnDate()
			throws AbsenceDaoException, DaoConnectionException, StudentDaoException, TeacherDaoException {
		absenceDaoMock.addAbsence(student, date);

		replay(factoryMock, absenceDaoMock);

		teacherController.assignAbsenceToStudentInDate(student, date);

		verify(factoryMock, absenceDaoMock);
	}

	@Test
	public void testDeleteAbsence()
			throws AbsenceDaoException, DaoConnectionException, StudentDaoException, TeacherDaoException {
		absenceDaoMock.deleteAbsence(student, date);

		replay(factoryMock, absenceDaoMock);

		teacherController.deleteAbsence(student, date);

		verify(factoryMock, absenceDaoMock);
	}
	
}
