package domainModel;

import java.util.Objects;

public class SchoolClass {
	private String className;
	

	public SchoolClass(String className) {
		this.className = className;
	}

	public String getClassName() {
		return className;
	}

	@Override
	public int hashCode() {
		return Objects.hash(className);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SchoolClass other = (SchoolClass) obj;
		return Objects.equals(className, other.className);
	}
	
	
}
