package businessLogic;

import java.time.LocalDate;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import daoFactory.DaoFactory;
import domainModel.Absence;
import domainModel.DisciplinaryReport;
import domainModel.Grade;
import domainModel.Homework;
import domainModel.Lesson;
import domainModel.Meeting;
import domainModel.MeetingAvailability;
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
import orm.SchoolClassDaoException;
import orm.StudentDaoException;
import orm.TeacherDaoException;
import strategyForGrade.GradeAverageStrategy;

public class TeacherController {

	private DaoFactory daoFactory;
	private Teacher teacher;

	public TeacherController(Teacher teacher, DaoFactory daoFactory) throws StudentDaoException, TeacherDaoException, DaoConnectionException {
		this.teacher = teacher;
		this.daoFactory = daoFactory;
	}

	public Teacher getTeacher() {
		return teacher;
	}
	
	//TEACHINGS
	
	public Iterator<TeachingAssignment> getAllMyTeachings(){
		return daoFactory.createTeachingAssignmentDao().getAllTeacherTeachings(teacher);
	}
	
	
	//SCHOOLCLASS

	public Iterator<SchoolClass> getClassByTeaching(TeachingAssignment teachingAssignment) throws SchoolClassDaoException, DaoConnectionException {
		return daoFactory.createSchoolClassDao().getAllSchoolClassesByTeaching(teachingAssignment);
	}

	
	//STUDENTS
	
	public Iterator<Student> getStudentsByClass(SchoolClass schoolClass) throws StudentDaoException, DaoConnectionException {
		return daoFactory.createStudentDao().getStudentsByClass(schoolClass);
	}

	
	//GRADES
	
	public Iterator<Grade> getAllStudentGradesByTeaching(Student student, TeachingAssignment teaching) throws GradeDaoException, DaoConnectionException {
		return daoFactory.createGradeDao().getStudentGradesByTeaching(student, teaching);
	}
	
	public void assignGradeToStudentInDate(double value, String description, TeachingAssignment teaching, Student student, LocalDate date)
			throws GradeDaoException, InvalidGradeValueException, DaoConnectionException {
		if (value >= 1 && value <= 10) {
			daoFactory.createGradeDao().addNewGrade(value, description, teaching, student, date);
		} else {
			throw new InvalidGradeValueException();
		}
	}

	public void assignGradeToStudentInDateWithWeight(double value, int weight, String description, TeachingAssignment teaching,
			Student student, LocalDate date)
			throws GradeDaoException, NegativeWeightException, InvalidGradeValueException, DaoConnectionException {
		if ((value >= 1 && value <= 10) && (weight >= 0)) {
			daoFactory.createGradeDao().addNewGradeWithWeight(value, weight, description, teaching, student, date);
		} else {
			if (weight < 0)
				throw new NegativeWeightException();
			else
				throw new InvalidGradeValueException();
		}
	}

	public void editGradeValue(Grade oldGrade, double value) throws GradeDaoException, DaoConnectionException {
		daoFactory.createGradeDao().editGradeValue(oldGrade, value);
	}

	public void editGradeWeight(Grade oldGrade, int weight) throws GradeDaoException, DaoConnectionException {
		daoFactory.createGradeDao().editGradeWeight(oldGrade, weight);
	}

	public void editGradeDescription(Grade oldGrade, String description) throws GradeDaoException, DaoConnectionException {
		daoFactory.createGradeDao().editGradeDescription(oldGrade, description);
	}
	
	public void deleteGrade(Grade grade) throws GradeDaoException, DaoConnectionException {
		daoFactory.createGradeDao().deleteGrade(grade);
	}
	
	public double calculateStudentTeachingGradeAverage(Student student, TeachingAssignment teaching, GradeAverageStrategy gradeAverageStrategy) throws GradeDaoException, DaoConnectionException {
		return gradeAverageStrategy.getAverage(getAllStudentGradesByTeaching(student, teaching));
	}

	
	//REPORTS
	
	public Iterator<DisciplinaryReport> getStudentDisciplinaryReports(Student student) throws DisciplinaryReportException, DaoConnectionException{
		return daoFactory.createDisciplinaryReportDao().getDisciplinaryReportsByStudent(student);	
	}
	
	public void assignDisciplinaryReportToStudentInDate(Student student, String description, LocalDate date) throws DisciplinaryReportException, DaoConnectionException {
		daoFactory.createDisciplinaryReportDao().addNewReport(teacher, student, description, date);
	}
	
	public void deleteDisciplinaryReport(DisciplinaryReport report) throws IllegalReportAccessException {
			if (report.getTeacher().equals(teacher)) {
				daoFactory.createDisciplinaryReportDao().deleteReport(report);
			}
			else {
				throw new IllegalReportAccessException();
			}
	}
	
	//ABSENCES

	public Iterator<Absence> getAbsencesByClassInDate(SchoolClass schoolClass, LocalDate date) throws AbsenceDaoException, DaoConnectionException {
		return daoFactory.createAbsenceDao().getAbsencesByClassInDate(schoolClass, date);
	}

	public Iterator<Absence> getAbsencesByStudent(Student student) throws AbsenceDaoException, DaoConnectionException {
		return daoFactory.createAbsenceDao().getAbsencesByStudent(student);
	}

	public void assignAbsenceToStudentInDate(Student student, LocalDate date) throws AbsenceDaoException, DaoConnectionException {
		daoFactory.createAbsenceDao().addAbsence(student, date);
	}

	public void deleteAbsence(Student student, LocalDate date) throws AbsenceDaoException, DaoConnectionException {
		daoFactory.createAbsenceDao().deleteAbsence(student, date);
	}

	
	//HOMEWORKS
	
	public void assignNewHomework(TeachingAssignment teaching, LocalDate date, String description,
			LocalDate subissionDate) throws HomeworkDaoException {
		daoFactory.createHomeworkDao().addHomework(teaching, date, description, subissionDate);
	}

	public void editHomeworkDescription(Homework homework, String description)
			throws HomeworkDaoException, IllegalHomeworkAccessException {
		if (homework.getTeaching().getTeacher().equals(teacher)) {
			daoFactory.createHomeworkDao().editHomeworkDescription(homework, description);
		}
		else {
			throw new IllegalHomeworkAccessException();
		}
	}
	
	public void editHomeworkSubmissionDate(Homework homework, LocalDate date)
			throws HomeworkDaoException, IllegalHomeworkAccessException {
		if (homework.getTeaching().getTeacher().equals(teacher)) {
			daoFactory.createHomeworkDao().editHomeworkSubmissionDate(homework, date);
		}
		else {
			throw new IllegalHomeworkAccessException();
		}
	}

	public Iterator<Homework> getClassHomeworksSubmissionDate(LocalDate date, SchoolClass schoolClass) throws DaoConnectionException, HomeworkDaoException {
		return daoFactory.createHomeworkDao().getHomeworksBySubmissionDate(date, schoolClass);
	}
	
		public void deleteHomework(Homework homework) throws IllegalHomeworkAccessException {
			if (homework.getTeaching().getTeacher().equals(teacher)) {
				daoFactory.createHomeworkDao().deleteHomework(homework);
			}
			else {
				throw new IllegalHomeworkAccessException();
			}
		}
	
	//LESSONS
	
	public void addNewLesson(TeachingAssignment teaching, LocalDate date, String description, LocalTime startHour,
			LocalTime endHour) throws LessonDaoException, DaoConnectionException {
		daoFactory.createLessonDao().addLesson(teaching, date, description, startHour, endHour);
	}

	public void editLessonDescription(Lesson lesson, String description) throws LessonDaoException, DaoConnectionException, IllegalLessonAccessException {
		if (lesson.getTeaching().getTeacher().equals(teacher)) {
			daoFactory.createLessonDao().editLessonDescription(lesson, description);
		}
		else {
			throw new IllegalLessonAccessException();
		}
	}
	
	public void editLessonDateTime(Lesson lesson, LocalDate date, LocalTime startHour, LocalTime endHour) throws LessonDaoException, DaoConnectionException, IllegalLessonAccessException {
		if (lesson.getTeaching().getTeacher().equals(teacher)) {
			daoFactory.createLessonDao().editLessonDateTime(lesson, date, startHour, endHour);
		}
		else {
			throw new IllegalLessonAccessException();
		}
	}
	

	public Iterator<Lesson> getClassLessonsInDay(LocalDate date, SchoolClass schoolClass) throws DaoConnectionException, LessonDaoException {
		return daoFactory.createLessonDao().getLessonsInDay(date, schoolClass);
	}
	
	public void deleteLesson(Lesson lesson) throws IllegalHomeworkAccessException {
		if (lesson.getTeaching().getTeacher().equals(teacher)) {
			daoFactory.createLessonDao().deleteLesson(lesson);
		}
		else {
			throw new IllegalHomeworkAccessException();
		}
	}
	
	
	//MEETING
	
	public void addNewMeetingAvailabilityInDate(LocalDate date) {
		daoFactory.createMeetingAvailabilityDao().addMeetingAvailabilityInDate(teacher, date);
	}
	
	public Iterator<MeetingAvailability> getMeetingAvailabilities(){
		List<MeetingAvailability> unboockedMeetings = new ArrayList<MeetingAvailability>();
		Iterator<MeetingAvailability> allMeetings =  daoFactory.createMeetingAvailabilityDao().getAllMeetingsAvaialabilityByTeacher(teacher);
		while (allMeetings.hasNext()) {
			MeetingAvailability meetingAvailability = allMeetings.next();
			if (!meetingAvailability.isBooked()) {
				unboockedMeetings.add(meetingAvailability);
			}
		}
		return unboockedMeetings.iterator();
	}
	
	public Iterator<Meeting> getBookedMeetings(){
		return daoFactory.createMeetingDao().getMeetingsByTeacher(teacher);
	}
	
	public void deleteMeetingAvailability(MeetingAvailability meetingAvailability) throws MeetingAlreadyBookedException {
		if (!meetingAvailability.isBooked()) {
			daoFactory.createMeetingAvailabilityDao().deleteMeetingAvailability(meetingAvailability);
		}
		else {
			throw new MeetingAlreadyBookedException();
		}
	}

	
	public class MeetingAlreadyBookedException extends Exception {
		private static final long serialVersionUID = 1L;
	}
	
	public class IllegalHomeworkAccessException extends Exception {
		private static final long serialVersionUID = 1L;
	}
	
	
	public class IllegalLessonAccessException extends Exception {
		private static final long serialVersionUID = 1L;
	}
	
	public class IllegalReportAccessException extends Exception {
		private static final long serialVersionUID = 1L;
	}
}
