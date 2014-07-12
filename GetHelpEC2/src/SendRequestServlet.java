
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SendRequestServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    @Override
    protected void doPost(final HttpServletRequest request,
                          final HttpServletResponse response)
            throws ServletException, IOException {
    	Double latitude = Double.parseDouble(request.getParameter("latitude"));
		Double longitude = Double.parseDouble(request.getParameter("longitude"));
		GetHelpLocations loc = new GetHelpLocations();
		String security_id = loc.getNearestSecurity(latitude, longitude);
        String key = request.getParameter("key");
        SendNotification send = new SendNotification();
        String phoneNo = request.getParameter("phoneNo");
        send.notify(security_id, latitude, longitude, key,phoneNo);
        //response.getWriter().println("post " + latitude + " " + longitude + " " + key);
    }

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);          
	}    
    
}
