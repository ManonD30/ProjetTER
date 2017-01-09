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
    String name = request.getParameter("nameUp");
    String affiliation = request.getParameter("affiliationUp");
    
    // write logs to catalina.out
    Logger myLog = Logger.getLogger(Signup.class.getName());

    myLog.log(Level.INFO, "Before try !!!");
    try {
      YamDatabaseConnector dbConnector = new YamDatabaseConnector();
      YamUser user = dbConnector.userCreate(mail, name, affiliation, password);

      if (user == null) {
        myLog.log(Level.INFO, "Already in DB !!! (user == null)");
        
        //response.sendRedirect("sign")
        //URI uri = new URL(prop.getProperty("appurl") + "/sign").toURI();

        // TODO: CHANGE IT
        this.getServletContext().getRequestDispatcher("/WEB-INF/index.jsp").forward(request, response);
        
      } else {
        myLog.log(Level.INFO, "Creating user... In else");
        
        HttpSession session = user.addUserToSession(request.getSession());
        // create session
        /*HttpSession session = request.getSession();
        // add user's key (mail) to session
        session.setAttribute("mail", user.getMail());
        // add user's name to session
        session.setAttribute("name", user.getName());
        //add canMatch to session
        session.setAttribute("canMatch", user.getCanMatch());
        session.setAttribute("asMatched", user.getAsMatched());*/
        // send response
      }
    } catch (IOException | ClassNotFoundException | SQLException | ServletException e) {
      myLog.log(Level.SEVERE, "Error creating the user: {0}", e.toString());
    }
    //URI uri = new URL(prop.getProperty("appurl") + "/index").toURI();
    // TODO: CHANGE IT
    this.getServletContext().getRequestDispatcher("/WEB-INF/index.jsp").forward(request, response);

  }

  /*@Context
  private HttpServletRequest request;

  @POST
  @Path("/signup")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  public Response signup(@FormDataParam("mailUp") String mail,
          @FormDataParam("nameUp") String name,
          @FormDataParam("affiliationUp") String affiliation,
          @FormDataParam("passwordUp") String password)
          throws MalformedURLException, URISyntaxException, IOException {

    // Load properties file for work directory
    Properties prop = new Properties();
    prop.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("conf.properties"));

    try {
      YamDatabaseConnector dbConnector = new YamDatabaseConnector();
      YamUser user = dbConnector.userCreate(mail, name, affiliation, password);

      if (user == null) {
        System.out.println("In DB");
        //response.sendRedirect("sign")
        URI uri = new URL(prop.getProperty("appurl") + "/sign")
                .toURI();
        return Response.seeOther(uri).build();
      } else {
        // create session
        HttpSession session = request.getSession();
        // add user's key (mail) to session
        session.setAttribute("mail", user.getMail());
        // add user's name to session
        session.setAttribute("name", user.getName());
        //add canMatch to session
        session.setAttribute("canMatch", user.getCanMatch());
        // send response
      }
    } catch (Exception e) {
      System.err.println("Exception catched!");
      System.err.println(e.getMessage());
    }
    URI uri = new URL(prop.getProperty("appurl") + "/index")
            .toURI();
    return Response.seeOther(uri).build();
  }*/
  
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
