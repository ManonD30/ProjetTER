package fr.lirmm.yamplusplus.yampponline;

import java.io.IOException;
import java.security.MessageDigest;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class Signin extends HttpServlet {

  private static final long serialVersionUID = 1L;

  public void doPost(HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException {

    String mail = request.getParameter("mailIn");
    String password = request.getParameter("passwordIn");

    // write logs to catalina.out
    Logger myLog = Logger.getLogger(Signin.class.getName());

    try {
      YamDatabaseConnector dbConnector = new YamDatabaseConnector();
      YamUser user = dbConnector.userConnection(mail, password);
      //name = user.getName();
      // add the YamUser to the session
      HttpSession session = user.addUserToSession(request.getSession());
      
      // send response
      this.getServletContext().getRequestDispatcher("/WEB-INF/index.jsp").forward(request, response);
      
    } catch (IOException | ClassNotFoundException | SQLException | ServletException e) {
      myLog.log(Level.SEVERE, "Login failed!!!{0}", e.toString());
      
      // error message
      request.setAttribute("error", "Invalid login or password");
      // send response
      this.getServletContext().getRequestDispatcher("/WEB-INF/sign.jsp").forward(request, response);
    }

  }

  /**
   *
   * @param request
   * @param response
   * @throws ServletException
   * @throws IOException
   */
  public void doGet(HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException {
    // error message
    request.setAttribute("error", "");
    // send response
    this.getServletContext().getRequestDispatcher("/WEB-INF/sign.jsp").forward(request, response);
  }

  /**
   * method which hash String with prefix prefix have to be the same when user
   * is registering or connecting
   *
   * @param password
   * @return
   */
  public String hash(String password) {
    try {
      password = password + "WONh31K5RYaal07";
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hash = digest.digest(password.getBytes("UTF-8"));
      StringBuffer hexString = new StringBuffer();

      for (int i = 0; i < hash.length; i++) {
        String hex = Integer.toHexString(0xff & hash[i]);
        if (hex.length() == 1) {
          hexString.append('0');
        }
        hexString.append(hex);
      }

      return hexString.toString();
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }
}
