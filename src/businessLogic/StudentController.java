package businessLogic;

import java.time.LocalDate;

import java.util.Iterator;

import daoFactory.DaoFactory;
import domainModel.Absence;
import domainModel.DisciplinaryReport;
import domainModel.Grade;
import domainModel.Homework;
import domainModel.Lesson;
import domainModel.SchoolClass;
import domainModel.Student;
import domainModel.TeachingAssignment;
import orm.AbsenceDaoException;
import orm.DaoConnectionException;
import orm.DisciplinaryReportException;
import orm.GradeDaoException;
import orm.HomeworkDaoException;
import orm.LessonDaoException;
import orm.SchoolClassDaoException;
import orm.StudentDaoException;
import orm.TeachingAssignmentDaoException;
import strategyForGrade.GradeAverageStrategy;

public class StudentController {

	private Student student;
	private DaoFactory daoFactory;

	public StudentController(Student student, DaoFactory daoFactory) throws StudentDaoException, DaoConnectionException {
		this.student = student;
		this.daoFactory = daoFactory;
	}
	
	public Student getStudent() {
		return student;
	}
	
	//TEACHINGS
	
	public Iterator<TeachingAssignment> getTeachings() throws TeachingAssignmentDaoException, DaoConnectionException {
		return daoFactory.createTeachingAssignmentDao().getAllStudentTeachings(student);
	}
	
	//GRADES
	
	public Iterator<Grade> getGradesByTeaching(TeachingAssignment teaching) throws GradeDaoException, DaoConnectionException, StudentDaoException, TeachingAssignmentDaoException {
		return daoFactory.createGradeDao().getStudentGradesByTeaching(student, teaching);
	}
	
	public Iterator<Grade> getAllStudentGrades() throws GradeDaoException, DaoConnectionException, StudentDaoException {
		return daoFactory.createGradeDao().getAllStudentGrades(student);
	}
	
	public double calculateTeachingGradeAverage(TeachingAssignment teaching, GradeAverageStrategy gradeAverageStrategy) throws GradeDaoException, DaoConnectionException, StudentDaoException, TeachingAssignmentDaoException {
		return gradeAverageStrategy.getAverage(getGradesByTeaching(teaching));
	}
	
	public double calculateTotalGradeAverage(GradeAverageStrategy gradeAverageStrategy) throws GradeDaoException, DaoConnectionException, StudentDaoException {
		return gradeAverageStrategy.getAverage(getAllStudentGrades());
	}
	
	//REPORTS
	
	public Iterator<DisciplinaryReport> getDisciplinaryReports() throws DisciplinaryReportException, DaoConnectionException, StudentDaoException {
		return daoFactory.createDisciplinaryReportDao().getDisciplinaryReportsByStudent(student);
	}
	
	
	//HOMEWORK
	
	public Iterator<Homework> getHomeworksBySubmissionDate(LocalDate date) throws DaoConnectionException, HomeworkDaoException, SchoolClassDaoException {
		return daoFactory.createHomeworkDao().getHomeworksBySubmissionDate(date, student.getSchoolClass());
	}
	
	
	//LESSON
	
	public Iterator<Lesson> getLessonInDate(LocalDate date) throws DaoConnectionException, LessonDaoException, SchoolClassDaoException {
		return daoFactory.createLessonDao().getLessonsInDay(date, student.getSchoolClass());
	}
	
	
	//ABSENCES
	
	public Iterator<Absence> getAllStudentAbsences() throws AbsenceDaoException, DaoConnectionException, StudentDaoException {
		return daoFactory.createAbsenceDao().getAbsencesByStudent(student);
	}
 	
	public boolean checkStudentAttendanceInDay(LocalDate date) throws AbsenceDaoException, DaoConnectionException, StudentDaoException {
		return daoFactory.createAbsenceDao().checkStudentAttendanceInDay(student, date);
	}

	
}