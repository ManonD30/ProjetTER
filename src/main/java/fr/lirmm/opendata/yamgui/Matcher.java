package fr.lirmm.opendata.yamgui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.RandomStringUtils;


import mainyam.MainProgram;
import org.apache.commons.io.FileUtils;

@WebServlet("/rest/matcher")
public class Matcher extends HttpServlet {

  private static final long serialVersionUID = 1L;

  /**
   * POST request. Use the processRequest method to upload file and run YAM
   * Upload file using either a local file or an URL. Use ont1 and ont2
   * parameters to define the 2 ontologies to work with Upload a file using
   * cURL: curl -X POST -H \"Content-Type: multipart/form-data\ -F
   * ont1=@/path/to/ontology_file.owl
   * http://localhost:8083/rest/matcher?sourceUrl2=http://purl.obolibrary.org/obo/po.owl
   * Only files: curl -X POST -H "Content-Type: multipart/form-data" -F
   * ont2=@/srv/yam2013/cmt.owl -F ont1=@/srv/yam2013/Conference.owl
   * http://localhost:8083/rest/matcher Only URL: curl -X POST
   * http://localhost:8083/rest/matcher -d
   * 'sourceUrl1=https://web.archive.org/web/20111213110713/http://www.movieontology.org/2010/01/movieontology.owl'
   * -d
   * 'sourceUrl2=https://web.archive.org/web/20111213110713/http://www.movieontology.org/2010/01/movieontology.owl'
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
      responseString = processRequest(request);
    } catch (Exception e) {
      request.setAttribute("errorMessage", "YAM matcher execution failed: " + e.getMessage());
      Logger.getLogger(Matcher.class.getName()).log(Level.SEVERE, null, e);
    }

    // TODO: output result.rdf content
    out.print(responseString);
    out.flush();
  }

  /**
   * Get request. Use the processRequest method to upload file and run YAM curl
   * -X GET
   * http://localhost:8083/rest/matcher?sourceUrl2=http://purl.obolibrary.org/obo/po.owl&sourceUrl1=https://web.archive.org/web/20111213110713/http://www.movieontology.org/2010/01/movieontology.owl
   * http://localhost:8083/rest/matcher?ont2=https://raw.githubusercontent.com/vemonet/sifr_project_ruby_scripts/master/src/Conference.owl&ont1=https://raw.githubusercontent.com/vemonet/sifr_project_ruby_scripts/master/src/cmt.owl
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
    String ont1 = request.getParameter("sourceUrl1");
    String ont2 = request.getParameter("sourceUrl2");
    if (ont1 != null && ont2 != null) {
      try {
        responseString = processRequest(request);
      } catch (Exception e) {
        request.setAttribute("errorMessage", "YAM matcher execution failed: " + e.getMessage());
        Logger.getLogger(Matcher.class.getName()).log(Level.SEVERE, null, e);
      }
    } else {
      responseString = "Example: <br/> curl -X POST -H \"Content-Type: multipart/form-data\" "
              + "-F ont1=@/path/to/ontology_file.owl http://localhost:8083/rest/matcher?sourceUrl2=https://web.archive.org/web/20111213110713/http://www.movieontology.org/2010/01/movieontology.owl <br/>"
              + "http://localhost:8083/rest/matcher?sourceUrl1=https://web.archive.org/web/20111213110713/http://www.movieontology.org/2010/01/movieontology.owl&sourceUrl2=https://web.archive.org/web/20111213110713/http://www.movieontology.org/2010/01/movieontology.owl";
    }

    out.print(responseString);
    out.flush();
  }

  /**
   * Call the YAM match method to get alignment giving 2 ontology files
   *
   * @param request
   * @return
   * @throws IOException
   */
  static String processRequest(HttpServletRequest request) throws Exception {
    String responseString = null;

    YamFileHandler fileHandler = new YamFileHandler();

    // Generate sub directory name randomly (example: BEN6J8VJPDUTWUA)
    String subDirName = RandomStringUtils.randomAlphanumeric(15).toUpperCase();

    String storagePath1 = fileHandler.uploadFile("1", subDirName, request);
    String storagePath2 = fileHandler.uploadFile("2", subDirName, request);

    String resultStoragePath = fileHandler.getWorkDir() + "/data/tmp/" + subDirName + "/result.rdf";

    // Execute YAM to get the mappings in RDF/XML
    MainProgram.match(storagePath1, storagePath2, resultStoragePath);

    responseString = FileUtils.readFileToString(new File(resultStoragePath));

    return responseString;
  }

  // upload "fileToUpload" into "fileLocation"
  public void uploadFile(InputStream fileToUpload, String fileLocation) {
    try {
      FileOutputStream out = new FileOutputStream(new File(fileLocation));
      int read = 0;
      byte[] bytes = new byte[1024];
      out = new FileOutputStream(new File(fileLocation));
      while ((read = fileToUpload.read(bytes)) != -1) {
        out.write(bytes, 0, read);
      }
      out.flush();
      out.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  // delete old files which doesn't have to be saved or already saved
  public void deleteOldFiles() throws IOException {
    long numDays = 1; // files will be kept 'numDays' days
    // if you change this, think about change it in Validator.java too

    // Load properties file for work directory
    Properties prop = new Properties();
    prop.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("conf.properties"));

    // /!\ WARNING! /!\ OLD FILES IN THIS DIRECTORY WILL BE DELETED. /!\
    String dir = prop.getProperty("workdir") + "/ontologies";
    // /!\ DON'T POINT THIS ON ANY SENSITIVE FILE /!\

    File directory = new File(dir);
    File[] fList = directory.listFiles();

    if (fList != null) {
      for (File file : fList) {
        if (file.isFile() && file.getName().contains("")) { // you can
          // add a
          // name
          // pattern
          long diff = new Date().getTime() - file.lastModified();
          long cutoff = (numDays * (24 * 60 * 60 * 1000));

          if (diff > cutoff) {
            file.delete();
          }
        }
      }
    }
  }
}
