package domainModel;

import java.util.Objects;

public class Parent {
	private String name;
	private String surname;
	private int id;
	private Student student;

	public Parent(int id, String name, String surname, Student student) {
		this.name = name;
		this.surname = surname;
		this.id = id;
		this.student = student;
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
	
	public Student getStudent() {
		return student;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name, student, surname);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Parent other = (Parent) obj;
		return id == other.id && Objects.equals(name, other.name) && Objects.equals(student, other.student)
				&& Objects.equals(surname, other.surname);
	}
	
}