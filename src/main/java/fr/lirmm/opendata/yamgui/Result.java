package fr.lirmm.opendata.yamgui;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.semanticweb.owl.align.AlignmentException;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import static fr.lirmm.opendata.yamgui.Matcher.processRequest;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

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
      this.getServletContext()    // send response
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

    String stringOnt1 = fileHandler.getOntFileFromRequest("1", request);
    String stringOnt2 = fileHandler.getOntFileFromRequest("2", request);

    JSONArray alignmentJson = null;
    // Parse OAEI alignment format to get the matcher results
    try {
      // TODO: use JSON ici [{mapping1:label,etc},{}]
      alignmentJson = fileHandler.parseOaeiAlignmentFormat(matcherResult);
    } catch (AlignmentException ex) {
      request.setAttribute("errorMessage", "Error when loading OAEI alignment results: " + ex.getMessage());
      this.getServletContext()    // send response
              .getRequestDispatcher("/WEB-INF/validation.jsp")
              .forward(request, response);
    }
    // add cell data list of matcher results to response
    request.setAttribute("alignment", alignmentJson);

    JSONObject loadedOnto1 = null;
    JSONObject loadedOnto2 = null;
    try {
      loadedOnto1 = fileHandler.loadOwlapiOntoFromRequest(request, "1");
      loadedOnto2 = fileHandler.loadOwlapiOntoFromRequest(request, "2");
    } catch (OWLOntologyCreationException ex) {
      Logger.getLogger(Result.class.getName()).log(Level.SEVERE, null, ex);
    }
    request.setAttribute("ont1", loadedOnto1);
    request.setAttribute("ont2", loadedOnto2);

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

  /**
   * Load ontology in Jena to get class label
   *
   * @param in
   * @param label
   * @throws IOException
   */
  public static void loadOnto(String in,
          java.util.Map<String, String> label) throws IOException {

    OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
    model.read(new ByteArrayInputStream(in.getBytes()), null);
    // OntResource

    Iterator<OntProperty> it1 = model.listAllOntProperties();
    Iterator<OntClass> it = model.listClasses();
    while (it.hasNext()) {
      OntClass ontclass = it.next();

      if (ontclass.getLabel(null) != null) {
        String items2 = ontclass.getLabel(null);

        label.put(ontclass.getURI(), items2);
      } else {
        String s = ontclass.getURI();
        if (s != null) {

          int i = ontclass.getURI().length() - 1;
          while (s.charAt(i) != '#') {
            i--;
          }
          String l = s.substring(i + 1, s.length());
          String items2 = l;
          label.put(ontclass.getURI(), items2);
        }
      }
    }
    while (it1.hasNext()) {
      OntProperty ontProp = it1.next();
      String s = ontProp.getLabel(null);
      if (s == null) {
        s = ontProp.getLocalName();
      }
      label.put(ontProp.getURI(), s);
    }
    model.close();
  }
}
