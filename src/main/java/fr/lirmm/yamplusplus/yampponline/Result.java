package fr.lirmm.yamplusplus.yampponline;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.semanticweb.owl.align.AlignmentException;

import static fr.lirmm.yamplusplus.yampponline.Matcher.processRequest;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Result extends HttpServlet {

  private static final long serialVersionUID = 1L;

  public static JSONArray liste = null;
  public static java.util.Map<String, String> Onto1 = new HashMap<>();
  public static java.util.Map<String, String> Onto2 = new HashMap<>();

  /**
   * servlet's doPost which run YAM++ and redirect to the .JSP
   *
   * @param request
   * @param response
   * @throws ServletException
   * @throws IOException
   */
  public void doPost(HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException {

    // check and update "asMatched" value
    int asMatched = 0;
    int canMatch = 0;

    Logger myLog = Logger.getLogger(Result.class.getName());
    try {
      // get user's mail
      String mail = (String) request.getSession().getAttribute("mail");

      YamDatabaseConnector dbConnector = new YamDatabaseConnector();
      YamUser user = dbConnector.updateAsMatched(mail);

      asMatched = user.getAsMatched();
      canMatch = user.getCanMatch();
    } catch (Exception e) {
      System.err.println("Exception catched for database login!");
      System.err.println(e.getMessage());
    }

    // Retrieve ontologies String
    YamFileHandler fileHandler = null;
    try {
      fileHandler = new YamFileHandler();
    } catch (ClassNotFoundException ex) {
      Logger.getLogger(Result.class.getName()).log(Level.SEVERE, null, ex);
    }

    response.setCharacterEncoding("UTF-8");

    // get time at the matching beginning
    long begin = System.currentTimeMillis();

    // Process request (upload files and run YAM)
    String matcherResult = null; //= fr.lirmm.opendata.yamgui.Matcher.processRequest(request);
    try {
      matcherResult = processRequest(request);
    } catch (Exception e) {
      request.setAttribute("errorMessage", "YAM matcher execution failed: " + e.getMessage());
      System.out.println("bug matcher: " + e.getMessage());
      this.getServletContext() // send response
              .getRequestDispatcher("/WEB-INF/validation.jsp")
              .forward(request, response);
    }

    request.setAttribute("matcherResult", matcherResult);

    // get time at the matching end
    long end = System.currentTimeMillis();

    // matching time equals to
    float execTime = ((float) (end - begin)) / 1000f;
    // String conversion to allow data transfer to result.jsp
    String s = Float.toString(execTime);
    // add matching time to response
    request.setAttribute("time", s);

    String sourceString = fileHandler.getOntFileFromRequest("source", request);
    String targetString = fileHandler.getOntFileFromRequest("target", request);

    JSONArray alignmentJson = null;
    // Parse OAEI alignment format to get the matcher results
    try {
      alignmentJson = fileHandler.parseOaeiAlignmentFormat(matcherResult);
    } catch (AlignmentException ex) {
      request.setAttribute("errorMessage", "Error when loading OAEI alignment results: " + ex.getMessage());
      this.getServletContext() // send response
              .getRequestDispatcher("/WEB-INF/validation.jsp")
              .forward(request, response);
    }
    // add cell data list of matcher results to response
    request.setAttribute("alignment", alignmentJson);

    // Load the 2 given onto using OWLAPI into a JSONObject, and send the object
    // in the reponse as attributes
    JSONObject sourceOntoJson = null;
    JSONObject targetOntoJson = null;
    try {
      // Old way, using OWLAPI:
      //loadedOnto1 = fileHandler.loadOwlapiOntoFromRequest(request, "source");
      //loadedOnto2 = fileHandler.loadOwlapiOntoFromRequest(request, "target");
      sourceOntoJson = fileHandler.jenaLoadOnto(request, "source");
      targetOntoJson = fileHandler.jenaLoadOnto(request, "target");
    } catch (Exception ex) {
      Logger.getLogger(Result.class.getName()).log(Level.SEVERE, null, ex);
    }
    request.setAttribute("sourceOnt", sourceOntoJson);
    request.setAttribute("targetOnt", targetOntoJson);

    // send response
    this.getServletContext()
            .getRequestDispatcher("/WEB-INF/validation.jsp")
            .forward(request, response);
  }

  // round a double to 2 decimal places
  public static double round(double value) {
    long factor = (long) Math.pow(10, 2);
    value = value * factor;
    long tmp = Math.round(value);
    return (double) tmp / factor;
  }

}
