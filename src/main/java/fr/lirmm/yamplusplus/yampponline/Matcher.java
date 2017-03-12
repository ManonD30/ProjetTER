package fr.lirmm.yamplusplus.yampponline;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.RandomStringUtils;

import fr.lirmm.yamplusplus.yamppls.YamppOntologyMatcher;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;
import org.xml.sax.SAXException;

public class Matcher extends HttpServlet {

  private static final long serialVersionUID = 1L;

  /**
   * POST request. Use the processRequest method to upload file and run YAM
   * Upload file using either a local file or an URL. Use source and target
   * parameters to define the 2 ontologies to work with Upload a file using
   * cURL: curl -X POST -H \"Content-Type: multipart/form-data\ -F
   * sourceFile=@/path/to/ontology_file.owl
   * http://localhost:8083/api/matcher?targetUrl=http://purl.obolibrary.org/obo/po.owl
   * Only files: curl -X POST -H "Content-Type: multipart/form-data" -F
   * targetFile=@/srv/yam2013/cmt.owl -F sourceFile=@/srv/yam2013/Conference.owl
   * http://localhost:8083/api/matcher Only URL: curl -X POST
   * http://localhost:8083/api/matcher -d
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
    response.setContentType("application/xml");
    PrintWriter out = response.getWriter();

    String responseString = null;

    try {
      request = processRequest(request);
      if (request.getAttribute("errorMessage") != null) {
        responseString = (String) request.getAttribute("errorMessage");
        response.setContentType("plain/text");
      } else {
        responseString = (String) request.getAttribute("matcherResult");
        response.setContentType("application/xml");
      }
    } catch (IOException | ClassNotFoundException e) {
      request.setAttribute("errorMessage", "YAM matcher execution failed: " + e.getMessage());
    }

    out.print(responseString);
    out.flush();
  }

  /**
   * Get request. Use the processRequest method to upload file and run YAM curl
   * -X GET
   * http://localhost:8083/api/matcher?targetUrl=http://purl.obolibrary.org/obo/po.owl&sourceUrl=https://web.archive.org/web/20111213110713/http://www.movieontology.org/2010/01/movieontology.owl
   * http://localhost:8083/api/matcher?targetUrl=https://raw.githubusercontent.com/vemonet/sifr_project_ruby_scripts/master/src/Conference.owl&sourceUrl=https://raw.githubusercontent.com/vemonet/sifr_project_ruby_scripts/master/src/cmt.owl
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
    PrintWriter out = response.getWriter();

    // Load properties file for Application URL
    Properties prop = new Properties();
    prop.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("conf.properties"));

    String responseString = null;

    // Check if source URL are fill, if not display a help text
    String sourceUrl = request.getParameter("sourceUrl");
    String targetUrl = request.getParameter("targetUrl");
    if (sourceUrl != null && targetUrl != null) {
      try {
        request = processRequest(request);
        if (request.getAttribute("errorMessage") != null) {
          responseString = (String) request.getAttribute("errorMessage");
          response.setContentType("text/html");
        } else {
          responseString = (String) request.getAttribute("matcherResult");
          response.setContentType("application/xml");
        }
      } catch (IOException | ClassNotFoundException e) {
        request.setAttribute("errorMessage", "YAM matcher execution failed: " + e.getMessage());
      }
    } else {
      // Print 2 examples to show how to use the matcher API
      responseString = "<b>Using the Matcher API:</b><li><a href='" + prop.getProperty("appurl") + "api/matcher?sourceUrl=https://raw.githubusercontent.com/DOREMUS-ANR/knowledge-base/master/vocabularies/mop-iaml.ttl&targetUrl=https://raw.githubusercontent.com/DOREMUS-ANR/knowledge-base/master/vocabularies/mop-diabolo.ttl&crisscrossConflict=false'>"
              + prop.getProperty("appurl") + "api/matcher?sourceUrl=https://raw.githubusercontent.com/DOREMUS-ANR/knowledge-base/master/vocabularies/mop-iaml.ttl&targetUrl=https://raw.githubusercontent.com/DOREMUS-ANR/knowledge-base/master/vocabularies/mop-diabolo.ttl&crisscrossConflict=false</a></li>"
              + "<li><b>Parameters available:</b> explicitConflict (default: true), relativeConflict (default: true), crisscrossConflict (default: true), altLabel2altLabel (default: false), labelSimWeight (default: 0.34)</li>"
              + "<li><b>cURL POST request:</b> curl -X POST -H \"Content-Type: multipart/form-data\" -F sourceFile=@/path/to/ontology_file.owl " + prop.getProperty("appurl") + "api/matcher?targetUrl=https://raw.githubusercontent.com/DOREMUS-ANR/knowledge-base/master/vocabularies/mop-iaml.ttl </li></ul>";
      response.setContentType("text/html");
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
    YamFileHandler fileHandler = new YamFileHandler();
    Logger myLog = Logger.getLogger(Matcher.class.getName());
    myLog.log(Level.SEVERE, "Start processRequest...");

    // Set the workspace for yampp-ls lib. Just used to check if scenario already existing
    String yampplsWorkspace = "/tmp/yamppls/";

    // Generate sub directory name randomly (example: BEN6J8VJPDUTWUA)
    String subDirName = RandomStringUtils.randomAlphanumeric(15).toUpperCase();

    /*SKOSManager manager = new SKOSManager();
    // use the manager to load a SKOS vocabulary from a URI (either physical or on the web)
    SKOSDataset skosDataset = manager.loadDataset(URI.create(storagePath1));
    SKOStoOWLConverter skosConverter = new SKOStoOWLConverter();
    OWLOntology convertedOwlOnto = skosConverter.getAsOWLOntology(skosDataset);
    OWLOntologyManager owlManager = OWLManager.createOWLOntologyManager();
    owlManager.saveOntology(convertedOwlOnto, new FileOutputStream("/tmp/yam-gui/teeest1.owl"));
    // Check if file is bigger than 4MB
    int maxFileSize = 4;
    if (fileHandler.getFileSize(sourceStoragePath) >= maxFileSize || fileHandler.getFileSize(targetStoragePath) >= maxFileSize) {
      System.out.println("File too big");
      throw new FileNotFoundException("File too big: its size should be less than " + maxFileSize + "MB");
    }*/
    String apikey = null;
    if (request.getParameter("apikey") != null) {
      // First get ApiKey from URL parameters
      apikey = request.getParameter("apikey");
    } else if (request.getSession().getAttribute("apikey") != null) {
      // Else get it from the HTTP session (if the user is logged)
      apikey = request.getSession().getAttribute("apikey").toString();
    }

    YamDatabaseConnector dbConnector = new YamDatabaseConnector();
    if (dbConnector.isValidApikey(apikey)) {

      // TODO:QUICK le problème des BK_onto qui ne matchent pas vient de là?
      // Ca stock: "Could not load provided ontology file" dans le fichier... Ca semble venir de OWLAPI
      /*String storagePath = fileHandler.uploadFiles(subDirName, request);
      String sourceStoragePath = storagePath + "/source.owl";
      String targetStoragePath = storagePath + "/target.owl";*/
      String sourceStoragePath = "error: loading source file";
      String targetStoragePath = "error: loading target  file";
      try {
        sourceStoragePath = fileHandler.uploadFile("source", subDirName, request);
        targetStoragePath = fileHandler.uploadFile("target", subDirName, request);
      } catch (URISyntaxException ex) {
        Logger.getLogger(Matcher.class.getName()).log(Level.SEVERE, null, "error: uploading ontology file on server. " + ex);
      }

      // If error when loading files from request
      if (sourceStoragePath.startsWith("error:")) {
        request.setAttribute("errorMessage", sourceStoragePath);
        return request;
      }
      if (targetStoragePath.startsWith("error:")) {
        request.setAttribute("errorMessage", targetStoragePath);
        return request;
      }

      YamUser user = dbConnector.updateMatchCount(apikey);
      user.addUserToSession(request.getSession());
      YamppOntologyMatcher matcher = new YamppOntologyMatcher();

      // Set params
      // To set matcherType
      /*if (request.getParameter("matcherType") != null) {
        matcher.setMatcherType(MatcherType.valueOf(request.getParameter("matcherType")));
      }*/
      // To retrieve sourceType (to enable XSD parsing)
      /*if (request.getParameter("sourceType") != null) {
        matcher.setSourceType(InputType.valueOf(request.getParameter("sourceType")));
      }
      if (request.getParameter("targetType") != null) {
        matcher.setTargetType(InputType.valueOf(request.getParameter("targetType")));
      }*/
      // Remove conflicts true by default
      if (request.getParameter("explicitConflict") == null || request.getParameter("explicitConflict").equals("false")) {
        matcher.setVlsExplicitDisjoint(false);
      } else {
        matcher.setVlsExplicitDisjoint(true);
      }
      if (request.getParameter("relativeConflict") == null || request.getParameter("relativeConflict").equals("false")) {
        matcher.setVlsRelativeDisjoint(false);
      } else {
        matcher.setVlsRelativeDisjoint(true);
      }
      if (request.getParameter("crisscrossConflict") == null || request.getParameter("crisscrossConflict").equals("false")) {
        matcher.setVlsCrisscross(false);
      } else {
        matcher.setVlsCrisscross(true);
      }
      // subLab2suLab and label sim weight false by default
      if (request.getParameter("altLabel2altLabel") == null || request.getParameter("altLabel2altLabel").equals("false")) {
        matcher.setVlsSubSrc2subTar(false);
        matcher.setVlsAllLevels(false);
      } else {
        matcher.setVlsSubSrc2subTar(true);
        matcher.setVlsAllLevels(true);
      }
      if (request.getParameter("labelSimWeight") != null) {
        matcher.setMaxWeightInformativeWord(Double.parseDouble(request.getParameter("labelSimWeight")));
      }

      // Execute YAM to get the mappings in RDF/XML
      // Soon to be String resultStoragePath = matcher.alignOntologies()
      myLog.log(Level.WARNING, "Before matcher.alignOntologies...");

      String scenarioName = org.apache.commons.lang3.RandomStringUtils.randomAlphabetic(10).toUpperCase();
      // Randomly generate the scenario name and check if a dir already got this name
      while (new File(yampplsWorkspace + scenarioName).exists()) {
        scenarioName = org.apache.commons.lang3.RandomStringUtils.randomAlphabetic(10).toUpperCase();
      }

      String resultStoragePath = "error:Error while performing the matching";
      //try {
        /*resultStoragePath = matcher.alignInScenario(new File(sourceStoragePath).toURI(),
                new File(targetStoragePath).toURI(), scenarioName);

        RuntimeMXBean mx = ManagementFactory.getRuntimeMXBean();
        List<String> jvmArgs = mx.getInputArguments();
        String cp = mx.getClassPath();

        List<String> listArgs = new ArrayList<String>();
        listArgs.add("java");
        listArgs.addAll(jvmArgs);
        listArgs.add("-cp");
        listArgs.add(cp);
        listArgs.add(Main.class.getName());
        listArgs.addAll(Arrays.asList(args));

        String[] res = new String[listArgs.size()];
        Runtime.getRuntime().exec(listArgs.toArray(res));*/
        
        //java -jar yampp-ls.jar -s ~/java_workspace/yampp-ls/src/test/resources/oaei2013/oaei2013_FMA_whole_ontology.owl -t ~/java_workspace/yampp-ls/src/test/resources/oaei2013/oaei2013_NCI_whole_ontology.owl
        ProcessBuilder pb = new ProcessBuilder("java", "-jar", "/srv/yampp-ls.jar", "-s", sourceStoragePath, "-t", targetStoragePath, "-sc", scenarioName);
        pb.redirectErrorStream(true); // equivalent of 2>&1
        Process p = pb.start();
      try {
        p.waitFor();
      } catch (InterruptedException ex) {
        Logger.getLogger(Matcher.class.getName()).log(Level.SEVERE, null, "errror: " + ex);
      }

        //myLog.log(Level.WARNING, "After matcher.alignOntologies. Result storage path: {0}", resultStoragePath);
      /*} catch (Exception e) {
        myLog.log(Level.SEVERE, "Error while running matcher.alignOntologies: ", e);
      }*/

      if (resultStoragePath == null) {
        request.setAttribute("errorMessage", "Matching process returned nothing");
        return request;
      }
      if (resultStoragePath.startsWith("error:")) {
        request.setAttribute("errorMessage", resultStoragePath.substring(6));
        return request;
      }

      request.setAttribute("srcOverlappingProportion", matcher.getSrcOverlappingProportion());
      request.setAttribute("tarOverlappingProportion", matcher.getTarOverlappingProportion());

      // No alignment file means no mappings found
      String matcherResult = "error: No mappings have been found";
      if (resultStoragePath != null) {
        //matcherResult = FileUtils.readFileToString(new File(resultStoragePath), "UTF-8");
        matcherResult = FileUtils.readFileToString(new File(yampplsWorkspace + scenarioName + "/alignment.rdf"), "UTF-8");
      }

      // Save file if asked
      //FileUtils.writeStringToFile(new File(sourceStoragePath), matcherResult, "UTF-8");
      if (request.getParameter("saveFile") != null) {
        String sourceName = "source";
        String targetName = "target";
        // Get ontologies name provided by user (source or target by default)
        if (request.getParameter("sourceName") != null) {
          sourceName = request.getParameter("sourceName");
        }
        if (request.getParameter("targetName") != null) {
          targetName = request.getParameter("targetName");
        }
        request.setAttribute("sourceName", sourceName);
        request.setAttribute("targetName", targetName);

        String subDir = sourceName + " to " + targetName;
        FileUtils.copyFile(new File(sourceStoragePath), new File(fileHandler.getWorkDir() + "/save/" + request.getSession().getAttribute("field") + "/"
                + request.getSession().getAttribute("username") + "/" + subDir + "/" + sourceName + ".rdf"));
        FileUtils.copyFile(new File(targetStoragePath), new File(fileHandler.getWorkDir() + "/save/" + request.getSession().getAttribute("field") + "/"
                + request.getSession().getAttribute("username") + "/" + subDir + "/" + targetName + ".rdf"));

        FileUtils.writeStringToFile(new File(fileHandler.getWorkDir() + "/save/" + request.getSession().getAttribute("field") + "/"
                + request.getSession().getAttribute("username") + "/" + subDir + "/alignment.rdf"), matcherResult, "UTF-8");
      }

      // If error we return it in errorMessage (removing error: at the start of the String)
      if (matcherResult.startsWith("error:")) {
        request.setAttribute("errorMessage", matcherResult.substring(6));
      } else {
        request.setAttribute("matcherResult", matcherResult);
      }

      //String sourceString = fileHandler.getOntFileFromRequest("source", request);
      //String targetString = fileHandler.getOntFileFromRequest("target", request);
      JSONObject alignmentJson = null;
      try {
        // Parse OAEI alignment format to get the matcher results
        alignmentJson = fileHandler.parseOaeiAlignmentFormat(matcherResult);
      } catch (SAXException ex) {
        Logger.getLogger(Matcher.class.getName()).log(Level.SEVERE, null, ex);
        request.setAttribute("errorMessage", "Error while parsing the alignment file: " + ex.getMessage());
        return request;
      }
      // add cell data list of matcher results to response
      request.setAttribute("alignment", alignmentJson);

      HashMap<String, List<String>> alignmentConceptsArrays = YamFileHandler.getAlignedConceptsArray(alignmentJson);

      request.setAttribute("sourceOnt", YamFileHandler.getOntoJsonFromJena(matcher.getSrcJenaModel(), (List<String>) alignmentConceptsArrays.get("source")));
      request.setAttribute("targetOnt", YamFileHandler.getOntoJsonFromJena(matcher.getTarJenaModel(), (List<String>) alignmentConceptsArrays.get("target")));

    } else {
      request.setAttribute("errorMessage", "Provide a valid apikey to match ontologies (get it by logging in)");
    }
    myLog.log(Level.WARNING, "End of processRequest");

    return request;
  }
}
