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
import domainModel.Teacher;

public class TeacherDaoDatabaseTest {

	private Connection conn;
	private String url = "jdbc:sqlite:database/testDB.db";
	private TeacherDaoDatabase teacherDao;
	private int teacherId;
	private Teacher teacher;
	
	@Before
	public void setUp() throws Exception {
		conn = DriverManager.getConnection(url);
		teacherDao = new TeacherDaoDatabase(conn);
		
		createTestData();

		String getTeachersIdQuery = "SELECT id_teacher FROM Teachers WHERE username = 'T00001'";
		ResultSet rs = conn.createStatement().executeQuery(getTeachersIdQuery);
		rs.next();
		teacherId =  rs.getInt("id_teacher");
		
		teacher = new Teacher("Alessandro", "Ferrari", teacherId);
	}
	
	private void createTestData() throws SQLException {
		String deleteTeachersQuery = "DELETE FROM Teachers";
		conn.createStatement().executeUpdate(deleteTeachersQuery);
		
		String insertTeachersQuery = "INSERT INTO Teachers (username, password, name, surname)"
									+ "VALUES ('T00001', 'secure123', 'Alessandro', 'Ferrari')";
		conn.createStatement().executeUpdate(insertTeachersQuery);
	}

	@Test
	public void testGetTeacherById() throws TeacherDaoException, DaoConnectionException {
		assertThat(teacherDao.getTeacherById(teacherId)).isEqualTo(teacher);
	}
	
	@Test
	public void testGetTeacherById_withInvalidId() {
		int indalidId = -1;
		
		assertThatThrownBy(() -> teacherDao.getTeacherById(indalidId))
        .isInstanceOf(TeacherDaoException.class)
        .hasMessageContaining("Teacher with id " + indalidId + "dosen't exist.");
	}
	
	@After
	public void tearDown() throws Exception {
		deleteTestData();
		conn.close();
	}

	private void deleteTestData() throws Exception {
		String deleteTeachersQuery = "DELETE FROM Teachers";
		conn.createStatement().executeUpdate(deleteTeachersQuery);
	}
}
