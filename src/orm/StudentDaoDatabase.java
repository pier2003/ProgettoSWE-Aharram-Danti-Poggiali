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
					return new Student(rs.getInt("id_student"), rs.getString("name"), rs.getString("surname"),
							new SchoolClass(rs.getString("class")));
				} else {
					throw new StudentDaoException("Student dosen't exist.");
				}
			}
		} catch (SQLException e) {
			throw new StudentDaoException("Error while fetching student");
		}
	}

	@Override
	public Iterator<Student> getStudentsByClass(SchoolClass schoolClass)
			throws StudentDaoException, DaoConnectionException, SchoolClassDaoException {
		ArrayList<Student> students = new ArrayList<>();
		DaoUtils.checkScoolClassExist(schoolClass, conn);
		String query = "SELECT * FROM Students WHERE class = ?";
		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setString(1, schoolClass.getClassName());
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					students.add(new Student(rs.getInt("id_student"), rs.getString("name"), rs.getString("surname"),
							new SchoolClass(rs.getString("class"))));
				}
			}
		} catch (SQLException e) {
			throw new StudentDaoException("Database error while fetching student.");
		}
		return students.iterator();
	}

}
