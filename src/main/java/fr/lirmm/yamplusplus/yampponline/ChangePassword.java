package fr.lirmm.yamplusplus.yampponline;

import java.io.IOException;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ChangePassword extends HttpServlet {

  private static final long serialVersionUID = 1L;

  /**
   * Change the password of a user
   *
   * @param request
   * @param response
   * @throws ServletException
   * @throws IOException
   */
  public void doPost(HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException {

    String apikey = (String) request.getSession().getAttribute("apikey");
    String oldPass = request.getParameter("oldPassword");
    String newPass = request.getParameter("newPassword");
    boolean passwordUpdated = false;
    try {
      YamDatabaseConnector dbConnector = new YamDatabaseConnector();
      passwordUpdated = dbConnector.updatePassword(apikey, oldPass, newPass);
    } catch (ClassNotFoundException ex) {
      Logger.getLogger(ChangePassword.class.getName()).log(Level.SEVERE, null, ex);
    }

    if (passwordUpdated) {
      request.setAttribute("error", "Password successfully updated.");
    } else {
      request.setAttribute("error", "Error updating the password.");
    }
    // send response
    this.getServletContext().getRequestDispatcher("/WEB-INF/change.jsp").forward(request, response);
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
    this.getServletContext().getRequestDispatcher("/WEB-INF/change.jsp")
            .forward(request, response);
  }
}
