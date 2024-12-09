package orm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import domainModel.SchoolClass;
import domainModel.Student;

public class StudentDaoDatabaseTest {

	private Connection conn;
	private String url = "jdbc:sqlite:database/testDB.db";
	private StudentDaoDatabase studentDao;
	private Student student;
	private int studentId;
	private SchoolClass schoolClass;

	@Before
	public void openConnection() throws SQLException {
		conn = DriverManager.getConnection(url);
		studentDao = new StudentDaoDatabase(conn);
		insertTestData();

	}

	private void insertTestData() throws SQLException {
		deleteTestData();

		String insertClassQuery = "INSERT INTO Classes (name, classroom) " + "VALUES ('1A', 'A01');";
		conn.createStatement().executeUpdate(insertClassQuery);

		String insertStudentQuery = "INSERT INTO Students (username, password, name, surname, date_of_birth, class) "
				+ "VALUES ('stu001', 'pass123', 'Mario', 'Rossi', '2005-03-15', '1A');";
		conn.createStatement().executeUpdate(insertStudentQuery);

		String getStudentIdQuery = "SELECT * FROM Students WHERE username = 'stu001'";
		ResultSet rs = conn.createStatement().executeQuery(getStudentIdQuery);
		if (rs.next()) {
			studentId = rs.getInt("id_student");
		}

		schoolClass = new SchoolClass("1A");

		student = new Student(studentId, rs.getString("name"), rs.getString("surname"), schoolClass);
	}

	@Test
	public void testGetStudentById()
			throws SQLException, TeacherDaoException, DaoConnectionException, StudentDaoException {
		Student studentActual = studentDao.getStudentById(studentId);

		assertThat(studentActual).isEqualTo(student);
	}

	@Test
	public void testGetStudentByInvalidId() throws SQLException {
		int invalidId = -1;
		assertThatThrownBy(() -> studentDao.getStudentById(invalidId)).isInstanceOf(StudentDaoException.class);
	}
	
	@Test
	public void testGetStudentByUsernameAndPassword() throws StudentDaoException {
	    Student studentFromDb = studentDao.getStudentByUsernameAndPassword("stu001", "pass123");
	    assertThat(studentFromDb).isEqualTo(student);
	}

	@Test
	public void testGetStudentByUsernameAndPassword_withInvalidUsername() {
	    assertThatThrownBy(() -> studentDao.getStudentByUsernameAndPassword("wrongUser", "pass123"))
	        .isInstanceOf(StudentDaoException.class)
	        .hasMessageContaining("Student with username wrongUser doesn't exist.");
	}

	@Test
	public void testGetStudentByUsernameAndPassword_withInvalidPassword() {
	    assertThatThrownBy(() -> studentDao.getStudentByUsernameAndPassword("stu001", "wrongPassword"))
	        .isInstanceOf(StudentDaoException.class)
	        .hasMessageContaining("Student with username stu001 doesn't exist.");
	}

	@Test
	public void testGetStudentByUsernameAndPassword_withInvalidCredentials() {
	    assertThatThrownBy(() -> studentDao.getStudentByUsernameAndPassword("wrongUser", "wrongPassword"))
	        .isInstanceOf(StudentDaoException.class)
	        .hasMessageContaining("Student with username wrongUser doesn't exist.");
	}


	@Test
	public void testGetStudentsByClass()
			throws SQLException, StudentDaoException, DaoConnectionException, SchoolClassDaoException {
		String insertSecondStudentQuery = "INSERT INTO Students (username, password, name, surname, date_of_birth, class) "
				+ "VALUES ('stu002', 'pass456', 'Luca', 'Bianchi', '2005-04-22', '1A')";
		conn.createStatement().executeUpdate(insertSecondStudentQuery);

		String getSecondStudentIdQuery = "SELECT * FROM Students WHERE username = 'stu002'";
		int secondStudentId = conn.createStatement().executeQuery(getSecondStudentIdQuery).getInt("id_student");

		Student secondStudent = new Student(secondStudentId, "Luca", "Bianchi", schoolClass);

		List<Student> expectedStudents = List.of(student, secondStudent);

		Iterator<Student> actualStudentsIterator = studentDao.getStudentsByClass(schoolClass);

		List<Student> actualStudents = new ArrayList<>();
		actualStudentsIterator.forEachRemaining(actualStudents::add);

		assertThat(actualStudents).containsExactlyInAnyOrderElementsOf(expectedStudents);
	}

	@Test
	public void testGetStudentsByClass_ThrowsExceptionForNonExistentClass()
			throws AbsenceDaoException, DaoConnectionException {
		SchoolClass nonExistentClass = new SchoolClass("Z9");

		assertThatThrownBy(() -> studentDao.getStudentsByClass(nonExistentClass))
				.isInstanceOf(SchoolClassDaoException.class);
	}

	@After
	public void closeConnection() throws SQLException {
		deleteTestData();
		conn.close();
	}

	private void deleteTestData() throws SQLException {
		String deleteStudentsQuery = "DELETE FROM Students;";
		conn.createStatement().executeUpdate(deleteStudentsQuery);

		String deleteClassesQuery = "DELETE FROM Classes;";
		conn.createStatement().executeUpdate(deleteClassesQuery);
	}

}
