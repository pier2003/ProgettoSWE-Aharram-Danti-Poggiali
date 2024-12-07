package orm;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;

import domainModel.DisciplinaryReport;
import domainModel.Student;
import domainModel.Teacher;

public class DisciplinaryReportDaoDatabase implements DisciplinaryReportDao {
	
	private Connection conn;

	public DisciplinaryReportDaoDatabase(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void addNewReport(Teacher teacher, Student student, String description, LocalDate date) throws DisciplinaryReportException, DaoConnectionException {
		checkStudentExist(student);
		
		String query = "INSERT INTO Reports (description, id_student, id_teacher, date) VALUES (?, ?, ?, ?)";
		
		try {
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setString(1, description);
			stmt.setInt(2, student.getId());
			stmt.setInt(3, teacher.getId());
			stmt.setDate(4, Date.valueOf(date));
			stmt.executeUpdate();
			if(stmt != null)
				stmt.close();
		} catch (SQLException e) {
			throw new DisciplinaryReportException("Database error while insert report.");
		}
	}

	@Override
	public Iterator<DisciplinaryReport> getDisciplinaryReportsByStudent(Student student) 
	        throws DisciplinaryReportException, DaoConnectionException {
	    ArrayList<DisciplinaryReport> reports = new ArrayList<>();
	    TeacherDaoDatabase teacherDaoDatabase = new TeacherDaoDatabase(conn);

	    checkStudentExist(student);

	    String query = "SELECT * FROM Reports WHERE id_student = ?";

	    try (PreparedStatement stmt = conn.prepareStatement(query)) {
	        stmt.setInt(1, student.getId());

	        try (ResultSet rs = stmt.executeQuery()) {
	            while (rs.next()) {
	                int idReport = rs.getInt("id_report");
	                String description = rs.getString("description");
	                int idTeacher = rs.getInt("id_teacher");
	                Teacher teacher = teacherDaoDatabase.getTeacherById(idTeacher);
	                LocalDate date = LocalDate.parse(rs.getString("date"));
	                
	                reports.add(new DisciplinaryReport(idReport, student, teacher, date, description));
	            }
	        }
	    } catch (SQLException | TeacherDaoException e) {
	        throw new DisciplinaryReportException("Database error while processing");
	    }
	    return reports.iterator();
	}

	
	
	
	
	
	@Override
	public void deleteReport(DisciplinaryReport report) {
		// TODO Auto-generated method stub
		
	}
	

	private void checkStudentExist(Student student) throws DisciplinaryReportException {
		StudentDaoDatabase studentDaoDatabase = new StudentDaoDatabase(conn);
		try {
			studentDaoDatabase.getStudentById(student.getId());
		} catch (StudentDaoException e) {
			throw new DisciplinaryReportException("Student doesn't exist.");
		}
	}


}
