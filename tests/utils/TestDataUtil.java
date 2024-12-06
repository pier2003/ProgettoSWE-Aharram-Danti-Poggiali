package utils;

import java.sql.*;
import java.time.LocalDate;

import domainModel.*;

public class TestDataUtil {

    public static Student getExistingStudent(Connection connection) throws SQLException {
        String query = "SELECT * FROM Students LIMIT 1";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return new Student(
                        rs.getString("name"),
                        rs.getString("surname"),
                        rs.getInt("id_student"),
                        new SchoolClass(rs.getString("class"))
                );
            } else {
                throw new SQLException("No students found in the database.");
            }
        }
    }

    public static void addAbsence(Connection connection, int studentId, LocalDate date, boolean justified) throws SQLException {
        String query = "INSERT INTO Absences (date, justification, id_student) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setDate(1, java.sql.Date.valueOf(date));
            stmt.setBoolean(2, justified);
            stmt.setInt(3, studentId);
            stmt.executeUpdate();
        }
    }

    public static void removeAbsence(Connection connection, int studentId, LocalDate date) throws SQLException {
        String query = "DELETE FROM Absences WHERE id_student = ? AND date = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, studentId);
            stmt.setDate(2, java.sql.Date.valueOf(date));
            stmt.executeUpdate();
        }
    }

    public static boolean isAbsencePresent(Connection connection, int studentId, LocalDate date) throws SQLException {
        String query = "SELECT * FROM Absences WHERE id_student = ? AND date = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, studentId);
            stmt.setDate(2, java.sql.Date.valueOf(date));
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

	public static SchoolClass getExistingSchoolClass(Connection connection) throws SQLException {
        String query = "SELECT * FROM Classes LIMIT 1";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return new SchoolClass(
                        rs.getString("name"));
            } else {
                throw new SQLException("No schoolClass found in the database.");
            }
        }
	}

    public static Teacher getExistingTeacher(Connection connection) throws SQLException {
        String query = "SELECT * FROM Teachers LIMIT 1";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return new Teacher(rs.getString("name"), rs.getString("surname"), rs.getInt("id_teacher"));
            } else {
                throw new SQLException("No students found in the database.");
            }
        }
    }
}