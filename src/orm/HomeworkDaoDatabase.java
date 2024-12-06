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

		try {
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setInt(1, teaching.getId());
			stmt.setString(2, "homework");
			stmt.setString(3, description);
			stmt.setDate(4, Date.valueOf(date));
			stmt.setDate(5, Date.valueOf(subissionDate));
			stmt.executeUpdate();
			if (stmt != null) {
				stmt.close();
			}
		} catch (SQLException e) {
			throw new HomeworkDaoException("Database error while entering homework data.");
		}
		

	}


	@Override
	public Iterator<Homework> getHomeworksInDay(LocalDate date, String className) throws DaoConnectionException, HomeworkDaoException {
		String query = "SELECT id_annotation, id_teaching, date, description, FROM Annotations NATURAL JOIN Teachings "
				+ "WHERE type = 'homework' AND class_name = '" + className + "';";
		List<Homework> homeworks = new ArrayList<Homework>();
		TeachingAssignmentDao teachingAssignmentDao = new TeachingAssignmentDaoDatabase(conn);
		try {
			ResultSet rs = getResultsFromDB(query);
			while(rs.next()) {
				homeworks.add(new Homework(rs.getInt(query), 
						teachingAssignmentDao.getTeachingById(rs.getInt("id_teaching")), 
						LocalDate.parse(rs.getString("date")), 
						rs.getString("description"),
						LocalDate.parse(rs.getString("submission_date"))));
			}
		} catch (SQLException | TeachingAssignmentDaoException  e) {
			throw new HomeworkDaoException("Database error while fetching homework data.");
		}
		
		return homeworks.iterator();
	}
	
	private ResultSet getResultsFromDB(String query) throws SQLException, DaoConnectionException {
		PreparedStatement stmt = conn.prepareStatement(query);
		ResultSet rs = stmt.executeQuery();
		return rs;
	}

	@Override
	public Homework getHomeworkById(int id) throws HomeworkDaoException, DaoConnectionException {
	    String query = "SELECT id_annotation, id_teaching, date, description, submission_date " +
	                   "FROM Annotations " +
	                   "WHERE id_annotation = ? AND type = 'homework'";
	    TeachingAssignmentDao teachingAssignmentDao = new TeachingAssignmentDaoDatabase(conn);

	    try (PreparedStatement stmt = conn.prepareStatement(query)) {
	        stmt.setInt(1, id);
	        try (ResultSet rs = stmt.executeQuery()) {
	            if (rs.next()) {
	                return new Homework(
	                    rs.getInt("id_annotation"),
	                    teachingAssignmentDao.getTeachingById(rs.getInt("id_teaching")),
	                    rs.getDate("date").toLocalDate(),
	                    rs.getString("description"),
	                    rs.getDate("submission_date").toLocalDate()
	                );
	            } else {
	                throw new HomeworkDaoException("No homework found with ID: " + id);
	            }
	        }
	    } catch (SQLException | TeachingAssignmentDaoException e) {
	        throw new HomeworkDaoException("Database error while fetching homework by ID.");
	    }
	}


	@Override
	public void editHomeworkDescription(int id, String description) throws HomeworkDaoException {
		String query = "UPDATE Annotations SET description = ? "
	            + "WHERE id_annotation = ?";
	    try {
	    	PreparedStatement stmt = conn.prepareStatement(query);
	        stmt.setString(1, description);
	        stmt.setInt(6, id);
	        if (stmt != null) {
	        	stmt.close();
	        }
	    } catch (SQLException e) {
	        throw new HomeworkDaoException("Database error while updating homework data.");
	    }
	}

	@Override
	public void editHomeworkSubmissionDate(int id, LocalDate submissionDate) throws HomeworkDaoException {
		String query = "UPDATE Annotations SET submission_date = ? "
	            + "WHERE id_annotation = ?";
	    try {
	    	PreparedStatement stmt = conn.prepareStatement(query);
	        stmt.setDate(1, Date.valueOf(submissionDate));
	        stmt.setInt(6, id);
	        if (stmt != null) {
	        	stmt.close();
	        }
	    } catch (SQLException e) {
	        throw new HomeworkDaoException("Database error while updating homework data.");
	    }
		
	}

}
