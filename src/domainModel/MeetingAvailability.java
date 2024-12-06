package domainModel;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

public class MeetingAvailability {
	private Teacher teacher;
	private LocalDate date;
	private LocalTime hour;
	private boolean isBooked;

	public MeetingAvailability(Teacher teacher, LocalDate date, LocalTime hour, boolean isBooked) {
		this.teacher = teacher;
		this.date = date;
		this.hour = hour;
		this.isBooked = isBooked;
	}

	public Teacher getTeacher() {
		return teacher;
	}

	public LocalDate getDate() {
		return date;
	}

	public LocalTime getHour() {
		return hour;
	}

	public boolean isBooked() {
		return isBooked;
	}

	@Override
	public int hashCode() {
		return Objects.hash(date, hour, isBooked, teacher);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MeetingAvailability other = (MeetingAvailability) obj;
		return Objects.equals(date, other.date) && Objects.equals(hour, other.hour) && isBooked == other.isBooked
				&& Objects.equals(teacher, other.teacher);
	}
	
	
}
