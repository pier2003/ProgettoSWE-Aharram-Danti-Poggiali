package orm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import domainModel.SchoolClass;
import domainModel.Student;
import domainModel.Teacher;
import domainModel.TeachingAssignment;

public class SchoolClassDaoDatabase implements SchoolClassDao {
	private Connection conn;
	
	public SchoolClassDaoDatabase(Connection conn) {
		this.conn = conn;
	}

	@Override
	public SchoolClass getSchoolClassByName(String name) throws SchoolClassDaoException, DaoConnectionException {
		String query = "SELECT * FROM Classes WHERE name = '" + name + "';";
		try {
			ResultSet rs = getResultsFromDB(query);
			if (rs.next()) {
				return new SchoolClass(rs.getString("name"));
			}
			else {
				throw new SchoolClassDaoException();
			}
		} catch (SQLException e) {
			throw new SchoolClassDaoException();
		}
	}


	@Override
	public SchoolClass getSchoolClassByStudent(Student student) throws SchoolClassDaoException, DaoConnectionException {
		String query = "SELECT * FROM Students WHERE id_student = " + student.getId() + ";";
		try {
			ResultSet rs = getResultsFromDB(query);
			if (rs.next()) {
				return new SchoolClass(rs.getString("class"));
			}
			else {
				throw new SchoolClassDaoException();
			}
		} catch (SQLException e) {
			throw new SchoolClassDaoException();
		}
	}

	private ResultSet getResultsFromDB(String query) throws SQLException, DaoConnectionException {
		PreparedStatement stmt = conn.prepareStatement(query);
		ResultSet rs = stmt.executeQuery();
		return rs;
	}

	@Override
	public Iterator<SchoolClass> getAllSchoolClassesByTeaching(TeachingAssignment teachingAssignment)
			throws SchoolClassDaoException, DaoConnectionException {
		// TODO Auto-generated method stub
		return null;
	}
}