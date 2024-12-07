package orm;

import java.util.Iterator;

import domainModel.Meeting;
import domainModel.MeetingAvailability;
import domainModel.Parent;
import domainModel.Teacher;

public interface MeetingDao {

	void bookMeeting(MeetingAvailability meetingAvailability, Parent parent);

	Iterator<Meeting> getAllMeetingsByParent(Parent parent);

	Iterator<Meeting> getMeetingsByTeacher(Teacher teacher);

}
