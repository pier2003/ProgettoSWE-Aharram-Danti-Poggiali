package orm;

import java.time.LocalDate;

public interface MeetingAvailabilityDao {

	void addMeetingAvailabilityInDate(int id, LocalDate date);

}
