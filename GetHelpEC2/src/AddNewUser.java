

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class AddNewUser
 */
public class AddNewUser extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddNewUser() {
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
		Connection connection = new DBConnection().createConnection();
		String phoneNo = request.getParameter("phoneNo");
		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		try {
			Statement statement = connection.createStatement();
			statement.execute("INSERT INTO USERS(FirstName,LastName,PhoneNo) VALUES (\""+firstName+"\",\""+lastName+"\",\""+phoneNo+"\");");
			statement.execute("INSERT INTO USER_STATUS(phone_no,isBanned) VALUES(\""+phoneNo+"\",0);");
			response.setHeader("isAdded", "true");
		} catch (SQLException e) {
			e.printStackTrace();
			response.setHeader("isAdded", "false");
		}finally{
			try {
				connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

}
