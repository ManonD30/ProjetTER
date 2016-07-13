package fr.lirmm.opendata.yamgui;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.semanticweb.owl.align.Alignment;
import org.semanticweb.owl.align.AlignmentVisitor;

import fr.inrialpes.exmo.align.impl.BasicParameters;
import fr.inrialpes.exmo.align.impl.URIAlignment;
import fr.inrialpes.exmo.align.impl.renderer.RDFRendererVisitor;
import java.util.Properties;

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

                // Load properties file for work directory
                Properties prop = new Properties();
                prop.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("conf.properties"));

		// request checked box
		checked.clear();
		String[] checkbox = request.getParameterValues("checkbox");
		for (String c : checkbox) {
			int mapNumber = Integer.parseInt(c);
			checked.add(Result.liste.get(mapNumber));
		}

		// create final file
		String alignmentString = generateAlignement(checked);
                
                response.setCharacterEncoding("UTF-8");
                response.setContentType("text/plain");
                PrintWriter out = response.getWriter();
                
                out.print(alignmentString);
                out.flush();
	}

	// save final rdf file
	public void saveRDFFile(String key) throws IOException {
		// current date to create a unique name
		// rdf+date.rdf
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		Date date = new Date();
		String dateName = dateFormat.format(date);

                // Load properties file for work directory
                Properties prop = new Properties();
                prop.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("conf.properties"));
                
		FileChannel in = null; // input
		FileChannel out = null; // output

		try {
			// Init

			in = new FileInputStream(prop.getProperty("workdir") + "/ontologies/finalResult" + key
					+ ".rdf").getChannel();
			out = new FileOutputStream(prop.getProperty("workdir") + "/save/" + dateName + "_"
					+ key + "rdf.rdf").getChannel();

			// Copy from in to out
			in.transferTo(0, in.size(), out);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}
	}

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
