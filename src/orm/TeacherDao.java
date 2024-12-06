package orm;


import domainModel.Teacher;

public interface TeacherDao {
	
	Teacher getTeacherById(int id) throws TeacherDaoException, DaoConnectionException;

}