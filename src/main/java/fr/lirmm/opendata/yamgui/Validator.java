package fr.lirmm.opendata.yamgui;

import static fr.lirmm.opendata.yamgui.Result.Onto1;
import static fr.lirmm.opendata.yamgui.Result.Onto2;
import static fr.lirmm.opendata.yamgui.Result.liste;
import static fr.lirmm.opendata.yamgui.Result.loadOnto;
import static fr.lirmm.opendata.yamgui.Result.round;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.semanticweb.owl.align.AlignmentException;
import org.semanticweb.owl.align.Cell;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

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
            //myLog.log(Level.INFO, "string log ont1 : " + stringOnt1);
            //myLog.log(Level.INFO, "string log ont2 : " + stringOnt2);
            loadOnto(stringOnt1, Onto1);
            loadOnto(stringOnt2, Onto2);
          } catch (Exception ex) {
            myLog.log(Level.INFO, "Failed to load ontologies in Jena");
            request.setAttribute("errorMessage", "Error when loading ontologies in Jena: " + ex.getMessage());
            Logger.getLogger(Validator.class.getName()).log(Level.SEVERE, null, ex);
          }
          JSONArray loadedOnto1 = null;
          try {
            loadedOnto1 = loadOntoFromRequest(request);
          } catch (OWLOntologyCreationException ex) {
            Logger.getLogger(Validator.class.getName()).log(Level.SEVERE, null, ex);
          }
          request.setAttribute("ont1", loadedOnto1);
          request.setAttribute("onto1", Onto1);
          request.setAttribute("onto2", Onto2);
          
          // Call result.jsp and send the request with ont1, ont2 and data results
          this.getServletContext()
                        .getRequestDispatcher("/WEB-INF/validation.jsp")
                        .forward(request, response);
        }
        
        public static JSONArray loadOntoFromRequest(HttpServletRequest request) throws IOException, OWLOntologyCreationException, ServletException {
                OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
                OWLOntology ont1 = null;
                
                // Load ontology in OWLAPI from the URL or the file
                if (request.getParameter("sourceUrl1") != null && !request.getParameter("sourceUrl1").isEmpty()) {
                  ont1 = manager.loadOntologyFromOntologyDocument(IRI.create(request.getParameter("sourceUrl1")));
                } else {
                      Part filePart = request.getPart("ont1"); // Retrieves <input type="file" name="file">
                      //String fileName = filePart.getSubmittedFileName();
                      InputStream fileContent = filePart.getInputStream();
                      ont1 = manager.loadOntologyFromOntologyDocument(fileContent);
                }
                
                JSONObject jObject = null;
                JSONArray jArray = new JSONArray();

                // Iterate over classes
                String ontologyString = "";
                // Iterate over all classes of the ontology
                for(OWLClass cls : ont1.getClassesInSignature()) {
                  jObject = new JSONObject();
                  jObject.put("id", cls.getIRI().toString());
                  
                  // Iterate over annotations of the class
                  for (OWLAnnotation annotation : cls.getAnnotations(ont1)) {
                    jObject.put(annotation.getProperty().toString(), annotation.getValue().toString());
                  }
                  jArray.add(jObject);
                }
                
                //return ontologyString;
                return jArray;
	}
}
