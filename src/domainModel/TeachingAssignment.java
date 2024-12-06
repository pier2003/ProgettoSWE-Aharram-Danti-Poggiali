package domainModel;

import java.util.Objects;

public class TeachingAssignment {
	private int id;
	private String subject;
	private Teacher teacher;
	private SchoolClass schoolClass;

	public TeachingAssignment(int id, String subject, Teacher teacher, SchoolClass schoolClass) {
		this.id = id;
		this.subject = subject;
		this.teacher = teacher;
		this.schoolClass = schoolClass;
	}

	public String getSubject() {
		return subject;
	}

	public Teacher getTeacher() {
		return teacher;
	}

	public SchoolClass getSchoolClass() {
		return schoolClass;
	}

	public int getId() {
		return id;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, schoolClass, subject, teacher);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TeachingAssignment other = (TeachingAssignment) obj;
		return id == other.id && Objects.equals(schoolClass, other.schoolClass)
				&& Objects.equals(subject, other.subject) && Objects.equals(teacher, other.teacher);
	}
	
	
}