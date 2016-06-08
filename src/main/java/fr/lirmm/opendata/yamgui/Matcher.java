package fr.lirmm.opendata.yamgui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

@Path("/matcher")
public class Matcher extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
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
			URI uri = new URL("http://localhost:8080/yam-gui-0.1/sign").toURI();
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

}
