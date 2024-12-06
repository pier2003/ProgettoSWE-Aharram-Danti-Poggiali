package businessLogic;

import static org.assertj.core.api.Assertions.assertThat;

import static org.easymock.EasyMock.*;

import org.junit.Before;
import org.junit.Test;

import daoFactory.DaoFactory;
import domainModel.Parent;
import domainModel.SchoolClass;
import domainModel.Student;
import orm.DaoConnectionException;
import orm.GradeDaoException;
import orm.ParentDao;
import orm.ParentDaoException;
import orm.SchoolClassDao;
import orm.SchoolClassDaoException;
import orm.StudentDao;
import orm.StudentDaoException;

public class ParenControllTestConMock {
	
	private DaoFactory factoryMock;
	private Parent parent;
	private ParentDao parentDaoMock;
	private int parentId;
	private int studentId;
	private StudentDao studentDaoMock;
	private SchoolClassDao schoolClassDaoMock;
	private Student student;
	
	@Before 
	public void setup() throws DaoConnectionException, ParentDaoException, StudentDaoException {
		factoryMock = createMock(DaoFactory.class);
		parentDaoMock = createMock(ParentDao.class);
		studentDaoMock = createMock(StudentDao.class);
		schoolClassDaoMock = createMock(SchoolClassDao.class);
		
		parentId = 1;
		studentId = 1;
		parent = new Parent("Luigi", "Rossi", parentId, new Student("Mario", "Rossi", studentId	, new SchoolClass("1A")));
		student = new Student("Pippo", "Pino", 1, new SchoolClass("1A"));
		
		expect(factoryMock.createParentDao()).andReturn(parentDaoMock).anyTimes();
		expect(factoryMock.createStudentDao()).andReturn(studentDaoMock).anyTimes();
		expect(factoryMock.createSchoolClassDao()).andReturn(schoolClassDaoMock).anyTimes();
		expect(parentDaoMock.getStudentOfParentByParentId(parentId)).andReturn(student).anyTimes();
		expect(parentDaoMock.getParentById(parentId)).andReturn(parent).anyTimes();
	}
	
	private ParentController createParentController() throws StudentDaoException, SchoolClassDaoException, DaoConnectionException, ParentDaoException {
		return new ParentController(parentId, factoryMock);
	}
	
	@Test
	public void testViewParent() throws GradeDaoException, DaoConnectionException, StudentDaoException, SchoolClassDaoException, ParentDaoException {
		replay(factoryMock, parentDaoMock);
		
		ParentController parentController = createParentController();
		assertThat(parentController.viewParent()).isEqualTo(parent);
		
		verify(factoryMock, parentDaoMock);
	}

}
