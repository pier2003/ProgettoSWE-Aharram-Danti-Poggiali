package orm;

import static org.junit.Assert.*; 


import org.junit.Test;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.annotation.Annotation;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import domainModel.TeachingAssignment;

public class LessonDaoDatabaseTest {
	
	//DA RIFARE COMPLETAMENTE

	private Connection conn;
	private String url = "jdbc:sqlite:database/testDB.db";
	private LessonDaoDatabase lessonsDao;
	private TeachingAssignment teachingAssignment;

	@Before
	public void setUp() throws Exception {
		conn = DriverManager.getConnection(url);
		lessonsDao = new LessonDaoDatabase(conn);
		createTestData();

		String getTeachingQuery = "SELECT * FROM Teachings WHERE id_teacher = 1";
		ResultSet rs = conn.createStatement().executeQuery(getTeachingQuery);
		rs.next();
		teachingAssignment = new TeachingAssignment(rs.getInt("id_teacher"), rs.getString("subject"), null,
				new SchoolClass(rs.getString("class_name")));
	}

	private void createTestData() throws Exception {
		String deleteAbsencesQuery = "DELETE FROM Annotations";
		conn.createStatement().executeUpdate(deleteAbsencesQuery);

		String deleteStudentQuery = "DELETE FROM Annotations";
		conn.createStatement().executeUpdate(deleteStudentQuery);

		String deleteClassesQuery = "DELETE FROM Annotations";
		conn.createStatement().executeUpdate(deleteClassesQuery);

		String insertLessonQuery = "INSERT INTO Annotations (id_teaching, type, description, date, start_hour, end_hour, submission_date)"
				+ "VALUES (1,  'lesson', 'Introduzione agli integrali', '2023-11-25', '09:00:00', '10:00:00', NULL)\n";
		conn.createStatement().executeUpdate(insertLessonQuery);
	}

	@Test
	public void testAddLesson() throws LessonDaoException, DaoConnectionException, SQLException {
		LocalDate newLessonDate = LocalDate.of(2024, 12, 6);
		lessonsDao.addLesson(teachingAssignment, newLessonDate, "Adescription", LocalTime.of(8, 30, 00),
				LocalTime.of(9, 30, 00));

		String countQuery = "SELECT COUNT(*) AS total FROM Annotations WHERE id_teaching = 1 AND type = 'lesson'";
		ResultSet rs = conn.createStatement().executeQuery(countQuery);
		assertThat(rs.next()).isTrue();

		int totalLessons = rs.getInt("total");
		assertThat(totalLessons).isEqualTo(2);
		String verifyQuery = "SELECT id_annotation FROM Annotations WHERE id_teaching = 1 AND date= '" + newLessonDate
				+ "';";

		ResultSet verifyRs = conn.createStatement().executeQuery(verifyQuery);
		assertThat(verifyRs.next()).isTrue();

		String deleteQuery = "DELETE FROM Annotations WHERE date= '" + newLessonDate + "' AND id_teaching = 1";
		conn.createStatement().executeUpdate(deleteQuery);
	}

	@Test
	public void testEditLesson() throws LessonDaoException, DaoConnectionException, SQLException {
		// TODO 
		fail();
	}
	
	@After
	public void closeConnection() throws SQLException {
		conn.close();
	}
	
}