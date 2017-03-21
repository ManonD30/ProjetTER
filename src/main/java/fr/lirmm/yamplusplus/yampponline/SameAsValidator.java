package fr.lirmm.yamplusplus.yampponline;

import com.hp.hpl.jena.rdf.model.Model;
import fr.lirmm.yamplusplus.yamppls.YamppUtils;
import static fr.lirmm.yamplusplus.yampponline.MatcherInterface.liste;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import javax.servlet.ServletException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.xml.sax.SAXException;

//@Path("/matcher")
public class SameAsValidator extends HttpServlet {

  private static final long serialVersionUID = 1L;

  /**
   * Redirect to validator.jsp to ask user to provide alignment and ontologies
   * files
   *
   * @param request
   * @param response
   * @throws ServletException
   * @throws IOException
   */
  public void doGet(HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException {
    this.getServletContext().getRequestDispatcher("/WEB-INF/sameAsValidator.jsp").forward(request, response);
  }

  /**
   * Process Post request (from /validator form submission) and redirect to
   * result.jsp
   *
   * @param request
   * @param response
   * @throws ServletException
   * @throws IOException
   */
  public void doPost(HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException {
    
    JSONObject alignmentJson = new JSONObject();

    // Retrieve ontologies String from file or URL
    YamFileHandler fileHandler = null;
    try {
      fileHandler = new YamFileHandler();
    } catch (ClassNotFoundException ex) {
      Logger.getLogger(SameAsValidator.class.getName()).log(Level.ERROR, null, ex);
    }

    // Get string of alignment from file
    //String stringAlignmentFile = fileHandler.uploadFile("rdfAlignmentFile", request);
    // Read alignmentAPI file to a String 
    Part filePart = null;

    String alignmentString = "error: loading alignment file";
    // Retrieve Alignment file from html input 
    filePart = request.getPart("rdfAlignmentFile");
    if (filePart != null) {
      String filename = filePart.getSubmittedFileName();
      InputStream fileStream = filePart.getInputStream();
      alignmentString = IOUtils.toString(fileStream, "UTF-8");
    }
    
    // Parse the alignment file to put its data in an Array of Map
    try {
      // Parse the alignment file to put its data in an Array of Map
      alignmentJson = fileHandler.parseOaeiAlignmentFormat(alignmentString);
    } catch (SAXException ex) {
      java.util.logging.Logger.getLogger(Validator.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
      request.setAttribute("errorMessage", "Error while parsing the alignment file: " + ex.getMessage());
      this.getServletContext().getRequestDispatcher("/WEB-INF/validation.jsp").forward(request, response);
    }
    
    // Save alignment JSON in the request (to be sent to the sameAsValidation.jsp)
    request.setAttribute("alignment", alignmentJson);

    /*
    // Generate sub directory name randomly (example: BEN6J8VJPDUTWUA)
    String subDirName = RandomStringUtils.randomAlphanumeric(15).toUpperCase();
    // Store ontology from URI or file in /tmp/yampponline/SCENARIO_HASH/source.rdf

    String sourceStoragePath = "error: loading source file";
    String targetStoragePath = "error: loading target file";
    // UploadFile save the ontology file on the server (taking it from the URL or the uploaded file)
    try {
      sourceStoragePath = fileHandler.uploadFile("source", subDirName, request);
      targetStoragePath = fileHandler.uploadFile("target", subDirName, request);
    } catch (URISyntaxException ex) {
      Logger.getLogger(SameAsValidator.class.getName()).log(Level.ERROR, "error: uploading ontology file on server. " + ex);
      request.setAttribute("errorMessage", "Error uploading ontologies");
      this.getServletContext().getRequestDispatcher("/WEB-INF/sameAsValidation.jsp").forward(request, response);
    }

    // Read ontology with Jena and get ontology JSON model for JavaScript
    Model srcJenaModel = YamppUtils.readUriWithJena(new File(sourceStoragePath).toURI(), Logger.getLogger(SameAsValidator.class.getName()));
    Model tarJenaModel = YamppUtils.readUriWithJena(new File(targetStoragePath).toURI(), Logger.getLogger(SameAsValidator.class.getName()));
    */
    
    // Retrieve provided URL
    URI sourceUrl = null;
    URI targetUrl = null;
    try {
      sourceUrl = new URI(request.getParameter("sourceUrl"));
      targetUrl = new URI(request.getParameter("targetUrl"));
    } catch (URISyntaxException ex) {
      java.util.logging.Logger.getLogger(SameAsValidator.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
      request.setAttribute("errorMessage", "Provided URL not valid");
      this.getServletContext().getRequestDispatcher("/WEB-INF/sameAsValidation.jsp").forward(request, response);
    }
    
    // Read ontology with Jena and get ontology JSON model for JavaScript from the provided URL
    Model srcJenaModel = YamppUtils.readUriWithJena(sourceUrl, Logger.getLogger(SameAsValidator.class.getName()));
    Model tarJenaModel = YamppUtils.readUriWithJena(targetUrl, Logger.getLogger(SameAsValidator.class.getName()));

    // Generate the ontology JSON from the Jena Model 
    JSONObject sourceOntJson = YamppUtils.getOntoJsonFromJena(srcJenaModel, null);
    JSONObject targetOntJson = YamppUtils.getOntoJsonFromJena(tarJenaModel, null);
    
    // Save the ontologies in the request to pass it to the validation page
    request.setAttribute("sourceOnt", sourceOntJson);
    request.setAttribute("targetOnt", targetOntJson);

    /*
    //  In percentage the proportion of a mapped ontology. Given the mapping count
    // Get number of mappings
    HashSet sourceUniqueMappings = new HashSet<>();
    HashSet targetUniqueMappings = new HashSet<>();
    JSONArray alignmentJsonArray = (JSONArray) alignmentJson.get("entities");
    // Get all mapped entities in an hashset to get the number of different concepts that have matched (not the number of match)
    for (int i = 0; i < alignmentJsonArray.size(); i++) {
      sourceUniqueMappings.add(((JSONObject) alignmentJsonArray.get(i)).get("entity1").toString());;
      targetUniqueMappings.add(((JSONObject) alignmentJsonArray.get(i)).get("entity2").toString());;
    }
    // number of mapped concept * 100 / number of concept in the ontology
    int srcOverlappingProportion = sourceUniqueMappings.size() * 100 / ((JSONObject) sourceOntJson.get("entities")).size();
    int tarOverlappingProportion = targetUniqueMappings.size() * 100 / ((JSONObject) targetOntJson.get("entities")).size();
    request.setAttribute("srcOverlappingProportion", srcOverlappingProportion);
    request.setAttribute("tarOverlappingProportion", tarOverlappingProportion);*/

    // Call sameAsValidation.jsp to display results in /validator URL path and send the request with sourceOnt, targetOnt and alignment results
    this.getServletContext().getRequestDispatcher("/WEB-INF/sameAsValidation.jsp").forward(request, response);
  }
}
