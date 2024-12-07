package domainModel;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;


public class Lesson {
	
	private int id;
	private TeachingAssignment teaching;
	private LocalDate date;
	private String description;
	private LocalTime startHour;
	private LocalTime endHour;

	public Lesson(int id, TeachingAssignment teaching, LocalDate date, String description, LocalTime startHour, LocalTime endHour) {
		this.id = id;
		this.teaching = teaching;
		this.date = date;
		this.description = description;
		this.startHour = startHour;
		this.endHour = endHour;
	}

	public int getId() {
		return id;
	}

	public TeachingAssignment getTeaching() {
		return teaching;
	}

	public LocalDate getDate() {
		return date;
	}

	public String getDescription() {
		return description;
	}

	public LocalTime getStartHour() {
		return startHour;
	}

	public LocalTime getEndHour() {
		return endHour;
	}

	@Override
	public int hashCode() {
		return Objects.hash(date, description, endHour, id, startHour, teaching);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Lesson other = (Lesson) obj;
		return Objects.equals(date, other.date) && Objects.equals(description, other.description)
				&& Objects.equals(endHour, other.endHour) && id == other.id
				&& Objects.equals(startHour, other.startHour) && Objects.equals(teaching, other.teaching);
	}
	
	
	

}