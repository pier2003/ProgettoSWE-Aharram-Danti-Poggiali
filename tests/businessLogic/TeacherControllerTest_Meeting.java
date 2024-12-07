package businessLogic;

import static org.easymock.EasyMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.Before;
import org.junit.Test;

import businessLogic.TeacherController.MeetingAlreadyBookedException;
import daoFactory.DaoFactory;
import domainModel.Meeting;
import domainModel.MeetingAvailability;
import domainModel.Teacher;
import orm.DaoConnectionException;
import orm.MeetingAvailabilityDao;
import orm.MeetingDao;
import orm.StudentDaoException;
import orm.TeacherDaoException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class TeacherControllerTest_Meeting {

	private DaoFactory factoryMock;
	private Teacher teacher;
	private TeacherController teacherController;
	private LocalDate date;
	private LocalTime hour;
	private MeetingAvailabilityDao meetingAvailabilityDaoMock;
	private MeetingDao meetingDaoMock;

	@Before
	public void setup() throws StudentDaoException, TeacherDaoException, DaoConnectionException {
		factoryMock = createMock(DaoFactory.class);
		meetingAvailabilityDaoMock = createMock(MeetingAvailabilityDao.class);
		meetingDaoMock = createMock(MeetingDao.class);

		teacher = new Teacher(1, "Mario", "Rossi");

		expect(factoryMock.createMeetingAvailabilityDao()).andReturn(meetingAvailabilityDaoMock).anyTimes();
		expect(factoryMock.createMeetingDao()).andReturn(meetingDaoMock).anyTimes();
		
		teacherController = new TeacherController(teacher, factoryMock);
				
		hour = LocalTime.of(10, 0);
		date = LocalDate.now();
	}
	
	@Test
	public void testAddNewMeetingAvailabilityInDate() {
	    LocalDate meetingDate = date;

	    meetingAvailabilityDaoMock.addMeetingAvailabilityInDate(teacher, meetingDate);
	    replay(factoryMock, meetingAvailabilityDaoMock);

	    teacherController.addNewMeetingAvailabilityInDate(meetingDate);

	    verify(factoryMock, meetingAvailabilityDaoMock);
	}
	
	@Test
	public void testGetMeetingAvailabilities() {
	    MeetingAvailability unbookedMeeting1 = new MeetingAvailability(teacher, date, hour, false);
	    MeetingAvailability unbookedMeeting2 = new MeetingAvailability(teacher, date, hour, true);
	    List<MeetingAvailability> allMeetings = Arrays.asList(unbookedMeeting1, unbookedMeeting2);
	    Iterator<MeetingAvailability> meetingsIterator = allMeetings.iterator();

	    expect(meetingAvailabilityDaoMock.getAllMeetingsAvaialabilityByTeacher(teacher))
	            .andReturn(meetingsIterator).once();
	    replay(factoryMock, meetingAvailabilityDaoMock);

	    Iterator<MeetingAvailability> result = teacherController.getMeetingAvailabilities();

	    assertThat(result).toIterable().containsExactlyInAnyOrder(unbookedMeeting1);

	    verify(factoryMock, meetingAvailabilityDaoMock);
	}

	@Test
	public void testGetBookedMeetings() {
		Meeting meeting1 = new Meeting(null, null);
	    Meeting meeting2 = new Meeting(null, null);
		List<Meeting> meetingList = Arrays.asList(meeting1, meeting2);
	    Iterator<Meeting> meetingsIterator = meetingList.iterator();

	    expect(meetingDaoMock.getMeetingsByTeacher(teacher)).andReturn(meetingsIterator).once();
	    replay(factoryMock, meetingDaoMock);

	    Iterator<Meeting> result = teacherController.getBookedMeetings();

	    assertThat(result).isEqualTo(meetingsIterator);

	    verify(factoryMock, meetingDaoMock);
	}


	@Test
	public void testDeleteMeetingAvailability() throws MeetingAlreadyBookedException {
	    MeetingAvailability unbookedMeeting = new MeetingAvailability(teacher, date, hour, false);

	    meetingAvailabilityDaoMock.deleteMeetingAvailability(unbookedMeeting);
	    replay(factoryMock, meetingAvailabilityDaoMock);

	    teacherController.deleteMeetingAvailability(unbookedMeeting);

	    verify(factoryMock, meetingAvailabilityDaoMock);
	}

	@Test
	public void testDeleteMeetingAvailability_BookedMeeting() throws MeetingAlreadyBookedException {
	    MeetingAvailability bookedMeeting = new MeetingAvailability(teacher, date, hour, true);

	    replay(factoryMock);

	    assertThatThrownBy(() -> teacherController.deleteMeetingAvailability(bookedMeeting))
	            .isInstanceOf(MeetingAlreadyBookedException.class);

	    verify(factoryMock);
	}
	
}
