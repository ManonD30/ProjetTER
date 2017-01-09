/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.lirmm.yamplusplus.yampponline;

import javax.servlet.http.HttpSession;

/**
 * Class for Yam Users. With: name, mail, password, matchCount and number of possible matchs
 *
 * @author emonet
 */
public class YamUser {
    String name;
    String mail;
    String passwordHash;
    String isAffiliateTo;
    Integer asMatched;
    Integer canMatch;

    /**
     * YamUser constructor
     * 
     * @param name
     * @param mail
     * @param passwordHash
     * @param isAffiliateTo
     * @param asMatched 
     * @param canMatch
     */
    public YamUser(String name, String mail, String passwordHash, String isAffiliateTo, int asMatched, int canMatch) {
        this.name = name;
        this.mail = mail;
        this.passwordHash = passwordHash;
        this.isAffiliateTo = isAffiliateTo;
        
        this.asMatched = asMatched;
        this.canMatch = canMatch;
    }
    
    /**
     * Build YAM from session
     * @param session 
     */
    public YamUser(HttpSession session) {
      this.mail = (String) session.getAttribute("mail");
      this.name = (String) session.getAttribute("name");
      this.isAffiliateTo = (String) session.getAttribute("isAffiliateTo");
      
      this.asMatched = (int) session.getAttribute("asMatched");
      this.canMatch = (int) session.getAttribute("canMatch");
    }
    
    /**
     * Add the YamUser param to the given session.
     * 
     * @param session
     * @return HttpSession
     */
    public HttpSession addUserToSession(HttpSession session) {
      session.setAttribute("mail", this.mail);
      session.setAttribute("name", this.name);
      session.setAttribute("asMatched", this.asMatched);
      session.setAttribute("canMatch", this.canMatch);
      session.setAttribute("isAffiliateTo", this.isAffiliateTo);
      
      return session;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getIsAffiliateTo() {
        return isAffiliateTo;
    }

    public void setIsAffiliateTo(String isAffiliateTo) {
        this.isAffiliateTo = isAffiliateTo;
    }

    public Integer getAsMatched() {
        return asMatched;
    }

    public void setAsMatched(Integer asMatched) {
        this.asMatched = asMatched;
    }
    
    public Integer getCanMatch() {
        return canMatch;
    }

    public void setCanMatch(Integer canMatch) {
        this.canMatch = canMatch;
    }
    
    
}
