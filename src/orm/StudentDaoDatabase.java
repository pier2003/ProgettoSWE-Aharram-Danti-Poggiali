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

	@Override
	public Iterator<Student> getAllStudents() throws StudentDaoException, DaoConnectionException {
		List<Student> students = new ArrayList<Student>();
		String query = "SELECT * FROM Students;";
		try {
			ResultSet rs = getResultsFromDB(query);
			while (rs.next()) {
				students.add(new Student
						(rs.getString("name"), 
						rs.getString("surname"), 
						rs.getInt("id_student"),
						new SchoolClass(rs.getString("class"))));
			}
			return students.iterator();
		} catch (SQLException e) {
			throw new StudentDaoException();
		}
	}

	@Override
	public Student getStudentById(int id) throws StudentDaoException, DaoConnectionException {
		String query = "SELECT * FROM Students WHERE id_student = " + id + ";";
		try {
			ResultSet rs = getResultsFromDB(query);
			if (rs.next()) {
				return new Student(rs.getString("name"),
						rs.getString("surname"),
						rs.getInt("id_student"),
						new SchoolClass(rs.getString("class")));
			}
			else {
				throw new StudentDaoException();
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
	public Iterator<Student> getStudentsByClass(String schoolClass_name) throws StudentDaoException, DaoConnectionException {
		String query = "SELECT * FROM Students WHERE class = '" + schoolClass_name + "';";
		try {
			ResultSet rs = getResultsFromDB(query);
			List<Student> students = new ArrayList<Student>();
			while (rs.next()) {
				students.add(new Student
						(rs.getString("name"), 
						rs.getString("surname"), 
						rs.getInt("id_student"),
						new SchoolClass(rs.getString("class"))));
			}
			return students.iterator();
		} catch (SQLException e) {
			throw new StudentDaoException();
		}
	}


	
}
