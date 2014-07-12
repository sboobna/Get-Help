import java.sql.Connection;
import java.sql.DriverManager;


public class DBConnection {
	// details of the Amazon RDS instance
		private String DB_END_POINT = "cloud-project.cvxkuyzf5ndd.us-east-1.rds.amazonaws.com";
		private final String DB_USER_NAME = "gethelp";
		private final String DB_PWD = "columbia";
		private final String DB_NAME = "gethelp";
		private final int DB_PORT = 3306;
		
		// this method creates a connection with RDS
		public Connection createConnection() {
			try {
				// This will load the MySQL driver, each DB has its own driver
				Class.forName("com.mysql.jdbc.Driver").newInstance();
				// Setup the connection with the DB
				return DriverManager.getConnection("jdbc:mysql://"
						+ DB_END_POINT + ":" + DB_PORT + "/" + DB_NAME,
						DB_USER_NAME, DB_PWD);

				// Statements allow to issue SQL queries to the database
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
}
