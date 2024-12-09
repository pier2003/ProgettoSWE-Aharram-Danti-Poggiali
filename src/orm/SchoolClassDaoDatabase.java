package orm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import domainModel.SchoolClass;

public class SchoolClassDaoDatabase implements SchoolClassDao {
	private Connection conn;

	public SchoolClassDaoDatabase(Connection conn) {
		this.conn = conn;
	}

	public SchoolClass getSchoolClassByName(String name) throws SchoolClassDaoException, SQLException {
		String query = "SELECT * FROM Classes WHERE name = ?";
		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setString(1, name);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return new SchoolClass(rs.getString("name"));
				} else {
					throw new SchoolClassDaoException("School class dosen't exist.");
				}
			} 
		} catch (SQLException e) {
			throw new SchoolClassDaoException("Error while fetching class.");
		}
	}
}