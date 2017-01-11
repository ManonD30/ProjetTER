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

public class Signup extends HttpServlet {

  /**
   * Servlet's doPost which run YAM++ and redirect to the .JSP
   *
   * @param request
   * @param response
   * @throws ServletException
   * @throws IOException
   */
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    // Load properties file for work directory
    //Properties prop = new Properties();
    //prop.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("conf.properties"));
    String mail = request.getParameter("mailUp");
    String password = request.getParameter("passwordUp");
    String username = request.getParameter("nameUp");
    String affiliation = request.getParameter("affiliationUp");
    String field = request.getParameter("fieldUp");
    
    // write logs to catalina.out
    Logger myLog = Logger.getLogger(Signup.class.getName());

    //myLog.log(Level.INFO, "Creating user...");
    try {
      YamDatabaseConnector dbConnector = new YamDatabaseConnector();
      YamUser user = dbConnector.userCreate(mail, username, affiliation, field, password);

      if (user == null) {
        myLog.log(Level.SEVERE, "User already in database!");
        request.setAttribute("error", "User already registered");
        this.getServletContext().getRequestDispatcher("/WEB-INF/sign.jsp").forward(request, response);
      } else {
        //myLog.log(Level.INFO, "Add user to session...");
        
        HttpSession session = user.addUserToSession(request.getSession());
        // create session
      }
    } catch (IOException | ClassNotFoundException | SQLException | ServletException e) {
      myLog.log(Level.SEVERE, "Error creating the user: {0}", e.toString());
      request.setAttribute("error", "Error creating the user");
      this.getServletContext().getRequestDispatcher("/WEB-INF/sign.jsp").forward(request, response);
    }
    this.getServletContext().getRequestDispatcher("/WEB-INF/index.jsp").forward(request, response);
  }
  
  /**
   * Method which hash String with prefix. Prefix have to be the same when user
   * is registering or connecting
   *
   * @param password
   * @return hashed String
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
