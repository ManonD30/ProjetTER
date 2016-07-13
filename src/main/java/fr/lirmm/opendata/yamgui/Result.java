package fr.lirmm.opendata.yamgui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.semanticweb.owl.align.Alignment;
import org.semanticweb.owl.align.AlignmentException;
import org.semanticweb.owl.align.Cell;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import fr.inrialpes.exmo.align.parser.AlignmentParser;
import static fr.lirmm.opendata.yamgui.Matcher.processRequest;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import mainyam.MainProgram;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

public class Result extends HttpServlet {
	private static final long serialVersionUID = 1L;
        
        public static ArrayList<Map> liste = new ArrayList<>();
	public static java.util.Map<String, String> Onto1 = new HashMap<>();
	public static java.util.Map<String, String> Onto2 = new HashMap<>();

	/**
         * servlet's doPost which run YAM++ and redirect to the .JSP
         * @param request
         * @param response
         * @throws ServletException
         * @throws IOException 
         */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// check and update "asMatched" value
		int asMatched = 0;
		int canMatch = 0;
                
                Logger myLog = Logger.getLogger (Result.class.getName());
		try {
                        // get user's mail
                        String mail = (String) request.getSession().getAttribute("mail");
                        
                        YamDatabaseConnector dbConnector = new YamDatabaseConnector();
                        YamUser user = dbConnector.updateAsMatched(mail);
                    
                        asMatched = user.getAsMatched();
                        canMatch = user.getCanMatch();
		} catch (Exception e) {
			System.err.println("Exception catched!");
			System.err.println(e.getMessage());
		}
                
                response.setCharacterEncoding("UTF-8");

                // get time at the matching beginning
                long begin = System.currentTimeMillis();
                
                // Process request (upload files and run YAM)
                String matcherResult = fr.lirmm.opendata.yamgui.Matcher.processRequest(request);
                request.setAttribute("matcherResult", matcherResult);
                
                // get time at the matching end
                long end = System.currentTimeMillis();

                // matching time equals to
                float execTime = ((float) (end - begin)) / 1000f;
                // String conversion to allow data transfer to result.jsp
                String s = Float.toString(execTime);
                // add matching time to response
                request.setAttribute("time", s);

                // Retrieve ontologies String
                YamFileHandler fileHandler = null;
                try {
                  fileHandler = new YamFileHandler();
                } catch (ClassNotFoundException ex) {
                  Logger.getLogger(Result.class.getName()).log(Level.SEVERE, null, ex);
                }
                String stringOnt1 = fileHandler.getOntFileFromRequest("1", request);
                String stringOnt2 = fileHandler.getOntFileFromRequest("2", request);
                
                // Parse OAEI alignment format to get the matcher results
                try {
                  // TODO: use JSON ici [{mapping1:label,etc},{}]
                  liste = fileHandler.parseOaeiAlignmentFormat(matcherResult);
                } catch (AlignmentException ex) {
                  request.setAttribute("errorMessage", "Error when loading OAEI alignment results: " + ex.getMessage());
                  Logger.getLogger(Result.class.getName()).log(Level.SEVERE, null, ex);
                }
                // add cell data list of matcher results to response
                request.setAttribute("data", liste);
                
                // add ontologies label<-->key translation to response
                try {
                  Onto1.clear();
                  Onto2.clear();                
                  myLog.log(Level.INFO, "string log ont1 : " + stringOnt1);
                  myLog.log(Level.INFO, "string log ont2 : " + stringOnt2);
                  // TODO: use JSON ici [{concept:label,etc},{}]
                  loadOnto(stringOnt1, Onto1);
                  loadOnto(stringOnt2, Onto2);
                } catch (Exception ex) {
                  myLog.log(Level.INFO, "Failed to load ontologies in Jena");
                  request.setAttribute("errorMessage", "Error when loading ontologies in Jena: " + ex.getMessage());
                  Logger.getLogger(Result.class.getName()).log(Level.SEVERE, null, ex);
                }
                request.setAttribute("onto1", Onto1);
                request.setAttribute("onto2", Onto2);
                
                // send response
                this.getServletContext()
                        .getRequestDispatcher("/WEB-INF/validation.jsp")
                        .forward(request, response);
	}

	// round a double to 2 decimal places
	public static double round(double value) {
		long factor = (long) Math.pow(10, 2);
		value = value * factor;
		long tmp = Math.round(value);
		return (double) tmp / factor;
	}

        /**
         * Load ontology in Jena to get class label
         * @param in
         * @param label
         * @throws IOException 
         */
	public static void loadOnto(String in,
			java.util.Map<String, String> label) throws IOException {

		OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
                model.read(new ByteArrayInputStream(in.getBytes()), null);
		// OntResource

		Iterator<OntProperty> it1 = model.listAllOntProperties();
		Iterator<OntClass> it = model.listClasses();
		while (it.hasNext()) {
			OntClass ontclass = it.next();

			if (ontclass.getLabel(null) != null) {
				String items2 = ontclass.getLabel(null);

				label.put(ontclass.getURI(), items2);
			} else {
				String s = ontclass.getURI();
				if (s != null) {

					int i = ontclass.getURI().length() - 1;
					while (s.charAt(i) != '#') {
						i--;
					}
					String l = s.substring(i + 1, s.length());
					String items2 = l;
					label.put(ontclass.getURI(), items2);
				}
			}
		}
		while (it1.hasNext()) {
			OntProperty ontProp = it1.next();
			String s = ontProp.getLabel(null);
			if (s == null) {
				s = ontProp.getLocalName();
			}
			label.put(ontProp.getURI(), s);
		}
		model.close();
	}
}
