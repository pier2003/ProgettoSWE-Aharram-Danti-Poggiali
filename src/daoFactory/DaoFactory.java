package daoFactory;
import orm.AbsenceDao;
import orm.DaoConnectionException;
import orm.DisciplinaryReportDao;
import orm.GradeDao;
import orm.HomeworkDao;
import orm.LessonDao;
import orm.MeetingAvailabilityDao;
import orm.MeetingDao;
import orm.ParentDao;
import orm.SchoolClassDao;
import orm.StudentDao;
import orm.TeacherDao;
import orm.TeachingAssignmentDao;

public interface DaoFactory {
	StudentDao createStudentDao() throws DaoConnectionException;
	TeacherDao creatTeacherDao() throws DaoConnectionException;
	GradeDao createGradeDao();
	DisciplinaryReportDao createDisciplinaryReportDao();
	SchoolClassDao createSchoolClassDao();
	AbsenceDao createAbsenceDao();
	HomeworkDao createHomeworkDao();
	LessonDao createLessonDao();
	ParentDao createParentDao();
	TeachingAssignmentDao createTeachingAssignmentDao();
	MeetingAvailabilityDao createMeetingAvailabilityDao();
	MeetingDao createMeetingDao();
}
