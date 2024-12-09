package orm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import domainModel.SchoolClass;

public class SchoolClassDaoDatabaseTest {
	private Connection conn;
	private String url = "jdbc:sqlite:database/testDB.db";
	private SchoolClassDaoDatabase schoolClassDao;
	
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
		SchoolClass schoolClassExpected = new SchoolClass(name);
		SchoolClass  schoolClassActual = schoolClassDao.getSchoolClassByName(name);
		assertThat(schoolClassActual).isEqualTo(schoolClassExpected);	
	}
	
	@Test
	public void testGetSchoolClassByName_WithWrongName() throws SQLException, SchoolClassDaoException, DaoConnectionException {
		String name = "2A";
		assertThatThrownBy(() -> schoolClassDao.getSchoolClassByName(name))
		.isInstanceOf(SchoolClassDaoException.class).hasMessage("School class dosen't exist.");
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