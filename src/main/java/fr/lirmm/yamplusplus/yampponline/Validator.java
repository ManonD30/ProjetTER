package fr.lirmm.yamplusplus.yampponline;

import com.hp.hpl.jena.rdf.model.Model;
import fr.lirmm.yamplusplus.yamppls.YamppUtils;
import static fr.lirmm.yamplusplus.yampponline.Result.liste;
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
public class Validator extends HttpServlet {

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
    this.getServletContext().getRequestDispatcher("/WEB-INF/validator.jsp")
            .forward(request, response);
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
    //Logger myLog = Logger.getLogger(Result.class.getName());

    // Retrieve ontologies String from file or URL
    YamFileHandler fileHandler = null;
    try {
      fileHandler = new YamFileHandler();
    } catch (ClassNotFoundException ex) {
      Logger.getLogger(Result.class.getName()).log(Level.ERROR, null, ex);
    }

    // Get string of alignment from file
    String stringAlignmentFile = fileHandler.readFileFromRequest("rdfAlignmentFile", request);

    // Parse the alignment file to put its data in an Array of Map
    liste = fileHandler.parseOaeiAlignmentFormat(stringAlignmentFile);
    // add cell data list to response
    // TODO: Change liste variable name?
    request.setAttribute("alignment", liste);

    // Generate sub directory name randomly (example: BEN6J8VJPDUTWUA)
    String subDirName = RandomStringUtils.randomAlphanumeric(15).toUpperCase();
    // Store ontology from URI or file in /tmp/yampponline/SCENARIO_HASH/source.rdf
    String sourceStoragePath = fileHandler.uploadFile("source", subDirName, request);
    String targetStoragePath = fileHandler.uploadFile("target", subDirName, request);

    // Read ontology with Jena and get ontology JSON model for JavaScript
    Model srcJenaModel = YamppUtils.readUriWithJena(new File(sourceStoragePath).toURI(), Logger.getLogger(Result.class.getName()));
    Model tarJenaModel = YamppUtils.readUriWithJena(new File(targetStoragePath).toURI(), Logger.getLogger(Result.class.getName()));
    request.setAttribute("sourceOnt", YamFileHandler.getOntoJsonFromJena(srcJenaModel));
    request.setAttribute("targetOnt", YamFileHandler.getOntoJsonFromJena(tarJenaModel));

    // Call validation.jsp to display results in /validator URL path and send the request with sourceOnt, targetOnt and alignment results
    this.getServletContext()
            .getRequestDispatcher("/WEB-INF/validation.jsp")
            .forward(request, response);
  }
}
