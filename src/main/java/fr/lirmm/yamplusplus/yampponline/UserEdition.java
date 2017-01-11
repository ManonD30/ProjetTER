package fr.lirmm.yamplusplus.yampponline;

import com.hp.hpl.jena.rdf.model.Model;
import fr.lirmm.yamplusplus.yamppls.YamppUtils;
import static fr.lirmm.yamplusplus.yampponline.MatcherInterface.liste;
import java.io.File;
import java.io.IOException;
import javax.servlet.ServletException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

//@Path("/matcher")
public class UserEdition extends HttpServlet {

  private static final long serialVersionUID = 1L;

  /**
   * Redirect to validator.jsp to ask user to provide alignment and ontologies files
   *
   * @param request
   * @param response
   * @throws ServletException
   * @throws IOException
   */
  public void doGet(HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException {
    this.getServletContext().getRequestDispatcher("/WEB-INF/userEdition.jsp").forward(request, response);
  }

  /**
   * Process Post request (from /validator form submission) and redirect to result.jsp
   *
   * @param request
   * @param response
   * @throws ServletException
   * @throws IOException
   */
  public void doPost(HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException {
    //Logger myLog = Logger.getLogger(MatcherInterface.class.getName());
    
    
    String affiliation = request.getParameter("affiliation");
    String field = request.getParameter("field");
    YamUser user = new YamUser(request.getSession());
    
    try {
      YamDatabaseConnector dbConnector = new YamDatabaseConnector();
      user = dbConnector.editUserInfos(user, affiliation, field);
      user.addUserToSession(request.getSession());
      request.setAttribute("error", "User informations updated.");
      
    } catch (ClassNotFoundException ex) {
      java.util.logging.Logger.getLogger(UserEdition.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    }
    

    // Call validation.jsp to display results in /validator URL path and send the request with sourceOnt, targetOnt and alignment results
    this.getServletContext().getRequestDispatcher("/WEB-INF/sign.jsp").forward(request, response);
  }
}
