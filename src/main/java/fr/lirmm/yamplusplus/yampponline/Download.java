package fr.lirmm.yamplusplus.yampponline;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.semanticweb.owl.align.Alignment;
import org.semanticweb.owl.align.AlignmentVisitor;

import fr.inrialpes.exmo.align.impl.BasicParameters;
import fr.inrialpes.exmo.align.impl.URIAlignment;
import fr.inrialpes.exmo.align.impl.renderer.RDFRendererVisitor;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

public class Download extends HttpServlet {

  private static final long serialVersionUID = 1L;

  /**
   * Redirect to doPost
   *
   * @param request
   * @param response
   * @throws ServletException
   * @throws IOException
   */
  public void doGet(HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException {
    doPost(request, response);
  }

  /**
   * Returns validated alignment file to user
   *
   * @param request
   * @param response
   * @throws ServletException
   * @throws IOException
   */
  public void doPost(HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException {
    String sourceUri = (String) request.getParameter("sourceUri");
    String targetUri = (String) request.getParameter("targetUri");
    String downloadString = null;

    // Force to get it as a file to download
    response.setContentType("application/force-download");
    response.setCharacterEncoding("UTF-8");

    if (request.getParameter("ddl") != null) {
      // If it is called by the admin ontology file download feature (in sign.jsp)
      YamDatabaseConnector dbConnector;
      try {
        dbConnector = new YamDatabaseConnector();
        if (dbConnector.isValidApikey(request.getSession().getAttribute("apikey").toString())) {

          // Admins can ddl anything
          if (request.getSession().getAttribute("role").toString().equals("admin")) {

            downloadString = FileUtils.readFileToString(new File(request.getParameter("ddl")), "UTF-8");
            response.setContentType("text/plain");
            response.setHeader("content-disposition", "inline; filename=\"" + request.getParameter("filename") + "\"");
          } else {
            File downloadedFile = new File(request.getParameter("ddl"));
            // Check if user is downloading a file from its save workspace.
            if (downloadedFile.getAbsolutePath().startsWith("/srv/yam-gui/save/" + request.getSession().getAttribute("field").toString() + "/" + request.getSession().getAttribute("username").toString()) 
                    && !downloadedFile.getAbsolutePath().contains("..")) {
              downloadString = FileUtils.readFileToString(new File(request.getParameter("ddl")), "UTF-8");
              response.setContentType("text/plain");
              response.setHeader("content-disposition", "inline; filename=\"" + request.getParameter("filename") + "\"");
            } else {
              downloadString = "Access denied";
              response.setContentType("text/plain");
            }
          }
        }
      } catch (ClassNotFoundException ex) {
        Logger.getLogger(Download.class.getName()).log(Level.SEVERE, "error: downloading file " + ex);
      }

    } else {
      // Returns the alignment file to download
      response.setHeader("content-disposition", "inline; filename=\"alignment_" + sourceUri.replaceAll("http://", "").replaceAll("https://", "")
              + "_" + targetUri.replaceAll("http://", "").replaceAll("https://", "") + ".rdf\"");

      HashMap<String, String> hashMapping = null;
      ArrayList<HashMap> arrayMappings = new ArrayList<>();
      String[] indexArray = request.getParameterValues("index");
      String[] entity1 = request.getParameterValues("entity1");
      String[] entity2 = request.getParameterValues("entity2");
      String[] relation = request.getParameterValues("relation");
      String[] measure = request.getParameterValues("measure");

      // Put all mappings in an Array of Hashtable
      if (indexArray != null) {
        for (String i : indexArray) {
          // Get the index in param arrays of the validate mappings
          int paramIndex = Arrays.asList(indexArray).indexOf(i);
          hashMapping = new HashMap<>();
          hashMapping.put("entity1", entity1[paramIndex]);
          hashMapping.put("entity2", entity2[paramIndex]);
          hashMapping.put("relation", relation[paramIndex]);
          hashMapping.put("measure", measure[paramIndex]);
          arrayMappings.add(hashMapping);
        }

        // Generate the alignment string depending on selected format
        String format = request.getParameter("validationSubmit");
        if (format.equals("Simple RDF format")) {
          downloadString = generateSimpleRdfAlignment(arrayMappings);
        } else if (format.equals("RDF format")) {
          downloadString = generateRdfAlignment(arrayMappings);
        } else if (format.equals("AlignmentAPI format")){
          downloadString = generateAlignment(arrayMappings, sourceUri, targetUri);
        }

      } else {
        // in case no checkbox have been checked
        downloadString = "No mappings";
        response.setContentType("plain/text");
      }
    }
    PrintWriter out = response.getWriter();
    out.print(downloadString);
    out.flush();
  }

  /**
   * Generate alignment String retrieved from validation UI to generate the
   * alignment in a simple RDF format (entity1-relation-entity2 triples). Take
   * an ArrayList of HashMap {entity1, entity2, relation, score}. It generates
   * symmetric mappings if exactMatch or closeMatch
   *
   * @param MapFinal
   * @return String
   */
  public static String generateSimpleRdfAlignment(ArrayList<HashMap> MapFinal) {
    /* return in nt format
    String rdfAlignmentString = "";
    for (int i = 0; i < MapFinal.size(); i++) {
      HashMap<String, String> hashMapping = null;
      hashMapping = MapFinal.get(i);
      if (hashMapping.get("relation").equals("http://www.w3.org/2004/02/skos/core#exactMatch") || hashMapping.get("relation").equals("http://www.w3.org/2004/02/skos/core#closeMatch")) {
        // exactMatch and closeMatch are symmetric
        rdfAlignmentString = rdfAlignmentString + "<" + hashMapping.get("entity1") + "> <" + hashMapping.get("relation") + "> <" + hashMapping.get("entity2") + "> .\n"
                + "<" + hashMapping.get("entity2") + "> <" + hashMapping.get("relation") + "> <" + hashMapping.get("entity1") + "> .\n";
      }
      if (!hashMapping.get("relation").equals("notvalid")) {
        rdfAlignmentString = rdfAlignmentString + "<" + hashMapping.get("entity1") + "> <" + hashMapping.get("relation") + "> <" + hashMapping.get("entity2") + "> .\n";
      }
    }
    return rdfAlignmentString;*/

    // create an empty Model
    Model model = ModelFactory.createDefaultModel();
    model.setNsPrefix("skos", "http://www.w3.org/2004/02/skos/core#");

    for (int i = 0; i < MapFinal.size(); i++) {
      HashMap<String, String> hashMapping = null;
      hashMapping = MapFinal.get(i);
      if (!hashMapping.get("relation").equals("notvalid")) {
        // Add relation between entities
        model.createResource(hashMapping.get("entity1"))
                .addProperty(model.createProperty(hashMapping.get("relation")), hashMapping.get("entity2"));
        if (hashMapping.get("relation").equals("http://www.w3.org/2004/02/skos/core#exactMatch") || hashMapping.get("relation").equals("http://www.w3.org/2004/02/skos/core#closeMatch")) {
          // exactMatch and closeMatch are symmetric
          model.createResource(hashMapping.get("entity2"))
                  .addProperty(model.createProperty(hashMapping.get("relation")), hashMapping.get("entity1"));
        }
      }
    }

    StringWriter out = new StringWriter();
    model.write(out);
    return out.toString();
  }

  /**
   * Generate alignment String retrieved from validation UI to generate the
   * alignment in RDF format
   *
   * @param MapFinal
   * @return String
   */
  public static String generateRdfAlignment(ArrayList<HashMap> MapFinal) {
    // create an empty Model
    Model model = ModelFactory.createDefaultModel();
    String alignmentUri = "http://yamplusplus.lirmm.fr/ontology#";
    model.setNsPrefix("align", alignmentUri);
    for (int i = 0; i < MapFinal.size(); i++) {
      HashMap<String, String> hashMapping = null;
      hashMapping = MapFinal.get(i);
      if (!hashMapping.get("relation").equals("notvalid")) {
        // Add Alignment URI to namespaces with align prefix

        model.createResource(alignmentUri + "mapping/" + Integer.toString(i))
                .addProperty(model.createProperty(alignmentUri + "entity"), hashMapping.get("entity1"))
                .addProperty(model.createProperty(alignmentUri + "entity"), hashMapping.get("entity2"))
                .addProperty(model.createProperty(alignmentUri + "relation"), hashMapping.get("relation"))
                .addProperty(model.createProperty(alignmentUri + "score"), hashMapping.get("measure"));
      }
    }

    StringWriter out = new StringWriter();
    model.write(out);
    return out.toString();
  }

  /**
   * Generate OAEI AlignAPI alignment String retrieved from validation UI to
   * generate the alignment in RDF/XML format
   *
   * @param MapFinal
   * @return String
   */
  public static String generateAlignment(ArrayList<HashMap> MapFinal, String sourceUri, String targetUri) {
    Alignment alignments = new URIAlignment();
    String alignmentString = null;
    try {
      alignments.init(new URI(sourceUri), new URI(targetUri));
      alignments.setLevel("0");
      alignments.setType("11");

    } catch (Exception e) {
      e.printStackTrace();
    }
    String errorMessage = "";
    List<String> validArray = new ArrayList();
    for (int i = 0; i < MapFinal.size(); i++) {
      HashMap<String, String> hashMapping = null;
      hashMapping = MapFinal.get(i);
      try {
        URI entity1 = new URI(hashMapping.get("entity1"));
        URI entity2 = new URI(hashMapping.get("entity2"));

        double score = Double.parseDouble(hashMapping.get("measure"));

        String relation = hashMapping.get("relation");
        validArray.add(hashMapping.get("relation"));

        // add to alignment
        alignments.addAlignCell(entity1, entity2, relation, score);

        //errorMessage = errorMessage + " SCORE " + score + " ENTITY1 " + entity1 + " ENTITY2 " + entity2 + " RELATION " + relation;
      } catch (Exception e) {
        errorMessage = errorMessage + " getMessage: " + e.getMessage() + " ERROR: " + e;
        e.printStackTrace();
      }
    }
    try {
      StringWriter swriter = new StringWriter();
      PrintWriter writer = new PrintWriter(swriter);

      // create an alignment visitor (renderer)
      AlignmentVisitor renderer = new RDFRendererVisitor(writer);
      renderer.init(new BasicParameters());

      alignments.render(renderer);
      alignments.clone();
      alignmentString = swriter.toString();
      swriter.flush();
      swriter.close();
      //return alignmentString;
    } catch (Exception e) {
      e.printStackTrace();
      return "0";
    }

    //return alignmentString;
    return alignmentString;
  }

}
