package orm;

import java.sql.Connection;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import domainModel.Lesson;
import domainModel.TeachingAssignment;

public class LessonDaoDatabase implements LessonDao {
	private Connection conn;

	public LessonDaoDatabase(Connection conn) {
		this.conn = conn;
	}
	
	@Override
	public void addLesson(TeachingAssignment teaching, LocalDate date, String description, LocalTime startHour,
			LocalTime endHour) throws LessonDaoException, DaoConnectionException {
		String query = "INSERT INTO Annotations (id_teaching, type, description, date, start_hour, end_hour, submission_date)"
				+ "VALUES(?, ?, ?, ?, ?, ?)";

		try {
			Connection conn = DatabaseManager.getInstance().getConnection();
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setInt(1, teaching.getId());
			stmt.setString(2, "lesson");
			stmt.setString(3, description);
			stmt.setDate(4, Date.valueOf(date));
			stmt.setTime(5, Time.valueOf(startHour));
			stmt.setTime(5, Time.valueOf(endHour));
			stmt.executeUpdate();
			if (stmt != null) {
				stmt.close();
			}
		} catch (SQLException e) {
			throw new LessonDaoException("");
		}
	}


	@Override
	public Iterator<Lesson> getLessonsInDay(LocalDate date, String schoolClassName) throws DaoConnectionException, LessonDaoException {
		String query = "SELECT * FROM Annotations NATURAL JOIN Teachings WHERE class_name = ? AND date = ?";
		List<Lesson> lessons = new ArrayList<Lesson>();
		TeachingAssignmentDao teachingAssignmentDao = new TeachingAssignmentDaoDatabase(conn);
		try (PreparedStatement stmt = conn.prepareStatement(query)) {
	        stmt.setString(1, schoolClassName); 
	        stmt.setDate(2, Date.valueOf(date)); 
	        try (ResultSet rs = stmt.executeQuery()) {
	            while (rs.next()) {               
	                lessons.add(new Lesson(
	                		rs.getInt("id_annotation"), 
	                		teachingAssignmentDao.getTeachingById(rs.getInt("id_teaching")), 
	                		rs.getDate("date").toLocalDate(), 
	                		rs.getString("description"),
	                		rs.getTime("start_hour").toLocalTime(), 
	                		rs.getTime("end_hour").toLocalTime()));
	            }
	        } catch (TeachingAssignmentDaoException e) {
	        	throw new LessonDaoException("Error retrieving lessons teaching from database");
			}
	    } catch (SQLException e) {
	        throw new LessonDaoException("Error retrieving lessons from database.");
	    }
	    
	    return lessons.iterator();
	}


	@Override
	public void editLessonDescription(int idLesson, String description) throws LessonDaoException, DaoConnectionException {
		String query = "UPDATE Annotions SET description = ? WHERE id_annotation = ?";
		try (PreparedStatement stmt = conn.prepareStatement(query)) {
		    stmt.setString(1, description);  
		    stmt.setInt(2, idLesson);
		    stmt.executeUpdate();                   
		} catch (SQLException e) {
		    throw new LessonDaoException("Error while editing in database lesson description.");
		}
	}

	@Override
	public void editLessonDateTime(int idLesson, LocalDate date, LocalTime startHour, LocalTime endHour)
			throws LessonDaoException, DaoConnectionException {
		String query = "UPDATE Annotions SET date = ?, start_hour = ?, end_hour = ? WHERE id_annotation = ?";
		try (PreparedStatement stmt = conn.prepareStatement(query)) {
		    stmt.setDate(1, Date.valueOf(date));      
		    stmt.setTime(2, Time.valueOf(startHour)); 
		    stmt.setTime(3, Time.valueOf(endHour)); 
		    stmt.setInt(4, idLesson);                
		    stmt.executeUpdate();                   
		} catch (SQLException e) {
		    throw new LessonDaoException("Error while editing in database lesson time and date.");
		}
	}

	@Override
	public Lesson getLessonById(int id) throws LessonDaoException, DaoConnectionException {
		String query = "SELECT * FROM Annotations "
				+ "WHERE id_annotation = ? AND type = 'lesson'";
		TeachingAssignmentDao teachingAssignmentDao = new TeachingAssignmentDaoDatabase(conn);

		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setInt(1, id);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return new Lesson(rs.getInt("id_annotation"),
							teachingAssignmentDao.getTeachingById(rs.getInt("id_teaching")),
							rs.getDate("date").toLocalDate(), 
							rs.getString("description"),
							rs.getTime("start_hour").toLocalTime(),
							rs.getTime("end_hour").toLocalTime());
				} else {
					throw new LessonDaoException("No lesson found with ID: " + id);
				}
			}
		} catch (SQLException | TeachingAssignmentDaoException e) {
			throw new LessonDaoException("Database error while fetching lesson by ID.");
		}
	}
	
}