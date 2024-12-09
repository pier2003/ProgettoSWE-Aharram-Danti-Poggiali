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
	public void addAbsence(Student student, LocalDate date) throws AbsenceDaoException, StudentDaoException {
		DaoUtils.checkStudentExist(student, conn);
	    String query = "INSERT INTO Absences (date, justification, id_student) VALUES (?, ?, ?)";
	    try (PreparedStatement stmt = conn.prepareStatement(query)) {
	        stmt.setDate(1, Date.valueOf(date));       
	        stmt.setBoolean(2, false);                
	        stmt.setInt(3, student.getId());       
	        stmt.executeUpdate();
	    } catch (SQLException e) {
	        throw new AbsenceDaoException("Database error while inserting absence.");
	    }
	}

	@Override
	public void deleteAbsence(Student student, LocalDate date) throws AbsenceDaoException, StudentDaoException {
		DaoUtils.checkStudentExist(student, conn);
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
	public Iterator<Absence> getAbsencesByClassInDate(SchoolClass schoolClass, LocalDate date)
	        throws AbsenceDaoException, SchoolClassDaoException {

		DaoUtils.checkScoolClassExist(schoolClass, conn);
	    List<Absence> absences = new ArrayList<>();
	    StudentDaoDatabase studentDaoDatabase = new StudentDaoDatabase(conn);
	    
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
	                Student student = studentDaoDatabase.getStudentById(studentId);
	                Absence absence = new Absence(student, date, justification);
	                absences.add(absence);
	            }
	        }
	    } catch (SQLException | StudentDaoException e) {
	        throw new AbsenceDaoException("Database error while fetching absences.");
	    }

	    return absences.iterator();
	}

	@Override
	public boolean checkStudentAttendanceInDay(Student student, LocalDate date) throws AbsenceDaoException, StudentDaoException {
	    try {
	    	DaoUtils.checkStudentExist(student, conn);
	        String query = "SELECT * FROM Absences WHERE id_student = ? AND date = ?;";
	        try (PreparedStatement stmt = conn.prepareStatement(query)) {
	            stmt.setInt(1, student.getId());
	            stmt.setDate(2, Date.valueOf(date));

	            try (ResultSet rs = stmt.executeQuery()) {
	                return rs.next();
	            }
	        }
	    } catch (SQLException e) {
	        throw new AbsenceDaoException("Database error while fetching absence data.");
	    }
	}


	@Override
	public Iterator<Absence> getAbsencesByStudent(Student student) throws AbsenceDaoException, StudentDaoException {
	    ArrayList<Absence> absences = new ArrayList<>();

	    DaoUtils.checkStudentExist(student, conn);

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



	@Override
	public void justifyAbsence(Absence absence) throws AbsenceDaoException {
		String query = "UPDATE Absences SET justification = 1 WHERE id_student = ? AND date = ?";
		try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, absence.getStudent().getId());
            stmt.setDate(2, Date.valueOf(absence.getDate()));
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 1) {
            	stmt.close();
            }
            else {
            	throw new AbsenceDaoException("Absence doesn't exists");
            }
		}
		catch (SQLException e) {
	        throw new AbsenceDaoException("Database error while fetching absence data.");
		}
	}


}
