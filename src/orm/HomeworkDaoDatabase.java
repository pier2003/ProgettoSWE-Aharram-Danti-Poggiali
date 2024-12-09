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

import domainModel.Homework;
import domainModel.SchoolClass;
import domainModel.TeachingAssignment;

public class HomeworkDaoDatabase implements HomeworkDao {

	private Connection conn;
	
	public HomeworkDaoDatabase(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void addHomework(TeachingAssignment teaching, LocalDate date, String description, LocalDate subissionDate)
			throws HomeworkDaoException {
		String query = "INSERT INTO Annotations (id_teaching, type, description, date, submission_date)"
				+ "VALUES(?, ?, ?, ?, ?)";

		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setInt(1, teaching.getId());
			stmt.setString(2, "homework");
			stmt.setString(3, description);
			stmt.setDate(4, Date.valueOf(date));
			stmt.setDate(5, Date.valueOf(subissionDate));
			stmt.executeUpdate();
		} catch (SQLException e) {
			throw new HomeworkDaoException("Database error while entering homework data");
		}
	}

	@Override
	public Iterator<Homework> getHomeworksBySubmissionDate(LocalDate date, SchoolClass schoolClass) throws HomeworkDaoException, SchoolClassDaoException {
	    DaoUtils.checkScoolClassExist(schoolClass, conn);
	    
		String query = "SELECT id_annotation, id_teaching, date, description, submission_date FROM Annotations NATURAL JOIN Teachings " +
	                   "WHERE type = 'homework' AND class_name = ? AND submission_date = ?";
	    List<Homework> homeworks = new ArrayList<>();
	    TeachingAssignmentDaoDatabase teachingAssignmentDao = new TeachingAssignmentDaoDatabase(conn);

	    try (PreparedStatement stmt = conn.prepareStatement(query)) {
	        stmt.setString(1, schoolClass.getClassName());
	        stmt.setDate(2, Date.valueOf(date));
	        
	        try (ResultSet rs = stmt.executeQuery()) {
	            while (rs.next()) {
	                homeworks.add(new Homework(
	                    rs.getInt("id_annotation"),
	                    teachingAssignmentDao.getTeachingById(rs.getInt("id_teaching")),
	                    rs.getDate("date").toLocalDate(),
	                    rs.getString("description"),
	                    rs.getDate("submission_date").toLocalDate()
	                ));
	            }
	        }
	    } catch (SQLException | TeachingAssignmentDaoException e) {
	        throw new HomeworkDaoException("Database error while fetching homework data");
	    }
	    return homeworks.iterator();
	}

	@Override
	public void editHomeworkDescription(Homework homework, String description) throws HomeworkDaoException {
		String query = "UPDATE Annotations SET description = ? "
	            + "WHERE id_annotation = ?";
	    try (PreparedStatement stmt = conn.prepareStatement(query)) {
	        stmt.setString(1, description);
	        stmt.setInt(2, homework.getId());
	        stmt.executeUpdate();
	    } catch (SQLException e) {
	        throw new HomeworkDaoException("Database error while updating homework data");
	    }
	}

	@Override
	public void editHomeworkSubmissionDate(Homework homework, LocalDate submissionDate) throws HomeworkDaoException {
		String query = "UPDATE Annotations SET submission_date = ? "
	            + "WHERE id_annotation = ?";
	    try (PreparedStatement stmt = conn.prepareStatement(query)) {
	        stmt.setDate(1, Date.valueOf(submissionDate));
	        stmt.setInt(2, homework.getId());
	        stmt.executeUpdate();
	    } catch (SQLException e) {
	        throw new HomeworkDaoException("Database error while updating homework data");
	    }
	}

	@Override
	public void deleteHomework(Homework homework) throws HomeworkDaoException {
	    String query = "DELETE FROM Annotations WHERE id_annotation = ? AND type = 'homework'";
	    try (PreparedStatement stmt = conn.prepareStatement(query)) {
	        stmt.setInt(1, homework.getId());
	        
	        int rowsAffected = stmt.executeUpdate();
	        if (rowsAffected != 1) {
		        throw new HomeworkDaoException("Homework doesn't exists");
	        }
	    } catch (SQLException e) {
	        throw new HomeworkDaoException("Database error while deleting homework data");
	    }
		
	}

}
