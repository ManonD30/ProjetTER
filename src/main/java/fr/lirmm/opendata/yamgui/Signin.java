package fr.lirmm.opendata.yamgui;

import java.io.IOException;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class Signin extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String mail = request.getParameter("mailIn");
		String password = request.getParameter("passwordIn");
		String name = null;
		String canMatch = null;

		try {
			// create a mysql database connection
			String myDriver = "org.gjt.mm.mysql.Driver";
			String myUrl = "jdbc:mysql://localhost/yam";
			Class.forName(myDriver);
			Connection conn = DriverManager.getConnection(myUrl, "root",
					"lirmmpass");

			// mysql request
			String query = "SELECT name, canMatch FROM user WHERE mail= ? AND password = ?";

			// create the mysql prepared statement
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			preparedStmt.setString(1, mail);
			String hashed = hash(password);
			preparedStmt.setString(2, hashed);

			// execute the prepared statement
			ResultSet result = preparedStmt.executeQuery();

			// get user's name
			while (result.next()) {
				name = result.getString("name");
				canMatch = result.getString("canMatch");
			}

			// close connection to database
			conn.close();

		} catch (Exception e) {
			System.err.println("Exception catched!");
			System.err.println(e.getMessage());
		}

		// if invalid password or mail
		if (name == null) {
			// error message
			request.setAttribute("error", "Invalid login or password");
			// send response
			this.getServletContext().getRequestDispatcher("/WEB-INF/sign.jsp")
					.forward(request, response);

		} else {
			// create session
			HttpSession session = request.getSession();
			// add user's key (mail) to session
			session.setAttribute("mail", mail);
			// add user's name to session
			session.setAttribute("name", name);
			//add number of matching allowed to session
			session.setAttribute("canMatch", canMatch);
			// send response
			this.getServletContext().getRequestDispatcher("/WEB-INF/index.jsp")
					.forward(request, response);
		}
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// error message
		request.setAttribute("error", "");
		// send response
		this.getServletContext().getRequestDispatcher("/WEB-INF/sign.jsp")
				.forward(request, response);
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
