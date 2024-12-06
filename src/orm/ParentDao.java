package orm;

import domainModel.Parent;
import domainModel.Student;

public interface ParentDao {

	Student getStudentOfParent(Parent parent);

}
