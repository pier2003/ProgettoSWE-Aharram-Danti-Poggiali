package daoFactory;

import orm.AbsenceDao;
import orm.DaoConnectionException;
import orm.DatabaseManager;
import orm.DisciplinaryReportDao;
import orm.GradeDao;
import orm.HomeworkDao;
import orm.LessonDao;
import orm.MeetingAvailabilityDao;
import orm.MeetingDao;
import orm.ParentDao;
import orm.SchoolClassDao;
import orm.StudentDao;
import orm.StudentDaoDatabase;
import orm.TeacherDao;
import orm.TeacherDaoDatabase;
import orm.TeachingAssignmentDao;

public class DatabaseDaoFactory implements DaoFactory {

	@Override
	public StudentDao createStudentDao() throws DaoConnectionException {
		return new StudentDaoDatabase(DatabaseManager.getInstance().getConnection());
	}

	@Override
	public TeacherDao creatTeacherDao() throws DaoConnectionException {
		return new TeacherDaoDatabase(DatabaseManager.getInstance().getConnection());
	}

	@Override
	public GradeDao createGradeDao() {
		return null;
	}

	@Override
	public DisciplinaryReportDao createDisciplinaryReportDao() {
		return null;
	}

	@Override
	public SchoolClassDao createSchoolClassDao() {
		return null;
	}

	@Override
	public AbsenceDao createAbsenceDao() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HomeworkDao createHomeworkDao() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LessonDao createLessonDao() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ParentDao createParentDao() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TeachingAssignmentDao createTeachingAssignmentDao() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MeetingAvailabilityDao createMeetingAvailabilityDao() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MeetingDao createMeetingDao() {
		// TODO Auto-generated method stub
		return null;
	}
}
