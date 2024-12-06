package orm;

import static org.assertj.core.api.Assertions.assertThat;

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
import domainModel.Teacher;
import domainModel.TeachingAssignment;

public class TeachingAssignmentDaoDatabaseTest {
	
	private Connection conn;
	private TeachingAssignmentDao teachingAssignmentDao;
	private String url = "jdbc:sqlite:database/testDB.db";
	private String usernameStudent = "stu001";
	private String usernameTeacher = "tch001";

	@Before
	public void openConnection() throws SQLException {
		conn = DriverManager.getConnection(url);
		teachingAssignmentDao = new TeachingAssignmentDaoDatabase(conn);
		createTestData();

	}
	
	private void createTestData() throws SQLException {
		deleteTestData();
		
		String insertClassQuery = "INSERT INTO Classes (name, classroom) VALUES ('1A', 'A01');";
		conn.createStatement().executeUpdate(insertClassQuery);
		
		String insertStudentQuery = "INSERT INTO Students (username, password, name, surname, date_of_birth, class) "
				+ "VALUES ( '" +  usernameStudent + "', 'pass123', 'Mario', 'Rossi', '2005-03-15', '1A');";
		conn.createStatement().executeUpdate(insertStudentQuery);
		
		String insertTeacherQuery = "INSERT INTO Teachers (username, password, name, surname) "
				+ "VALUES  ('" + usernameTeacher + "', 'pass123', 'Casimiro', 'Grumaioli');";
		conn.createStatement().executeUpdate(insertTeacherQuery);
		
		String insertTeachingQuery = "INSERT INTO Teachings (id_teacher, class_name, subject) "
				+ "VALUES  ( " + getTeacherId(usernameTeacher) + ", '1A', 'Matematica');";
		conn.createStatement().executeUpdate(insertTeachingQuery);
	}

	
	@Test
	public void testGetAllStudentTeachings() throws SQLException, TeacherDaoException, DaoConnectionException, TeachingAssignmentDaoException {
		String usernameStudent = "stu001";
		int studentId = getStudentId(usernameStudent);
		String query = "SELECT id_teaching, subject, id_teacher, class_name "
				+ "FROM Students JOIN Classes ON Students.class = Classes.name "
				+ "JOIN Teachings ON Classes.name = Teachings.class_name "
				+ "WHERE id_student = " + studentId + ";";
		ResultSet rs = conn.prepareStatement(query).executeQuery();
		List<TeachingAssignment> teachingsExpected = new ArrayList<TeachingAssignment>();
		
		TeacherDao teacherDao = new TeacherDaoDatabase(conn);
	
		while (rs.next()) {
			teachingsExpected.add(new TeachingAssignment(rs.getInt("id_teaching"), 
					rs.getString("subject"), 
					teacherDao.getTeacherById(rs.getInt("id_teacher")), 
					new SchoolClass("1A")));
		}
		
		Iterator<TeachingAssignment> teachingActual = teachingAssignmentDao.getAllStudentTeachings(studentId);
		List<TeachingAssignment> teachingsActualList = new ArrayList<TeachingAssignment>();
		while (teachingActual.hasNext()) {
			teachingsActualList.add(teachingActual.next());
		}
		assertThat(teachingsActualList).containsExactlyInAnyOrderElementsOf(teachingsExpected);
		
	}
		
	
	
	private int getTeacherId(String username) throws SQLException {
		String getTeacherIdQuery = "SELECT id_teacher FROM Teachers WHERE username = '" + username + "';";
		ResultSet rs = conn.createStatement().executeQuery(getTeacherIdQuery);
		rs.next();
		int teacherID = rs.getInt("id_teacher");
		return teacherID;
	}
	
	private int getStudentId(String username) throws SQLException {
		String getStudentIdQuery = "SELECT id_student FROM Students WHERE username = '" + username + "';";
		ResultSet rs = conn.createStatement().executeQuery(getStudentIdQuery);
		rs.next();
		int studentId = rs.getInt("id_student");
		return studentId;
	}
	
	@Test
	public void testGetTeachingById() throws SQLException, TeachingAssignmentDaoException, DaoConnectionException {
		Teacher teacher = new Teacher("Casimiro", "Grumaioli", getTeacherId(usernameTeacher));
		
		ResultSet rs = conn.prepareStatement("SELECT * FROM Teachings WHERE subject = 'Matematica';").executeQuery();
		rs.next();
		
		int idTeaching = rs.getInt("id_teaching");
		TeachingAssignment teachingExpected = new TeachingAssignment(idTeaching, "Matematica", teacher, new SchoolClass("1A"));
		
		assertThat(teachingAssignmentDao.getTeachingById(idTeaching)).isEqualTo(teachingExpected);
		
	}
	
	@After
	public void closeConnection() throws SQLException {
		deleteTestData();
		conn.close();
		
	}
	
	private void deleteTestData() throws SQLException {
		String deleteTeachingsQuery = "DELETE FROM Teachings;";
		conn.createStatement().executeUpdate(deleteTeachingsQuery);
		
		String deleteTeachersQuery = "DELETE FROM Teachers;";
		conn.createStatement().executeUpdate(deleteTeachersQuery);

		String deleteStudentsQuery = "DELETE FROM Students;";
		conn.createStatement().executeUpdate(deleteStudentsQuery);
		
		String deleteClassesQuery = "DELETE FROM Classes;";
		conn.createStatement().executeUpdate(deleteClassesQuery);
	}
	
	

}
