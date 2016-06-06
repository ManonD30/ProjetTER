package fr.lirmm.opendata.yamgui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
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

public class Validation extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// get the key in the cookie
		String key = null;
		Cookie[] cookies = request.getCookies();
		for (int i = 0; i < cookies.length; i++) {
			Cookie ck = cookies[i];
			if (ck.getName().equals("key")) {
				key = cookies[i].getValue();
			}
		}

		// if the user allow the save of his files
		String save = request.getParameter("saveOption");
		if (save.equals("yes")) {
			saveFirstFile(key);// save source ontology
			saveSecondFile(key);// save target ontology
		}

		// add cell data to list
		try {
			getCellData(validationListe, key);
		} catch (AlignmentException e) {
			e.printStackTrace();
		}
		// add cell data list to response
		request.setAttribute("data", validationListe);

		// add ontologies label<-->key translation to response
		InputStream in1 = new FileInputStream(
				"WebContent/ontologies/validationSource" + key + ".owl");
		InputStream in2 = new FileInputStream(
				"WebContent/ontologies/validationTarget" + key + ".owl");
		validationOnto1.clear();
		validationOnto2.clear();
		loadOnto(in1, validationOnto1);
		loadOnto(in2, validationOnto2);
		request.setAttribute("onto1", validationOnto1);
		request.setAttribute("onto2", validationOnto2);

		// send response
		this.getServletContext()
				.getRequestDispatcher("/WEB-INF/validation.jsp")
				.forward(request, response);
	}

	// //////////////////////////SAVING FILES FUNCTIONS///////////////////////
	// files name pattern is: YYYYMMDD_keyfile

	// save the user's first ontology if he checked "yes"
	public void saveFirstFile(String key) throws IOException {
		// current date to create a unique name
		// date+first.owl
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		Date date = new Date();
		String dateName = dateFormat.format(date);

		FileChannel in = null; // input
		FileChannel out = null; // output

		try {
			// Init

			in = new FileInputStream("WebContent/ontologies/validationSource"
					+ key + ".owl").getChannel();
			out = new FileOutputStream("WebContent/save/" + dateName + "_"
					+ key + "validationSource.owl").getChannel();

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

	// save the user's second ontology if he checked "yes"
	public void saveSecondFile(String key) throws IOException {
		// current date to create a unique name
		// date+second.owl
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		Date date = new Date();
		String dateName = dateFormat.format(date);

		FileChannel in = null; // input
		FileChannel out = null; // output

		try {
			// Init
			in = new FileInputStream("WebContent/ontologies/validationTarget"
					+ key + ".owl").getChannel();
			out = new FileOutputStream("WebContent/save/" + dateName + "_"
					+ key + "validationTarget.owl").getChannel();

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


	public void getCellData(ArrayList<Map> validationListe, String key)
			throws AlignmentException, IOException {
		AlignmentParser aparser = new AlignmentParser(0);
		// rdf file
		Alignment file = aparser.parse(new File("WebContent/ontologies/rdf"
				+ key + ".rdf").toURI());

		// cell iterator
		Iterator<Cell> align = file.iterator();
		// clear the list
		validationListe.clear();
		// add all iteration to the list
		while (align.hasNext()) {
			// new Map which will contain a cell
			Map mapping = new Map();
			Cell cell = align.next();
			mapping.e1 = (cell.getObject1().toString());
			mapping.e2 = (cell.getObject2().toString());
			mapping.relation = (cell.getRelation().getRelation().toString());
			mapping.score = round(cell.getStrength());
			validationListe.add(mapping);
		}
	}

	// round a double to 2 decimal places
	public static double round(double value) {
		long factor = (long) Math.pow(10, 2);
		value = value * factor;
		long tmp = Math.round(value);
		return (double) tmp / factor;
	}

	public static void loadOnto(InputStream in,
			java.util.Map<String, String> label) throws IOException {

		OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
		model.read(in, null);
		in.close();
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

	public static ArrayList<Map> validationListe = new ArrayList<>();
	public static java.util.Map<String, String> validationOnto1 = new HashMap<>();
	public static java.util.Map<String, String> validationOnto2 = new HashMap<>();
}
