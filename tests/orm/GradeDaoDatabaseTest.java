package orm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import domainModel.Grade;
import domainModel.SchoolClass;
import domainModel.Student;
import domainModel.Teacher;
import domainModel.TeachingAssignment;

public class GradeDaoDatabaseTest {
	
	private Connection conn;
	private GradeDao gradeDao;
	private String url = "jdbc:sqlite:database/testDB.db";

	
	@Before
	public void setUp() throws SQLException {
		conn = DriverManager.getConnection(url);
		gradeDao = new GradeDaoDatabase(conn);
		createTestData();
	}
	
	private void createTestData() throws SQLException {
		String deleteGradesQuery = "DELETE FROM Grades;";
		conn.createStatement().executeUpdate(deleteGradesQuery);
		
		String deleteTeachingsQuery = "DELETE FROM Teachings;";
		conn.createStatement().executeUpdate(deleteTeachingsQuery);
		
		String deleteTeachersQuery = "DELETE FROM Teachers;";
		conn.createStatement().executeUpdate(deleteTeachersQuery);

		String deleteStudentsQuery = "DELETE FROM Students;";
		conn.createStatement().executeUpdate(deleteStudentsQuery);
		
		String deleteClassesQuery = "DELETE FROM Classes;";
		conn.createStatement().executeUpdate(deleteClassesQuery);
		
		String insertClassQuery = "INSERT INTO Classes (name, classroom) "
				+ "VALUES ('1A', 'A01');";
		conn.createStatement().executeUpdate(insertClassQuery);
		
		String insertStudentQuery = "INSERT INTO Students (username, password, name, surname, date_of_birth, class) "
				+ "VALUES ('stu001', 'pass123', 'Mario', 'Rossi', '2005-03-15', '1A');";
		conn.createStatement().executeUpdate(insertStudentQuery);
		
		String insertTeacherQuery = "INSERT INTO Teachers (username, password, name, surname) "
				+ "VALUES  ('tch001', 'pass123', 'Casimiro', 'Grumaioli');";
		conn.createStatement().executeUpdate(insertTeacherQuery);
		
		String insertTeachingQuery = "INSERT INTO Teachings (id_teacher, class_name, subject) "
				+ "VALUES  ( " + getTeachingId("tch001") + ", '1A', 'Matematica');";
		conn.createStatement().executeUpdate(insertTeachingQuery);

		String insertGradeQuery = " INSERT INTO Grades (rating, description, date, id_student, id_teaching) "
				+ "VALUES (4, 'Brutto compito', '2023-09-11', " + getStudentId("stu001") + ", " + getTeachingId("Matematica") + ");";
		conn.createStatement().executeUpdate(insertGradeQuery);
	}
	
	private int getStudentId(String username) throws SQLException {
		String getStudentIdQuery = "SELECT id_student FROM Students WHERE username = '" + username + "';";
		ResultSet rs = conn.createStatement().executeQuery(getStudentIdQuery);
		rs.next();
		int studentId = rs.getInt("id_student");
		return studentId;
	}
	
	private int getTeachingId(String subject) throws SQLException {
		String getTeachingIdQuery = "SELECT id_teaching FROM Teachings WHERE subject = '" + subject + "';";
		ResultSet rs = conn.createStatement().executeQuery(getTeachingIdQuery);
		rs.next();
		int teachingID = rs.getInt("id_teaching");
		return teachingID;
	}

	
	@Test
	public void testAddNewGradeWithWeight() throws SQLException, GradeDaoException, DaoConnectionException {
		SchoolClass schoolClass = new SchoolClass("1A");
		Student student = new Student("Mario", "Rossi", 1, schoolClass); 
		Teacher teacher = new Teacher("Casimiro", "Grumaioli", 1);
		TeachingAssignment teaching = new TeachingAssignment(1, "Matematica", teacher, schoolClass); 
		LocalDate date = LocalDate.of(2023, 11, 10);
		
		gradeDao.addNewGradeWithWeight(8.5, 2, "Voto di prova inserito", teaching, student, date);
		
		int gradeId = getGradeId(date.toString());
		
		ResultSet rs = conn.prepareStatement("SELECT * FROM Grades WHERE rating = 8.5 "
				+ "AND weight = 2 AND id_teaching = 1 AND id_student = 1 AND date = '" + date.toString() + "';").executeQuery();
		rs.next();
		assertThat(rs.getInt("id_grade")).isEqualTo(gradeId);
	}
	
	@Test
	public void testDeleteGrade() throws SQLException, GradeDaoException, DaoConnectionException {
		String data = "2023-09-11";
		int gradeId = getGradeId(data);

		gradeDao.deleteGrade(gradeId);
		
		ResultSet rs = conn.prepareStatement("SELECT * FROM Grades WHERE id_grade = " + gradeId + ";").executeQuery();
		assertThat(rs.next()).isFalse();
	}
	
	@Test
	public void testEditGradeValue() throws SQLException, GradeDaoException, DaoConnectionException {
		String data = "2023-09-11";
		int gradeId = getGradeId(data);
		double newValue = 9.5;
		
		gradeDao.editGradeValue(gradeId, newValue);

		ResultSet rs = conn.prepareStatement("SELECT * FROM Grades WHERE id_grade = " + gradeId + ";").executeQuery();
		rs.next();
		assertThat(rs.getDouble("rating")).isEqualTo(newValue);
	}
	
	@Test
	public void testEditGradeWeight() throws SQLException, GradeDaoException, DaoConnectionException {
		String data = "2023-09-11";
		int gradeId = getGradeId(data);
		int newWeight = 5;

		gradeDao.editGradeWeight(gradeId, newWeight);

		ResultSet rs = conn.prepareStatement("SELECT * FROM Grades WHERE id_grade = " + gradeId + ";").executeQuery();
		rs.next();
		assertThat(rs.getInt("weight")).isEqualTo(newWeight);
	}
	
	@Test
	public void testEditGradeDescription() throws SQLException, GradeDaoException, DaoConnectionException {
		String data = "2023-09-11";
		int gradeId = getGradeId(data);
		String newDescription = "Descrizione aggiornata";
		
		gradeDao.editGradeDescription(gradeId, newDescription);

		ResultSet rs = conn.prepareStatement("SELECT description FROM Grades WHERE id_grade = " + gradeId + ";").executeQuery();
		rs.next();
		assertThat(rs.getString("description")).isEqualTo(newDescription);
	}
	
	@Test
	public void testGetGradeById() throws SQLException, GradeDaoException, StudentDaoException, TeachingAssignmentDaoException, DaoConnectionException {
		int gradeId = getGradeId("2023-09-11");
		StudentDao studentDao = new StudentDaoDatabase(conn);
		TeachingAssignmentDao teachingDao = new TeachingAssignmentDaoDatabase(conn);
		
		ResultSet rs = conn.prepareStatement("SELECT * FROM Grades WHERE id_grade = " + gradeId + ";").executeQuery();
		rs.next();
		
		Grade gradeExpected = new Grade(
			rs.getInt("id_grade"),
			studentDao.getStudentById(rs.getInt("id_student")),
			teachingDao.getTeachingById(rs.getInt("id_teaching")),
			LocalDate.parse(rs.getString("date")),
			rs.getDouble("rating"),
			rs.getInt("weight"),
			rs.getString("description")
		);
		
		Grade gradeActual = gradeDao.getGradeById(gradeId);
		assertThat(gradeActual).isEqualTo(gradeExpected);		
	}
	
	private int getGradeId(String data) throws SQLException {
		String getGradeIdQuery = "SELECT id_grade FROM Grades WHERE date = '" + data + "';";
		ResultSet rs = conn.createStatement().executeQuery(getGradeIdQuery);
		rs.next();
		int gradeId = rs.getInt("id_grade");
		return gradeId;
	}
	
	@Test
	public void testGetGradeByInvalidId() throws SQLException {
		int invalidId = -1; 
	    assertThatThrownBy(() -> gradeDao.getGradeById(invalidId))
           .isInstanceOf(GradeDaoException.class);
	}
	
	@Test
	public void testGetAllStudentGrades() throws SQLException, GradeDaoException, StudentDaoException, TeachingAssignmentDaoException, DaoConnectionException {
		String usernameStudent = "stu001";
		int studentId = getStudentId(usernameStudent);
		ResultSet rs = conn.prepareStatement("SELECT * FROM Grades WHERE id_student = " + studentId + ";").executeQuery();
		List<Grade> gradesExpected = new ArrayList<>();
		StudentDao studentDao = new StudentDaoDatabase(conn);
		TeachingAssignmentDao teachingDao = new TeachingAssignmentDaoDatabase(conn);
		
		while (rs.next()) {
			gradesExpected.add(new Grade(
				rs.getInt("id_grade"),
				studentDao.getStudentById(rs.getInt("id_student")),
				teachingDao.getTeachingById(rs.getInt("id_teaching")),
				LocalDate.parse(rs.getString("date")),
				rs.getDouble("rating"),
				rs.getInt("weight"),
				rs.getString("description"))
			);
		}
		Iterator<Grade> iteratorGradesActual = gradeDao.getAllStudentGrades(studentId);
		List<Grade> gradesActualList = new ArrayList<Grade>();
		while (iteratorGradesActual.hasNext()) {
			gradesActualList.add(iteratorGradesActual.next());
		}
		assertThat(gradesActualList).containsExactlyInAnyOrderElementsOf(gradesExpected);
	}
	
	@Test
	public void testGetStudentGradesByTeaching() throws SQLException, GradeDaoException, StudentDaoException, TeachingAssignmentDaoException, DaoConnectionException {
		String usernameStudent = "stu001";
		String subjectTeaching = "Matematica";
		int studentId = getStudentId(usernameStudent); 
		int teachingId = getTeachingId(subjectTeaching); 
		ResultSet rs = conn.prepareStatement("SELECT * FROM Grades WHERE id_student = " + studentId + " AND id_teaching = " + teachingId + ";").executeQuery();
		List<Grade> gradesExpected = new ArrayList<Grade>();
		StudentDao studentDao = new StudentDaoDatabase(conn);
		TeachingAssignmentDao teachingDao = new TeachingAssignmentDaoDatabase(conn);
		while (rs.next()) {
			gradesExpected.add(new Grade(
					rs.getInt("id_grade"),
					studentDao.getStudentById(rs.getInt("id_student")),
					teachingDao.getTeachingById(rs.getInt("id_teaching")),
					LocalDate.parse(rs.getString("date")),
					rs.getDouble("rating"),
					rs.getInt("weight"),
					rs.getString("description"))
				);
		}
		Iterator<Grade> gradesActual = gradeDao.getStudentGradesByTeaching(studentId, teachingId);
		List<Grade> gradesActualList = new ArrayList<>();
		gradesActual.forEachRemaining(gradesActualList::add);
		assertThat(gradesActualList).containsExactlyInAnyOrderElementsOf(gradesExpected);
	}
	
	@After
	public void closeConnection() throws SQLException {
		deleteTestData();
		conn.close();
	}
	
	private void deleteTestData() throws SQLException {
		String deleteGradesQuery = "DELETE FROM Grades;";
		conn.createStatement().executeUpdate(deleteGradesQuery);
		
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
