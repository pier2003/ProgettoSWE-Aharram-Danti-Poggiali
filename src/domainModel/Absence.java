package domainModel;

import java.time.LocalDate;
import java.util.Objects;

public class Absence {
    private Student student;
    private LocalDate date;
    private boolean isJustified;

    public Absence(Student student, LocalDate date, boolean isJustified) {
		this.student = student;
		this.date = date;
		this.isJustified = isJustified;
	}

	public Student getStudent() {
		return student;
	}

	public LocalDate getDate() {
		return date;
	}

	public boolean isJustified() {
		return isJustified;
	}

	@Override
	public int hashCode() {
		return Objects.hash(date, isJustified, student);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Absence other = (Absence) obj;
		return Objects.equals(date, other.date) && isJustified == other.isJustified
				&& Objects.equals(student, other.student);
	}
	
}