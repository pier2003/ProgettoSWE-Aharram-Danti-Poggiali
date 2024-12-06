package businessLogic;

import java.time.LocalDate;

import java.util.Iterator;

import daoFactory.DaoFactory;
import domainModel.DisciplinaryReport;
import domainModel.Grade;
import domainModel.Homework;
import domainModel.Lesson;
import domainModel.SchoolClass;
import domainModel.Student;
import domainModel.TeachingAssignment;
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
	private SchoolClass schoolClass;

	public StudentController(int id, DaoFactory daoFactory) throws StudentDaoException, SchoolClassDaoException, DaoConnectionException {
		this.daoFactory = daoFactory;
		student = daoFactory.createStudentDao().getStudentById(id);
		schoolClass = daoFactory.createSchoolClassDao().getSchoolClassByStudent(student);
	}
	
	public Student viewStudent() {
		return student;
	}
	
	public Iterator<TeachingAssignment> getTeachings() throws TeachingAssignmentDaoException, DaoConnectionException {
		return daoFactory.createTeachingAssignmentDao().getAllStudentTeachings(student.getId());
	}
	
	public Iterator<Grade> getGradesByTeaching(TeachingAssignment teaching) throws GradeDaoException, DaoConnectionException {
		return daoFactory.createGradeDao().getStudentGradesByTeaching(student.getId(), teaching.getId());
	}
	
	public Iterator<Grade> getAllStudentGrades() throws GradeDaoException, DaoConnectionException {
		return daoFactory.createGradeDao().getAllStudentGrades(student.getId());
	}
	
	public Iterator<DisciplinaryReport> getDisciplinaryReports() throws DisciplinaryReportException, DaoConnectionException {
		return daoFactory.createDisciplinaryReportDao().getDisciplinaryReportsByStudent(student);
	}
	
	public Iterator<Homework> getHomeworkInDate(LocalDate date) throws DaoConnectionException, HomeworkDaoException {
		return daoFactory.createHomeworkDao().getHomeworksInDay(date, schoolClass.getClassName());
	}
	
	public Iterator<Lesson> getLessonInDate(LocalDate date) throws DaoConnectionException, LessonDaoException {
		return daoFactory.createLessonDao().getLessonsInDay(date, schoolClass.getClassName());
	}
	
	
	public double calculateTeachingGradeAverage(TeachingAssignment teaching, GradeAverageStrategy gradeAverageStrategy) throws GradeDaoException, DaoConnectionException {
		return gradeAverageStrategy.getAverage(getGradesByTeaching(teaching));
	}
	
	public double calculateTotalGradeAverage(GradeAverageStrategy gradeAverageStrategy) throws GradeDaoException, DaoConnectionException {
		return gradeAverageStrategy.getAverage(getAllStudentGrades());
	}
}