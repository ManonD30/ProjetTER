package fr.lirmm.opendata.yamgui;

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
import java.util.Arrays;
import java.util.HashMap;

public class Download extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
         * Returns validated alignment file to user
         * @param request
         * @param response
         * @throws ServletException
         * @throws IOException 
         */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
          // Force to get it as a file to download
          response.setContentType("application/force-download");
          response.setHeader("content-disposition", "inline; filename=\"yam_alignment_result.rdf\"");

          response.setCharacterEncoding("UTF-8");
          PrintWriter out = response.getWriter();
          
          HashMap<String, String> hashMapping = null;
          ArrayList<HashMap> arrayMappings = new ArrayList<>();
          String[] indexArray = request.getParameterValues("index");
          String[] entity1 = request.getParameterValues("entity1");
          String[] entity2 = request.getParameterValues("entity2");
          String[] relation = request.getParameterValues("relation");
          String[] measure = request.getParameterValues("measure");
          
          String alignmentString = null;
          // Put all checked mappings in an Array of Hashtable
          String[] checkbox = request.getParameterValues("checkbox");
          if (checkbox != null && indexArray != null) {
            for (String c : checkbox) {
                    // Get the index in param arrays of the validate mappings
                    int paramIndex = Arrays.asList(indexArray).indexOf(c);
                    hashMapping = new HashMap<>();
                    hashMapping.put("entity1", entity1[paramIndex]);
                    hashMapping.put("entity2", entity2[paramIndex]);
                    hashMapping.put("relation", relation[paramIndex]);
                    hashMapping.put("measure", measure[paramIndex]);
                    arrayMappings.add(hashMapping);
            }
            // Generate the alignment string
            //alignmentString = generateAlignement(arrayMappings);
            alignmentString = generateSimpleRdfAlignment(arrayMappings);
          } else {
            // in case no checkbox have been checked
            alignmentString = "No mappings have been selectioned";
            response.setContentType("plain/text");
          }
            
          out.print(alignmentString);
          out.flush();
	}

	/**
         * Generate alignment String retrieved from validation UI to generate the 
         * alignment in RDF/XML format
         * @param MapFinal
         * @return 
         */
	public static String generateSimpleRdfAlignment(ArrayList<HashMap> MapFinal) {
                String rdfAlignmentString = "";
		for (int i = 0; i < MapFinal.size(); i++) {
                        HashMap<String, String> hashMapping = null;
			hashMapping = MapFinal.get(i);
                        rdfAlignmentString = rdfAlignmentString + "<" + hashMapping.get("entity1") + "> <" + hashMapping.get("relation") + "> <" + hashMapping.get("entity2") + "> .\n";
		}
                return rdfAlignmentString;
	}
        
        
        /**
         * Generate alignment String retrieved from validation UI to generate the 
         * alignment in RDF/XML format
         * @param MapFinal
         * @return 
         */
	public static String generateAlignment(ArrayList<HashMap> MapFinal) {
		Alignment alignments = new URIAlignment();
		try {
			alignments.init(new URI("c"), new URI("c"));
			alignments.setLevel("0");
			alignments.setType("11");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
                String errorMessage = "";
		for (int i = 0; i < MapFinal.size(); i++) {
                        HashMap<String, String> hashMapping = null;
			hashMapping = MapFinal.get(i);
			try {
				URI entity1 = new URI(hashMapping.get("entity1"));
				URI entity2 = new URI(hashMapping.get("entity2"));

				double score = Double.parseDouble(hashMapping.get("measure"));

				String relation = hashMapping.get("relation");

				// add to alignment
				alignments.addAlignCell(entity1, entity2, relation, score);
                                //errorMessage = errorMessage + " SCORE " + score + " ENTITY1 " + entity1 + " ENTITY2 " + entity2 + " RELATION " + relation;
			} catch (Exception e) {
				// TODO Auto-generated catch block
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
                        String alignmentString = swriter.toString();
			swriter.flush();
			swriter.close();
			//return alignmentString;
                        return alignmentString;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "0";
		}
	}
}
