package orm;


import domainModel.Teacher;

public interface TeacherDao {
	
	Teacher getTeacherByUsernameAndPassword(String username, String password) throws TeacherDaoException;

}