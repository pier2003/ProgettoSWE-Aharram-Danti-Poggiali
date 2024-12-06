package orm;

import domainModel.Parent;
import domainModel.Student;

public interface ParentDao {

	Parent getParentById(int id) throws StudentDaoException, DaoConnectionException, ParentDaoException;

	Student getStudentOfParentByParentId(int id) throws DaoConnectionException, ParentDaoException, StudentDaoException;

}
