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

import domainModel.Absence;
import domainModel.SchoolClass;
import domainModel.Student;

public class AbsenceDaoDatabase implements AbsenceDao {

	private Connection conn;

	public AbsenceDaoDatabase(Connection conn) {
		this.conn = conn;
	}


	@Override
	public void addAbsence(Student student, LocalDate date) throws AbsenceDaoException, DaoConnectionException {
	    checkStudentExist(student);
	    String query = "INSERT INTO Absences (date, justification, id_student) VALUES (?, ?, ?)";
	    try (PreparedStatement stmt = conn.prepareStatement(query)) {
	        stmt.setDate(1, Date.valueOf(date));       
	        stmt.setInt(2, 0);                
	        stmt.setInt(3, student.getId());       
	        stmt.executeUpdate();
	    } catch (SQLException e) {
	        throw new AbsenceDaoException("Database error while inserting absence.");
	    }
	}

	@Override
	public void deleteAbsence(Student student, LocalDate date) throws AbsenceDaoException, DaoConnectionException {
	    checkStudentExist(student);
	    String query = "DELETE FROM Absences WHERE date = ? AND id_student = ?";
	    
	    try (PreparedStatement stmt = conn.prepareStatement(query)) {
	        stmt.setDate(1, Date.valueOf(date));      
	        stmt.setInt(2, student.getId());         
	        
	        int rowsAffected = stmt.executeUpdate();
	        if (rowsAffected == 0) {
	            throw new AbsenceDaoException(
	                "No absence found to delete for student " + student.getId() + " on " + date);
	        }
	    } catch (SQLException e) {
	        throw new AbsenceDaoException("Database error while deleting absence.");
	    }
	}

	@Override
	public boolean checkStudentAttendanceInDay(Student student, LocalDate date) throws AbsenceDaoException {
	    try {
	        checkStudentExist(student);
	        String query = "SELECT * FROM Absences WHERE id_student = ? AND date = ?;";
	        try (PreparedStatement stmt = conn.prepareStatement(query)) {
	            stmt.setInt(1, student.getId());
	            stmt.setDate(2, Date.valueOf(date));

	            try (ResultSet rs = stmt.executeQuery()) {
	                return rs.next();
	            }
	        }
	    } catch (SQLException | AbsenceDaoException | DaoConnectionException e) {
	        throw new AbsenceDaoException("Database error while fetching absence data.");
	    }
	}

	@Override
	public Iterator<Absence> getAbsencesByClassInDate(SchoolClass schoolClass, LocalDate date)
	        throws AbsenceDaoException, DaoConnectionException {

	    checkScoolClassExist(schoolClass);
	    List<Absence> absences = new ArrayList<>();

	    String query = "SELECT Absences.date, Absences.justification, Absences.id_student " +
	                   "FROM Absences " +
	                   "JOIN Students ON Absences.id_student = Students.id_student " +
	                   "WHERE Absences.date = ? AND Students.class = ?";
	    try (PreparedStatement stmt = conn.prepareStatement(query)) {
	        stmt.setDate(1, Date.valueOf(date));                 
	        stmt.setString(2, schoolClass.getClassName());        

	        try (ResultSet rs = stmt.executeQuery()) {
	            while (rs.next()) {
	                int studentId = rs.getInt("id_student");
	                boolean justification = rs.getBoolean("justification");
	                Student student = getStudentById(studentId);
	                Absence absence = new Absence(student, date, justification);
	                absences.add(absence);
	            }
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

	    String query = "SELECT date, justification FROM Absences WHERE id_student = ?";
	    try (PreparedStatement stmt = conn.prepareStatement(query)) {
	        stmt.setInt(1, student.getId());          

	        try (ResultSet rs = stmt.executeQuery()) {
	            while (rs.next()) {
	                LocalDate date = rs.getDate("date").toLocalDate();   
	                boolean justification = rs.getBoolean("justification");
	                Absence absence = new Absence(student, date, justification);
	                absences.add(absence);
	            }
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


	@Override
	public void justifyAbsence(Absence absence) {
		// TODO Auto-generated method stub
		
	}

}
