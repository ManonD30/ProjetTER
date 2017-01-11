package fr.lirmm.yamplusplus.yampponline;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AdminControl extends HttpServlet {

  private static final long serialVersionUID = 1L;

  /**
   * Reset the password of a user to "changeme" or delete the user. Only works if user is admin
   *
   * @param request
   * @param response
   * @throws ServletException
   * @throws IOException
   */
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    // Default reset password is "changeme"
    String newPassword = "changeme";

    String role = (String) request.getSession().getAttribute("role");
    String resetApikey = request.getParameter("resetApikey");
    String deleteApikey = request.getParameter("deleteApikey");

    if (role.equals("admin")) {
      boolean passwordUpdated = false;
      try {
        YamDatabaseConnector dbConnector = new YamDatabaseConnector();

        if (resetApikey != null) {
          passwordUpdated = dbConnector.resetPassword(resetApikey, newPassword);
          if (passwordUpdated) {
            request.setAttribute("error", "Password successfully reset.");
          } else {
            request.setAttribute("error", "Error resetting the password.");
          }
        } else if (deleteApikey != null) {
          passwordUpdated = dbConnector.deleteUser(deleteApikey);
          if (passwordUpdated) {
            request.setAttribute("error", "User successfully deleted.");
          } else {
            request.setAttribute("error", "Error deleting the user.");
          }
        }
      } catch (ClassNotFoundException ex) {
        request.setAttribute("error", "Error processing request.");
        Logger.getLogger(AdminControl.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
    // send response
    this.getServletContext().getRequestDispatcher("/WEB-INF/sign.jsp").forward(request, response);
  }

  /**
   * Redirect to POST
   *
   * @param request
   * @param response
   * @throws ServletException
   * @throws IOException
   */
  public void doGet(HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException {

    request.setAttribute("error", "");
    this.getServletContext().getRequestDispatcher("/WEB-INF/sign.jsp")
            .forward(request, response);
  }
}
