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
import domainModel.SchoolClass;
import domainModel.TeachingAssignment;

public class LessonDaoDatabase implements LessonDao {
	private Connection conn;

	public LessonDaoDatabase(Connection conn) {
		this.conn = conn;
	}
	
	@Override
	public void addLesson(TeachingAssignment teaching, LocalDate date, String description, LocalTime startHour,
			LocalTime endHour) throws LessonDaoException, DaoConnectionException {
		String query = "INSERT INTO Annotations (id_teaching, type, description, date, start_hour, end_hour)"
				+ "VALUES(?, ?, ?, ?, ?, ?)";

		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setInt(1, teaching.getId());
			stmt.setString(2, "lesson");
			stmt.setString(3, description);
			stmt.setDate(4, Date.valueOf(date));
			stmt.setString(5, startHour.toString());
			stmt.setString(6, endHour.toString());
			stmt.executeUpdate();
		} catch (SQLException e) {
			throw new LessonDaoException("Database error while entering lesson data");
		}
	}

	@Override
	public void editLessonDescription(Lesson lesson, String description) throws LessonDaoException {
	    String query = "UPDATE Annotations SET description = ? "
	            + "WHERE id_annotation = ? AND type = 'lesson'";
	    try (PreparedStatement stmt = conn.prepareStatement(query)) {
	        stmt.setString(1, description);
	        stmt.setInt(2, lesson.getId());
	        stmt.executeUpdate();
	    } catch (SQLException e) {
	        throw new LessonDaoException("Database error while updating lesson data");
	    }
	}

	@Override
	public void editLessonDateTime(Lesson lesson, LocalDate date, LocalTime startHour, LocalTime endHour) throws LessonDaoException {
	    String query = "UPDATE Annotations SET date = ?, start_hour = ?, end_hour = ? "
	            + "WHERE id_annotation = ? AND type = 'lesson'";
	    try (PreparedStatement stmt = conn.prepareStatement(query)) {
	        stmt.setDate(1, Date.valueOf(date));
	        stmt.setString(2, startHour.toString());
	        stmt.setString(3, endHour.toString());
	        stmt.setInt(4, lesson.getId());

	        int rowsAffected = stmt.executeUpdate();
	        if (rowsAffected != 1) {
	            throw new LessonDaoException("Lesson doesn't exist or update failed");
	        }
	    } catch (SQLException e) {
	        throw new LessonDaoException("Database error while updating lesson date and time");
	    }
	}

	@Override
	public Iterator<Lesson> getLessonsInDay(LocalDate date, SchoolClass schoolClass) throws LessonDaoException, SchoolClassDaoException {
	    DaoUtils.checkScoolClassExist(schoolClass, conn);

	    String query = "SELECT id_annotation, id_teaching, date, description, start_hour, end_hour FROM Annotations NATURAL JOIN Teachings " +
	                   "WHERE type = 'lesson' AND class_name = ? AND date = ?";
	    List<Lesson> lessons = new ArrayList<>();
	    TeachingAssignmentDaoDatabase teachingAssignmentDao = new TeachingAssignmentDaoDatabase(conn);

	    try (PreparedStatement stmt = conn.prepareStatement(query)) {
	        stmt.setString(1, schoolClass.getClassName());
	        stmt.setDate(2, Date.valueOf(date));

	        try (ResultSet rs = stmt.executeQuery()) {
	            while (rs.next()) {
	                lessons.add(new Lesson(
	                    rs.getInt("id_annotation"),
	                    teachingAssignmentDao.getTeachingById(rs.getInt("id_teaching")),
	                    rs.getDate("date").toLocalDate(),
	                    rs.getString("description"),
	                    LocalTime.parse(rs.getString("start_hour")),
	                    LocalTime.parse(rs.getString("end_hour"))
	                ));
	            }
	        }
	    } catch (SQLException | TeachingAssignmentDaoException e) {
	        throw new LessonDaoException("Database error while fetching lesson data");
	    }
	    return lessons.iterator();
	}


	@Override
	public void deleteLesson(Lesson lesson) throws LessonDaoException {
		String query = "DELETE FROM Annotations WHERE id_annotation = ? AND type = 'lesson'";
		try (PreparedStatement stmt = conn.prepareStatement(query)) {
		    stmt.setInt(1, lesson.getId());
		    
		    int rowsAffected = stmt.executeUpdate();
		    if (rowsAffected != 1) {
		        throw new LessonDaoException("Lesson doesn't exist");
		    }
		} catch (SQLException e) {
		    throw new LessonDaoException("Database error while deleting lesson data");
		}
	}
	
}