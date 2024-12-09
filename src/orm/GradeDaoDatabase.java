package orm;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import domainModel.Grade;
import domainModel.Student;
import domainModel.TeachingAssignment;

public class GradeDaoDatabase implements GradeDao {
	
	private Connection conn;

	public GradeDaoDatabase(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void addNewGradeWithWeight(double value, int weight, String description, TeachingAssignment teaching,
			Student student, LocalDate date) throws GradeDaoException, StudentDaoException {
		DaoUtils.checkStudentExist(student, conn);
		
		String query = "INSERT INTO Grades (rating, weight, description, id_teaching, id_student, date) VALUES (?, ?, ?, ?, ?, ?);";
		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setDouble(1, value);
			stmt.setInt(2, weight);
			stmt.setString(3, description);
			stmt.setInt(4, teaching.getId());
			stmt.setInt(5, student.getId());
			stmt.setDate(6, Date.valueOf(date));
			stmt.executeUpdate();
		} catch (SQLException e) {
			throw new GradeDaoException("Error while insert grade");
		}
	}

	@Override
	public void deleteGrade(Grade grade) throws GradeDaoException {
	    String query = "DELETE FROM Grades WHERE id_grade = ?";
	    try (PreparedStatement stmt = conn.prepareStatement(query)) {
	        stmt.setInt(1, grade.getId());
	        
	        int rowsAffected = stmt.executeUpdate();
	        if (rowsAffected != 1) {
	        	throw new GradeDaoException("Grade not present in database");
	        }
	    } catch (SQLException e) {
	        throw new GradeDaoException("Error deleting grade");
	    }
	}

	@Override
	public void editGradeValue(Grade grade, double value) throws GradeDaoException {
	    String query = "UPDATE Grades SET rating = ? WHERE id_grade = ?";
	    try (PreparedStatement stmt = conn.prepareStatement(query)) {
	        stmt.setDouble(1, value);
	        stmt.setInt(2, grade.getId());
	        
	        int rowsAffected = stmt.executeUpdate();
	        if (rowsAffected != 1) {
	        	throw new GradeDaoException("Grade not present in database");
	        }
	    } catch (SQLException e) {
	        throw new GradeDaoException("Error updating rating for selected grade");
	    }
	}


	@Override
	public void editGradeWeight(Grade grade, int weight) throws GradeDaoException {
	    String query = "UPDATE Grades SET weight = ? WHERE id_grade = ?";
	    try (PreparedStatement stmt = conn.prepareStatement(query)) {
	        stmt.setInt(1, weight);
	        stmt.setInt(2, grade.getId());
	        
	        int rowsAffected = stmt.executeUpdate();
	        if (rowsAffected != 1) {
	        	throw new GradeDaoException("Grade not present in database");
	        }
	    } catch (SQLException e) {
	        throw new GradeDaoException("Error updating weight for selected grade");
	    }
	}


	@Override
	public void editGradeDescription(Grade grade, String description) throws GradeDaoException {
	    String query = "UPDATE Grades SET description = ? WHERE id_grade = ?";
	    try (PreparedStatement stmt = conn.prepareStatement(query)) {
	        stmt.setString(1, description);
	        stmt.setInt(2, grade.getId());
	        
	        int rowsAffected = stmt.executeUpdate();
	        if (rowsAffected != 1) {
	        	throw new GradeDaoException("Grade not present in database");
	        }
	    } catch (SQLException e) {
	        throw new GradeDaoException("Error updating description for selected grade");
	    }
	}


	@Override
	public Iterator<Grade> getAllStudentGrades(Student student) throws GradeDaoException, StudentDaoException {
		DaoUtils.checkStudentExist(student, conn);

	    String query = "SELECT id_grade, id_teaching, date, rating, weight, description "
	                 + "FROM Grades WHERE id_student = ?";
	    List<Grade> grades = new ArrayList<>();
	    int id_student = student.getId();
	    
	    try (PreparedStatement pstmt = conn.prepareStatement(query)) {
	        pstmt.setInt(1, id_student);
	        
	        try (ResultSet rs = pstmt.executeQuery()) {
	            TeachingAssignmentDaoDatabase teachingAssignmentDao = new TeachingAssignmentDaoDatabase(conn);
	            while (rs.next()) {
	                grades.add(new Grade(
	                    rs.getInt("id_grade"),
	                    student,
	                    teachingAssignmentDao.getTeachingById(rs.getInt("id_teaching")),
	                    rs.getDate("date").toLocalDate(),
	                    rs.getDouble("rating"),
	                    rs.getInt("weight"),
	                    rs.getString("description")
	                ));
	            }
	        }
	    } catch (SQLException | TeachingAssignmentDaoException e) {
	        throw new GradeDaoException("Error retrieving grades for selected student");
	    }
	    return grades.iterator();
	}

	@Override
	public Iterator<Grade> getStudentGradesByTeaching(Student student, TeachingAssignment teaching) throws GradeDaoException, StudentDaoException, TeachingAssignmentDaoException {
		DaoUtils.checkStudentExist(student, conn);
		DaoUtils.checkTeachingAssignmentExist(teaching, conn);
		
		String query = "SELECT id_grade, date, rating, weight, description "
	                 + "FROM Grades WHERE id_student = ? AND id_teaching = ?";
	    List<Grade> grades = new ArrayList<>();
	    
	    try (PreparedStatement pstmt = conn.prepareStatement(query)) {
	        pstmt.setInt(1, student.getId());
	        pstmt.setInt(2, teaching.getId());
	        
	        try (ResultSet rs = pstmt.executeQuery()) {
	            while (rs.next()) {
	                grades.add(new Grade(
	                    rs.getInt("id_grade"),
	                    student,
	                    teaching,
	                    rs.getDate("date").toLocalDate(),
	                    rs.getDouble("rating"),
	                    rs.getInt("weight"),
	                    rs.getString("description")
	                ));
	            }
	        }
	    } catch (SQLException e) {
	        throw new GradeDaoException("Error retrieving grades for selected student and teaching ");
	    }
	    return grades.iterator();
	}

}
