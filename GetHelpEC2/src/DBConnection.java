import java.sql.Connection;
import java.sql.DriverManager;


public class DBConnection {
	// details of the Amazon RDS instance
		private String DB_END_POINT = <END-POINT>;
		private final String DB_USER_NAME = <USER>;
		private final String DB_PWD = <PWD>;
		private final String DB_NAME = <NAME>;
		private final int DB_PORT = <PORT>;
		
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
