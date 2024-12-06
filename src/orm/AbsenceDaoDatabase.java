package orm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;

import domainModel.Absence;
import domainModel.SchoolClass;
import domainModel.Student;

public class AbsenceDaoDatabase implements AbsenceDao {

	private Connection conn;

	public AbsenceDaoDatabase(Connection conn) {
		this.conn = conn;
	}

	@Override
	public Absence getAbsenceByStudentInDate(Student student, LocalDate date)
			throws AbsenceDaoException, DaoConnectionException {

		checkStudentExist(student);
		
		String query = "SELECT * FROM ABSENCES WHERE id_student = " + student.getId() + " AND date = '"
				+ java.sql.Date.valueOf(date) + "';";
		try {
			PreparedStatement stmt = conn.prepareStatement(query);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				boolean isJustified = rs.getBoolean("justification");
				return new Absence(student, date, isJustified);
			} else {
				throw new AbsenceDaoException("No absence found for student in date " + date.toString() + ".");
			}
		} catch (SQLException e) {
			throw new AbsenceDaoException("Database error while fetching absence data.");
		}
	}

	@Override
	public void addAbsence(Student student, LocalDate date) throws AbsenceDaoException, DaoConnectionException {

		checkStudentExist(student);

		String query = "INSERT INTO Absences (date, justification, id_student) VALUES ('" + java.sql.Date.valueOf(date) + "',"
						+ false + ", " + student.getId() + ")";
		try {
			
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.executeUpdate();

		} catch (SQLException e) {
			throw new AbsenceDaoException("Database error while insert absence.");
		}
	}

	@Override
	public void deleteAbsence(Student student, LocalDate date) throws AbsenceDaoException, DaoConnectionException {

		checkStudentExist(student);

		String query = "DELETE FROM Absences WHERE date = '" + java.sql.Date.valueOf(date) + "'"
					+ " AND id_student = " + student.getId();
		try {
			PreparedStatement stmt = conn.prepareStatement(query);
			int rowsAffected = stmt.executeUpdate();

			if (rowsAffected == 0) {
				throw new AbsenceDaoException(
						"No absence found to delete for student " + student.getId() + " on " + date.toString());
			}
		} catch (SQLException e) {
			throw new AbsenceDaoException("Database error while deleting absence.");
		}
	}

	@Override
	public Iterator<Absence> getAbsencesByClassInDate(SchoolClass schoolClass, LocalDate date)
	        throws AbsenceDaoException, DaoConnectionException {

	    checkScoolClassExist(schoolClass);

	    ArrayList<Absence> absences = new ArrayList<>();

	    String query = "SELECT Absences.date, Absences.justification, Absences.id_student " +
	                   "FROM Absences " +
	                   "JOIN Students ON Absences.id_student = Students.id_student " +
	                   "WHERE Absences.date = '" + java.sql.Date.valueOf(date) + "' " +
	                   "AND Students.class = '" + schoolClass.getClassName() + "'";

	    try {
	        PreparedStatement stmt = conn.prepareStatement(query);
	        ResultSet rs = stmt.executeQuery();
	        
	        while (rs.next()) {
	            int studentId = rs.getInt("id_student");
	            Boolean justification = rs.getBoolean("justification");
	            Student student = getStudentById(studentId);
	            Absence absence = new Absence(student, date, justification);
	            absences.add(absence);
	        }

	    } catch (SQLException e) {
	        throw new AbsenceDaoException("Database error while fetching absences.");
	    }

	    return absences.iterator();
	}


	@Override
	public Iterator<Absence> getAbsencesByStudent(Student student) throws AbsenceDaoException, DaoConnectionException {
		ArrayList<Absence> absences = new ArrayList<>();

		checkStudentExist(student);

		String query = "SELECT * FROM Absences WHERE id_student = " + student.getId();

		try {
			PreparedStatement stmt = conn.prepareStatement(query);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				Boolean justification = rs.getBoolean("justification");
				LocalDate date = LocalDate.parse(rs.getString("date"));
				Absence absence = new Absence(student, date, justification);
				absences.add(absence);
			} 

		} catch (SQLException e) {
			throw new AbsenceDaoException("Database error while fetching absences.");
		}

		return absences.iterator();
	}

	void checkStudentExist(Student student) throws AbsenceDaoException, DaoConnectionException {
		StudentDaoDatabase studentDaoDatabase = new StudentDaoDatabase(conn);
		try {
			studentDaoDatabase.getStudentById(student.getId());
		} catch (StudentDaoException e) {
			throw new AbsenceDaoException("Student doesn't exist.");
		} catch (DaoConnectionException e) {
			throw new AbsenceDaoException("Connection failed.");
		}
	}

	// NOTA è da sistemare di la!
	void checkScoolClassExist(SchoolClass schoolClass) throws AbsenceDaoException, DaoConnectionException {
		SchoolClassDaoDatabase schoolClassDaoDatabase = new SchoolClassDaoDatabase(conn);
		try {
			schoolClassDaoDatabase.getSchoolClassByName(schoolClass.getClassName());
		} catch (SchoolClassDaoException e) {
			throw new AbsenceDaoException("Class with name: " + schoolClass.getClassName() + " does not exist.");
		}
	}

	Student getStudentById(int studentId) throws AbsenceDaoException {
		StudentDaoDatabase studentDaoDatabase = new StudentDaoDatabase(conn);
		try {
			return studentDaoDatabase.getStudentById(studentId);
		} catch (StudentDaoException e) {
			throw new AbsenceDaoException("Student doesn't exist.");
		} catch (DaoConnectionException e) {
			throw new AbsenceDaoException("Connection failed.");
		}
	}

	void setConnection(Connection connection) {
		conn = connection;
	}

}
