package businessLogic;

import java.time.LocalDate;


import java.util.Iterator;

import daoFactory.DaoFactory;
import domainModel.Absence;
import domainModel.DisciplinaryReport;
import domainModel.Grade;
import domainModel.Homework;
import domainModel.Lesson;
import domainModel.Meeting;
import domainModel.MeetingAvailability;
import domainModel.Parent;
import domainModel.SchoolClass;
import domainModel.Student;
import domainModel.Teacher;
import domainModel.TeachingAssignment;
import orm.AbsenceDaoException;
import orm.DaoConnectionException;
import orm.DisciplinaryReportException;
import orm.GradeDaoException;
import orm.HomeworkDaoException;
import orm.LessonDaoException;
import orm.ParentDaoException;
import orm.SchoolClassDaoException;
import orm.StudentDaoException;
import orm.TeachingAssignmentDaoException;
import strategyForGrade.GradeAverageStrategy;

public class ParentController {

	private StudentController studentController;
	private Parent parent;
	private DaoFactory daoFactory;
	private Student student;

	public ParentController(Parent parent, DaoFactory daoFactory)
			throws StudentDaoException, SchoolClassDaoException, DaoConnectionException, ParentDaoException {
		this.parent = parent;
		this.daoFactory = daoFactory;
		student = parent.getStudent();
		studentController = new StudentController(student, daoFactory);
	}

	public Parent getParent() {
		return parent;
	}
	
	//SCHOOLCLASS
	
	public SchoolClass getSchoolClass() throws SchoolClassDaoException, DaoConnectionException {
		return student.getSchoolClass();
	}
	
	//TEACHINGS
	
	public Iterator<TeachingAssignment> getTeachings() throws TeachingAssignmentDaoException, DaoConnectionException {
		return studentController.getTeachings();
	}

	//GRADES
	
	public Iterator<Grade> getGradesByTeaching(TeachingAssignment teaching)
			throws GradeDaoException, DaoConnectionException, StudentDaoException, TeachingAssignmentDaoException {
		return studentController.getGradesByTeaching(teaching);
	}

	public Iterator<Grade> getAllStudentGrades() throws GradeDaoException, DaoConnectionException, StudentDaoException {
		return studentController.getAllStudentGrades();
	}
	
	public double calculateTeachingGradeAverage(TeachingAssignment teaching, GradeAverageStrategy gradeAverageStrategy)
			throws GradeDaoException, DaoConnectionException, StudentDaoException, TeachingAssignmentDaoException {
		return studentController.calculateTeachingGradeAverage(teaching, gradeAverageStrategy);
	}

	public double calculateTotalGradeAverage(GradeAverageStrategy gradeAverageStrategy)
			throws GradeDaoException, DaoConnectionException, StudentDaoException {
		return studentController.calculateTotalGradeAverage(gradeAverageStrategy);
	}
	
	
	//REPORTS

	public Iterator<DisciplinaryReport> getDisciplinaryReports()
			throws DisciplinaryReportException, DaoConnectionException, StudentDaoException {
		return studentController.getDisciplinaryReports();
	}

	//HOMEWORK
	
	public Iterator<Homework> getHomeworksBySubmissionDate(LocalDate date) throws DaoConnectionException, HomeworkDaoException, SchoolClassDaoException {
		return studentController.getHomeworksBySubmissionDate(date);
	}

	//LESSON
	
	public Iterator<Lesson> getLessonInDate(LocalDate date) throws DaoConnectionException, LessonDaoException, SchoolClassDaoException {
		return studentController.getLessonInDate(date);
	}

	//MEETINGS

	public Iterator<MeetingAvailability> getAllMeetingsAvaialabilityByTeacher(Teacher teacher) {
		return daoFactory.createMeetingAvailabilityDao().getAllMeetingsAvaialabilityByTeacher(teacher);
	}

	public void bookAMeeting(MeetingAvailability meetingAvailability) throws AlreadyBookedMeetingException {
		if (meetingAvailability.isBooked()) {
			throw new AlreadyBookedMeetingException();
		}
		daoFactory.createMeetingDao().bookMeeting(meetingAvailability, parent);
		daoFactory.createMeetingAvailabilityDao().editBooking(meetingAvailability);
	}

	public Iterator<Meeting> getAllMyMeetings() {
		return daoFactory.createMeetingDao().getAllMeetingsByParent(parent);
	}

	
	//ABSENCE
	
	public Iterator<Absence> getAllStudentAbsences() throws AbsenceDaoException, DaoConnectionException, StudentDaoException {
		return studentController.getAllStudentAbsences();
	}

	public void justifyAbsence(Absence absence) throws AbsenceDaoException {
		daoFactory.createAbsenceDao().justifyAbsence(absence);
	}
	
	public boolean checkStudentAttendanceInDay(LocalDate date) throws AbsenceDaoException, DaoConnectionException, StudentDaoException {
		return studentController.checkStudentAttendanceInDay(date);
	}

	
	
	public class AlreadyBookedMeetingException extends Exception {

		private static final long serialVersionUID = 1L;

	}
}
