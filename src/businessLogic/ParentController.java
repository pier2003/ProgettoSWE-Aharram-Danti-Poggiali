package businessLogic;

import java.time.LocalDate;


import java.util.Iterator;

import daoFactory.DaoFactory;
import domainModel.DisciplinaryReport;
import domainModel.Grade;
import domainModel.Homework;
import domainModel.Lesson;
import domainModel.Parent;
import domainModel.Student;
import domainModel.TeachingAssignment;
import orm.DaoConnectionException;
import orm.DisciplinaryReportException;
import orm.GradeDaoException;
import orm.HomeworkDaoException;
import orm.LessonDaoException;
import orm.ParentDao;
import orm.ParentDaoException;
import orm.SchoolClassDaoException;
import orm.StudentDaoException;
import orm.TeachingAssignmentDaoException;
import strategyForGrade.GradeAverageStrategy;

public class ParentController {

	private StudentController studentController;
	private Parent parent;

	public ParentController(int idParent, DaoFactory daoFactory) throws StudentDaoException, SchoolClassDaoException, DaoConnectionException, ParentDaoException {
		ParentDao parentDao = daoFactory.createParentDao();
		Student student = parentDao.getStudentOfParentByParentId(idParent);
		studentController = new StudentController(student.getId(), daoFactory);
		parent = parentDao.getParentById(idParent);
	}
	
	public Parent viewParent() {
		return parent;
	}
	
	public Iterator<TeachingAssignment> getTeachings() throws TeachingAssignmentDaoException, DaoConnectionException {
		return studentController.getTeachings();
	}
	
	public Iterator<Grade> getGradesByTeaching(TeachingAssignment teaching) throws GradeDaoException, DaoConnectionException {
		return studentController.getGradesByTeaching(teaching);
	}
	
	public Iterator<Grade> getAllStudentGrades() throws GradeDaoException, DaoConnectionException {
		return studentController.getAllStudentGrades();
	}
	
	public Iterator<DisciplinaryReport> getDisciplinaryReports() throws DisciplinaryReportException, DaoConnectionException {
		return studentController.getDisciplinaryReports();
	}
	
	
	public Iterator<Homework> getHomeworkInDate(LocalDate date) throws DaoConnectionException, HomeworkDaoException {
		return studentController.getHomeworkInDate(date);
	}
	
	public Iterator<Lesson> getLessonInDate(LocalDate date) throws DaoConnectionException, LessonDaoException {
		return studentController.getLessonInDate(date);
	}
	
	public double calculateTeachingGradeAverage(TeachingAssignment teaching, GradeAverageStrategy gradeAverageStrategy) throws GradeDaoException, DaoConnectionException {
		return studentController.calculateTeachingGradeAverage(teaching, gradeAverageStrategy);
	}
	
	public double calculateTotalGradeAverage(GradeAverageStrategy gradeAverageStrategy) throws GradeDaoException, DaoConnectionException {
		return studentController.calculateTotalGradeAverage(gradeAverageStrategy);
	}
	
	public void bookAMeeting() {
		// TODO
	}
	
}
