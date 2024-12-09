package orm;

import static org.junit.Assert.*; 
import org.junit.Test;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.annotation.Annotation;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import domainModel.Absence;
import domainModel.Lesson;
import domainModel.SchoolClass;
import domainModel.Student;
import domainModel.Teacher;
import domainModel.TeachingAssignment;

public class LessonDaoDatabaseTest {
	
	//DA RIFARE COMPLETAMENTE

	private Connection conn;
	private String url = "jdbc:sqlite:database/testDB.db";
	private LessonDaoDatabase lessonDao;
	private TeachingAssignment teachingAssignment;
	private TeachingAssignment teaching;
	private Lesson lesson;
	private SchoolClass schoolClass;

	@Before
	public void setUp() throws SQLException {
		conn = DriverManager.getConnection(url);
		lessonDao = new LessonDaoDatabase(conn);
		createTestData();
	}
	
	private void createTestData() throws SQLException {

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
		
		schoolClass = new SchoolClass("1A");
		teaching = new TeachingAssignment(teachingId, "Matematica", teacher, schoolClass);
		
		String insertLessonQuery = "INSERT INTO Annotations (type, description, date, id_teaching, start_hour, end_hour) "
		        + "VALUES ('lesson', ?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = conn.prepareStatement(insertLessonQuery)) {
		    pstmt.setString(1, "Equazioni differenziali");
		    pstmt.setDate(2, Date.valueOf("2023-12-01"));
		    pstmt.setInt(3, teaching.getId()); 
		    pstmt.setString(4, "10:00");
		    pstmt.setString(5, "11:00");
		    pstmt.executeUpdate();
		}

		String getLessonIdQuery = "SELECT id_annotation FROM Annotations WHERE description = 'Equazioni differenziali';";
		rs = conn.createStatement().executeQuery(getLessonIdQuery);
		rs.next();
		int lessonId = rs.getInt("id_annotation");

		lesson = new Lesson(lessonId, teaching, LocalDate.of(2023, 12, 01), 
		        "Equazioni differenziali", 
		        LocalTime.of(10, 0), LocalTime.of(11, 0));
	}
	
	private int getLessonId(String description) throws SQLException {
	    String getLessonIdQuery = "SELECT id_annotation FROM Annotations WHERE description = ? AND type = 'lesson'";
	    try (PreparedStatement pstmt = conn.prepareStatement(getLessonIdQuery)) {
	        pstmt.setString(1, description); 
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            return rs.getInt("id_annotation");
	        } else {
	            throw new SQLException("No lesson found with the given description.");
	        }
	    }
	}

	@Test
	public void testAddLesson() throws SQLException, LessonDaoException, DaoConnectionException {
	    LocalDate dateExpected = LocalDate.of(2023, 11, 10);
	    String descriptionExpected = "Lezione di prova inserita";
	    LocalTime startHourExpected = LocalTime.of(9, 0);
	    LocalTime endHourExpected = LocalTime.of(10, 0);
	    
	    lessonDao.addLesson(teaching, dateExpected, descriptionExpected, startHourExpected, endHourExpected);
	    
	    int lessonId = getLessonId(descriptionExpected);
	    Lesson lessonExpected = new Lesson(lessonId, teaching, dateExpected, descriptionExpected, startHourExpected, endHourExpected);
	    
	    ResultSet rs = conn.prepareStatement("SELECT * FROM Annotations WHERE id_annotation = " + lessonId + ";").executeQuery();
	    assertThat(rs.next()).isTrue();
	    
	    Lesson lessonActual = new Lesson(
	            rs.getInt("id_annotation"),
	            teaching,
	            rs.getDate("date").toLocalDate(),
	            rs.getString("description"),
	            LocalTime.parse(rs.getString("start_hour")),
	            LocalTime.parse(rs.getString("end_hour"))
	    );

	    assertThat(lessonActual).isEqualTo(lessonExpected);
	    
	    assertThat(rs.getInt("id_teaching")).isEqualTo(teaching.getId());
	}

	@Test
	public void testDeleteLesson() throws SQLException, LessonDaoException {
	    lessonDao.deleteLesson(lesson);
	    
	    ResultSet rs = conn.prepareStatement("SELECT * FROM Annotations WHERE id_annotation = " + lesson.getId() + ";").executeQuery();
	    assertThat(rs.next()).isFalse();
	}

	@Test
	public void testDeleteLessonNotFound() throws SQLException, LessonDaoException {
	    Lesson nonExistentLesson = new Lesson(-1, teaching, LocalDate.of(2023, 12, 12), "Lezione inesistente", LocalTime.of(9, 0), LocalTime.of(10, 0));
	    
	    assertThrows(LessonDaoException.class, () -> 
	        lessonDao.deleteLesson(nonExistentLesson));
	}

	@Test
	public void testEditLessonDescription() throws SQLException, LessonDaoException {
	    String newDescription = "Descrizione modificata";

	    lessonDao.editLessonDescription(lesson, newDescription);

	    String query = "SELECT * FROM Annotations WHERE id_annotation = " + lesson.getId() + " AND type = 'lesson';";
	    try (ResultSet rs = conn.prepareStatement(query).executeQuery()) {
	        rs.next();
	        assertThat(rs.getString("description")).isEqualTo(newDescription);
	    }
	}

	@Test
	public void testEditLessonDateTime() throws SQLException, LessonDaoException {
	    LocalDate newDate = LocalDate.of(2023, 12, 15);
	    LocalTime newStartHour = LocalTime.of(11, 0);
	    LocalTime newEndHour = LocalTime.of(12, 0);

	    lessonDao.editLessonDateTime(lesson, newDate, newStartHour, newEndHour);

	    String query = "SELECT * FROM Annotations WHERE id_annotation = " + lesson.getId() + " AND type = 'lesson';";
	    try (ResultSet rs = conn.prepareStatement(query).executeQuery()) {
	        rs.next();
	        assertThat(rs.getDate("date").toLocalDate()).isEqualTo(newDate);
	        assertThat(rs.getString("start_hour")).isEqualTo(newStartHour.toString());
	        assertThat(rs.getString("end_hour")).isEqualTo(newEndHour.toString());
	    }
	}

	@Test
	public void testGetLessonsByDate() throws SQLException, LessonDaoException, SchoolClassDaoException, TeachingAssignmentDaoException {
	    String annotationQuery = "INSERT INTO Annotations (id_teaching, description, date, start_hour, end_hour, type) " +
	                             "VALUES (?, ?, ?, ?, ?, ?)";
	    try (PreparedStatement pstmt = conn.prepareStatement(annotationQuery)) {
	        pstmt.setInt(1, teaching.getId());
	        pstmt.setString(2, "Prima Lezione");
	        pstmt.setDate(3, Date.valueOf("2023-12-01"));
	        pstmt.setString(4, "09:00");
	        pstmt.setString(5, "10:00");
	        pstmt.setString(6, "lesson");
	        pstmt.executeUpdate();

	        pstmt.setInt(1, teaching.getId());
	        pstmt.setString(2, "Seconda Lezione, non verrà selezionata dal metodo per la data");
	        pstmt.setDate(3, Date.valueOf("2023-12-02"));
	        pstmt.setString(4, "10:00");
	        pstmt.setString(5, "11:00");
	        pstmt.setString(6, "lesson");
	        pstmt.executeUpdate();
	    }
	    
	    Lesson lesson2 = new Lesson(getLessonId("Prima Lezione"), teaching, LocalDate.of(2023, 12, 1), 
	            "Prima Lezione", LocalTime.of(9, 0), LocalTime.of(10, 0));

	    Iterator<Lesson> iteratorLessonsActual = lessonDao.getLessonsInDay(LocalDate.of(2023, 12, 1), schoolClass);
	    List<Lesson> lessonsActualList = new ArrayList<>();
	    while (iteratorLessonsActual.hasNext()) {
	        lessonsActualList.add(iteratorLessonsActual.next());
	    }

	    List<Lesson> lessonsExpected = List.of(lesson, lesson2);

	    assertThat(lessonsActualList).containsExactlyInAnyOrderElementsOf(lessonsExpected);
	}

	@Test
	public void testGetLessonsByDateForNonExistentClass() throws SQLException {
	    SchoolClass nonExistentClass = new SchoolClass("1Z");

	    assertThrows(SchoolClassDaoException.class, () -> 
	        lessonDao.getLessonsInDay(LocalDate.of(2023, 12, 1), nonExistentClass));
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