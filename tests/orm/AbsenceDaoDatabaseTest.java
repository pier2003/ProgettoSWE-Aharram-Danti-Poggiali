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
	private Absence absence;
	private Student student;

	@Before
	public void setUp() throws Exception {
		conn = DriverManager.getConnection(url);

		absenceDao = new AbsenceDaoDatabase(conn);

		createTestData();
	}
	
	private void createTestData() throws SQLException {
	    deleteTestData();

	    String insertClassQuery = "INSERT INTO Classes (name, classroom) VALUES ('A1', '123')";
	    conn.createStatement().executeUpdate(insertClassQuery);

	    String insertStudentQuery = "INSERT INTO Students (username, password, name, surname, date_of_birth, class) "
	            + "VALUES ('stu001', 'pass123', 'Mario', 'Rossi', '2005-03-15', 'A1')";
	    conn.createStatement().executeUpdate(insertStudentQuery);

	    String getStudentIdQuery = "SELECT * FROM Students WHERE username = 'stu001'";
	    ResultSet rs = conn.createStatement().executeQuery(getStudentIdQuery);
	    int studentId = -1;
	    if (rs.next()) {
	        studentId = rs.getInt("id_student");
	    }
	    
	    student = new Student(studentId, 
	    		rs.getString("name"), 
	    		rs.getString("surname"), 
	    		new SchoolClass("A1"));

	    String insertAbsenceQuery = "INSERT INTO Absences (date, justification, id_student) VALUES (?, ?, ?)";
	    try (PreparedStatement stmt = conn.prepareStatement(insertAbsenceQuery)) {
	        stmt.setDate(1, Date.valueOf("2023-11-03"));
	        stmt.setInt(2, 0);
	        stmt.setInt(3, studentId);
	        stmt.executeUpdate();
	    }

	    absence = new Absence(student, LocalDate.of(2023, 11, 3), false);
	}
	
	
	@Test
	public void testAddAbsence() throws SQLException, AbsenceDaoException, DaoConnectionException, StudentDaoException {
		StudentDaoDatabase studentDaoDatabase = new StudentDaoDatabase(conn);
		
		Absence expectedAbsence = new Absence(student, LocalDate.of(2023, 11, 2), false);
		absenceDao.addAbsence(student, expectedAbsence.getDate());
		
		String selectAbsence = "SELECT id_student, date, justification FROM Absences WHERE date = ?";
		
		try (PreparedStatement stmt = conn.prepareStatement(selectAbsence)) {
			stmt.setDate(1, Date.valueOf(expectedAbsence.getDate()));
			
			try (ResultSet rs = stmt.executeQuery()) {
				assertThat(rs.next()).isTrue();
				
				LocalDate date = rs.getDate("date").toLocalDate();
				boolean justification = rs.getBoolean("justification");
				Student actualStudent = studentDaoDatabase.getStudentById(rs.getInt("id_student"));
				Absence actualAbsence = new Absence(actualStudent, date, justification);
				
				assertThat(conn.prepareStatement("SELECT COUNT(*) AS total FROM Absences")
						.executeQuery().getInt("total")).isEqualTo(2);
				
				assertThat(actualAbsence).isEqualTo(expectedAbsence);
			}
		}
	}
	
	@Test
	public void testAddAbsence_ThrowsExceptionForNonExistentStudent() {
	    Student nonExistentStudent = new Student(-1, "Inesistente", "Studente", student.getSchoolClass());

	    assertThatThrownBy(() -> absenceDao.addAbsence(nonExistentStudent, LocalDate.of(2023, 11, 3)))
	        .isInstanceOf(StudentDaoException.class);
	}
	
	@Test
	public void testDeleteAbsence() throws SQLException, AbsenceDaoException, DaoConnectionException, StudentDaoException {
	    absenceDao.deleteAbsence(absence.getStudent(), absence.getDate());

	    String selectAbsence = "SELECT id_student, date, justification FROM Absences "
	    		+ "WHERE date = ? AND id_student = ?";
	    
	    try (PreparedStatement stmt = conn.prepareStatement(selectAbsence)) {
	        stmt.setDate(1, Date.valueOf(absence.getDate()));
	        stmt.setInt(2, absence.getStudent().getId());
	        
	        try (ResultSet rs = stmt.executeQuery()) {
	            assertThat(rs.next()).isFalse(); 
	        }
	    }
	}
	
	
	@Test
	public void testDeleteAbsence_ThrowsExceptionForNonExistentAbsence() {
	    LocalDate nonExistentAbsenceDate = LocalDate.of(2023, 11, 10);

	    assertThatThrownBy(() -> absenceDao.deleteAbsence(student, nonExistentAbsenceDate))
	        .isInstanceOf(AbsenceDaoException.class);
	}

	
	@Test
	public void testGetAbsencesByStudent() throws SQLException, AbsenceDaoException, DaoConnectionException, StudentDaoException {
	    String insertAbsence = "INSERT INTO Absences (date, justification, id_student) VALUES (?, 0, ?)";
	    try (PreparedStatement stmt = conn.prepareStatement(insertAbsence)) {
	        stmt.setDate(1, Date.valueOf(LocalDate.of(2023, 11, 5)));
	        stmt.setInt(2, student.getId());
	        stmt.executeUpdate();
	    }

	    Absence firstAbsence = new Absence(student, LocalDate.of(2023, 11, 3), false);
	    Absence secondAbsence = new Absence(student, LocalDate.of(2023, 11, 5), false);
	    List<Absence> expectedAbsences = List.of(firstAbsence, secondAbsence);

	    Iterator<Absence> actualAbsencesIterator = absenceDao.getAbsencesByStudent(student);

	    List<Absence> actualAbsences = new ArrayList<>();
	    actualAbsencesIterator.forEachRemaining(actualAbsences::add);

	    assertThat(actualAbsences).containsExactlyInAnyOrderElementsOf(expectedAbsences);
	}
	
	@Test
	public void testGetAbsencesByStudent_ThrowsExceptionForNonExistentStudent() {
	    Student nonExistentStudent = new Student(-1, "Inesistente", "Studente", student.getSchoolClass());
	    
	    assertThatThrownBy(() -> absenceDao.getAbsencesByStudent(nonExistentStudent))
	        .isInstanceOf(StudentDaoException.class);
	}


	@Test
	public void testGetAbsencesByClassInDate() throws SQLException, AbsenceDaoException, SchoolClassDaoException {
		String insertSecondStudentQuery = "INSERT INTO Students (username, password, name, surname, date_of_birth, class) "
				+ "VALUES ('stu002', 'pass456', 'Luca', 'Bianchi', '2005-04-22', 'A1')";
		conn.createStatement().executeUpdate(insertSecondStudentQuery);

		String getSecondStudentIdQuery = "SELECT * FROM Students WHERE username = 'stu002'";
		int secondStudentId = conn.createStatement().executeQuery(getSecondStudentIdQuery).getInt("id_student");

		Student secondStudent = new Student(secondStudentId, "Luca", "Bianchi", new SchoolClass("A1"));

		String insertAbsence = "INSERT INTO Absences (date, justification, id_student) VALUES (?, 0, ?)";
		try (PreparedStatement stmt = conn.prepareStatement(insertAbsence)) {
			stmt.setDate(1, Date.valueOf(absence.getDate()));
			stmt.setInt(2, secondStudent.getId());
			stmt.executeUpdate();
		}

		Absence secondAbsence = new Absence(secondStudent, absence.getDate(), false);
		List<Absence> expectedAbsences = List.of(absence, secondAbsence);
		
		Iterator<Absence> actualAbsencesIterator = absenceDao
				.getAbsencesByClassInDate(absence.getStudent().getSchoolClass(), absence.getDate());

		List<Absence> actualAbsences = new ArrayList<>();
		actualAbsencesIterator.forEachRemaining(actualAbsences::add);

		assertThat(actualAbsences).containsExactlyInAnyOrderElementsOf(expectedAbsences);
	}
	
	@Test
	public void testGetAbsencesByClassInDate_ThrowsExceptionForNonExistentClass() throws AbsenceDaoException, DaoConnectionException {
	    SchoolClass nonExistentClass = new SchoolClass("Z9");
	    
	    assertThatThrownBy(() -> absenceDao.getAbsencesByClassInDate(nonExistentClass, LocalDate.of(2023, 11, 3)))
	        .isInstanceOf(SchoolClassDaoException.class);
	}

	

	@Test
	public void testCheckStudentAttendanceInDay() throws AbsenceDaoException, StudentDaoException {
	    LocalDate existingAbsenceDate = absence.getDate();
	    LocalDate nonExistingAbsenceDate = LocalDate.of(2023, 10, 10);
	    
	    assertThat(absenceDao.checkStudentAttendanceInDay(student, existingAbsenceDate)).isTrue();
	
	    assertThat(absenceDao.checkStudentAttendanceInDay(student, nonExistingAbsenceDate)).isFalse();	    
	}
	
	@Test
	public void testCheckStudentAttendanceInDay_ThrowsExceptionForNonExistentStudent() {
	    Student nonExistentStudent = new Student(-1, "Inesistente", "Studente", student.getSchoolClass());

	    assertThatThrownBy(() -> absenceDao.checkStudentAttendanceInDay(nonExistentStudent, LocalDate.of(2023, 11, 3)))
	        .isInstanceOf(StudentDaoException.class);
	}

	@Test
	public void testJustifyAbsence() throws SQLException, AbsenceDaoException, DaoConnectionException {
	    absenceDao.justifyAbsence(absence);
	    
	    String selectAbsenceQuery = "SELECT justification FROM Absences WHERE id_student = ? AND date = ?";
	    try (PreparedStatement stmt = conn.prepareStatement(selectAbsenceQuery)) {
	        stmt.setInt(1, absence.getStudent().getId());
	        stmt.setDate(2, Date.valueOf(absence.getDate()));
	        try (ResultSet rs = stmt.executeQuery()) {
	            assertThat(rs.next()).isTrue(); 
	            assertThat(rs.getBoolean("justification")).isTrue(); 
	        }
	    }
	}

	@Test
	public void testJustifyAbsence_ThrowsExceptionForNonExistentAbsence() {
	    Absence nonExistentAbsence = new Absence(student, LocalDate.of(2023, 11, 10), false);

	    assertThatThrownBy(() -> absenceDao.justifyAbsence(nonExistentAbsence))
	        .isInstanceOf(AbsenceDaoException.class);
	}


	
	
	private void deleteTestData() throws SQLException {
	    String deleteAbsencesQuery = "DELETE FROM Absences";
	    conn.createStatement().executeUpdate(deleteAbsencesQuery);
	    
	    String deleteStudentsQuery = "DELETE FROM Students";
	    conn.createStatement().executeUpdate(deleteStudentsQuery);

	    String deleteClassesQuery = "DELETE FROM Classes";
	    conn.createStatement().executeUpdate(deleteClassesQuery);
	}
	
	
	@After
	public void tearDown() throws Exception {
		deleteTestData();
		conn.close();
	}

}
