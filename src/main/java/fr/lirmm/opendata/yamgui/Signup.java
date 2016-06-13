package fr.lirmm.opendata.yamgui;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

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
			throws MalformedURLException, URISyntaxException, IOException {
            
                // Load properties file for work directory
                Properties prop = new Properties();
                prop.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("conf.properties"));

		try {
                        YamDatabaseConnector dbConnector = new YamDatabaseConnector();
                        YamUser user = dbConnector.userCreate(mail, name, affiliation, password);
                    
                        if (user == null) {
                            System.out.println("In DB");
                            //response.sendRedirect("sign")
                            URI uri = new URL(prop.getProperty("appurl") + "/sign")
                                            .toURI();
                            return Response.seeOther(uri).build();
                        } else {
                            // create session
                            HttpSession session = request.getSession();
                            // add user's key (mail) to session
                            session.setAttribute("mail", user.getMail());
                            // add user's name to session
                            session.setAttribute("name", user.getName());
                            //add canMatch to session
                            session.setAttribute("canMatch", user.getCanMatch());
                            // send response
                        }
		} catch (Exception e) {
			System.err.println("Exception catched!");
			System.err.println(e.getMessage());
		}
		URI uri = new URL(prop.getProperty("appurl") + "/index")
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
