package orm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

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
	private Grade grade;
	private Student student;
	private TeachingAssignment teaching;
	private Teacher teacher;

	
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
				+ "VALUES (?, ?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = conn.prepareStatement(insertStudentQuery)) {
			pstmt.setString(1, "stu001");
			pstmt.setString(2, "pass123");
			pstmt.setString(3, "Mario");
			pstmt.setString(4, "Rossi");
			pstmt.setDate(5, Date.valueOf("2005-03-15"));
			pstmt.setString(6, "1A");
			pstmt.executeUpdate();
		}
		
		String getStudentIdQuery = "SELECT id_student FROM Students WHERE username = 'stu001';";
		ResultSet rs = conn.createStatement().executeQuery(getStudentIdQuery);
		rs.next();
		int studentId = rs.getInt("id_student");
		
		student = new Student(studentId, "Mario", "Rossi", new SchoolClass("1A"));

		
		
		String insertTeacherQuery = "INSERT INTO Teachers (username, password, name, surname) " + "VALUES (?, ?, ?, ?)";
		try (PreparedStatement pstmt = conn.prepareStatement(insertTeacherQuery)) {
			pstmt.setString(1, "tch001");
			pstmt.setString(2, "pass123");
			pstmt.setString(3, "Casimiro");
			pstmt.setString(4, "Grumaioli");
			pstmt.executeUpdate();
		}
		
		String getTeacherIdQuery = "SELECT id_teacher FROM Teachers WHERE username = 'tch001';";
		rs = conn.createStatement().executeQuery(getTeacherIdQuery);
		rs.next();
		int teacherId = rs.getInt("id_teacher");
		
		teacher = new Teacher(teacherId, "Casimiro", "Grumaioli");
		
		
		
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
		

		
		String insertGradeQuery = "INSERT INTO Grades (rating, description, date, id_student, id_teaching) "
				+ "VALUES (?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = conn.prepareStatement(insertGradeQuery)) {
			pstmt.setInt(1, 4); 
			pstmt.setString(2, "Brutto compito");
			pstmt.setDate(3, Date.valueOf("2023-09-11")); 
			pstmt.setInt(4, student.getId()); 
			pstmt.setInt(5, teaching.getId()); 
			pstmt.executeUpdate();
		}
		
		String getGradeIdQuery = "SELECT id_grade FROM Grades WHERE rating = 4;";
		rs = conn.createStatement().executeQuery(getGradeIdQuery);
		rs.next();
		int gradeId = rs.getInt("id_grade");
		
		grade = new Grade(gradeId, student, teaching, LocalDate.of(2023, 9, 11), 4, 1, "Brutto compito");
	}

	private int getGradeId(String description) throws SQLException {
	    String getGradeIdQuery = "SELECT id_grade FROM Grades WHERE description = ?";
	    try (PreparedStatement pstmt = conn.prepareStatement(getGradeIdQuery)) {
	        pstmt.setString(1, description); 
	        ResultSet rs = pstmt.executeQuery();
	        return rs.getInt("id_grade");
	    }
	}

	
	@Test
	public void testAddNewGradeWithWeight() throws SQLException, GradeDaoException, DaoConnectionException, StudentDaoException {
		LocalDate dateExpected = LocalDate.of(2023, 11, 10);
		String descriptionExpected = "Voto di prova inserito";
		gradeDao.addNewGradeWithWeight(8.5, 2, descriptionExpected, teaching, student, dateExpected);
		
		
		int gradeId = getGradeId(descriptionExpected);
		Grade gradeExpected = new Grade(gradeId, student, teaching, dateExpected, 8.5, 2, descriptionExpected);
		
		ResultSet rs = conn.prepareStatement("SELECT * FROM Grades WHERE id_grade = " + gradeId + ";").executeQuery();
		assertThat(rs.next()).isTrue();
		
		Grade gradeActual = new Grade(rs.getInt("id_grade"),
				student,
				teaching,
				rs.getDate("date").toLocalDate(),
				rs.getDouble("rating"),
				rs.getInt("weight"),
				rs.getString("description")
				);
		
		assertThat(gradeActual).isEqualTo(gradeExpected);
		
		assertThat(rs.getInt("id_student")).isEqualTo(student.getId());
		assertThat(rs.getInt("id_teaching")).isEqualTo(teaching.getId());
	}
	
	
	@Test
	public void testDeleteGrade() throws SQLException, GradeDaoException {
		gradeDao.deleteGrade(grade);
		
		ResultSet rs = conn.prepareStatement("SELECT * FROM Grades WHERE id_grade = " + grade.getId() + ";").executeQuery();
		assertThat(rs.next()).isFalse();
	}
	
	@Test
	public void testDeleteGradeNotFound() throws SQLException, GradeDaoException {
	    Grade nonExistentGrade = new Grade(-1, student, teaching, LocalDate.of(2023, 11, 10), 8.5, 2, "Voto inesistente");
	    
	    assertThrows(GradeDaoException.class, () -> {
	        gradeDao.deleteGrade(nonExistentGrade);
	    });
	}

	@Test
	public void testEditGradeValue() throws SQLException, GradeDaoException, DaoConnectionException {
		double newValue = 9.5;
		
		gradeDao.editGradeValue(grade, newValue);

		ResultSet rs = conn.prepareStatement("SELECT * FROM Grades WHERE id_grade = " + grade.getId() + ";").executeQuery();
		rs.next();
		
		assertThat(rs.getDouble("rating")).isEqualTo(newValue);
	}
	
	@Test
	public void testEditGradeValueNotFound() throws SQLException, GradeDaoException {
	    Grade nonExistentGrade = new Grade(-1, student, teaching, LocalDate.of(2023, 11, 10), 8.5, 2, "Voto inesistente");
	    
	    assertThrows(GradeDaoException.class, () -> {
	        gradeDao.editGradeValue(nonExistentGrade, 10);
	    });
	}
	
	
	@Test
	public void testEditGradeWeight() throws SQLException, GradeDaoException, DaoConnectionException {
		int newWeight = 5;

		gradeDao.editGradeWeight(grade, newWeight);

		ResultSet rs = conn.prepareStatement("SELECT * FROM Grades WHERE id_grade = " + grade.getId() + ";").executeQuery();
		rs.next();
		
		assertThat(rs.getInt("weight")).isEqualTo(newWeight);
	}
	
	
	@Test
	public void testEditGradeWeightNotFound() throws SQLException, GradeDaoException {
	    Grade nonExistentGrade = new Grade(-1, student, teaching, LocalDate.of(2023, 11, 10), 8.5, 2, "Voto inesistente");
	    
	    assertThrows(GradeDaoException.class, () -> {
	        gradeDao.editGradeWeight(nonExistentGrade, 5);
	    });
	}
	
	@Test
	public void testEditGradeDescription() throws SQLException, GradeDaoException, DaoConnectionException {
		String newDescription = "Descrizione aggiornata";
		
		gradeDao.editGradeDescription(grade, newDescription);

		ResultSet rs = conn.prepareStatement("SELECT description FROM Grades WHERE id_grade = " + grade.getId() + ";").executeQuery();
		rs.next();
		
		assertThat(rs.getString("description")).isEqualTo(newDescription);
	}
	
	
	@Test
	public void testEditGradeDescriptionNotFound() throws SQLException, GradeDaoException {
	    Grade nonExistentGrade = new Grade(-1, student, teaching, LocalDate.of(2023, 11, 10), 8.5, 2, "Voto inesistente");
	    
	    assertThrows(GradeDaoException.class, () -> {
	        gradeDao.editGradeDescription(nonExistentGrade, "Modifica voto inesistente");
	    });
	}
	

	@Test
	public void testGetAllStudentGrades() throws SQLException, GradeDaoException, StudentDaoException, TeachingAssignmentDaoException {
	    String secondGradeQuery = "INSERT INTO Grades (rating, weight, description, id_teaching, id_student, date) "
	                             + "VALUES (?, ?, ?, ?, ?, ?)";
	    try (PreparedStatement pstmt = conn.prepareStatement(secondGradeQuery)) {
	        pstmt.setDouble(1, 7); 
	        pstmt.setInt(2, 3); 
	        pstmt.setString(3, "Secondo voto"); 
	        pstmt.setInt(4, teaching.getId()); 
	        pstmt.setInt(5, student.getId()); 
	        pstmt.setDate(6, Date.valueOf("2023-09-16")); 
	        pstmt.executeUpdate();
	    }
	    Grade secondGrade = new Grade(getGradeId("Secondo voto"), student, teaching, LocalDate.of(2023, 9, 16), 7, 3, "Secondo voto");

	    String thirdGradeQuery = "INSERT INTO Grades (rating, weight, description, id_teaching, id_student, date) "
	                            + "VALUES (?, ?, ?, ?, ?, ?)";
	    try (PreparedStatement pstmt = conn.prepareStatement(thirdGradeQuery)) {
	        pstmt.setDouble(1, 7); 
	        pstmt.setInt(2, 3);
	        pstmt.setString(3, "Voto di un altro studente"); 
	        pstmt.setInt(4, teaching.getId()); 
	        pstmt.setInt(5, -1); 
	        pstmt.setDate(6, Date.valueOf("2023-09-16")); 
	        pstmt.executeUpdate();
	    }

	    List<Grade> gradesExpected = List.of(grade, secondGrade);

	    Iterator<Grade> iteratorGradesActual = gradeDao.getAllStudentGrades(student);
	    List<Grade> gradesActualList = new ArrayList<>();
	    while (iteratorGradesActual.hasNext()) {
	        gradesActualList.add(iteratorGradesActual.next());
	    }

	    assertThat(gradesActualList).containsExactlyInAnyOrderElementsOf(gradesExpected);
	}
	
	
	@Test
	public void testGetAllStudentGradesForNonExistentStudent() throws SQLException, GradeDaoException {
	    Student nonExistentStudent = new Student(-1, "Non", "Esistente", new SchoolClass("1A"));
	    
	    assertThrows(StudentDaoException.class, () -> {
	        Iterator<Grade> gradesIterator = gradeDao.getAllStudentGrades(nonExistentStudent);
	        gradesIterator.next(); 
	    });
	}

	

	@Test
	public void testGetStudentGradesByTeaching() throws SQLException, GradeDaoException, StudentDaoException, TeachingAssignmentDaoException, DaoConnectionException {
	    String secondGradeQuery = "INSERT INTO Grades (rating, weight, description, id_teaching, id_student, date) "
	                             + "VALUES (?, ?, ?, ?, ?, ?)";
	    try (PreparedStatement pstmt = conn.prepareStatement(secondGradeQuery)) {
	        pstmt.setDouble(1, 7);
	        pstmt.setInt(2, 3);
	        pstmt.setString(3, "Secondo voto"); 
	        pstmt.setInt(4, teaching.getId());
	        pstmt.setInt(5, student.getId()); 
	        pstmt.setDate(6, Date.valueOf("2023-09-16")); 
	        pstmt.executeUpdate();
	    }
	    Grade secondGrade = new Grade(getGradeId("Secondo voto"), student, teaching, LocalDate.of(2023, 9, 16), 7, 3, "Secondo voto");
	    
	    String thirdGradeQuery = "INSERT INTO Grades (rating, weight, description, id_teaching, id_student, date) "
	                            + "VALUES (?, ?, ?, ?, ?, ?)";
	    try (PreparedStatement pstmt = conn.prepareStatement(thirdGradeQuery)) {
	        pstmt.setDouble(1, 7); 
	        pstmt.setInt(2, 3); 
	        pstmt.setString(3, "Voto di un altro insegnamento"); 
	        pstmt.setInt(4, -1); 
	        pstmt.setInt(5, student.getId()); 
	        pstmt.setDate(6, Date.valueOf("2023-09-16")); 
	        pstmt.executeUpdate();
	    }

	    List<Grade> gradesExpected = List.of(grade, secondGrade);

	    Iterator<Grade> iteratorGradesActual = gradeDao.getStudentGradesByTeaching(student, teaching);
	    List<Grade> gradesActualList = new ArrayList<>();
	    while (iteratorGradesActual.hasNext()) {
	    	gradesActualList.add(iteratorGradesActual.next());
	    }

	    assertThat(gradesActualList).containsExactlyInAnyOrderElementsOf(gradesExpected);
	}
	
	@Test
	public void testGetStudentGradesByTeachingForNonExistentStudent() throws SQLException, GradeDaoException {
	    Student nonExistentStudent = new Student(-1, "Non", "Esistente", new SchoolClass("1A"));
	    
	    assertThrows(StudentDaoException.class, () -> {
	        Iterator<Grade> gradesIterator = gradeDao.getStudentGradesByTeaching(nonExistentStudent, teaching);
	        gradesIterator.next(); 
	    });
	}
	
	
	@Test
	public void testGetStudentGradesByTeachingForNonExistentTeaching() throws SQLException, GradeDaoException {
	    TeachingAssignment nonExistentTeaching = new TeachingAssignment(-1, "Storia", teacher, new SchoolClass("1A"));
	    
	    assertThrows(TeachingAssignmentDaoException.class, () -> gradeDao.getStudentGradesByTeaching(student, nonExistentTeaching));
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
