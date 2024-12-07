package domainModel;

import java.util.Objects;

public class Teacher {

	private String name;
	private String surname;
	private int id;

	public Teacher( int id, String name, String surname) {
		this.name = name;
		this.surname = surname;
		this.id = id;
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
	
	@Override
	public int hashCode() {
		return Objects.hash(id, name, surname);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Teacher other = (Teacher) obj;
		return id == other.id && Objects.equals(name, other.name) && Objects.equals(surname, other.surname);
	}

}
