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
import java.util.Properties;
import main.MainProgram;

public class Result extends HttpServlet {
	private static final long serialVersionUID = 1L;

	// servlet's doPost which run YAM++ and redirect to the .JSP
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// check and update "asMatched" value
		int asMatched = 0;
		int canMatch = 0;
                // Load properties file for work directory
                Properties prop = new Properties();
                prop.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("conf.properties"));
		try {
			// create a mysql database connection
			String myDriver = "org.gjt.mm.mysql.Driver";
			String myUrl = "jdbc:mysql://localhost/yam";
			Class.forName(myDriver);
			Connection conn = DriverManager.getConnection(myUrl, "root",
					"lirmmpass");

			// get user's mail
			String mail = (String) request.getSession().getAttribute("mail");

			// increment asMatched value
			String query = "UPDATE user SET asMatched=asMatched+1 WHERE mail=?";

			// create the mysql prepared statement
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			preparedStmt.setString(1, mail);

			// execute the preparedstatement
			preparedStmt.execute();

			// check asMatched value
			query = "SELECT asMatched, canMatch FROM user WHERE mail=?";

			// create the mysql prepared statement
			preparedStmt = conn.prepareStatement(query);
			preparedStmt.setString(1, mail);

			// execute the preparedstatement
			ResultSet result = preparedStmt.executeQuery();

			while (result.next()) {
				asMatched = Integer.parseInt(result.getString("asMatched"));
				canMatch = Integer.parseInt(result.getString("canMatch"));
			}
			// close connection to database
			conn.close();

		} catch (Exception e) {
			System.err.println("Exception catched!");
			System.err.println(e.getMessage());
		}

		if (asMatched > canMatch) {
			// add number of match to response
			request.setAttribute("asMatched", Integer.toString(asMatched));
			request.setAttribute("canMatch", Integer.toString(canMatch));
			this.getServletContext()
					.getRequestDispatcher("/WEB-INF/tooManyMatch.jsp")
					.forward(request, response);
			;
		} else {

			// get the key in the cookie
			String key = null;
			Cookie[] cookies = request.getCookies();
			for (int i = 0; i < cookies.length; i++) {
				Cookie ck = cookies[i];
				if (ck.getName().equals("key")) {
					key = cookies[i].getValue();
				}
			}

			// save the file if the user checked 'yes'
			String save = request.getParameter("saveOption");
			if (save.equals("yes")) {
				saveFirstFile(key);// save first ontology
				saveSecondFile(key);// save second ontology
			}

			// get time at the matching beginning
			long begin = System.currentTimeMillis();

			// run YAM++ with two ontologies (.owl) to a new .rdf
			MainProgram.match(prop.getProperty("workdir") + "/ontologies/first" + key + ".owl",
					prop.getProperty("workdir") + "/ontologies/second" + key + ".owl",
					prop.getProperty("workdir") + "/ontologies/result" + key + ".rdf");

			// get time at the matching end
			long end = System.currentTimeMillis();

			// matching time equals to
			float execTime = ((float) (end - begin)) / 1000f;
			// String conversion to allow data transfer to result.jsp
			String s = Float.toString(execTime);
			// add matching time to response
			request.setAttribute("time", s);

			// add cell data to the list
			try {
				getCellData(liste, key);
			} catch (AlignmentException e) {
				e.printStackTrace();
			}

			// add cell data list to response
			request.setAttribute("data", liste);

			// add ontologies label<-->key translation to response
			InputStream in1 = new FileInputStream(prop.getProperty("workdir") + "/ontologies/first"
					+ key + ".owl");
			InputStream in2 = new FileInputStream(
					prop.getProperty("workdir") + "/ontologies/second" + key + ".owl");
			Onto1.clear();
			Onto2.clear();
			loadOnto(in1, Onto1);
			loadOnto(in2, Onto2);
			request.setAttribute("onto1", Onto1);
			request.setAttribute("onto2", Onto2);

			// send response
			this.getServletContext()
					.getRequestDispatcher("/WEB-INF/result.jsp")
					.forward(request, response);
		}
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
                
                // Load properties file for work directory
                Properties prop = new Properties();
                prop.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("conf.properties"));

		try {
			// Init

			in = new FileInputStream(prop.getProperty("workdir") + "/ontologies/first" + key
					+ ".owl").getChannel();
			out = new FileOutputStream(prop.getProperty("workdir") + "/save/" + dateName + "_"
					+ key + "source.owl").getChannel();

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
                
                // Load properties file for work directory
                Properties prop = new Properties();
                prop.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("conf.properties"));

		try {
			// Init
			in = new FileInputStream(prop.getProperty("workdir") + "/ontologies/second" + key
					+ ".owl").getChannel();
			out = new FileOutputStream(prop.getProperty("workdir") + "/save/" + dateName + "_"
					+ key + "target.owl").getChannel();

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

	public void getCellData(ArrayList<Map> liste, String key)
			throws AlignmentException, IOException {
            
                // Load properties file for work directory
                Properties prop = new Properties();
                prop.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("conf.properties"));
                
		AlignmentParser aparser = new AlignmentParser(0);
		// rdf file
		Alignment file = aparser.parse(new File(prop.getProperty("workdir") + "/ontologies/result"
				+ key + ".rdf").toURI());

		// cell iterator
		Iterator<Cell> align = file.iterator();
		// clear the list
		liste.clear();
		// add all iteration to the list
		while (align.hasNext()) {
			// new Map which will contain a cell
			Map mapping = new Map();
			Cell cell = align.next();

			mapping.e1 = (cell.getObject1().toString());
			mapping.e2 = (cell.getObject2().toString());
			mapping.relation = (cell.getRelation().getRelation().toString());
			mapping.score = round(cell.getStrength());
			liste.add(mapping);
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

	public static ArrayList<Map> liste = new ArrayList<>();
	public static java.util.Map<String, String> Onto1 = new HashMap<>();
	public static java.util.Map<String, String> Onto2 = new HashMap<>();
}
