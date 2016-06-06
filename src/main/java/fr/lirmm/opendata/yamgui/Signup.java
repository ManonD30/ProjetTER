package fr.lirmm.opendata.yamgui;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataParam;

@Path("/account")
public class Signup {

	@Context
	private HttpServletRequest request;

	@POST
	@Path("/signup")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response signup(@FormDataParam("mailUp") String mail,
			@FormDataParam("nameUp") String name,
			@FormDataParam("affiliationUp") String affiliation,
			@FormDataParam("passwordUp") String password)
			throws MalformedURLException, URISyntaxException {

		try {
			// create a mysql database connection
			String myDriver = "org.gjt.mm.mysql.Driver";
			String myUrl = "jdbc:mysql://localhost/yam";
			Class.forName(myDriver);
			Connection conn = DriverManager.getConnection(myUrl, "root",
					"lirmmpass");

			// check if user is in database
			// the mysql insert statement
			String query = "SELECT name FROM user WHERE mail=?";

			// create the mysql insert preparedstatement
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			preparedStmt.setString(1, mail);

			// execute the prepared statement
			ResultSet result = preparedStmt.executeQuery();

			// get result
			String inDatabase = null;
			while (result.next()) {
				inDatabase = result.getString("name");
			}

			// if user not in database
			System.out.println(name);
			if (inDatabase == null) {
				// Insert into Database
				// the mysql insert statement
				query = " insert into user (mail, name, isAffiliateTo, password)"
						+ " values (?, ?, ?, ?)";

				// create the mysql insert preparedstatement
				preparedStmt = conn.prepareStatement(query);
				preparedStmt.setString(1, mail);
				preparedStmt.setString(2, name);
				String hashed = hash(password);
				preparedStmt.setString(3, affiliation);
				preparedStmt.setString(4, hashed);

				// execute the preparedstatement
				preparedStmt.execute();

				conn.close();

				// create session
				HttpSession session = request.getSession();
				// add user's key (mail) to session
				session.setAttribute("mail", mail);
				// add user's name to session
				session.setAttribute("name", name);
				//add default canMatch to session
				String canMatch= "5";
				session.setAttribute("canMatch", canMatch);
				// send response

			
			} else { // if user not in database redirect to sign
				System.out.println("In DB");
				URI uri = new URL("http://193.49.107.124/matcher.rest.sw/sign")
						.toURI();
				return Response.seeOther(uri).build();
			}
		} catch (Exception e) {
			System.err.println("Exception catched!");
			System.err.println(e.getMessage());
		}
		URI uri = new URL("http://193.49.107.124/matcher.rest.sw/index")
		.toURI();
		return Response.seeOther(uri).build();
	}

	// method which hash String with prefix
	// prefix have to be the same when user is registering or connecting
	public String hash(String password) {
		try {
			password = password + "WONh31K5RYaal07";
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(password.getBytes("UTF-8"));
			StringBuffer hexString = new StringBuffer();

			for (int i = 0; i < hash.length; i++) {
				String hex = Integer.toHexString(0xff & hash[i]);
				if (hex.length() == 1)
					hexString.append('0');
				hexString.append(hex);
			}

			return hexString.toString();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
}
