package domainModel;

import java.time.LocalDate;
import java.util.Objects;

public class DisciplinaryReport {
	private Student student;
	private Teacher teacher;
	private LocalDate date;
	private String description;
	private int id;

	public DisciplinaryReport(int id, Student student, Teacher teacher, LocalDate date, String description) {
		this.id = id;
		this.student = student;
		this.teacher = teacher;
		this.date = date;
		this.description = description;
	}

	public Student getStudent() {
		return student;
	}

	public Teacher getTeacher() {
		return teacher;
	}

	public LocalDate getDate() {
		return date;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public int hashCode() {
		return Objects.hash(date, description, student, teacher);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DisciplinaryReport other = (DisciplinaryReport) obj;
		return Objects.equals(date, other.date) && Objects.equals(description, other.description)
				&& Objects.equals(student, other.student) && Objects.equals(teacher, other.teacher);
	}

	public int getId() {
		return id;
	}
	
	
	
}