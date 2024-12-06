package orm;

import static org.assertj.core.api.Assertions.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Iterator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import domainModel.DisciplinaryReport;
import domainModel.SchoolClass;
import domainModel.Student;
import domainModel.Teacher;

public class DisciplinaryReportDaoDatabaseTest {

	private Connection conn;
	private DisciplinaryReportDaoDatabase disciplinaryReportDaoDatabase;
	private String url = "jdbc:sqlite:database/testDB.db";
	private int studentId;
	private int teacherId;
	private Student student;
	private Teacher teacher;
	private DisciplinaryReport disciplinaryReport;

	@Before
	public void setUp() throws Exception {
		conn = DriverManager.getConnection(url);

		disciplinaryReportDaoDatabase = new DisciplinaryReportDaoDatabase(conn);

		createTestData();

		student = new Student("Carlo", "Bianchi", studentId, new SchoolClass("1A"));
		teacher = new Teacher("Giovanna", "Rossi", teacherId);
		disciplinaryReport = new DisciplinaryReport(student, teacher, LocalDate.of(2023, 11, 21), "Ha disturbato durante la verifica");
	}

	private void createTestData() throws SQLException {
		String deleteTeachersQuery = "DELETE FROM Teachers";
		conn.createStatement().executeUpdate(deleteTeachersQuery);

		String deleteStudentQuery = "DELETE FROM Students";
		conn.createStatement().executeUpdate(deleteStudentQuery);

		String deleteReportsQuery = "DELETE FROM Reports";
		conn.createStatement().executeUpdate(deleteReportsQuery);

		String insertTeachersQuery = "INSERT INTO Teachers (username, password, name, surname)"
				+ "VALUES ('T54321', 'teacherPass', 'Giovanna', 'Rossi')";
		conn.createStatement().executeUpdate(insertTeachersQuery);

		String insertStudentsQuery = "INSERT INTO Students (username, password, name, surname, date_of_birth, class) "
				+ "VALUES ('S12345', 'securePass', 'Carlo', 'Bianchi', '2005-08-15', '1A')";
		conn.createStatement().executeUpdate(insertStudentsQuery);

		String getStudentIdQuery = "SELECT id_student FROM Students WHERE username = 'S12345'";
		ResultSet rs = conn.createStatement().executeQuery(getStudentIdQuery);
		rs.next();
		studentId = rs.getInt("id_student");

		String getTeacherIdQuery = "SELECT id_teacher FROM Teachers WHERE username = 'T54321'";
		rs = conn.createStatement().executeQuery(getTeacherIdQuery);
		rs.next();
		teacherId = rs.getInt("id_teacher");

		String insertReportsQuery = "INSERT INTO Reports (description, id_student, id_teacher, date) "
				+ "VALUES ('Ha disturbato durante la verifica', " + studentId + ", " + teacherId +  ", '" +  LocalDate.of(2023, 11, 21).toString() + "')";
		conn.createStatement().executeUpdate(insertReportsQuery);

	}

	@Test
	public void testAddNewReport() throws DisciplinaryReportException, DaoConnectionException, SQLException {
		String newDescription = "Parla durante la lezione";

		disciplinaryReportDaoDatabase.addNewReport(teacher, student, newDescription, LocalDate.of(2023, 11, 21));

		String countQuery = "SELECT COUNT(*) AS total FROM Reports WHERE id_student = " + studentId
				+ " AND id_teacher = " + teacherId;
		ResultSet rs = conn.createStatement().executeQuery(countQuery);

		assertThat(rs.next()).isTrue();
		int totalReports = rs.getInt("total");
		assertThat(totalReports).isEqualTo(2);

		String verifyQuery = "SELECT description FROM Reports WHERE id_student = " + studentId + " AND id_teacher = "
				+ teacherId + " AND description = '" + newDescription + "'";
		ResultSet verifyRs = conn.createStatement().executeQuery(verifyQuery);

		assertThat(verifyRs.next()).isTrue();
		assertThat(verifyRs.getString("description")).isEqualTo(newDescription);

		String deleteQuery = "DELETE FROM Reports WHERE description = '" + newDescription + "' AND id_student = "
				+ studentId + " AND id_teacher = " + teacherId;
		conn.createStatement().executeUpdate(deleteQuery);
	}

	@Test
	public void testGetDisciplinaryReportsByStudent() throws DisciplinaryReportException, DaoConnectionException {
		Iterator<DisciplinaryReport> reports = disciplinaryReportDaoDatabase.getDisciplinaryReportsByStudent(student);
		assertThat(reports).toIterable().containsExactly(disciplinaryReport);
	}

	@Test
	public void testGetTeacherById_withTeahcerDosentExist() {
		int teacherInvalidId = -1;
		assertThatThrownBy(() -> disciplinaryReportDaoDatabase.getTeacherById(teacherInvalidId))
				.isInstanceOf(DisciplinaryReportException.class).hasMessageContaining("Teacher dosen't exist.");
	}
	
	@Test
	public void testCheckStudentExist_withStudentDosentExist() {
		Student studentDosentExist = new Student("Studente", "Inesistente", -1, new SchoolClass("1A"));
		 assertThatThrownBy(() -> disciplinaryReportDaoDatabase.checkStudentExist(studentDosentExist))
	        .isInstanceOf(DisciplinaryReportException.class)
	        .hasMessageContaining("Student doesn't exist.");
	}
	
	@After
	public void tearDown() throws Exception {
		deleteTestData();
		conn.close();
	}

	private void deleteTestData() throws Exception {
		String deleteTeachersQuery = "DELETE FROM Teachers";
		conn.createStatement().executeUpdate(deleteTeachersQuery);

		String deleteStudentQuery = "DELETE FROM Students";
		conn.createStatement().executeUpdate(deleteStudentQuery);

		String deleteReportsQuery = "DELETE FROM Reports";
		conn.createStatement().executeUpdate(deleteReportsQuery);
	}

}
