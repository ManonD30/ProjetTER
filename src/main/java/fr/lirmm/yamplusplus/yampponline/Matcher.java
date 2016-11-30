package fr.lirmm.yamplusplus.yampponline;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.RandomStringUtils;

import fr.lirmm.yamplusplus.yamppls.YamppOntologyMatcher;
import org.apache.commons.io.FileUtils;

@WebServlet("/rest/matcher")
public class Matcher extends HttpServlet {

  private static final long serialVersionUID = 1L;

  /**
   * POST request. Use the processRequest method to upload file and run YAM
   * Upload file using either a local file or an URL. Use source and target
   * parameters to define the 2 ontologies to work with Upload a file using
   * cURL: curl -X POST -H \"Content-Type: multipart/form-data\ -F
   * sourceFile=@/path/to/ontology_file.owl
   * http://localhost:8083/rest/matcher?targetUrl=http://purl.obolibrary.org/obo/po.owl
   * Only files: curl -X POST -H "Content-Type: multipart/form-data" -F
   * targetFile=@/srv/yam2013/cmt.owl -F sourceFile=@/srv/yam2013/Conference.owl
   * http://localhost:8083/rest/matcher Only URL: curl -X POST
   * http://localhost:8083/rest/matcher -d
   * 'sourceUrl=https://web.archive.org/web/20111213110713/http://www.movieontology.org/2010/01/movieontology.owl'
   * -d
   * 'targetUrl=https://web.archive.org/web/20111213110713/http://www.movieontology.org/2010/01/movieontology.owl'
   *
   * @param request
   * @param response
   * @throws ServletException
   * @throws IOException
   */
  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    response.setCharacterEncoding("UTF-8");
    response.setContentType("text/plain");
    PrintWriter out = response.getWriter();

    String responseString = null;

    try {
      request = processRequest(request);
      responseString = (String) request.getAttribute("matcherResult");
    } catch (IOException | ClassNotFoundException e) {
      request.setAttribute("errorMessage", "YAM matcher execution failed: " + e.getMessage());
      Logger.getLogger(Matcher.class.getName()).log(Level.SEVERE, null, e);
    }

    out.print(responseString);
    out.flush();
  }

  /**
   * Get request. Use the processRequest method to upload file and run YAM curl
   * -X GET
   * http://localhost:8083/rest/matcher?targetUrl=http://purl.obolibrary.org/obo/po.owl&sourceUrl=https://web.archive.org/web/20111213110713/http://www.movieontology.org/2010/01/movieontology.owl
   * http://localhost:8083/rest/matcher?targetUrl=https://raw.githubusercontent.com/vemonet/sifr_project_ruby_scripts/master/src/Conference.owl&sourceUrl=https://raw.githubusercontent.com/vemonet/sifr_project_ruby_scripts/master/src/cmt.owl
   * http://data.bioportal.lirmm.fr/ontologies/MEDLINEPLUS/download?apikey=7b82f0a5-a784-494c-9d2e-cae6698099db
   * http://data.bioportal.lirmm.fr/ontologies/CIF/download?apikey=7b82f0a5-a784-494c-9d2e-cae6698099db
   *
   * @param request
   * @param response
   * @throws ServletException
   * @throws IOException
   */
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    response.setCharacterEncoding("UTF-8");
    response.setContentType("text/plain");
    PrintWriter out = response.getWriter();

    String responseString = null;

    // Check if source URL are fill, if not display a help text
    String sourceUrl = request.getParameter("sourceUrl");
    String targetUrl = request.getParameter("targetUrl");
    if (sourceUrl != null && targetUrl != null) {
      try {
        request = processRequest(request);
        responseString = (String) request.getAttribute("matcherResult");
      } catch (IOException | ClassNotFoundException e) {
        request.setAttribute("errorMessage", "YAM matcher execution failed: " + e.getMessage());
        Logger.getLogger(Matcher.class.getName()).log(Level.SEVERE, null, e);
      }
    } else {
      responseString = "Example: <br/> curl -X POST -H \"Content-Type: multipart/form-data\" "
              + "-F sourceFile=@/path/to/ontology_file.owl http://localhost:8083/rest/matcher?targetUrl=https://web.archive.org/web/20111213110713/http://www.movieontology.org/2010/01/movieontology.owl <br/>"
              + "http://localhost:8083/rest/matcher?sourceUrl=https://web.archive.org/web/20111213110713/http://www.movieontology.org/2010/01/movieontology.owl&targetUrl=https://web.archive.org/web/20111213110713/http://www.movieontology.org/2010/01/movieontology.owl";
    }

    out.print(responseString);
    out.flush();
  }

  /**
   * Call the YAM match method to get alignment giving 2 ontology files
   *
   * @param request
   * @return response String
   * @throws IOException
   */
  static HttpServletRequest processRequest(HttpServletRequest request) throws IOException, ClassNotFoundException, ServletException {
    String responseString = null;
    YamFileHandler fileHandler = new YamFileHandler();

    // Generate sub directory name randomly (example: BEN6J8VJPDUTWUA)
    String subDirName = RandomStringUtils.randomAlphanumeric(15).toUpperCase();

    String sourceStoragePath = fileHandler.uploadFile("source", subDirName, request);
    String targetStoragePath = fileHandler.uploadFile("target", subDirName, request);

    /*SKOSManager manager = new SKOSManager();
    // use the manager to load a SKOS vocabulary from a URI (either physical or on the web)
    SKOSDataset skosDataset = manager.loadDataset(URI.create(storagePath1));
    SKOStoOWLConverter skosConverter = new SKOStoOWLConverter();
    OWLOntology convertedOwlOnto = skosConverter.getAsOWLOntology(skosDataset);
    OWLOntologyManager owlManager = OWLManager.createOWLOntologyManager();
    owlManager.saveOntology(convertedOwlOnto, new FileOutputStream("/tmp/yam-gui/teeest1.owl"));*/
    // Check if file is bigger than 4MB
    /*int maxFileSize = 4;
    if (fileHandler.getFileSize(sourceStoragePath) >= maxFileSize || fileHandler.getFileSize(targetStoragePath) >= maxFileSize) {
      System.out.println("File too big");
      throw new FileNotFoundException("File too big: its size should be less than " + maxFileSize + "MB");
    }*/
    YamppOntologyMatcher matcher = new YamppOntologyMatcher();

    // Execute YAM to get the mappings in RDF/XML
    // Soon to be String resultStoragePath = matcher.alignOntologies()
    String resultStoragePath = matcher.alignOntologies(new File(sourceStoragePath).toURI(),
            new File(targetStoragePath).toURI());

    request.setAttribute("sourceOnt", YamFileHandler.getOntoJsonFromJena(matcher.getSrcJenaModel()));
    request.setAttribute("targetOnt", YamFileHandler.getOntoJsonFromJena(matcher.getTarJenaModel()));

    //MainProgram.match(sourceConvertedPath, targetConvertedPath, resultStoragePath);
    String matcherResult = FileUtils.readFileToString(new File(resultStoragePath), "UTF-8");
    if (matcherResult.startsWith("error:")) {
      request.setAttribute("errorMessage", matcherResult);
    }
    request.setAttribute("matcherResult", matcherResult);

    return request;
  }
}
