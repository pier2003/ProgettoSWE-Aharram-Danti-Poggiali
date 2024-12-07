package orm;

import java.time.LocalDate;
import java.util.Iterator;

import domainModel.MeetingAvailability;
import domainModel.Teacher;
import domainModel.TeachingAssignment;

public interface MeetingAvailabilityDao {

	void addMeetingAvailabilityInDate(Teacher teacher, LocalDate date);

	void editBooking(MeetingAvailability meetingAvailability);

	Iterator<MeetingAvailability> getAllMeetingsAvaialabilityByTeacher(Teacher teacher);

	void deleteMeetingAvailability(MeetingAvailability meetingAvailability);

}
