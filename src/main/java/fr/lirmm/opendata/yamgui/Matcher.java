package fr.lirmm.opendata.yamgui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import mainyam.MainProgram;
import org.apache.commons.io.FileUtils;

@WebServlet("/rest/matcher")
public class Matcher extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
    * Display infos about uploaded file (like its content)
    * Upload file using either a local file or an URL. 
    * Use ont1 and ont2 parameters to define the 2 ontologies to work with
    * Upload a file using cURL: curl -X POST -H \"Content-Type: multipart/form-data\ -F ont1=@/path/to/ontology_file.owl http://localhost:8083/rest/matcher?ont2=http://purl.obolibrary.org/obo/po.owl
    * curl -X POST http://localhost:8083/rest/matcher?ont2=https://web.archive.org/web/20111213110713/http://www.movieontology.org/2010/01/movieontology.owl&ont1=https://web.archive.org/web/20111213110713/http://www.movieontology.org/2010/01/movieontology.owl
    * curl -X POST -H "Content-Type: multipart/form-data" -F ont2=@/srv/yam2013/cmt.owl -F ont1=@/srv/yam2013/Conference.owl http://localhost:8083/rest/matcher
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
      
      String responseString = processRequest(request);
      
      // TODO: output result.rdf content
      out.print(responseString);
      out.flush();
    }
    
    /**
     * curl -X GET http://localhost:8083/rest/matcher?ont2=http://purl.obolibrary.org/obo/po.owl&ont1=https://web.archive.org/web/20111213110713/http://www.movieontology.org/2010/01/movieontology.owl
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
      String ont1 = request.getParameter("ont1");
      String ont2 = request.getParameter("ont2");
      if (ont1 != null && ont2 != null) {
        // Get file and upload it to the server
        YamFileHandler fileHandler = null;
        try {
          fileHandler = new YamFileHandler();
        } catch (ClassNotFoundException ex) {
          Logger.getLogger(Matcher.class.getName()).log(Level.SEVERE, null, ex);
        }
        String subDirName = RandomStringUtils.randomAlphanumeric(15).toUpperCase();
        String storagePath1 = fileHandler.uploadFile("ont1", subDirName, request);
        String storagePath2 = fileHandler.uploadFile("ont2", subDirName, request);
        
        String resultStoragePath = "/srv/yam-gui/data/tmp/" + subDirName + "/result.rdf";
        
        // Execute YAM to get the mappings in RDF/XML
        MainProgram.match(storagePath1, storagePath2, resultStoragePath);
        
        responseString = FileUtils.readFileToString(new File(resultStoragePath));
      } else {
        responseString = "Example: <br/> curl -X POST -H \"Content-Type: multipart/form-data\" "
                + "-F ont1=@/path/to/ontology_file.owl http://localhost:8083/rest/matcher?ont2=https://web.archive.org/web/20111213110713/http://www.movieontology.org/2010/01/movieontology.owl <br/>"
                + "http://localhost:8083/rest/matcher?ont1=https://web.archive.org/web/20111213110713/http://www.movieontology.org/2010/01/movieontology.owl&ont2=https://web.archive.org/web/20111213110713/http://www.movieontology.org/2010/01/movieontology.owl";
      }
      
      //responseString = ont1 + " et " + ont2;
      
      out.print(responseString);
      out.flush();
    }
    
    static String processRequest(HttpServletRequest request) throws IOException {
      String responseString = null;
      try {        
        YamFileHandler fileHandler = new YamFileHandler();
        
        // Generate sub directory name randomly (example: BEN6J8VJPDUTWUA)
        String subDirName = RandomStringUtils.randomAlphanumeric(15).toUpperCase();
        
        String storagePath1 = fileHandler.uploadFile("ont1", subDirName, request);
        String storagePath2 = fileHandler.uploadFile("ont2", subDirName, request);
        
        String resultStoragePath = fileHandler.getWorkDir() + "/data/tmp/" + subDirName + "/result.rdf";
        
        // Execute YAM to get the mappings in RDF/XML
        MainProgram.match(storagePath1, storagePath2, resultStoragePath);
        
        responseString = FileUtils.readFileToString(new File(resultStoragePath));
      } catch (ClassNotFoundException ex) {
        Logger.getLogger(Matcher.class.getName()).log(Level.SEVERE, null, ex);
      }
      
      return responseString;
    }


    // allow user to upload the ontologies (.owl)
    @POST
    @Path("/uploadFiles")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFiles(
                    @FormDataParam("firstFile") InputStream uploadedFirstInputStream,
                    @FormDataParam("firstFile") FormDataContentDisposition firstFileDetail,
                    @FormDataParam("secondFile") InputStream uploadedSecondInputStream,
                    @FormDataParam("secondFile") FormDataContentDisposition secondFileDetail,
                    @Context HttpServletRequest request,
                    @CookieParam("key") String key) throws MalformedURLException, URISyntaxException, IOException {

            // Load properties file for work directory
            Properties prop = new Properties();
            prop.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("conf.properties"));

            HttpSession session = request.getSession();
            if(session.getAttribute("mail")==null){
                    URI uri = new URL(prop.getProperty("appurl") + "/sign").toURI();
                    return Response.seeOther(uri).build();
            } else {
            // delete old files in temp folder
            // in uploadFiles() because this is the most used function
            deleteOldFiles();

            // upload first file
            String firstFileLocation = prop.getProperty("workdir") + "/ontologies/first" + key + ".owl";
            uploadFile(uploadedFirstInputStream, firstFileLocation);

            // upload second file
            String secondFileLocation = prop.getProperty("workdir") + "/ontologies/second" + key
                            + ".owl";
            uploadFile(uploadedSecondInputStream, secondFileLocation);
            }
            return null;
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
    
    /**
     * Get the content of a URL page (to get ontologies from the URL)
     * 
     * @param sourceUrl
     * @return
     * @throws IOException 
     */
    public String getUrlContent(String sourceUrl) throws IOException {
      CloseableHttpClient client = HttpClientBuilder.create().build();
        HttpResponse httpResponse = null;
        try{
          URI uri = new URI(sourceUrl);
          httpResponse = client.execute(new HttpGet(uri));
        }catch(IOException e){
          Logger.getLogger(Matcher.class.getName()).log(Level.SEVERE, null, e);
        } catch (URISyntaxException ex) {
          Logger.getLogger(Matcher.class.getName()).log(Level.SEVERE, null, ex);
        }

        // process response
        BufferedReader reader = null;
        try{
          reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), Charset.forName("UTF-8")));
        } catch (IOException e){
          Logger.getLogger(Matcher.class.getName()).log(Level.SEVERE, null, e);
        }

        String contentString = "";
        String line;
        while ((line = reader.readLine()) != null) {
          contentString += line;
        }
        reader.close();
        return contentString;
    }

}
