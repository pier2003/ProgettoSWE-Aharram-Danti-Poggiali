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

public class StudentDaoDatabase implements StudentDao {

	private Connection conn;

	public StudentDaoDatabase(Connection conn) {
		this.conn = conn;
	}

	public Student getStudentById(int id) throws StudentDaoException {
	    String query = "SELECT * FROM Students WHERE id_student = ?";
	    try (PreparedStatement stmt = conn.prepareStatement(query)) {
	        stmt.setInt(1, id);
	        try (ResultSet rs = stmt.executeQuery()) {
	            if (rs.next()) {
	                return new Student(
	                    rs.getInt("id_student"),           
	                    rs.getString("name"),          
	                    rs.getString("surname"),             
	                    new SchoolClass(rs.getString("class")) 
	                );
	            } else {
	                throw new StudentDaoException();
	            }
	        }
	    } catch (SQLException e) {
	        throw new StudentDaoException();
	    }
	}

	
	private ResultSet getResultsFromDB(String query) throws SQLException, DaoConnectionException {
		PreparedStatement stmt = conn.prepareStatement(query);
		ResultSet rs = stmt.executeQuery();
		return rs;
	}

	@Override
	public Iterator<Student> getStudentsByClass(SchoolClass schoolClass) throws StudentDaoException, DaoConnectionException {
		String query = "SELECT * FROM Students WHERE class = '" + schoolClass.getClassName() + "';";
		try {
			ResultSet rs = getResultsFromDB(query);
			List<Student> students = new ArrayList<Student>();
			while (rs.next()) {
				students.add(new Student
						(rs.getInt("id_student"), 
						rs.getString("surname"), 
						rs.getString("name"),
						new SchoolClass(rs.getString("class"))));
			}
			return students.iterator();
		} catch (SQLException e) {
			throw new StudentDaoException();
		}
	}


	
}
