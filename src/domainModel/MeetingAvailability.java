package domainModel;

import java.time.LocalDate;

public class MeetingAvailability {
	private Teacher teacher;
	private LocalDate date;

	public MeetingAvailability(Teacher teacher, LocalDate date) {
		this.teacher = teacher;
		this.date = date;
	}

	public Teacher getTeacher() {
		return teacher;
	}

	public LocalDate getDate() {
		return date;
	}
	
}
