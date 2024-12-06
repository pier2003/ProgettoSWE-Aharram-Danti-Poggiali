package domainModel;

import java.util.Objects;

public class Student {
	
	private String name;
	private String surname;
	private int id;
	private SchoolClass schoolClass;

	public Student(int id, String name, String surname, SchoolClass schoolClass) {
		this.name = name;
		this.surname = surname;
		this.id = id;
		this.schoolClass = schoolClass;
	}

	public String getName() {
		return name;
	}

	public String getSurname() {
		return surname;
	}

	public int getId() {
		return id;
	}

	public SchoolClass getSchoolClass() {
		return schoolClass;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name, schoolClass, surname);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Student other = (Student) obj;
		return id == other.id && Objects.equals(name, other.name) && Objects.equals(schoolClass, other.schoolClass)
				&& Objects.equals(surname, other.surname);
	}
	
	
	
}