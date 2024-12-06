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
import utils.TestDataUtil;

public class SchoolClassDaoDatabaseTest {
	private Connection conn;
	private String url = "jdbc:sqlite:database/testDB.db";
	private SchoolClassDao schoolClassDao;
	
	@Before
	public void openConnection() throws SQLException {
		conn = DriverManager.getConnection(url);
		schoolClassDao = new SchoolClassDaoDatabase(conn);
		insertTestdata();
		
	}

	private void insertTestdata() throws SQLException {
		String deleteStudentsQuery = "DELETE FROM Students;";
		conn.createStatement().executeUpdate(deleteStudentsQuery);
		
		String deleteTeachingsQuery = "DELETE FROM Teachings;";
		conn.createStatement().executeUpdate(deleteTeachingsQuery);
		
		String deleteTeachersQuery = "DELETE FROM Teachers;";
		conn.createStatement().executeUpdate(deleteTeachersQuery);
		
		String deleteClassesQuery = "DELETE FROM Classes;";
		conn.createStatement().executeUpdate(deleteClassesQuery);
		
		String insertClassQuery = "INSERT INTO Classes (name, classroom) VALUES ('1A', 'A01');";
		conn.createStatement().executeUpdate(insertClassQuery);
		
		String insertStudentQuery = "INSERT INTO Students (username, password, name, surname, date_of_birth, class) "
				+ "VALUES ('stu001', 'pass123', 'Mario', 'Rossi', '2005-03-15', '1A');";
		conn.createStatement().executeUpdate(insertStudentQuery);
		
		String insertTeacherQuery = "INSERT INTO Teachers (username, password, name, surname) "
				+ "VALUES  ('tch001', 'pass123', 'Casimiro', 'Grumaioli');";
		conn.createStatement().executeUpdate(insertTeacherQuery);
		
		String insertTeachingQuery = "INSERT INTO Teachings (id_teacher, class_name, subject) "
				+ "VALUES  (1, '1A', 'Matematica');";
		conn.createStatement().executeUpdate(insertTeachingQuery);
		
	}
	
	@Test
	public void testGetSchoolClassByName() throws SQLException, SchoolClassDaoException, DaoConnectionException {
		String name = "1A"; 
		ResultSet rs = conn.prepareStatement("SELECT * FROM Classes WHERE name = '" +name+ "';").executeQuery();
		rs.next();
		SchoolClass schoolClassExpected = new SchoolClass(rs.getString("name"));
		SchoolClass  schoolClassActual = schoolClassDao.getSchoolClassByName(name);
		assertThat(schoolClassActual).isEqualTo(schoolClassExpected);	
	}
	
	@Test
	public void testGetSchoolClassByStudent() throws StudentDaoException, SQLException, SchoolClassDaoException, DaoConnectionException {
		String username = "stu001";
		int id = getStudentId(username); 
		StudentDaoDatabase studentDaoDatabase = new StudentDaoDatabase(conn);
		Student student = studentDaoDatabase.getStudentById(id);
		ResultSet rs = conn.prepareStatement("SELECT * FROM Students WHERE id_student = " + student.getId() + ";").executeQuery();
		rs.next();
		SchoolClass schoolClassExpected = new SchoolClass(rs.getString("class"));
		SchoolClass schoolClassActual = schoolClassDao.getSchoolClassByStudent(student);
		assertThat(schoolClassActual.getClassName()).isEqualTo(schoolClassExpected.getClassName());			
	}
	
	private int getStudentId(String username) throws SQLException {
		String getStudentIdQuery = "SELECT id_student FROM Students WHERE username = '" + username + "';";
		ResultSet rs = conn.createStatement().executeQuery(getStudentIdQuery);
		rs.next();
		int studentId = rs.getInt("id_student");
		return studentId;
	}
	
	@Test
	public void testGetAllSchoolClassesByTeacher() throws SQLException, SchoolClassDaoException, DaoConnectionException, TeacherDaoException {
		TeacherDao teacherDao = new TeacherDaoDatabase(conn);
		String username = "tch001";
		int teacherId = getTeacherId(username);
		Teacher teacher = teacherDao.getTeacherById(teacherId);
		List<SchoolClass> classesExpectedList = new ArrayList<SchoolClass>();
		ResultSet rs = conn.prepareStatement("SELECT class_name FROM Teachers NATURAL JOIN Teachings WHERE id_teacher = " + teacher.getId() + ";").executeQuery();
		while(rs.next()) {
			classesExpectedList.add(new SchoolClass(rs.getString("class_name")));
		}
		Iterator<SchoolClass> classesActual = schoolClassDao.getAllSchoolClassesByTeacher(teacher);
		List<SchoolClass> classesActualList = new ArrayList<SchoolClass>();
		classesActual.forEachRemaining(schoolClass -> classesActualList.add(schoolClass));
		assertThat(classesActualList).containsExactlyInAnyOrderElementsOf(classesExpectedList);
		
	}
	
	private int getTeacherId(String username) throws SQLException {
		String getTeacherIdQuery = "SELECT id_teacher FROM Teachers WHERE username = '" + username + "';";
		ResultSet rs = conn.createStatement().executeQuery(getTeacherIdQuery);
		rs.next();
		int teacherId = rs.getInt("id_teacher");
		return teacherId;
	}
	
	@After
	public void close() throws SQLException {
		deleteTestData();
		conn.close();
	}

	private void deleteTestData() throws SQLException {
		String deleteTeachingsQuery = "DELETE FROM Teachings;";
		conn.createStatement().executeUpdate(deleteTeachingsQuery);
		
		String deleteStudentsQuery = "DELETE FROM Students;";
		conn.createStatement().executeUpdate(deleteStudentsQuery);

		String deleteTeachersQuery = "DELETE FROM Teachers;";
		conn.createStatement().executeUpdate(deleteTeachersQuery);
		
		String deleteClassesQuery = "DELETE FROM Classes;";
		conn.createStatement().executeUpdate(deleteClassesQuery);
	}
	
}