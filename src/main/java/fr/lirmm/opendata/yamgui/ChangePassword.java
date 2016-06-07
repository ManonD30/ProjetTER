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

public class ChangePassword extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String mail = (String) request.getSession().getAttribute("mail");
		String oldPass = request.getParameter("oldPassword");
		String newPass = request.getParameter("newPassword");
		String user = null;

		try {
			// create a mysql database connection
			String myDriver = "org.gjt.mm.mysql.Driver";
			String myUrl = "jdbc:mysql://localhost/yam";
			Class.forName(myDriver);
			Connection conn = DriverManager.getConnection(myUrl, "root",
					"lirmmpass");

			// mysql request
			String query =  "SELECT name FROM user WHERE mail= ? AND password = ?";

			// create the mysql prepared statement
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			preparedStmt.setString(1, mail);
			preparedStmt.setString(2, hash(oldPass));
			
			// execute the prepared statement
			ResultSet result = preparedStmt.executeQuery();
			while (result.next()) {
				user = result.getString("name");
			}
			// close connection to database
			conn.close();

		} catch (Exception e) {
			System.err.println("Exception catched!");
			System.err.println(e.getMessage());
		}

		// if mail / password not in DB
		if (user == null) {
			// error message
			request.setAttribute("error",
					"You entered an invalid old password.");
			// send response
			this.getServletContext()
					.getRequestDispatcher("/WEB-INF/change.jsp")
					.forward(request, response);

		} else {
			// change password
			try {
				// create a mysql database connection
				String myDriver = "org.gjt.mm.mysql.Driver";
				String myUrl = "jdbc:mysql://localhost/yam";
				Class.forName(myDriver);
				Connection conn = DriverManager.getConnection(myUrl, "root",
						"lirmmpass");

				// mysql request
				String query = "UPDATE user SET password=? WHERE mail=?";
				// create the mysql prepared statement
				PreparedStatement preparedStmt = conn.prepareStatement(query);
				preparedStmt.setString(1, hash(newPass));
				preparedStmt.setString(2, mail);
				// execute the prepared statement
				preparedStmt.executeUpdate();
				// close connection to database
				conn.close();
			} catch (Exception e) {
				System.err.println("Exception catched!");
				System.err.println(e.getMessage());
			}

			request.setAttribute("error", "Password successfully changed.");
			// send response
			this.getServletContext()
					.getRequestDispatcher("/WEB-INF/change.jsp")
					.forward(request, response);
		}
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setAttribute("error", "");
		this.getServletContext().getRequestDispatcher("/WEB-INF/change.jsp")
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
