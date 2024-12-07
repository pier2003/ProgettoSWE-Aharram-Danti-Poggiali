package testDaoFactory;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;


import org.junit.Before;
import org.junit.Test;

import daoFactory.DaoFactory;
import daoFactory.DatabaseDaoFactory;
import orm.DaoConnectionException;
import orm.DatabaseManager;
import orm.GradeDao;
import orm.StudentDao;
import orm.StudentDaoDatabase;
import orm.TeacherDao;
import orm.TeacherDaoDatabase;

public class TestDaoFactory {

	private DatabaseDaoFactory factory;

	@Before
	public void setup() {
		factory = new DatabaseDaoFactory();

	}

	@Test
	public void testCreateStudentDao() throws DaoConnectionException {
		StudentDao dao = factory.createStudentDao();
		assertThat(dao).isInstanceOf(StudentDaoDatabase.class);
	}
	
	@Test
	public void testCreateTeacherDao() throws DaoConnectionException {
		TeacherDao dao = factory.creatTeacherDao();
		assertThat(dao).isInstanceOf(TeacherDaoDatabase.class);
	}
	
	@Test
	public void testCreateGradeDao() {
	}

}
