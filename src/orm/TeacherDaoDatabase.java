package orm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import domainModel.Teacher;

public class TeacherDaoDatabase implements TeacherDao {

    private final Connection conn;

    public TeacherDaoDatabase(Connection conn) {
        this.conn = conn;
    }

    public Teacher getTeacherById(int id) throws TeacherDaoException {
        String query = "SELECT * FROM Teachers WHERE id_teacher = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Teacher(
                        rs.getInt("id_teacher"),
                        rs.getString("name"),
                        rs.getString("surname")
                    );
                } else {
                    throw new TeacherDaoException("Teacher with id " + id + " doesn't exist.");
                }
            }
        } catch (SQLException e) {
            throw new TeacherDaoException("Database error while fetching teacher data.");
        }
    }

    @Override
    public Teacher getTeacherByUsernameAndPassword(String username, String password) throws TeacherDaoException {
        String query = "SELECT * FROM Teachers WHERE username = ? AND password = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Teacher(
                        rs.getInt("id_teacher"),
                        rs.getString("name"),
                        rs.getString("surname")
                    );
                } else {
                    throw new TeacherDaoException("Teacher with username " + username + " doesn't exist.");
                }
            }
        } catch (SQLException e) {
            throw new TeacherDaoException("Database error while fetching teacher data");
        }
    }
}

