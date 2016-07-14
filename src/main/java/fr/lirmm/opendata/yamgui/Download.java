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

public class Download extends HttpServlet {
	private static final long serialVersionUID = 1L;
	// checked box list
	public static ArrayList<Map> checked = new ArrayList<>();

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
          //response.setContentType("application/force-download");
          //response.setHeader("content-disposition", "inline; filename=\"yam_alignment_result.rdf\"");
          response.setContentType("text/plain");

          //request.getParameterMap().toString();
          // TODO: modifier ça pour utiliser le JSON
          
          // request checked box
          checked.clear();
          String[] checkbox = request.getParameterValues("checkbox");
          for (String c : checkbox) {
                  int mapNumber = Integer.parseInt(c);
                  //checked.add(Result.liste.get(mapNumber));
          }

          //String alignmentString = generateAlignement(checked);

          response.setCharacterEncoding("UTF-8");
          PrintWriter out = response.getWriter();
          
          String[] entity1 = request.getParameterValues("entity1");
          String[] entity2 = request.getParameterValues("entity2");
          String[] relation = request.getParameterValues("relation");
          String[] measure = request.getParameterValues("measure");
          String allEntities = "";
          if (entity1 != null) {
            for (String e : entity1) {
              allEntities = allEntities + e + "\n";
            } 
          } else {
            allEntities = "nuuuullll";
          }
         

          out.print(allEntities);
          out.flush();
	}

	/**
         * Generate alignment String retrieved from validation UI to generate the 
         * alignment in RDF/XML format
         * @param MapFinal
         * @return 
         */
	public static String generateAlignement(ArrayList<Map> MapFinal) {

		Alignment alignments = new URIAlignment();
		try {
			alignments.init(new URI("c"), new URI("c"));
			alignments.setLevel("0");
			alignments.setType("11");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int i = 0; i < MapFinal.size(); i++) {
			Map ee = new Map();
			ee = MapFinal.get(i);

			try {
				URI entity1 = new URI(ee.e1);
				URI entity2 = new URI(ee.e2);

				double score = ee.score;

				String relation = ee.relation;

				// add to alignment
				alignments.addAlignCell(entity1, entity2, relation, score);
			} catch (Exception e) {
				// TODO Auto-generated catch block
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
			return alignmentString;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "0";
		}
	}
}
