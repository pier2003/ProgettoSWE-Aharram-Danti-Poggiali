package domainModel;

import java.time.LocalDate;

public class Meeting {
	
	private Parent parent;
	private Teacher teacher;
	private LocalDate date;

	public Meeting(Parent parent, Teacher teacher, LocalDate date) {
		this.parent = parent;
		this.teacher = teacher;
		this.date = date;
	}

	public Parent getParent() {
		return parent;
	}

	public Teacher getTeacher() {
		return teacher;
	}

	public LocalDate getDate() {
		return date;
	}

}