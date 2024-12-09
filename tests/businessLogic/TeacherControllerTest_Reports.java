package businessLogic;

import static org.easymock.EasyMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.Before;
import org.junit.Test;

import businessLogic.TeacherController.IllegalReportAccessException;
import daoFactory.DaoFactory;
import domainModel.DisciplinaryReport;
import domainModel.SchoolClass;
import domainModel.Student;
import domainModel.Teacher;
import orm.AbsenceDaoException;
import orm.DaoConnectionException;
import orm.DisciplinaryReportDao;
import orm.DisciplinaryReportException;
import orm.SchoolClassDao;
import orm.StudentDaoException;
import orm.TeacherDaoException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;

public class TeacherControllerTest_Reports {

	private DaoFactory factoryMock;
	private Teacher teacher;
	private SchoolClassDao schoolClassDaoMock;
	private TeacherController teacherController;
	private DisciplinaryReportDao disciplinaryReportDaoMock;

	private SchoolClass schoolClass;
	private Student student;
	private LocalDate date;
	private ArrayList<DisciplinaryReport> reports;
	private DisciplinaryReport report1;
	private DisciplinaryReport report2;

	@Before
	public void setup() throws StudentDaoException, TeacherDaoException, DaoConnectionException {
		factoryMock = createMock(DaoFactory.class);
		disciplinaryReportDaoMock = createMock(DisciplinaryReportDao.class);

		teacher = new Teacher(1, "Mario", "Rossi");

		expect(factoryMock.createSchoolClassDao()).andReturn(schoolClassDaoMock).anyTimes();
		expect(factoryMock.createDisciplinaryReportDao()).andReturn(disciplinaryReportDaoMock).anyTimes();
		
		teacherController = new TeacherController(teacher, factoryMock);
		
		date = LocalDate.now();

		schoolClass = new SchoolClass("1A");
		student = new Student(1, "Mario", "Bianche", schoolClass);

		reports = new ArrayList<>();
		report1 = new DisciplinaryReport(1, student, teacher, date, "Mangia in classe durante la lezione");
		report2 = new DisciplinaryReport(2, student, teacher, date, "Parla col compagno in classe durante la lezione");
		reports.add(report1);
		reports.add(report2);
	}
	
	@Test
	public void testGetStudentDisciplinaryReports() throws DisciplinaryReportException, DaoConnectionException, StudentDaoException {
		Iterator<DisciplinaryReport> reportsIterator = reports.iterator();

		expect(disciplinaryReportDaoMock.getDisciplinaryReportsByStudent(student)).andReturn(reportsIterator).once();

		replay(factoryMock, disciplinaryReportDaoMock);

		assertThat(teacherController.getStudentDisciplinaryReports(student)).toIterable()
				.containsExactlyInAnyOrder(report1, report2);

		verify(factoryMock, disciplinaryReportDaoMock);
	}

	@Test
	public void testAssignDisciplinaryReportToStudentInDate() throws StudentDaoException, TeacherDaoException,
			DaoConnectionException, AbsenceDaoException, DisciplinaryReportException {
		String description = "Arriva in ritardo.";

		disciplinaryReportDaoMock.addNewReport(teacher, student, description, date);

		replay(factoryMock, disciplinaryReportDaoMock);

		teacherController.assignDisciplinaryReportToStudentInDate(student, description, date);

		verify(factoryMock, disciplinaryReportDaoMock);
	}

	@Test
	public void testdeleteDisciplinaryReport() throws IllegalReportAccessException, DisciplinaryReportException {
		disciplinaryReportDaoMock.deleteReport(report1);

		replay(factoryMock, disciplinaryReportDaoMock);

		teacherController.deleteDisciplinaryReport(report1);

		verify(factoryMock, disciplinaryReportDaoMock);
	}

	@Test
	public void testdeleteDisciplinaryReport_WithAnotherTeacher() {
		Teacher otherTeacher = new Teacher(-1, "", "");
		DisciplinaryReport report = new DisciplinaryReport(0, student, otherTeacher, date, "");

		replay(factoryMock);

		assertThatThrownBy(() -> teacherController.deleteDisciplinaryReport(report))
				.isInstanceOf(IllegalReportAccessException.class);

		verify(factoryMock);
	}
	
}
