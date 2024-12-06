package businessLogic;

import java.time.LocalDate;

import java.time.LocalTime;
import java.util.Iterator;

import daoFactory.DaoFactory;
import domainModel.Absence;
import domainModel.Homework;
import domainModel.Lesson;
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

public class TeacherController {

	private DaoFactory daoFactory;
	private Teacher teacher;

	public TeacherController(int id, DaoFactory daoFactory) throws StudentDaoException, TeacherDaoException, DaoConnectionException {
		this.daoFactory = daoFactory;
		this.teacher = daoFactory.creatTeacherDao().getTeacherById(id);
	}

	public Teacher viewTeacher() {
		return teacher;
	}

	public Iterator<SchoolClass> getAllMyClasses() throws SchoolClassDaoException, DaoConnectionException {
		return daoFactory.createSchoolClassDao().getAllSchoolClassesByTeacher(teacher);
	}

	public Iterator<Student> getStudentsByClass(String schoolClassName) throws StudentDaoException, DaoConnectionException {
		return daoFactory.createStudentDao().getStudentsByClass(schoolClassName);
	}

	public Student getStudentById(int id) throws StudentDaoException, DaoConnectionException {
		return daoFactory.createStudentDao().getStudentById(id);
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

	public void editGradeValueById(int id, double value) throws GradeDaoException, DaoConnectionException {
		daoFactory.createGradeDao().editGradeValue(id, value);
	}

	public void editGradeWeightById(int id, int weight) throws GradeDaoException, DaoConnectionException {
		daoFactory.createGradeDao().editGradeWeight(id, weight);
	}

	public void editGradeDescriptionById(int id, String description) throws GradeDaoException, DaoConnectionException {
		daoFactory.createGradeDao().editGradeDescription(id, description);
	}

	public void assignDisciplinaryReportToStudentInDate(Student student, String description, LocalDate date) throws DisciplinaryReportException, DaoConnectionException {
		daoFactory.createDisciplinaryReportDao().addNewReport(teacher, student, description, date);
	}

	public Iterator<Absence> getAbsencesByClassInDate(SchoolClass schoolClass, LocalDate date) throws AbsenceDaoException, DaoConnectionException {
		return daoFactory.createAbsenceDao().getAbsencesByClassInDate(schoolClass, date);
	}

	public int getNumberOfAbsencesByClassInDate(SchoolClass schoolClass, LocalDate date) throws AbsenceDaoException, DaoConnectionException {
		Iterator<Absence> absences = getAbsencesByClassInDate(schoolClass, date);
		int cont = 0;
		while (absences.hasNext()) {
			absences.next();
			cont++;
		}
		return cont;
	}

	public Iterator<Absence> getAbsencesByStudent(Student student) throws AbsenceDaoException, DaoConnectionException {
		return daoFactory.createAbsenceDao().getAbsencesByStudent(student);
	}

	public void assignAbsenceToStudentOnDate(Student student, LocalDate date) throws AbsenceDaoException, DaoConnectionException {
		daoFactory.createAbsenceDao().addAbsence(student, date);
	}

	public void deleteAbsence(Student student, LocalDate date) throws AbsenceDaoException, DaoConnectionException {
		daoFactory.createAbsenceDao().deleteAbsence(student, date);
	}

	public void assignNewHomework(TeachingAssignment teaching, LocalDate date, String description,
			LocalDate subissionDate) throws HomeworkDaoException {
		daoFactory.createHomeworkDao().addHomework(teaching, date, description, subissionDate);
	}

	public void addNewLesson(TeachingAssignment teaching, LocalDate date, String description, LocalTime startHour,
			LocalTime endHour) throws LessonDaoException, DaoConnectionException {
		daoFactory.createLessonDao().addLesson(teaching, date, description, startHour, endHour);
	}

	public void editHomeworkDescription(Homework homework, String description)
			throws HomeworkDaoException {
		daoFactory.createHomeworkDao().editHomeworkDescription(homework.getId(), description);
	}
	
	public void editHomeworkSubmissionDate(Homework homework, LocalDate date)
			throws HomeworkDaoException {
		daoFactory.createHomeworkDao().editHomeworkSubmissionDate(homework.getId(), date);
	}


	public void editLessonDescription(Lesson lesson, String description) throws LessonDaoException, DaoConnectionException {
		daoFactory.createLessonDao().editLessonDescription(lesson.getId(), description);
	}
	
	public void editLessonDateTime(Lesson lesson, LocalDate date, LocalTime startHour, LocalTime endHour) throws LessonDaoException, DaoConnectionException {
		daoFactory.createLessonDao().editLessonDateTime(lesson.getId(), date, startHour, endHour);
	}
	

	public Iterator<Homework> getHomeworksInDay(LocalDate date, SchoolClass schoolClass) throws DaoConnectionException, HomeworkDaoException {
		return daoFactory.createHomeworkDao().getHomeworksInDay(date, schoolClass.getClassName());
	}

	public Iterator<Lesson> getLessonsInDay(LocalDate date, SchoolClass schoolClass) throws DaoConnectionException, LessonDaoException {
		return daoFactory.createLessonDao().getLessonsInDay(date, schoolClass.getClassName());
	}
	
	// TODO parte dei colloqui
	
	public void addNewMeetingAvailabilityInDate(LocalDate date) {
		daoFactory.createMeetingAvailabilityDao().addMeetingAvailabilityInDate(teacher.getId(), date);
	}

}
