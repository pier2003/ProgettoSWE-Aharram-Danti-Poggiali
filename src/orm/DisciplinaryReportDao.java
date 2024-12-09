package orm;

import java.time.LocalDate;
import java.util.Iterator;

import domainModel.DisciplinaryReport;
import domainModel.Student;
import domainModel.Teacher;

public interface DisciplinaryReportDao {

	void addNewReport(Teacher teacher, Student student, String description, LocalDate date) throws DisciplinaryReportException, StudentDaoException, TeacherDaoException;
	
	Iterator<DisciplinaryReport> getDisciplinaryReportsByStudent(Student student) throws DisciplinaryReportException, StudentDaoException;

	void deleteReport(DisciplinaryReport report) throws DisciplinaryReportException;


}
