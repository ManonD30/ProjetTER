package fr.lirmm.yamplusplus.yampponline;

import com.hp.hpl.jena.rdf.model.Model;
import fr.lirmm.yamplusplus.yamppls.YamppUtils;
import static fr.lirmm.yamplusplus.yampponline.Result.liste;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import org.apache.commons.lang.RandomStringUtils;
import org.json.simple.JSONObject;
import org.semanticweb.owl.align.AlignmentException;

//@Path("/matcher")
public class Validator extends HttpServlet {

  private static final long serialVersionUID = 1L;

  /**
   * Redirect to validator.jsp
   *
   * @param request
   * @param response
   * @throws ServletException
   * @throws IOException
   */
  public void doGet(HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException {
    this.getServletContext().getRequestDispatcher("/WEB-INF/validator.jsp")
            .forward(request, response);
  }

  /**
   * Process Post request and redirect to result.jsp
   *
   * @param request
   * @param response
   * @throws ServletException
   * @throws IOException
   */
  public void doPost(HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException {
    //Logger myLog = Logger.getLogger(Result.class.getName());

    // Retrieve ontologies String from file or URL
    YamFileHandler fileHandler = null;
    try {
      fileHandler = new YamFileHandler();
    } catch (ClassNotFoundException ex) {
      Logger.getLogger(Result.class.getName()).log(Level.SEVERE, null, ex);
    }

    // Get string of alignment from file
    String stringAlignmentFile = fileHandler.readFileFromRequest("rdfAlignmentFile", request);

    // Parse the alignment file to put its data in an Array of Map
    try {
      liste = fileHandler.parseOaeiAlignmentFormat(stringAlignmentFile);
    } catch (AlignmentException ex) {
      request.setAttribute("errorMessage", "Error when loading OAEI alignment results: " + ex.getMessage());
      Logger.getLogger(Validator.class.getName()).log(Level.SEVERE, null, ex);
    }
    // add cell data list to response
    // TODO: Change liste variable name
    request.setAttribute("alignment", liste);

    // Generate sub directory name randomly (example: BEN6J8VJPDUTWUA)
    String subDirName = RandomStringUtils.randomAlphanumeric(15).toUpperCase();
    // Store ontology from URI or file in /tmp/yampponline/SCENARIO_HASH/source.rdf
    String sourceStoragePath = fileHandler.uploadFile("source", subDirName, request);
    String targetStoragePath = fileHandler.uploadFile("target", subDirName, request);

    // Read ontology with Jena and get ontology JSON model for JavaScript
    JSONObject sourceOntoJson = null;
    JSONObject targetOntoJson = null;
    Model srcJenaModel = YamppUtils.readUriWithJena(new File(sourceStoragePath).toURI());
    Model tarJenaModel = YamppUtils.readUriWithJena(new File(targetStoragePath).toURI());
    request.setAttribute("sourceOnt", YamFileHandler.getOntoJsonFromJena(srcJenaModel));
    request.setAttribute("targetOnt", YamFileHandler.getOntoJsonFromJena(tarJenaModel));

    // Call result.jsp and send the request with sourceOnt, targetOnt and alignment results
    this.getServletContext()
            .getRequestDispatcher("/WEB-INF/validation.jsp")
            .forward(request, response);
  }
}
