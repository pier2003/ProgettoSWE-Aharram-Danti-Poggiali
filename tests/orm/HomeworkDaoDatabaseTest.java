package orm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

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

import domainModel.Homework;
import domainModel.SchoolClass;
import domainModel.Teacher;
import domainModel.TeachingAssignment;

public class HomeworkDaoDatabaseTest {
	private Connection conn;
	private HomeworkDao homeworkDao;
	private String url = "jdbc:sqlite:database/testDB.db";
	private Homework homework;
	private TeachingAssignment teaching;
	
	@Before
	public void setUp() throws SQLException {
		conn = DriverManager.getConnection(url);
		homeworkDao = new HomeworkDaoDatabase(conn);
		createTestData();
	}
	
	private void createTestData() throws SQLException {
		String deleteHomeworkQuery = "DELETE FROM Annotations;";
		conn.createStatement().executeUpdate(deleteHomeworkQuery);
		
		String deleteClassesQuery = "DELETE FROM Classes;";
		conn.createStatement().executeUpdate(deleteClassesQuery);
		
		String insertClassQuery = "INSERT INTO Classes (name, classroom) "
				+ "VALUES ('1A', 'A01');";
		conn.createStatement().executeUpdate(insertClassQuery);
		
		
		String insertTeacherQuery = "INSERT INTO Teachers (username, password, name, surname) " + "VALUES (?, ?, ?, ?)";
		try (PreparedStatement pstmt = conn.prepareStatement(insertTeacherQuery)) {
			pstmt.setString(1, "tch001");
			pstmt.setString(2, "pass123");
			pstmt.setString(3, "Casimiro");
			pstmt.setString(4, "Grumaioli");
			pstmt.executeUpdate();
		}
		
		String getTeacherIdQuery = "SELECT id_teacher FROM Teachers WHERE username = 'tch001';";
		ResultSet rs = conn.createStatement().executeQuery(getTeacherIdQuery);
		rs.next();
		int teacherId = rs.getInt("id_teacher");
		
		Teacher teacher = new Teacher(teacherId, "Casimiro", "Grumaioli");
		
		
		
		String insertTeachingQuery = "INSERT INTO Teachings (id_teacher, class_name, subject) " + "VALUES (?, ?, ?)";
		try (PreparedStatement pstmt = conn.prepareStatement(insertTeachingQuery)) {
			pstmt.setInt(1, teacher.getId());
			pstmt.setString(2, "1A");
			pstmt.setString(3, "Matematica");
			pstmt.executeUpdate();
		}
		
		String getTeachingIdQuery = "SELECT id_teaching FROM Teachings WHERE subject = 'Matematica';";
		rs = conn.createStatement().executeQuery(getTeachingIdQuery);
		rs.next();
		int teachingId = rs.getInt("id_teaching");
		
		teaching = new TeachingAssignment(teachingId, "Matematica", teacher, new SchoolClass("1A"));
		

		
		String insertHomeworkQuery = "INSERT INTO Annotations (type, description, date, id_teaching, submission_date) "
				+ "VALUES ('homework', ?, ?, ?, ?)";
		try (PreparedStatement pstmt = conn.prepareStatement(insertHomeworkQuery)) {
			pstmt.setString(1, "Studiare pagina 45");
			pstmt.setDate(2, Date.valueOf("2023-12-11")); 
			pstmt.setInt(3, teaching.getId()); 
			pstmt.setDate(4, Date.valueOf("2023-12-21")); 
			pstmt.executeUpdate();
		}
		
		String getHomeworkIdQuery = "SELECT id_annotation FROM Annotations WHERE description = 'Studiare pagina 45';";
		rs = conn.createStatement().executeQuery(getHomeworkIdQuery);
		rs.next();
		int homeworkId = rs.getInt("id_annotation");
		
		homework = new Homework(homeworkId, teaching, LocalDate.of(2023, 12, 11), 
				"Studiare pagina 45",  LocalDate.of(2023, 12, 21));
		
	}
	
	private int getHomeworkId(String description) throws SQLException {
	    String getHomeworkIdQuery = "SELECT id_annotation FROM Annotations WHERE description = ? AND type = 'homework'";
	    try (PreparedStatement pstmt = conn.prepareStatement(getHomeworkIdQuery)) {
	        pstmt.setString(1, description); 
	        ResultSet rs = pstmt.executeQuery();
	        return rs.getInt("id_annotation");
	    }
	}
	

	@Test
	public void testAddHomework() throws SQLException, HomeworkDaoException {
		LocalDate dateExpected = LocalDate.of(2023, 11, 10);
		LocalDate submissiondDateExpected = LocalDate.of(2023, 11, 10);
		String descriptionExpected = "Compito di prova inserito";
		
		homeworkDao.addHomework(teaching, dateExpected, descriptionExpected, submissiondDateExpected);
		
		int homeworkId = getHomeworkId(descriptionExpected);
		Homework homeworkExpected = new Homework(homeworkId, teaching, dateExpected, descriptionExpected, submissiondDateExpected);
		
		ResultSet rs = conn.prepareStatement("SELECT * FROM Annotations WHERE id_annotation = " + homeworkId + ";").executeQuery();
		assertThat(rs.next()).isTrue();
		
		Homework homeworkActual = new Homework(rs.getInt("id_annotation"),
				teaching,
				rs.getDate("date").toLocalDate(),
				rs.getString("description"),
				rs.getDate("submission_date").toLocalDate()
				);
		
		assertThat(homeworkActual).isEqualTo(homeworkExpected);
		
		assertThat(rs.getInt("id_teaching")).isEqualTo(teaching.getId());
	}
	
	@Test
	public void testDeleteHomework() throws SQLException, HomeworkDaoException {
		homeworkDao.deleteHomework(homework);
		
		ResultSet rs = conn.prepareStatement("SELECT * FROM Annotations WHERE id_annotation = " + homework.getId() + ";").executeQuery();
		assertThat(rs.next()).isFalse();
	}
	
	@Test
	public void testDeleteHomeworkNotFound() throws SQLException, HomeworkDaoException {
	    Homework nonExistentHomework = new Homework(-1, teaching, LocalDate.of(2023, 12, 12), "Compito inesistente", LocalDate.of(2023, 12, 13));
	    
	    assertThrows(HomeworkDaoException.class, () -> 
	        homeworkDao.deleteHomework(nonExistentHomework));
	}
	
	@Test
	public void testGetHomeworksBySubmissionDate() throws SQLException, HomeworkDaoException, SchoolClassDaoException, TeachingAssignmentDaoException {

	    String annotationQuery = "INSERT INTO Annotations (id_teaching, date, description, submission_date, type) " +
	                             "VALUES (?, ?, ?, ?, ?)";
	    try (PreparedStatement pstmt = conn.prepareStatement(annotationQuery)) {
	        pstmt.setInt(1, teaching.getId());
	        pstmt.setDate(2, Date.valueOf("2023-12-01"));
	        pstmt.setString(3, "Secondo Homework");
	        pstmt.setDate(4, Date.valueOf("2023-12-21"));
	        pstmt.setString(5, "homework");
	        pstmt.executeUpdate();

	        pstmt.setInt(1, teaching.getId());
	        pstmt.setDate(2, Date.valueOf("2023-12-02"));
	        pstmt.setString(3, "Terzo Homework, non verrà selezionato dal metodo per submission date");
	        pstmt.setDate(4, Date.valueOf("2023-12-11"));
	        pstmt.setString(5, "homework");
	        pstmt.executeUpdate();
	    }

	    Homework homework2 = new Homework(getHomeworkId("Secondo Homework"), teaching, LocalDate.of(2023, 12, 1), "Secondo Homework", 
	    		LocalDate.of(2023, 12, 21));

	    Iterator<Homework> iteratorHomeworksActual = homeworkDao.getHomeworksBySubmissionDate(LocalDate.of(2023, 12, 21), new SchoolClass("1A"));
	    List<Homework> homeworksActualList = new ArrayList<>();
	    while (iteratorHomeworksActual.hasNext()) {
	        homeworksActualList.add(iteratorHomeworksActual.next());
	    }

	    List<Homework> homeworksExpected = List.of(homework, homework2);

	    assertThat(homeworksActualList).containsExactlyInAnyOrderElementsOf(homeworksExpected);
	}
	
	@Test
	public void testGetHomeworksBySubmissionDateForNonExistentClass() throws SQLException {
	    SchoolClass nonExistentClass = new SchoolClass("1Z");
	    
	    assertThrows(SchoolClassDaoException.class, () -> 
	        homeworkDao.getHomeworksBySubmissionDate(LocalDate.of(2023, 12, 21), nonExistentClass));
	}
	
	@Test
	public void testEditHomeworkDescription() throws SQLException, HomeworkDaoException {
	    String newDescription = "Descrizione modificata";

	    homeworkDao.editHomeworkDescription(homework, newDescription);

	    String query = "SELECT * FROM Annotations WHERE id_annotation = " + homework.getId() + ";";
	    try (ResultSet rs = conn.prepareStatement(query).executeQuery()) {
	        rs.next();
	        assertThat(rs.getString("description")).isEqualTo(newDescription);
	    }
	}
	
	
	@Test
	public void testEditHomeworkSubmissionDate() throws SQLException, HomeworkDaoException {
	    LocalDate newSubmissionDate = LocalDate.of(2023, 12, 15);

	    homeworkDao.editHomeworkSubmissionDate(homework, newSubmissionDate);

	    String query = "SELECT * FROM Annotations WHERE id_annotation = " + homework.getId() + ";";
	    try (ResultSet rs = conn.prepareStatement(query).executeQuery()) {
	        rs.next();
	        assertThat(rs.getDate("submission_date").toLocalDate()).isEqualTo(newSubmissionDate);
	    }
	}



	@After
	public void closeConnection() throws SQLException {
		deleteTestData();
		conn.close();
	}
	
	private void deleteTestData() throws SQLException {
		String deleteHomeworksQuery = "DELETE FROM Annotations;";
		conn.createStatement().executeUpdate(deleteHomeworksQuery);
		
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
