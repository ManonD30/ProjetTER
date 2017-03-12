package fr.lirmm.yamplusplus.yampponline;

import com.hp.hpl.jena.rdf.model.Model;
import fr.lirmm.yamplusplus.yamppls.YamppUtils;
import static fr.lirmm.yamplusplus.yampponline.MatcherInterface.liste;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import javax.servlet.ServletException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.xml.sax.SAXException;

//@Path("/matcher")
public class Validator extends HttpServlet {

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
    this.getServletContext().getRequestDispatcher("/WEB-INF/validator.jsp").forward(request, response);
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
    java.util.logging.Logger.getLogger(Matcher.class.getName()).log(java.util.logging.Level.INFO, "Start doPost Validator");

    // Retrieve ontologies String from file or URL
    YamFileHandler fileHandler = null;
    try {
      fileHandler = new YamFileHandler();
    } catch (ClassNotFoundException ex) {
      Logger.getLogger(Validator.class.getName()).log(Level.ERROR, null, ex);
    }

    // Get string of alignment from file
    //String stringAlignmentFile = fileHandler.uploadFile("rdfAlignmentFile", request);
    // Read alignmentAPI file to a String 
    Part filePart = null;

    String alignmentString = "error: loading alignment file";
    // Retrieve file from input where name is sourceFile or targetFile
    filePart = request.getPart("rdfAlignmentFile");
    if (filePart != null) {
      String filename = filePart.getSubmittedFileName();
      InputStream fileStream = filePart.getInputStream();
      alignmentString = IOUtils.toString(fileStream, "UTF-8");
    }

    if (alignmentString.startsWith("error:")) {
      request.setAttribute("errorMessage", "Error uploading alignment file");
      this.getServletContext().getRequestDispatcher("/WEB-INF/validation.jsp").forward(request, response);
    }
    try {
      // Parse the alignment file to put its data in an Array of Map
      liste = fileHandler.parseOaeiAlignmentFormat(alignmentString);
    } catch (SAXException ex) {
      java.util.logging.Logger.getLogger(Validator.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
      request.setAttribute("errorMessage", "Error while parsing the alignment file: " + ex.getMessage());
      this.getServletContext().getRequestDispatcher("/WEB-INF/validation.jsp").forward(request, response);
    }
    // add cell data list to response
    // TODO: Change liste variable name?
    request.setAttribute("alignment", liste);

    // Generate sub directory name randomly (example: BEN6J8VJPDUTWUA)
    String subDirName = RandomStringUtils.randomAlphanumeric(15).toUpperCase();
    // Store ontology from URI or file in /tmp/yampponline/SCENARIO_HASH/source.rdf

    String sourceStoragePath = "error: loading source file";
    String targetStoragePath = "error: loading target file";
    try {
      sourceStoragePath = fileHandler.uploadFile("source", subDirName, request);
      targetStoragePath = fileHandler.uploadFile("target", subDirName, request);
    } catch (URISyntaxException ex) {
      Logger.getLogger(Validator.class.getName()).log(Level.ERROR, "error: uploading ontology file on server. " + ex);
      request.setAttribute("errorMessage", "Error uploading ontologies");
      this.getServletContext().getRequestDispatcher("/WEB-INF/validation.jsp").forward(request, response);
    }

    Logger.getLogger(Matcher.class.getName()).log(Level.INFO, "BEFORE JENA apache");
    java.util.logging.Logger.getLogger(Matcher.class.getName()).log(java.util.logging.Level.INFO, "BEFORE JENA java util log");
    // Read ontology with Jena and get ontology JSON model for JavaScript
    Model srcJenaModel = YamppUtils.readUriWithJena(new File(sourceStoragePath).toURI(), Logger.getLogger(Validator.class.getName()));
    Model tarJenaModel = YamppUtils.readUriWithJena(new File(targetStoragePath).toURI(), Logger.getLogger(Validator.class.getName()));
    Logger.getLogger(Matcher.class.getName()).log(Level.INFO, "AFTER JENA");
    java.util.logging.Logger.getLogger(Matcher.class.getName()).log(java.util.logging.Level.INFO, "AFTER JENA java util log");

    HashMap<String, List<String>> alignmentConceptsArrays = YamppUtils.getAlignedConceptsArray(liste);
    JSONObject sourceOntJson = YamppUtils.getOntoJsonFromJena(srcJenaModel, (List<String>) alignmentConceptsArrays.get("source"));
    JSONObject targetOntJson = YamppUtils.getOntoJsonFromJena(tarJenaModel, (List<String>) alignmentConceptsArrays.get("target"));
    
    request.setAttribute("sourceOnt", sourceOntJson);
    request.setAttribute("targetOnt", targetOntJson);
    java.util.logging.Logger.getLogger(Matcher.class.getName()).log(java.util.logging.Level.INFO, "setAttribute ontologies DONE");

    //  In percentage the proportion of a mapped ontology. Given the mapping count
    // Get number of mappings
    HashSet sourceUniqueMappings = new HashSet<>();
    HashSet targetUniqueMappings = new HashSet<>();
    JSONArray alignmentJsonArray = (JSONArray) liste.get("entities");
    // Get all mapped entities in an hashset to get the number of different concepts that have matched (not the number of match)
    for (int i = 0; i < alignmentJsonArray.size(); i++) {
      sourceUniqueMappings.add(((JSONObject) alignmentJsonArray.get(i)).get("entity1").toString());;
      targetUniqueMappings.add(((JSONObject) alignmentJsonArray.get(i)).get("entity2").toString());;
    }
    // number of mapped concept * 100 / number of concept in the ontology
    int srcOverlappingProportion = sourceUniqueMappings.size() * 100 / ((JSONObject) sourceOntJson.get("entities")).size();
    int tarOverlappingProportion = targetUniqueMappings.size() * 100 / ((JSONObject) targetOntJson.get("entities")).size();
    request.setAttribute("srcOverlappingProportion", srcOverlappingProportion);
    request.setAttribute("tarOverlappingProportion", tarOverlappingProportion);

    java.util.logging.Logger.getLogger(Matcher.class.getName()).log(java.util.logging.Level.INFO, "just before dispatcher");
    
    // Call validation.jsp to display results in /validator URL path and send the request with sourceOnt, targetOnt and alignment results
    this.getServletContext().getRequestDispatcher("/WEB-INF/validation.jsp").forward(request, response);
  }
}
