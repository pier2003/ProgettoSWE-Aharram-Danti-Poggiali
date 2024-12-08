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
	public void addNewReport(Teacher teacher, Student student, String description, LocalDate date) throws DisciplinaryReportException {
		checkStudentExist(student);
		checkTeacherExist(teacher);
		
		String query = "INSERT INTO Reports (description, id_student, id_teacher, date) VALUES (?, ?, ?, ?)";
		
		try (PreparedStatement stmt = conn.prepareStatement(query)){
			stmt.setString(1, description);
			stmt.setInt(2, student.getId());
			stmt.setInt(3, teacher.getId());
			stmt.setDate(4, Date.valueOf(date));
			stmt.executeUpdate();
		} catch (SQLException e) {
			throw new DisciplinaryReportException("Database error while insert report");
		}
	}

	@Override
	public Iterator<DisciplinaryReport> getDisciplinaryReportsByStudent(Student student) 
	        throws DisciplinaryReportException {
	    ArrayList<DisciplinaryReport> reports = new ArrayList<>();
	    TeacherDaoDatabase teacherDaoDatabase = new TeacherDaoDatabase(conn);

	    checkStudentExist(student);

	    String query = "SELECT id_report, description, id_teacher, date FROM Reports WHERE id_student = ?";

	    try (PreparedStatement stmt = conn.prepareStatement(query)) {
	        stmt.setInt(1, student.getId());

	        try (ResultSet rs = stmt.executeQuery()) {
	            while (rs.next()) {
	                int idReport = rs.getInt("id_report");
	                String description = rs.getString("description");
	                int idTeacher = rs.getInt("id_teacher");
	                Teacher teacher = teacherDaoDatabase.getTeacherById(idTeacher);
	                LocalDate date = rs.getDate("date").toLocalDate();
	                reports.add(new DisciplinaryReport(idReport, student, teacher, date, description));
	                
	            }
	        }
	    } catch (SQLException | TeacherDaoException e) {
	        throw new DisciplinaryReportException("Database error while processing");
	    }

	    return reports.iterator();
	}


	@Override
	public void deleteReport(DisciplinaryReport report) throws DisciplinaryReportException {
		String query = "DELETE FROM Reports WHERE id_report = ?";

		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setInt(1, report.getId());

			int rowsAffected = stmt.executeUpdate();
			if (rowsAffected != 1) {
				throw new DisciplinaryReportException("Database error while deleting report");
			}
		} catch (SQLException e) {
			throw new DisciplinaryReportException("Database error while deleting report");
		}

	}
	

	private void checkStudentExist(Student student) throws DisciplinaryReportException {
		StudentDaoDatabase studentDaoDatabase = new StudentDaoDatabase(conn);
		try {
			studentDaoDatabase.getStudentById(student.getId());
		} catch (StudentDaoException e) {
			throw new DisciplinaryReportException("Student doesn't exist.");
		}
	}
	
	private void checkTeacherExist(Teacher teacher) throws DisciplinaryReportException {
		TeacherDaoDatabase teacherDaoDatabase = new TeacherDaoDatabase(conn);
		try {
			teacherDaoDatabase.getTeacherById(teacher.getId());
		} catch (TeacherDaoException e) {
			throw new DisciplinaryReportException("Teacher doesn't exist.");
		}
	}


}
