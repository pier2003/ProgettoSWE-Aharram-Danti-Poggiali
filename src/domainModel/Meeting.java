package domainModel;

import java.util.Objects;

public class Meeting {
	
	private Parent parent;
	private MeetingAvailability meetingAvailability;
	
	public Meeting(Parent parent, MeetingAvailability meetingAvailability) {
		this.parent = parent;
		this.meetingAvailability = meetingAvailability;
	}

	public Parent getParent() {
		return parent;
	}

	public MeetingAvailability getMeetingAvailability() {
		return meetingAvailability;
	}

	@Override
	public int hashCode() {
		return Objects.hash(meetingAvailability, parent);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Meeting other = (Meeting) obj;
		return Objects.equals(meetingAvailability, other.meetingAvailability) && Objects.equals(parent, other.parent);
	}

	

}