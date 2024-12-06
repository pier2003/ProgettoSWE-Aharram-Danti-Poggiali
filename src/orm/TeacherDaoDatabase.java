package orm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import domainModel.Teacher;

public class TeacherDaoDatabase implements TeacherDao {

	private Connection conn;

	public TeacherDaoDatabase(Connection conn) {
		this.conn = conn;
	}

	@Override
	public Teacher getTeacherById(int id) throws TeacherDaoException, DaoConnectionException {
		String query = "SELECT * FROM Teachers WHERE id_teacher = " + id + ";";
		try {
			PreparedStatement stmt = conn.prepareStatement(query);
			ResultSet rs = stmt.executeQuery();

			if(!rs.next())
				throw new TeacherDaoException("Teacher with id " + id + "dosen't exist.");
			
			return new Teacher(rs.getString("name"), 
					rs.getString("surname"), 
					rs.getInt("id_teacher"));
		} catch (SQLException e) {
			throw new TeacherDaoException("Database error while fetching teachers data.");
		}
	}
	
}
