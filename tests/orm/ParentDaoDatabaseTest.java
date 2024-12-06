package orm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import domainModel.Parent;
import domainModel.SchoolClass;
import domainModel.Student;

public class ParentDaoDatabaseTest {

	private Connection conn;
	private String url = "jdbc:sqlite:database/testDB.db";
	private ParentDaoDatabase parentDao;
	private int parentId;
	private Parent parent;
	private int studentId;

	@Before
	public void setUp() throws Exception {
		conn = DriverManager.getConnection(url);
		parentDao = new ParentDaoDatabase(conn);

		createTestData();

		String getParentIdQuery = "SELECT id_parent FROM Parents WHERE name = 'Maria' AND surname = 'Rossi'";
		ResultSet rs = conn.createStatement().executeQuery(getParentIdQuery);
		rs.next();
		parentId = rs.getInt("id_parent");

		parent = new Parent("Maria", "Rossi", parentId, new Student("Carlo", "Bianchi", studentId, new SchoolClass("1A")));
	}

	private void createTestData() throws SQLException {
		String deleteParentsQuery = "DELETE FROM Parents";
		conn.createStatement().executeUpdate(deleteParentsQuery);

		String deleteStudentsQuery = "DELETE FROM Students";
		conn.createStatement().executeUpdate(deleteStudentsQuery);

		String insertStudentsQuery = "INSERT INTO Students (username, password, name, surname, date_of_birth, class) "
				+ "VALUES ('S12345', 'securePass', 'Carlo', 'Bianchi', '2005-08-15', '1A')";
		conn.createStatement().executeUpdate(insertStudentsQuery);
		
		String getStudentIdQuery = "SELECT id_student FROM Students WHERE username = 'S12345'";
		ResultSet rs = conn.createStatement().executeQuery(getStudentIdQuery);
		rs.next();
		studentId = rs.getInt("id_student");

		String insertParentsQuery = "INSERT INTO Parents (username, password, name, surname, id_student) "
				+ "VALUES ('P00001', 'securePass', 'Maria', 'Rossi'," + studentId + ")";
		conn.createStatement().executeUpdate(insertParentsQuery);
	}

	@Test
	public void testGetParentById()
			throws ParentDaoException, DaoConnectionException, SQLException, StudentDaoException {
		Parent retrievedParent = parentDao.getParentById(parentId);
		assertThat(retrievedParent).isEqualTo(parent);
	}

	@Test
	public void testGetParentById_withInvalidId() {
		int invalidId = -1;

		assertThatThrownBy(() -> parentDao.getParentById(invalidId)).isInstanceOf(ParentDaoException.class);
	}

	@Test
	public void testGetStudentOfParentByParentId()
			throws DaoConnectionException, ParentDaoException, StudentDaoException {
		Student student = parentDao.getStudentOfParentByParentId(parentId);
		assertThat(student.getName()).isEqualTo("Carlo");
		assertThat(student.getSurname()).isEqualTo("Bianchi");
	}

	@Test
	public void testGetStudentOfParentByParentId_withInvalidParentId() {
		int invalidParentId = -1;

		assertThatThrownBy(() -> parentDao.getStudentOfParentByParentId(invalidParentId))
				.isInstanceOf(ParentDaoException.class);
	}

	@After
	public void tearDown() throws Exception {
		deleteTestData();
		conn.close();
	}

	private void deleteTestData() throws SQLException {
		String deleteParentsQuery = "DELETE FROM Parents";
		conn.createStatement().executeUpdate(deleteParentsQuery);

		String deleteStudentsQuery = "DELETE FROM Students";
		conn.createStatement().executeUpdate(deleteStudentsQuery);
	}
}
