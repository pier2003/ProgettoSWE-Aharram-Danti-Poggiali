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
    }
    
    private void createTestData() throws SQLException {
        String deleteTeachersQuery = "DELETE FROM Teachers";
        conn.createStatement().executeUpdate(deleteTeachersQuery);
        
        String insertTeachersQuery = "INSERT INTO Teachers (username, password, name, surname)"
                                    + "VALUES ('T00001', 'secure123', 'Alessandro', 'Ferrari')";
        conn.createStatement().executeUpdate(insertTeachersQuery);

        String getTeachersIdQuery = "SELECT id_teacher FROM Teachers WHERE username = 'T00001'";
        ResultSet rs = conn.createStatement().executeQuery(getTeachersIdQuery);
        rs.next();
        teacherId = rs.getInt("id_teacher");
        
        teacher = new Teacher(teacherId, "Alessandro", "Ferrari");
    }

    @Test
    public void testGetTeacherById() throws TeacherDaoException {
        Teacher teacherFromDb = teacherDao.getTeacherById(teacherId);
        assertThat(teacherFromDb).isEqualTo(teacher);
    }
    
    @Test
    public void testGetTeacherById_withInvalidId() {
        int invalidId = -1;
        
        assertThatThrownBy(() -> teacherDao.getTeacherById(invalidId))
            .isInstanceOf(TeacherDaoException.class)
            .hasMessageContaining("Teacher with id " + invalidId + " doesn't exist.");
    }
    
    @Test
    public void testGetTeacherByUsernameAndPassword() throws TeacherDaoException {
        Teacher teacherFromDb = teacherDao.getTeacherByUsernameAndPassword("T00001", "secure123");
        assertThat(teacherFromDb).isEqualTo(teacher);
    }

    @Test
    public void testGetTeacherByUsernameAndPassword_withInvalidUsername() {
        assertThatThrownBy(() -> teacherDao.getTeacherByUsernameAndPassword("wrongUser", "secure123"))
            .isInstanceOf(TeacherDaoException.class)
            .hasMessageContaining("Teacher with username wrongUser doesn't exist.");
    }

    @Test
    public void testGetTeacherByUsernameAndPassword_withInvalidPassword() {
        assertThatThrownBy(() -> teacherDao.getTeacherByUsernameAndPassword("T00001", "wrongPassword"))
            .isInstanceOf(TeacherDaoException.class)
            .hasMessageContaining("Teacher with username T00001 doesn't exist.");
    }

    @Test
    public void testGetTeacherByUsernameAndPassword_withInvalidCredentials() {
        assertThatThrownBy(() -> teacherDao.getTeacherByUsernameAndPassword("wrongUser", "wrongPassword"))
            .isInstanceOf(TeacherDaoException.class)
            .hasMessageContaining("Teacher with username wrongUser doesn't exist.");
    }

    @After
    public void tearDown() throws Exception {
        deleteTestData();
        conn.close();
    }

    private void deleteTestData() throws SQLException {
        String deleteTeachersQuery = "DELETE FROM Teachers";
        conn.createStatement().executeUpdate(deleteTeachersQuery);
    }
}
