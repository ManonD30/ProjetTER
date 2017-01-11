/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.lirmm.yamplusplus.yampponline;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A Class to connect to the MySQL Database used by Yam to manage users
 *
 * @author emonet
 */
public class YamDatabaseConnector {

  Connection dbConnection;// Remove it?
  String dbUrl;
  String dbUsername;
  String dbPassword;
  String workDir;
  String driver;

  public YamDatabaseConnector() throws IOException, ClassNotFoundException {
    // Load properties file for work directory
    Properties prop = new Properties();
    prop.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("conf.properties"));

    this.dbUrl = "jdbc:mysql://" + prop.getProperty("dbhost") + "/" + prop.getProperty("dbname");
    this.dbUsername = prop.getProperty("dbusername");
    this.dbPassword = prop.getProperty("dbpassword");
    this.workDir = prop.getProperty("workdir");

    this.driver = "com.mysql.jdbc.Driver";
    Class.forName(this.driver);
  }

  public String getWorkDir() {
    return workDir;
  }

  /**
   * To connect as a user. It takes the mail and password. And retrieves the
   * corresponding user in the database
   *
   * @param mail
   * @param password
   * @return YamUser
   * @throws SQLException
   * @throws ClassNotFoundException
   */
  public YamUser userConnection(String mail, String password) throws SQLException, ClassNotFoundException {
    // create a mysql database connection
    //Class.forName(this.driver);
    Connection conn = DriverManager.getConnection(this.dbUrl, this.dbUsername, this.dbPassword);

    // mysql request
    String query = "SELECT * FROM user WHERE mail= ? AND password = ?";

    // create the mysql prepared statement
    PreparedStatement preparedStmt = conn.prepareStatement(query);
    preparedStmt.setString(1, mail);
    String hashed = this.getPasswordHash(password);
    preparedStmt.setString(2, hashed);

    // execute the prepared statement
    ResultSet result = preparedStmt.executeQuery();

    YamUser user = null;
    // get user
    while (result.next()) {
      user = new YamUser(result.getString("apikey"), result.getString("mail"), result.getString("username"), result.getString("password"),
              result.getString("isAffiliateTo"), result.getInt("matchCount"), result.getInt("canMatch"), result.getString("role"));
    }

    // close connection to database
    conn.close();
    return user;
  }

  /**
   * To create a user. It takes the mail and password. And create the
   * corresponding user in the database
   *
   * @param mail
   * @param username
   * @param affiliation
   * @param field
   * @param password
   * @return YamUser
   * @throws SQLException
   * @throws ClassNotFoundException
   */
  public YamUser userCreate(String mail, String username, String affiliation, String field, String password) throws SQLException, ClassNotFoundException {
    // create a mysql database connection
    //Class.forName(this.driver);
    Connection conn = DriverManager.getConnection(this.dbUrl, this.dbUsername, this.dbPassword);

    // check if user is in database
    // the mysql insert statement
    String query = "SELECT username FROM user WHERE mail=?";

    // create the mysql insert preparedstatement
    PreparedStatement preparedStmt = conn.prepareStatement(query);
    preparedStmt.setString(1, mail);

    // execute the prepared statement
    ResultSet result = preparedStmt.executeQuery();

    // get result
    String inDatabase = null;
    while (result.next()) {
      inDatabase = result.getString("username");
    }

    // Generate unique random apikey
    SecureRandom random = new SecureRandom();
    String apikey = null;
    boolean apikeyExists = true;
    while (apikeyExists) {
      apikey = new BigInteger(130, random).toString(32).substring(0, 16);
      apikeyExists = isValidApikey(apikey);
    }

    // Set matchCount to 0 at creation
    int matchCount = 0;
    // Set canMatch to 10 at creation
    int canMatch = 10;
    String role = "user";
    // Here we set the admin user as an admin when created. We can add other
    if (username.equals("admin")) {
      role = "admin";
    }

    // if user not in database
    if (inDatabase == null) {
      // Insert into Database
      // the mysql insert statement
      query = " insert into user (apikey, mail, username, isAffiliateTo, field, matchCount, canMatch, role, password)"
              + " values (?, ?, ?, ?, ?, ?, ?, ?, ?)";

      // create the mysql insert preparedstatement
      preparedStmt = conn.prepareStatement(query);
      preparedStmt.setString(1, apikey);
      preparedStmt.setString(2, mail);
      preparedStmt.setString(3, username);
      preparedStmt.setString(4, affiliation);
      preparedStmt.setString(5, field);
      preparedStmt.setInt(6, matchCount);
      preparedStmt.setInt(7, canMatch);
      preparedStmt.setString(8, role);

      String hashed = getPasswordHash(password);
      preparedStmt.setString(9, hashed);
      
      // execute the preparedstatement
      preparedStmt.execute();
    } else {
      Logger.getLogger(Matcher.class.getName()).log(Level.WARNING, "Already in database");
      return null;
    }
    conn.close();

    return new YamUser(apikey, mail, username, affiliation, password, matchCount, canMatch, role);
  }

  /**
   * Get all users in database
   *
   * @return ArrayList of YamUser
   */
  public ArrayList<YamUser> getUserList() {
    ArrayList<YamUser> userList = new ArrayList<>();

    try {
      Connection conn = DriverManager.getConnection(this.dbUrl, this.dbUsername, this.dbPassword);
      // mysql request
      String query = "SELECT * FROM user";

      // create the mysql prepared statement
      PreparedStatement preparedStmt = conn.prepareStatement(query);

      // execute the prepared statement
      ResultSet result = preparedStmt.executeQuery();

      YamUser user = null;
      // get user
      while (result.next()) {
        userList.add(new YamUser(result.getString("apikey"), result.getString("mail"), result.getString("username"), result.getString("password"),
                result.getString("isAffiliateTo"), result.getInt("matchCount"), result.getInt("canMatch"), result.getString("role")));
      }
      conn.close();
    } catch (SQLException e) {
      Logger.getLogger(Matcher.class.getName()).log(Level.SEVERE, "Error when retrieving all users: {0}", e.toString());;
    }

    return userList;
  }

  /**
   * Return true is given apikey found in the dabatase
   *
   * @param apikey
   * @return boolean
   */
  public boolean isValidApikey(String apikey) {
    try {
      Connection conn = DriverManager.getConnection(this.dbUrl, this.dbUsername, this.dbPassword);
      String apikeyQuery = "SELECT username FROM user WHERE apikey=?";

      // create the mysql insert preparedstatement
      PreparedStatement apikeyPreparedStmt = conn.prepareStatement(apikeyQuery);
      apikeyPreparedStmt.setString(1, apikey);
      // execute the prepared statement
      ResultSet apikeyResult = apikeyPreparedStmt.executeQuery();
      while (apikeyResult.next()) {
        conn.close();
        return true;
      }
      conn.close();
    } catch (SQLException e) {
      Logger.getLogger(Matcher.class.getName()).log(Level.SEVERE, "Error when checking apikey validity: {0}", e.toString());;
    }
    return false;
  }

  /**
   * To update matchCount of a user. It takes the apikey. And retrieves the
   * corresponding user in the database
   *
   * @param apikey
   * @return YamUser
   * @throws ClassNotFoundException
   */
  public YamUser updateMatchCount(String apikey) throws ClassNotFoundException {
    YamUser user = null;
    try {
      // create a mysql database connection
      Connection conn = DriverManager.getConnection(this.dbUrl, this.dbUsername, this.dbPassword);

      // increment matchCount value
      String query = "UPDATE user SET matchCount=matchCount+1 WHERE apikey=?";

      // create the mysql prepared statement
      PreparedStatement preparedStmt = conn.prepareStatement(query);
      preparedStmt.setString(1, apikey);

      // execute the preparedstatement
      preparedStmt.execute();

      // check matchCount value
      query = "SELECT * FROM user WHERE apikey=?";

      // create the mysql prepared statement
      preparedStmt = conn.prepareStatement(query);
      preparedStmt.setString(1, apikey);

      // execute the preparedstatement
      ResultSet result = preparedStmt.executeQuery();

      while (result.next()) {
        user = new YamUser(result.getString("apikey"), result.getString("mail"), result.getString("username"),
                result.getString("password"), result.getString("isAffiliateTo"), result.getInt("matchCount"), result.getInt("canMatch"), result.getString("role"));
      }
      // close connection to database
      conn.close();
    } catch (SQLException e) {
      Logger.getLogger(Matcher.class.getName()).log(Level.SEVERE, "Error updating matchCount: {0}", e.toString());
    }
    return user;
  }

  /**
   * Change the password linked to this apikey
   *
   * @param apikey
   * @param oldPassword
   * @param newPassword
   * @return boolean
   * @throws ClassNotFoundException
   */
  public boolean updatePassword(String apikey, String oldPassword, String newPassword) throws ClassNotFoundException {
    boolean isValidPassword = false;
    try {
      // create a mysql database connection
      Connection conn = DriverManager.getConnection(this.dbUrl, this.dbUsername, this.dbPassword);

      String query = "SELECT username FROM user WHERE apikey= ? AND password = ?";
      // create the mysql prepared statement
      PreparedStatement preparedStmt = conn.prepareStatement(query);
      preparedStmt.setString(1, apikey);
      preparedStmt.setString(2, getPasswordHash(oldPassword));
      // execute the prepared statement
      ResultSet result = preparedStmt.executeQuery();
      while (result.next()) {
        isValidPassword = true;
      }
      // close connection to database
      conn.close();

    } catch (SQLException e) {
      Logger.getLogger(Matcher.class.getName()).log(Level.SEVERE, "Error checking old password: {0}", e.toString());
    }
    if (isValidPassword) {
      return resetPassword(apikey, newPassword);
    }
    return false;
  }
  
  /**
   * Delete user given its apikey
   *
   * @param apikey
   * @return boolean
   * @throws ClassNotFoundException
   */
  public boolean deleteUser(String apikey) throws ClassNotFoundException {
    try {
      // create a mysql database connection
      Connection conn = DriverManager.getConnection(this.dbUrl, this.dbUsername, this.dbPassword);

      String query = "DELETE FROM user WHERE apikey=?";
      // create the mysql prepared statement
      PreparedStatement preparedStmt = conn.prepareStatement(query);
      preparedStmt.setString(1, apikey);
      // execute the prepared statement
      preparedStmt.executeUpdate();
      // close connection to database
      conn.close();
      return true;
    } catch (SQLException e) {
      Logger.getLogger(Matcher.class.getName()).log(Level.SEVERE, "Error deleting user: {0}", e.toString());
    }
    return false;
  }

  /**
   * Reset password to given new password
   *
   * @param apikey
   * @param newPassword
   * @return boolean
   * @throws ClassNotFoundException
   */
  public boolean resetPassword(String apikey, String newPassword) throws ClassNotFoundException {
    try {
      // create a mysql database connection
      Connection conn = DriverManager.getConnection(this.dbUrl, this.dbUsername, this.dbPassword);

      String query = "UPDATE user SET password=? WHERE apikey=?";
      // create the mysql prepared statement
      PreparedStatement preparedStmt = conn.prepareStatement(query);
      preparedStmt.setString(1, getPasswordHash(newPassword));
      preparedStmt.setString(2, apikey);
      // execute the prepared statement
      preparedStmt.executeUpdate();
      // close connection to database
      conn.close();
      return true;
    } catch (SQLException e) {
      Logger.getLogger(Matcher.class.getName()).log(Level.SEVERE, "Error changing password: {0}", e.toString());
    }
    return false;
  }

  // method which hash String with prefix
  // prefix have to be the same when user is registering or connecting
  /**
   * Get the password hash. It is how the password is stored in the DB. We had a
   * suffix before converting it
   *
   * @param password
   * @return password hash as String
   */
  public String getPasswordHash(String password) {
    try {
      password = password + "WONh31K5RYaal07";
      // Hash password using SHA (like MD5 but on 256 bits)
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hash = digest.digest(password.getBytes("UTF-8"));
      StringBuffer hexString = new StringBuffer();

      for (int i = 0; i < hash.length; i++) {
        String hex = Integer.toHexString(0xff & hash[i]);
        if (hex.length() == 1) {
          hexString.append('0');
        }
        hexString.append(hex);
      }
      return hexString.toString();
    } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
      throw new RuntimeException(ex);
    }
  }

}
