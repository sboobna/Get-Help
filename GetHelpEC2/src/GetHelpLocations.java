import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class GetHelpLocations {

	private Connection connect = null;
	private Statement statement = null;
	private ResultSet resultSet = null;

	// details of the Amazon RDS instance
	private String DB_END_POINT = "cloud-project.cvxkuyzf5ndd.us-east-1.rds.amazonaws.com";
	private final String DB_USER_NAME = "gethelp";
	private final String DB_PWD = "columbia";
	private final String DB_NAME = "gethelp";
	private final int DB_PORT = 3306;

	// private String query = "";

	// constructor: it creates a connection whenever an instance of this class
	// is made in the servlet
	public GetHelpLocations() {
		createConnectionAndStatement();
	}

	// this method creates a connection with RDS
	private void createConnectionAndStatement() {
		try {
			// This will load the MySQL driver, each DB has its own driver
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			// Setup the connection with the DB
			connect = DriverManager.getConnection("jdbc:mysql://"
					+ DB_END_POINT + ":" + DB_PORT + "/" + DB_NAME,
					DB_USER_NAME, DB_PWD);

			// Statements allow to issue SQL queries to the database
			statement = connect.createStatement();
		} catch (Exception e) {
			e.printStackTrace();
			close();
		}
	}

	public String getNearestSecurity(Double lat, Double lng) {
		String query = "select * from Location;";
		Double latitude = 0.0, longitude = 0.0;
		Double min = Double.MAX_VALUE;
		Double distance = 0.0, lat_diff = 0.0, lng_diff = 0.0;
		String id = "";
		try {
			resultSet = statement.executeQuery(query);
			while (resultSet.next()) {
				latitude = resultSet.getDouble("latitude");
				longitude = resultSet.getDouble("longitude");
				lat_diff = latitude - lat;
				lng_diff = longitude - lng;
				distance = Math.sqrt((lat_diff * lat_diff)
						+ (lng_diff * lng_diff));
				if (distance < min) {
					min = distance;
					id = resultSet.getString("id");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		close();
		return id;
	}

	public void update(Double latitude, Double longitude, String id) {
		String query = "INSERT INTO Location VALUES ('" + id + "'," + latitude
				+ "," + longitude + ") ON DUPLICATE KEY UPDATE latitude="
				+ latitude + ",longitude=" + longitude + ";";
		try {
			statement.executeUpdate(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		close();
	}

	private void close() {
		try {
			if (resultSet != null) {
				resultSet.close();
			}

			if (statement != null) {
				statement.close();
			}

			if (connect != null) {
				connect.close();
			}
		} catch (Exception e) {

		}
	}

}
