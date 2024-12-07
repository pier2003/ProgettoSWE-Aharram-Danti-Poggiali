package domainModel;

import java.time.LocalDate;
import java.util.Objects;

public class Homework {
	
	private int id;
	private TeachingAssignment teaching;
	private LocalDate date;
	private String description;
	private LocalDate submissionDate;

	public Homework(int id, TeachingAssignment teaching, LocalDate date, String description, LocalDate submissionDate) {
		this.id = id;
		this.teaching = teaching;
		this.date = date;
		this.description = description;
		this.submissionDate = submissionDate;
		
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

	public LocalDate getSubmissionDate() {
		return submissionDate;
	}

	@Override
	public int hashCode() {
		return Objects.hash(date, description, id, submissionDate, teaching);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Homework other = (Homework) obj;
		return Objects.equals(date, other.date) && Objects.equals(description, other.description) && id == other.id
				&& Objects.equals(submissionDate, other.submissionDate) && Objects.equals(teaching, other.teaching);
	}

	
}
