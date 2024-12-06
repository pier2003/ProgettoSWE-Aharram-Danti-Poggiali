package orm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import domainModel.Parent;
import domainModel.Student;
import domainModel.Teacher;

public class ParentDaoDatabase implements ParentDao {

	private Connection conn;

	public ParentDaoDatabase(Connection conn) {
		this.conn = conn;
	}

	@Override
	public Parent getParentById(int id) throws ParentDaoException, StudentDaoException, DaoConnectionException {

		String query = "SELECT * FROM Parents WHERE id_parent = " + id;
		try {
			PreparedStatement stmt = conn.prepareStatement(query);
			ResultSet rs = stmt.executeQuery();

			if (!rs.next())
				throw new ParentDaoException("Parent with id " + id + "dosen't exist.");

			Student son = new StudentDaoDatabase(conn).getStudentById(rs.getInt("id_student"));

			return new Parent(rs.getString("name"), rs.getString("surname"), id, son);
		} catch (SQLException e) {
			throw new ParentDaoException("Database error while fetching teachers data.");
		}
	}

	public Student getStudentOfParentByParentId(int parentId) throws DaoConnectionException, ParentDaoException, StudentDaoException {
	    String query = "SELECT id_student FROM Parents WHERE id_parent = ?;";
	    try (PreparedStatement stmt = conn.prepareStatement(query)) {
	        stmt.setInt(1, parentId);
	        ResultSet rs = stmt.executeQuery();

	        if (!rs.next()) {
	            throw new ParentDaoException("No student associated with parent ID " + parentId);
	        }

	        int studentId = rs.getInt("id_student");

	        return new StudentDaoDatabase(conn).getStudentById(studentId);

	    } catch (SQLException e) {
	        throw new DaoConnectionException();
	    }
	}

}
