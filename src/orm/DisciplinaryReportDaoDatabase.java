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
	public Iterator<DisciplinaryReport> getDisciplinaryReportsByStudent(Student student) throws DisciplinaryReportException, DaoConnectionException {
		ArrayList<DisciplinaryReport> reports = new ArrayList<DisciplinaryReport>();
		
		checkStudentExist(student);
		
		String query = "SELECT * FROM Reports WHERE id_student = ?";
		
		try {
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setInt(1, student.getId());
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next()) {
				String descrption = rs.getString("description");
				int id_teacher = rs.getInt("id_teacher");
				Teacher teacher = getTeacherById(id_teacher);
				LocalDate date = LocalDate.parse(rs.getString("date"));
				reports.add(new DisciplinaryReport(student, teacher, date, descrption));
			}
				
		} catch (SQLException e) {
			throw new DisciplinaryReportException("Database error while processing");
		}
		
		return reports.iterator();
	}
	
	Teacher getTeacherById(int id_teacher) throws DisciplinaryReportException {
		try {
			return new TeacherDaoDatabase(conn).getTeacherById(id_teacher);
		} catch (TeacherDaoException e) {
			throw new DisciplinaryReportException("Teacher dosen't exist.");
		} catch (DaoConnectionException e) {
			throw new DisciplinaryReportException("Connection failed.");
		}
	}

	void checkStudentExist(Student student) throws DaoConnectionException, DisciplinaryReportException {
		StudentDaoDatabase studentDaoDatabase = new StudentDaoDatabase(conn);
		try {
			studentDaoDatabase.getStudentById(student.getId());
		} catch (StudentDaoException e) {
			throw new DisciplinaryReportException("Student doesn't exist.");
		} catch (DaoConnectionException e) {
			throw new DisciplinaryReportException("Connection failed.");
		}
	}

}
