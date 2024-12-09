package orm;

import java.util.Iterator;

import domainModel.Meeting;
import domainModel.MeetingAvailability;
import domainModel.Parent;
import domainModel.Teacher;

public class MeetingDaoDatabase implements MeetingDao {

	@Override
	public void bookMeeting(MeetingAvailability meetingAvailability, Parent parent) {
		System.out.println("");
	}

	@Override
	public Iterator<Meeting> getAllMeetingsByParent(Parent parent) {
		return null;
	}

	@Override
	public Iterator<Meeting> getMeetingsByTeacher(Teacher teacher) {
		return null;
	}

}
