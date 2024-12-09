package orm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import domainModel.SchoolClass;
import domainModel.TeachingAssignment;

public class TeachingAssignmentDaoDatabase implements TeachingAssignmentDao {
	
	private Connection conn;

	public TeachingAssignmentDaoDatabase(Connection conn) {
		this.conn = conn;
	}
	
	@Override
	public Iterator<TeachingAssignment> getAllStudentTeachings(int id) throws TeachingAssignmentDaoException, DaoConnectionException  {
		String query = "SELECT id_teaching, subject, id_teacher, class_name "
				+ "FROM Students JOIN Classes ON Students.class = Classes.name "
				+ "JOIN Teachings ON Classes.name = Teachings.class_name "
				+ "WHERE id_student = " + id + ";";
		List<TeachingAssignment> teachings = new ArrayList<TeachingAssignment>();
		try {
			TeacherDao teacherDao = new TeacherDaoDatabase(conn);
			ResultSet rs = getResultsFromDB(query);
			while (rs.next()) {
				teachings.add(new TeachingAssignment(
						rs.getInt("id_teaching"), 
						rs.getString("subject"), 
						teacherDao.getTeacherById(rs.getInt("id_teacher")), 
						new SchoolClass(rs.getString("class_name"))));
			}
		} catch (SQLException | TeacherDaoException e) {
			throw new TeachingAssignmentDaoException("");
		}
		return teachings.iterator();
	}


	public TeachingAssignment getTeachingById(int id) throws TeachingAssignmentDaoException {
		String query = "SELECT id_teaching, subject, id_teacher, class_name FROM Teachings WHERE id_teaching = " + id + ";";
		try {
			TeacherDaoDatabase teacherDao = new TeacherDaoDatabase(conn);
			ResultSet rs = getResultsFromDB(query);
			rs.next();
			return new TeachingAssignment(
						rs.getInt("id_teaching"), 
						rs.getString("subject"), 
						teacherDao.getTeacherById(rs.getInt("id_teacher")), 
						new SchoolClass(rs.getString("class_name")));
			}
		catch (SQLException | TeacherDaoException e) {
			throw new TeachingAssignmentDaoException("");
		}
	}

	private ResultSet getResultsFromDB(String query) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement(query);
		ResultSet rs = stmt.executeQuery();
		return rs;
	}
}
