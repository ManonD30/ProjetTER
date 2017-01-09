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
import java.util.Properties;

/**
 * A Class to connect to the MySQL Database used by Yam to manage users
 *
 * @author emonet
 */
public class YamDatabaseConnector {

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

    //this.driver = "org.gjt.mm.mysql.Driver";
    this.driver = "com.mysql.jdbc.Driver";

    Class.forName(this.driver);
    //Connection conn = DriverManager.getConnection(myUrl, "root", "password");
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
    Class.forName(this.driver);
    Connection conn = DriverManager.getConnection(this.dbUrl, this.dbUsername,
            this.dbPassword);

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
      user = new YamUser(result.getString("apikey"), result.getString("mail"), result.getString("name"), result.getString("password"),
              result.getString("isAffiliateTo"), result.getInt("matchCount"), result.getInt("canMatch"));
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
   * @param name
   * @param affiliation
   * @param password
   * @return YamUser
   * @throws SQLException
   * @throws ClassNotFoundException
   */
  public YamUser userCreate(String mail, String name, String affiliation, String password) throws SQLException, ClassNotFoundException {
    // create a mysql database connection
    Class.forName(this.driver);
    Connection conn = DriverManager.getConnection(this.dbUrl, this.dbUsername, this.dbPassword);

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

    // Generate unique random apikey
    SecureRandom random = new SecureRandom();
    String apikey = null;
    boolean apikeyExists = true;
    while (apikeyExists) {
      apikeyExists = false;
      apikey = new BigInteger(130, random).toString(32).substring(0,16);
      String apikeyQuery = "SELECT name FROM user WHERE apikey=?";

      // create the mysql insert preparedstatement
      PreparedStatement apikeyPreparedStmt = conn.prepareStatement(apikeyQuery);
      apikeyPreparedStmt.setString(1, apikey);
      // execute the prepared statement
      ResultSet apikeyResult = apikeyPreparedStmt.executeQuery();
      while (apikeyResult.next()) {
        apikeyExists = true;
      }
    }

    // Set matchCount to 0 at creation
    int matchCount = 0;
    // Set canMatch to 10 at creation
    int canMatch = 10;

    // if user not in database
    if (inDatabase == null) {
      // Insert into Database
      // the mysql insert statement
      query = " insert into user (apikey, mail, name, isAffiliateTo, matchCount, canMatch, password)"
              + " values (?, ?, ?, ?, ?, ?, ?)";

      // create the mysql insert preparedstatement
      preparedStmt = conn.prepareStatement(query);
      preparedStmt.setString(1, apikey);
      preparedStmt.setString(2, mail);
      preparedStmt.setString(3, name);
      preparedStmt.setString(4, affiliation);
      preparedStmt.setInt(5, matchCount);
      preparedStmt.setInt(6, canMatch);

      String hashed = getPasswordHash(password);
      preparedStmt.setString(7, hashed);

      // execute the preparedstatement
      preparedStmt.execute();

    } else { // if user not in database redirect to sign
      System.out.println("In DB");
      return null;
    }
    conn.close();

    return new YamUser(apikey, mail, name, affiliation, password, matchCount, canMatch);
  }

  /**
   * To update matchCount of a user. It takes the apikey. And retrieves the
   * corresponding user in the database
   *
   * @param apikey
   * @return YamUser
   * @throws SQLException
   * @throws ClassNotFoundException
   */
  public YamUser updateMatchCount(String apikey) throws SQLException, ClassNotFoundException {
    // create a mysql database connection
    Class.forName(this.driver);
    Connection conn = DriverManager.getConnection(this.dbUrl, this.dbUsername, this.dbPassword);

    Class.forName(this.driver);

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

    YamUser user = null;
    while (result.next()) {
      user = new YamUser(result.getString("apikey"), result.getString("mail"), result.getString("name"),
              result.getString("password"), result.getString("isAffiliateTo"), result.getInt("matchCount"), result.getInt("canMatch"));
    }
    // close connection to database
    conn.close();
    return user;
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
