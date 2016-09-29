package fr.lirmm.opendata.yamgui;

import static fr.lirmm.opendata.yamgui.Result.liste;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;
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
          
          // Get string of alignment from file
          String stringAlignmentFile = fileHandler.readFileFromRequest("rdfAlignmentFile", request);

          // Parse the alignment file to put its data in an Array of Map
          try {
            liste = fileHandler.parseOaeiAlignmentFormat(stringAlignmentFile);
          } catch (AlignmentException ex) {
            request.setAttribute("errorMessage", "Error when loading OAEI alignment results: " + ex.getMessage());
            Logger.getLogger(Validator.class.getName()).log(Level.SEVERE, null, ex);
          }
          // add cell data list to response
          request.setAttribute("alignment", liste);

          JSONObject loadedOnto1 = null;
          JSONObject loadedOnto2 = null;
          try {
            // Old way to get ontology files:
            //String stringOnt1 = fileHandler.getOntFileFromRequest("1", request);
            //String stringOnt2 = fileHandler.getOntFileFromRequest("2", request);
            loadedOnto1 = fileHandler.jenaLoadOnto(request, "1");
            loadedOnto2 = fileHandler.jenaLoadOnto(request, "2");
          } catch (Exception ex) {
            request.setAttribute("errorMessage", "Error when loading ontologies in Jena: " + ex.getMessage());
            Logger.getLogger(Validator.class.getName()).log(Level.SEVERE, null, ex);
          }
          request.setAttribute("ont1", loadedOnto1);
          request.setAttribute("ont2", loadedOnto2);
          
          // Call result.jsp and send the request with ont1, ont2 and data results
          this.getServletContext()
                        .getRequestDispatcher("/WEB-INF/validation.jsp")
                        .forward(request, response);
        }
}
