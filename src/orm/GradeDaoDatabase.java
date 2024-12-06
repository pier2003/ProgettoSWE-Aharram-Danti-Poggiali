package orm;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import domainModel.Grade;
import domainModel.Student;
import domainModel.TeachingAssignment;

public class GradeDaoDatabase implements GradeDao {
	
	private Connection conn;

	public GradeDaoDatabase(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void addNewGradeWithWeight(double value, int weight, String description, TeachingAssignment teaching,
			Student student, LocalDate date) throws GradeDaoException, DaoConnectionException {
		String query = "INSERT INTO Grades (rating, weight, description, id_teaching, id_student, date) VALUES (?, ?, ?, ?, ?, ?);";
		try {
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setDouble(1, value);
			stmt.setInt(2, weight);
			stmt.setString(3, description);
			stmt.setInt(4, teaching.getId());
			stmt.setInt(5, student.getId());
			stmt.setDate(6, Date.valueOf(date));
			stmt.executeUpdate();
			if (stmt != null) {
				stmt.close();
			}
		} catch (SQLException e) {
			throw new GradeDaoException();
		}
	}

	@Override
	public void deleteGrade(int id_grade) throws GradeDaoException, DaoConnectionException {
		String query = "DELETE FROM Grades WHERE id_grade = " + id_grade + ";";
		executeUpdate(query);
	}

	@Override
	public void editGradeValue(int id_grade, double value) throws GradeDaoException, DaoConnectionException {
		String query = "UPDATE Grades SET rating = " + value + " WHERE id_grade = " + id_grade + ";";
		executeUpdate(query);
	}

	@Override
	public void editGradeWeight(int id_grade, int weight) throws GradeDaoException, DaoConnectionException {
		String query = "UPDATE Grades SET weight = " + weight + " WHERE id_grade = " + id_grade + ";";
		executeUpdate(query);
	}

	@Override
	public void editGradeDescription(int id_grade, String description) throws GradeDaoException, DaoConnectionException {
		String query = "UPDATE Grades SET description = '" + description + "' WHERE id_grade = " + id_grade + ";";
		executeUpdate(query);
	}

	@Override
	public Grade getGradeById(int id_grade) throws GradeDaoException, DaoConnectionException {
		String query = "SELECT id_grade, id_student, id_teaching, "
				+ "date, rating, weight, description "
				+ "FROM Grades "
				+ "WHERE id_grade = " + id_grade + ";";
		StudentDao studentDao = new StudentDaoDatabase(conn);
		TeachingAssignmentDao teachingAssignmentDao = new TeachingAssignmentDaoDatabase(conn);
		try {
			ResultSet rs = getResultsFromDB(query);
			if (rs.next()) {
			return new Grade(rs.getInt("id_grade"), 
					studentDao.getStudentById(rs.getInt("id_student")),
					teachingAssignmentDao.getTeachingById(rs.getInt("id_teaching")), 
					LocalDate.parse(rs.getString("date")),
					rs.getDouble("rating"), 
					rs.getInt("weight"),
					rs.getString("description"));
			}
			else {
				throw new GradeDaoException();
			}
		} catch (SQLException | StudentDaoException | TeachingAssignmentDaoException e) {
			throw new GradeDaoException();
		}
	}

	@Override
	public Iterator<Grade> getAllStudentGrades(int id_student) throws GradeDaoException, DaoConnectionException {
		String query = "SELECT id_grade, id_teaching, "
				+ "date, rating, weight, description "
				+ "FROM Grades "
				+ "WHERE id_student = " + id_student + ";";
		List<Grade> grades = new ArrayList<Grade>();
		try {
			StudentDao studentDao = new StudentDaoDatabase(conn);
			Student student = studentDao.getStudentById(id_student);
			TeachingAssignmentDao teachingAssignmentDao = new TeachingAssignmentDaoDatabase(conn);
			ResultSet rs = getResultsFromDB(query);
			while (rs.next()) {
				grades.add(new Grade(rs.getInt(1), 
						student, 
						teachingAssignmentDao.getTeachingById(rs.getInt(2)),
						LocalDate.parse(rs.getString(3)), 
						rs.getDouble(4), 
						rs.getInt(5), 
						rs.getString(6)));
			}
		} catch (SQLException | StudentDaoException | TeachingAssignmentDaoException e) {
			throw new GradeDaoException();
		}
		return grades.iterator();
	}

	@Override
	public Iterator<Grade> getStudentGradesByTeaching(int id_student, int id_teaching) throws GradeDaoException, DaoConnectionException {
		String query = "SELECT id_grade, date, rating, weight, description "
				+ "FROM Grades "
				+ "WHERE id_student = " + id_student + " AND id_teaching = " + id_teaching + ";";
		List<Grade> grades = new ArrayList<Grade>();
		try {
			StudentDao studentDao = new StudentDaoDatabase(conn);
			Student student = studentDao.getStudentById(id_student);
			TeachingAssignmentDao teachingAssignmentDao = new TeachingAssignmentDaoDatabase(conn);
			TeachingAssignment teaching = teachingAssignmentDao.getTeachingById(id_teaching);
			ResultSet rs = getResultsFromDB(query);
			while (rs.next()) {
				grades.add(new Grade(rs.getInt(1), 
						student, 
						teaching, 
						LocalDate.parse(rs.getString(2)), 
						rs.getDouble(3),
						rs.getInt(4), 
						rs.getString(5)));
			}
		} catch (TeachingAssignmentDaoException | StudentDaoException | SQLException e) {
			throw new GradeDaoException();
		}
		return grades.iterator();
	}

	private void executeUpdate(String query) throws GradeDaoException, DaoConnectionException {
		try {
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.executeUpdate();
			if (stmt != null) {
				stmt.close();
			}
		} catch (SQLException e) {
			throw new GradeDaoException();
		}
	}

	private ResultSet getResultsFromDB(String query) throws SQLException, DaoConnectionException {
		PreparedStatement stmt = conn.prepareStatement(query);
		ResultSet rs = stmt.executeQuery();
		return rs;
	}
}
