package orm;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {

    private static DatabaseManager instance;

    private Connection connection;
	private static final String url = "jdbc:mysql://95.234.128.59:3306/school";
	private static final String user = "pippo";
	private static final String password = "pupu";

    private DatabaseManager() throws DaoConnectionException {
          try {
			connection = DriverManager.getConnection(url, user, password);
		} catch (SQLException e) {
			throw new DaoConnectionException();
		}
    }

    public static synchronized DatabaseManager getInstance() throws DaoConnectionException {
		if (instance == null) {
			instance = new DatabaseManager();
		}
		return instance;
    }

    public Connection getConnection() {
        return connection;
    }

	public void closeConnection() throws SQLException {
		if (connection != null) {
			connection.close();
		}
    }

}
