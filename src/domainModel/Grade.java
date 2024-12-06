package domainModel;

import java.time.LocalDate;
import java.util.Objects;

public class Grade {
	private Student student;
	private TeachingAssignment teaching;
	private LocalDate date;
	private double value;
	private int weight;
	private String description;
	private int id;

	public Grade(int id, Student student, TeachingAssignment teaching, LocalDate date, double value, int weight, String description) {
		this.id = id;
		this.student = student;
		this.teaching = teaching;
		this.date = date;
		this.value = value;
		this.description = description;
		this.weight = weight;
	}

	public Student getStudent() {
		return student;
	}

	public LocalDate getDate() {
		return date;
	}

	public double getValue() {
		return value;
	}

	public int getWeight() {
		return weight;
	}

	public TeachingAssignment getTeaching() {
		return teaching;
	}

	public String getDescription() {
		return description;
	}

	public int getId() {
		return id;
	}

	@Override
	public int hashCode() {
		return Objects.hash(date, description, id, student, teaching, value, weight);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Grade other = (Grade) obj;
		return Objects.equals(date, other.date) && Objects.equals(description, other.description) && id == other.id
				&& Objects.equals(student, other.student) && Objects.equals(teaching, other.teaching)
				&& Double.doubleToLongBits(value) == Double.doubleToLongBits(other.value) && weight == other.weight;
	}
	
	
}
