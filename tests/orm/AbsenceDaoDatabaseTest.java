package orm;

import static org.assertj.core.api.Assertions.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Iterator;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import domainModel.Absence;
import domainModel.SchoolClass;
import domainModel.Student;

public class AbsenceDaoDatabaseTest {
	private Connection conn;
	private AbsenceDaoDatabase absenceDao;
	private String url = "jdbc:sqlite:database/testDB.db";
	private int studentId;
	private Student student;
	private SchoolClass schoolClass;
	private Absence absence;

	@Before
	public void setUp() throws Exception {
		conn = DriverManager.getConnection(url);

		absenceDao = new AbsenceDaoDatabase(conn);

		createTestData();

		String getStudentIdQuery = "SELECT id_student FROM Students WHERE username = 'stu001'";
		ResultSet rs = conn.createStatement().executeQuery(getStudentIdQuery);
		rs.next();
		studentId = rs.getInt("id_student");

		schoolClass = new SchoolClass("A1");
		student = new Student("Mario", "Rossi", studentId, schoolClass);
		
		absence = new Absence(student, LocalDate.of(2024, 12, 03), false);
	}

	private void createTestData() throws Exception {
		String deleteAbsencesQuery = "DELETE FROM Absences";
		conn.createStatement().executeUpdate(deleteAbsencesQuery);

		String deleteStudentQuery = "DELETE FROM Students";
		conn.createStatement().executeUpdate(deleteStudentQuery);
		
		String deleteClassesQuery = "DELETE FROM Classes";
		conn.createStatement().executeUpdate(deleteClassesQuery);
		
		String insertStudentQuery = "INSERT INTO Students (username, password, name, surname, date_of_birth, class) "
				+ "VALUES ('stu001', 'pass123', 'Mario', 'Rossi', '2005-03-15', 'A1')";
		conn.createStatement().executeUpdate(insertStudentQuery);

		String insertAbsenceQuery = "INSERT INTO Absences (date, justification, id_student) "
				+ "VALUES ('2024-12-03', 0, (SELECT id_student FROM Students WHERE username = 'stu001'))";
		conn.createStatement().executeUpdate(insertAbsenceQuery);
		
		String insertClassQuery = "INSERT INTO classes (name, classroom) "
				+ "VALUES ('A1', '123')";
		conn.createStatement().executeUpdate(insertClassQuery);
	}

	@Test
	public void testGetAbsenceByStudentInDate() throws Exception {
		Absence absence = absenceDao.getAbsenceByStudentInDate(student, LocalDate.of(2024, 12, 3));

		assertThat(absence).isNotNull();
		assertThat(absence.getStudent().getId()).isEqualTo(student.getId());
		assertThat(absence.getDate()).isEqualTo(LocalDate.of(2024, 12, 3));
		assertThat(absence.isJustified()).isFalse();
	}
	
	@Test
	public void testGetAbsenceByStudentInDate_withWrongData() {
	    LocalDate nonExistentAbsenceDate = LocalDate.of(2024, 12, 6);

		 assertThatThrownBy(() -> absenceDao.getAbsenceByStudentInDate(student, nonExistentAbsenceDate))
	        .isInstanceOf(AbsenceDaoException.class)
	        .hasMessageContaining("No absence found for student in date " + nonExistentAbsenceDate.toString() + ".");
	}

	@Test
	public void testAddAbsance() throws AbsenceDaoException, DaoConnectionException, SQLException {
		LocalDate newAbsenceDate = LocalDate.of(2024, 12, 4);
		absenceDao.addAbsence(student, newAbsenceDate);

		String countQuery = "SELECT COUNT(*) AS total FROM Absences WHERE id_student = " + student.getId();
		ResultSet rs = conn.createStatement().executeQuery(countQuery);

		assertThat(rs.next()).isTrue();
		
		int totalAbsences = rs.getInt("total");
		assertThat(totalAbsences).isEqualTo(2);

		String verifyQuery = "SELECT date FROM Absences WHERE id_student = " + student.getId() + " AND date = '"
				+ newAbsenceDate + "'";
		ResultSet verifyRs = conn.createStatement().executeQuery(verifyQuery);

		assertThat(verifyRs.next()).isTrue();

		String deleteQuery = "DELETE FROM Absences WHERE date = '" + newAbsenceDate + "' AND id_student = "
				+ student.getId();
		conn.createStatement().executeUpdate(deleteQuery);
	}
	
	@Test
	public void testDeleteAbsence() throws AbsenceDaoException, DaoConnectionException, SQLException {
	    LocalDate absenceDate = LocalDate.of(2024, 12, 5);
	    String addAbsenceQuery = "INSERT INTO Absences (date, justification, id_student) VALUES ('" + 
	                             absenceDate + "', 0, " + student.getId() + ")";
	    conn.createStatement().executeUpdate(addAbsenceQuery);

	    String verifyAddedQuery = "SELECT COUNT(*) AS total FROM Absences WHERE id_student = " + student.getId() +
	                              " AND date = '" + absenceDate + "'";
	    ResultSet rsAdded = conn.createStatement().executeQuery(verifyAddedQuery);
	    rsAdded.next();
	    int absencesBeforeDeletion = rsAdded.getInt("total");
	    assertThat(absencesBeforeDeletion).isEqualTo(1); 
	    
	    absenceDao.deleteAbsence(student, absenceDate);

	    String verifyDeletedQuery = "SELECT COUNT(*) AS total FROM Absences WHERE id_student = " + student.getId() +
	                                " AND date = '" + absenceDate + "'";
	    ResultSet rsDeleted = conn.createStatement().executeQuery(verifyDeletedQuery);
	    rsDeleted.next();
	    int absencesAfterDeletion = rsDeleted.getInt("total");
	    assertThat(absencesAfterDeletion).isEqualTo(0);
	}
	
	@Test
	public void testDeleteAbsenceThrowsExceptionWhenAbsenceNotFound() {
	    LocalDate nonExistentAbsenceDate = LocalDate.of(2024, 12, 6);

	    assertThatThrownBy(() -> absenceDao.deleteAbsence(student, nonExistentAbsenceDate))
	        .isInstanceOf(AbsenceDaoException.class)
	        .hasMessageContaining("No absence found to delete for student " + student.getId() + 
	                              " on " + nonExistentAbsenceDate.toString());
	}

	@Test
	public void testGetAbsencesByClassInDate() throws AbsenceDaoException, DaoConnectionException, SQLException {
	    LocalDate absenceDate = LocalDate.of(2024, 12, 3);  
	    
	    Iterator<Absence> absencees = absenceDao.getAbsencesByClassInDate(schoolClass, absenceDate);
	    
	    assertThat(absencees).toIterable().containsExactly(absence);
	}

	@Test
	public void testGetAbsencesByStudent() throws AbsenceDaoException, DaoConnectionException {
		Iterator<Absence> absences = absenceDao.getAbsencesByStudent(student);
		assertThat(absences).toIterable().containsExactly(absence);
	}
	
	@Test
	public void testCheckStudentExist_withStudentDosentExist() {
		Student studentDosentExist = new Student("Studente", "Inesistente", -1, schoolClass);
		 assertThatThrownBy(() -> absenceDao.checkStudentExist(studentDosentExist))
	        .isInstanceOf(AbsenceDaoException.class)
	        .hasMessageContaining("Student doesn't exist.");
	}
	
	@Test
	public void testCheckScoolClassExist() {
		
	}

	@Test
	public void testGetStudentById_withStudentDosentExist() {
		Student studentDosentExist = new Student("Studente", "Inesistente", -1, schoolClass);
		 assertThatThrownBy(() -> absenceDao.getStudentById(studentDosentExist.getId()))
	        .isInstanceOf(AbsenceDaoException.class)
	        .hasMessageContaining("Student doesn't exist.");
	}
	
	@After
	public void tearDown() throws Exception {
		deleteTestData();
		conn.close();
	}

	private void deleteTestData() throws Exception {
		String deleteAbsencesQuery = "DELETE FROM Absences";
		conn.createStatement().executeUpdate(deleteAbsencesQuery);

		String deleteStudentQuery = "DELETE FROM Students";
		conn.createStatement().executeUpdate(deleteStudentQuery);
		
		String deleteClassesQuery = "DELETE FROM Classes";
		conn.createStatement().executeUpdate(deleteClassesQuery);
	}
}
