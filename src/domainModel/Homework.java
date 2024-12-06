package domainModel;

import java.time.LocalDate;

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

	
}
