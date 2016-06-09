/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.lirmm.opendata.yamgui;

import java.io.IOException;
import java.security.MessageDigest;
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
        
        this.driver = "org.gjt.mm.mysql.Driver";
        
        Class.forName(this.driver);
        //Connection conn = DriverManager.getConnection(myUrl, "root", "lirmmpass");
    }
    
    
    public String getWorkDir() {
        return workDir;
    }
    
    /**
     * To connect as a user. It takes the mail and password. And retrieves the 
     * corresponding in the database
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
            user = new YamUser(result.getString("name"), result.getString("mail"), 
                    result.getString("password"), result.getString("isAffiliateTo"), result.getString("asMatched"), result.getString("canMatch"));
                
        }
        

        // close connection to database
        conn.close();
        return user;
    }
    
    
    // method which hash String with prefix
    // prefix have to be the same when user is registering or connecting
    public String getPasswordHash(String password) {
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
