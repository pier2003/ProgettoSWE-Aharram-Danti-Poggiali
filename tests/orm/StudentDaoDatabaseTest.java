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
	private StudentDao studentDao;
	
	
	@Before
	public void openConnection() throws SQLException {
		conn = DriverManager.getConnection(url);
		studentDao = new StudentDaoDatabase(conn);
		insertTestData();
		
	}

	private void insertTestData() throws SQLException {
		deleteTestData();
		
		String insertClassQuery = "INSERT INTO Classes (name, classroom) "
				+ "VALUES ('1A', 'A01');";
		conn.createStatement().executeUpdate(insertClassQuery);
		
		String insertStudentQuery = "INSERT INTO Students (username, password, name, surname, date_of_birth, class) "
				+ "VALUES ('stu001', 'pass123', 'Mario', 'Rossi', '2005-03-15', '1A');";
		conn.createStatement().executeUpdate(insertStudentQuery);
	}
	
	@Test
	public void testGetAllStudents() throws SQLException, TeacherDaoException, DaoConnectionException, StudentDaoException {
		ResultSet rs = conn.prepareStatement("SELECT * FROM Students;").executeQuery();
		List<Student> stuentsExpected = new ArrayList<Student>();
		while (rs.next()) {
			stuentsExpected.add(new Student(rs.getString("name"), 
					rs.getString("surname"), 
					rs.getInt("id_student"), 
					new SchoolClass(rs.getString("class"))));
		}
		ArrayList<Student> studentsActual = new ArrayList<Student>();
		Iterator<Student> iteratorStudents = studentDao.getAllStudents();
		while (iteratorStudents.hasNext()) {
			studentsActual.add(iteratorStudents.next());
		}
		assertThat(studentsActual).containsExactlyInAnyOrderElementsOf(stuentsExpected);
	}
	
	@Test
	public void testGetStudentById() throws SQLException, TeacherDaoException, DaoConnectionException, StudentDaoException {
		String username = "stu001";
		int id = getStudentId(username); 
		ResultSet rs = conn.prepareStatement("SELECT * FROM Students WHERE id_student = " + id + ";").executeQuery();
		rs.next();
		Student studentExpected = new Student(rs.getString("name"), 
				rs.getString("surname"), 
				rs.getInt("id_student"), 
				new SchoolClass(rs.getString("class")));
		Student studentActual = studentDao.getStudentById(id);
		assertThat(studentActual).isEqualTo(studentExpected);		
	}
	
	private int getStudentId(String username) throws SQLException {
		String getStudentIdQuery = "SELECT id_student FROM Students WHERE username = '" + username + "';";
		ResultSet rs = conn.createStatement().executeQuery(getStudentIdQuery);
		rs.next();
		int studentId = rs.getInt("id_student");
		return studentId;
	}
	
	@Test
	public void testGetStudentByInvalidId() throws SQLException {
		int invalidId = -1; 
	    assertThatThrownBy(() -> studentDao.getStudentById(invalidId))
           .isInstanceOf(StudentDaoException.class);
	}
	
	@Test
	public void testGetStudentsByClass() throws SQLException, TeacherDaoException, DaoConnectionException, StudentDaoException {
		String class_name = "1A";
		ResultSet rs = conn.prepareStatement("SELECT * FROM Students WHERE class = '" + class_name + "';").executeQuery();
		List<Student> stuentsExpected = new ArrayList<Student>();
		while (rs.next()) {
			stuentsExpected.add(new Student(rs.getString("name"), 
					rs.getString("surname"), 
					rs.getInt("id_student"), 
					new SchoolClass(rs.getString("class"))));
		}
		ArrayList<Student> studentsActual = new ArrayList<Student>();
		Iterator<Student> iteratorStudents = studentDao.getStudentsByClass(class_name);
		while (iteratorStudents.hasNext()) {
			studentsActual.add(iteratorStudents.next());
		}
		assertThat(studentsActual).containsExactlyInAnyOrderElementsOf(stuentsExpected);	
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
