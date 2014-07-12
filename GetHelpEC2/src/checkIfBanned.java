

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class checkIfBanned
 */
public class checkIfBanned extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public checkIfBanned() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request,response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request,response);
	}

	private void processRequest(HttpServletRequest request,
			HttpServletResponse response) {
		String phoneNo = request.getParameter("phoneNo");
		DBConnection connector = new DBConnection();
		Connection connection = connector.createConnection();
		Statement statement = null;
		String returnValue = "true";
		int databaseValue = 999;
		try {
			statement = connection.createStatement();
			String query = "SELECT * FROM USER_STATUS WHERE phone_no=\""+phoneNo+"\"";
			ResultSet results = statement.executeQuery(query);
			if(results.next()){
				databaseValue = results.getInt(2) ;
				boolean isBanned = ((databaseValue == 0) ? false : true);
				if(!isBanned){
					returnValue = "false";
				}
			}
			statement.close();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		response.setHeader("isBanned", returnValue);
	}

}
