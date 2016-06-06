package fr.lirmm.opendata.yamgui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Date;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
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
public class Validator extends HttpServlet {
	private static final long serialVersionUID = 1L;

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
			throws MalformedURLException, URISyntaxException {

		// delete old files in temp folder
		// in uploadFiles() because this is the most used function
		deleteOldFiles();

		// upload first file
		String firstFileLocation = "WebContent/ontologies/validationSource"
				+ key + ".owl";
		uploadFile(uploadedFirstInputStream, firstFileLocation);

		// upload second file
		String secondFileLocation = "WebContent/ontologies/validationTarget"
				+ key + ".owl";
		uploadFile(uploadedSecondInputStream, secondFileLocation);

		// upload rdf file
		String rdfFileLocation = "WebContent/ontologies/rdf" + key + ".rdf";
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
	public void deleteOldFiles() {
		long numDays = 1; // files will be kept 'numDays' days
		// if you change this, think about change it in Matcher.java too

		// WARNING! /!\ OLD FILES IN THIS DIRECTORY WILL BE DELETED. /!\
		String dir = "WebContent/ontologies";
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
