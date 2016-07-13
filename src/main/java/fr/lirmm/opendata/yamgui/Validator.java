package fr.lirmm.opendata.yamgui;

import static fr.lirmm.opendata.yamgui.Result.Onto1;
import static fr.lirmm.opendata.yamgui.Result.Onto2;
import static fr.lirmm.opendata.yamgui.Result.liste;
import static fr.lirmm.opendata.yamgui.Result.loadOnto;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.semanticweb.owl.align.AlignmentException;

//@Path("/matcher")
public class Validator extends HttpServlet {
	private static final long serialVersionUID = 1L;
        
        /**
         * Redirect to validator.jsp
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
         * @param request
         * @param response
         * @throws ServletException
         * @throws IOException 
         */
        public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
          Logger myLog = Logger.getLogger (Result.class.getName());
          
          // Retrieve ontologies String from file or URL
          YamFileHandler fileHandler = null;
          try {
            fileHandler = new YamFileHandler();
          } catch (ClassNotFoundException ex) {
            Logger.getLogger(Result.class.getName()).log(Level.SEVERE, null, ex);
          }
          String stringOnt1 = fileHandler.getOntFileFromRequest("1", request);
          String stringOnt2 = fileHandler.getOntFileFromRequest("2", request);
          
          // Get string of alignment from file
          String stringAlignmentFile = fileHandler.readFileFromRequest("rdfAlignmentFile", request);

          // Parse the alignment file to put its data in an Array of Map
          try {
            liste = fileHandler.parseOaeiAlignmentFormat(stringAlignmentFile);
          } catch (AlignmentException ex) {
            request.setAttribute("errorMessage", "Error when loading OAEI alignment results: " + ex.getMessage());
            Logger.getLogger(Result.class.getName()).log(Level.SEVERE, null, ex);
          }
          // add cell data list to response
          request.setAttribute("data", liste);

          // add ontologies label<-->key translation to response
          try {
            Onto1.clear();
            Onto2.clear();                
            myLog.log(Level.INFO, "string log ont1 : " + stringOnt1);
            myLog.log(Level.INFO, "string log ont2 : " + stringOnt2);
            loadOnto(stringOnt1, Onto1);
            loadOnto(stringOnt2, Onto2);
          } catch (Exception ex) {
            myLog.log(Level.INFO, "Failed to load ontologies in Jena");
            request.setAttribute("errorMessage", "Error when loading ontologies in Jena: " + ex.getMessage());
            Logger.getLogger(Result.class.getName()).log(Level.SEVERE, null, ex);
          }
          request.setAttribute("onto1", Onto1);
          request.setAttribute("onto2", Onto2);
          
          // Call result.jsp and send the request with ont1, ont2 and data results
          this.getServletContext()
                        .getRequestDispatcher("/WEB-INF/validation.jsp")
                        .forward(request, response);
        }

	// allow user to upload the ontologies (.owl) and the .rdf file
	@POST
	@Path("/uploadValidationFiles")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadValidationFiles(
			@FormDataParam("firstFile") InputStream uploadedFirstInputStream,
			@FormDataParam("firstFile") FormDataContentDisposition firstFileDetail,
			@FormDataParam("secondFile") InputStream uploadedSecondInputStream,
			@FormDataParam("secondFile") FormDataContentDisposition secondfileDetail,
			@FormDataParam("rdfFile") InputStream uploadedRDFInputStream,
			@FormDataParam("rdfFile") FormDataContentDisposition rdfFileDetail,
			@Context HttpServletRequest request, @CookieParam("key") String key)
			throws MalformedURLException, URISyntaxException, IOException {

                // Load properties file for work directory
                Properties prop = new Properties();
                prop.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("conf.properties"));
            
		// delete old files in temp folder
		// in uploadFiles() because this is the most used function
		deleteOldFiles();

		// upload first file
		String firstFileLocation = prop.getProperty("workdir") + "/ontologies/validationSource"
				+ key + ".owl";
		uploadFile(uploadedFirstInputStream, firstFileLocation);

		// upload second file
		String secondFileLocation = prop.getProperty("workdir") + "/ontologies/validationTarget"
				+ key + ".owl";
		uploadFile(uploadedSecondInputStream, secondFileLocation);

		// upload rdf file
		String rdfFileLocation = prop.getProperty("workdir") + "/ontologies/rdf" + key + ".rdf";
		uploadFile(uploadedRDFInputStream, rdfFileLocation);
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
		// if you change this, think about change it in Matcher.java too
                
                // Load properties file for work directory
                Properties prop = new Properties();
                prop.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("conf.properties"));

		// WARNING! /!\ OLD FILES IN THIS DIRECTORY WILL BE DELETED. /!\
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
