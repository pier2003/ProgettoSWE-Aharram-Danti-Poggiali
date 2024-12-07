package businessLogic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import static org.easymock.EasyMock.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import businessLogic.ParentController.AlreadyBookedMeetingException;
import daoFactory.DaoFactory;
import domainModel.Absence;
import domainModel.Meeting;
import domainModel.MeetingAvailability;
import domainModel.Parent;
import domainModel.SchoolClass;
import domainModel.Student;
import domainModel.Teacher;
import orm.AbsenceDao;
import orm.DaoConnectionException;
import orm.GradeDaoException;
import orm.MeetingAvailabilityDao;
import orm.MeetingDao;
import orm.ParentDao;
import orm.ParentDaoException;
import orm.SchoolClassDao;
import orm.SchoolClassDaoException;
import orm.StudentDao;
import orm.StudentDaoException;

public class ParenControllTestConMock {
	
	private DaoFactory factoryMock;
	private Parent parent;
	private ParentDao parentDaoMock;
	private int parentId;
	private int studentId;
	private StudentDao studentDaoMock;
	private SchoolClassDao schoolClassDaoMock;
	private Student student;
	private ParentController parentController;
	private MeetingAvailabilityDao meetingAvailabilityDaoMock;
	private Teacher teacher;
	private LocalDate date;
	private LocalTime hour;
	private MeetingDao meetingDaoMock;
	private AbsenceDao absenceDaoMock;
	
	@Before 
	public void setup() throws DaoConnectionException, ParentDaoException, StudentDaoException, SchoolClassDaoException {
		factoryMock = createMock(DaoFactory.class);
		meetingAvailabilityDaoMock = createMock(MeetingAvailabilityDao.class);
		meetingDaoMock = createMock(MeetingDao.class);
		absenceDaoMock = createMock(AbsenceDao.class);
		
		date = LocalDate.now();
		hour = LocalTime.of(10, 0);
		SchoolClass schoolClass = new SchoolClass("1A");
		student = new Student(1, "Pippo", "Pino", schoolClass);
		parent = new Parent(1, "Luigi", "Rossi", student);
		teacher = new Teacher(1, "Mario", "Rossi");
		
		expect(factoryMock.createMeetingAvailabilityDao()).andReturn(meetingAvailabilityDaoMock).anyTimes();
		expect(factoryMock.createMeetingDao()).andReturn(meetingDaoMock).anyTimes();
		
		parentController = new ParentController(parent, factoryMock);
	} 
	
	@Test
	public void testGetParent() throws GradeDaoException, DaoConnectionException, StudentDaoException, SchoolClassDaoException, ParentDaoException {
		assertThat(parentController.getParent()).isEqualTo(parent);
	}
	
	@Test
	public void testGetAllMeetingsAvailabilityByTeacher() {
		ArrayList<MeetingAvailability> meetingsAvailability = new ArrayList<MeetingAvailability>();
		MeetingAvailability meetingAvailability1 = new MeetingAvailability(teacher, date, hour, false);
		MeetingAvailability meetingAvailability2 = new MeetingAvailability(teacher, date, hour, false);
		meetingsAvailability.add(meetingAvailability1);
		meetingsAvailability.add(meetingAvailability2);
		Iterator<MeetingAvailability> meetingsAvailabilityIterator = meetingsAvailability.iterator();
		
		expect(meetingAvailabilityDaoMock.getAllMeetingsAvaialabilityByTeacher(teacher)).andReturn(meetingsAvailabilityIterator).once();
		
		replay(factoryMock, meetingAvailabilityDaoMock);
		
		assertThat(parentController.getAllMeetingsAvaialabilityByTeacher(teacher)).toIterable().containsExactlyInAnyOrder(meetingAvailability1, meetingAvailability2);
		
		verify(factoryMock, meetingAvailabilityDaoMock);
	}
	
	@Test
    public void testBookAMeetingSuccessfully() throws Exception {
		MeetingAvailability meetingAvailability = new MeetingAvailability(teacher, date, hour, false);
		
        meetingDaoMock.bookMeeting(meetingAvailability, parent);
        meetingAvailabilityDaoMock.editBooking(meetingAvailability);

        replay(factoryMock, meetingDaoMock, meetingAvailabilityDaoMock);

        parentController.bookAMeeting(meetingAvailability);

        verify(factoryMock, meetingDaoMock, meetingAvailabilityDaoMock);
    }

    @Test
    public void testBookAMeetingThrowsAlreadyBookedMeetingException() {
		MeetingAvailability meetingAvailability = new MeetingAvailability(teacher, date, hour, true);

        assertThatThrownBy(() -> parentController.bookAMeeting(meetingAvailability))
                .isInstanceOf(AlreadyBookedMeetingException.class);
    }

    @Test
    public void testGetAllMyMeetings() {
        ArrayList<Meeting> meetings = new ArrayList<>();
        Meeting meeting1 = new Meeting(parent, null);
        Meeting meeting2 = new Meeting(parent, null);
        meetings.add(meeting1);
        meetings.add(meeting2);
        Iterator<Meeting> meetingsIterator = meetings.iterator();

        expect(meetingDaoMock.getAllMeetingsByParent(parent)).andReturn(meetingsIterator).once();

        replay(factoryMock, meetingDaoMock);

        assertThat(parentController.getAllMyMeetings()).toIterable().containsExactlyInAnyOrder(meeting1, meeting2);

        verify(factoryMock, meetingDaoMock);
    }
    
    @Test
    public void testJustifyAbsence() {
        Absence absence = new Absence(student, date, false);

        expect(factoryMock.createAbsenceDao()).andReturn(absenceDaoMock);
        absenceDaoMock.justifyAbsence(absence);
        
        replay(factoryMock, absenceDaoMock);

        parentController.justifyAbsence(absence);
        
        verify(factoryMock, absenceDaoMock);
    }

}
