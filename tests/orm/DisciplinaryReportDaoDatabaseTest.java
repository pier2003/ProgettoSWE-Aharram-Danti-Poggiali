package orm;

import static org.assertj.core.api.Assertions.*;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
	private Student student;
	private Teacher teacher;
	private DisciplinaryReport disciplinaryReport;

	@Before
	public void setUp() throws Exception {
		conn = DriverManager.getConnection(url);

		disciplinaryReportDaoDatabase = new DisciplinaryReportDaoDatabase(conn);

		createTestData();
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
		int studentId = rs.getInt("id_student");
		
		student = new Student(studentId, "Carlo", "Bianchi", new SchoolClass("1A"));


		String getTeacherIdQuery = "SELECT id_teacher FROM Teachers WHERE username = 'T54321'";
		rs = conn.createStatement().executeQuery(getTeacherIdQuery);
		rs.next();
		int teacherId = rs.getInt("id_teacher");

		teacher = new Teacher(teacherId, "Giovanna", "Rossi");

		
		String insertReportsQuery = "INSERT INTO Reports (description, id_student, id_teacher, date) VALUES (?, ?, ?, ?)";
		try (PreparedStatement stmt = conn.prepareStatement(insertReportsQuery)) {
		    stmt.setString(1, "Ha disturbato durante la verifica"); 
		    stmt.setInt(2, studentId);                              
		    stmt.setInt(3, teacherId);                             
		    stmt.setDate(4, Date.valueOf(LocalDate.of(2023, 11, 21))); 
		    stmt.executeUpdate();                                   
		}

		
		String getReportIdQuery = "SELECT id_report FROM Reports WHERE description = 'Ha disturbato durante la verifica'";
		rs = conn.createStatement().executeQuery(getReportIdQuery);
		rs.next();
		int reportId = rs.getInt("id_report");

		disciplinaryReport = new DisciplinaryReport(reportId, student, teacher, LocalDate.of(2023, 11, 21), "Ha disturbato durante la verifica");


	}

	@Test
	public void testAddNewReport() throws DisciplinaryReportException, SQLException, StudentDaoException, TeacherDaoException {
		DisciplinaryReport expectedReport = new DisciplinaryReport(0, student, teacher, LocalDate.of(2023, 11, 21) , "Parla durante la lezione");

		disciplinaryReportDaoDatabase.addNewReport(teacher, student, 
				expectedReport.getDescription(), expectedReport.getDate());

		String verifyQuery = "SELECT * FROM Reports WHERE description = '" + expectedReport.getDescription() + "'";
		ResultSet verifyRs = conn.createStatement().executeQuery(verifyQuery);

		assertThat(verifyRs.next()).isTrue();
		
		DisciplinaryReport actualReport = new DisciplinaryReport(verifyRs.getInt("id_report"), 
				new Student(verifyRs.getInt("id_student"), "Carlo", "Bianchi", new SchoolClass("1A")), 
				new Teacher(verifyRs.getInt("id_teacher"), "Giovanna", "Rossi"), 
				verifyRs.getDate("date").toLocalDate(), 
				verifyRs.getString("description"));
		
		assertThat(actualReport).isEqualTo(expectedReport);
	}
	
	@Test
	public void testAddNewReportWithInvalidTeacher() {
	    DisciplinaryReport invalidReport = new DisciplinaryReport(0, student, 
	    		new Teacher(-1, "Invalid", "Teacher"), LocalDate.of(2023, 11, 22), "Parla troppo in classe");

	    assertThatThrownBy(() -> disciplinaryReportDaoDatabase.addNewReport(invalidReport.getTeacher(), 
	    		student, invalidReport.getDescription(), invalidReport.getDate()))
	        .isInstanceOf(TeacherDaoException.class);
	}
	
	@Test
	public void testAddNewReportWithInvalidStudent() {
	    DisciplinaryReport invalidReport = new DisciplinaryReport(0, new Student(-1, "Invalid", "Student", new SchoolClass("1B")), teacher, LocalDate.of(2023, 11, 23), "Fuma durante la pausa");

	    assertThatThrownBy(() -> disciplinaryReportDaoDatabase.addNewReport(teacher, invalidReport.getStudent(), invalidReport.getDescription(), invalidReport.getDate()))
	        .isInstanceOf(StudentDaoException.class);
	}


	@Test
	public void testGetDisciplinaryReportsByStudent() throws DisciplinaryReportException, SQLException, StudentDaoException {
		LocalDate newDate = LocalDate.of(2023, 11, 17);
		
		try (PreparedStatement statement = conn.prepareStatement(
		             "INSERT INTO Reports (id_student, id_teacher, description, date) VALUES (?, ?, ?, ?)")) {

		        statement.setInt(1, disciplinaryReport.getStudent().getId());
		        statement.setInt(2, disciplinaryReport.getTeacher().getId());  
		        statement.setString(3, "Esce durante la lezione.");
		        statement.setDate(4, Date.valueOf(newDate));  
		        
		        statement.executeUpdate();
		    }
		
		String getReportIdQuery = "SELECT id_report FROM Reports WHERE description = 'Esce durante la lezione.'";
		ResultSet rs = conn.createStatement().executeQuery(getReportIdQuery);
		rs.next();
		int report2Id = rs.getInt("id_report");
		
		DisciplinaryReport report2 = new DisciplinaryReport(report2Id, student, teacher, newDate, "Esce durante la lezione.");
		
		List<DisciplinaryReport> expectedReports = List.of(disciplinaryReport, report2);
		
		Iterator<DisciplinaryReport> reportsIterator = disciplinaryReportDaoDatabase.getDisciplinaryReportsByStudent(student);
		List<DisciplinaryReport> actualReports = new ArrayList<DisciplinaryReport>();
		reportsIterator.forEachRemaining(actualReports::add);
		
		assertThat(expectedReports).containsExactlyInAnyOrderElementsOf(actualReports);
	}
	
	@Test
	public void testGetDisciplinaryReportsByStudent_invalidStudent() {
	    Student newStudent = new Student(-1, "Luca", "Verdi", new SchoolClass("2B"));
	    
	    assertThatThrownBy(() -> disciplinaryReportDaoDatabase.getDisciplinaryReportsByStudent(newStudent))
	    .isInstanceOf(StudentDaoException.class);
	}

	
	
	@Test
	public void testDeleteReport() throws SQLException, DisciplinaryReportException, DaoConnectionException {
	    disciplinaryReportDaoDatabase.deleteReport(disciplinaryReport);

	    String selectReportQuery = "SELECT id_report FROM Reports WHERE id_report = ?";
	    try (PreparedStatement stmt = conn.prepareStatement(selectReportQuery)) {
	        stmt.setInt(1, disciplinaryReport.getId());
	        
	        try (ResultSet rs = stmt.executeQuery()) {
	            assertThat(rs.next()).isFalse();
	        }
	    }
	}

	
	@Test
	public void testDeleteNonExistentReport() throws SQLException {
	    DisciplinaryReport nonExistentReport = new DisciplinaryReport(-1, student, teacher, LocalDate.of(2023, 11, 22), "Abuso verbale");

	    assertThatThrownBy(() -> disciplinaryReportDaoDatabase.deleteReport(nonExistentReport))
	        .isInstanceOf(DisciplinaryReportException.class);
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
