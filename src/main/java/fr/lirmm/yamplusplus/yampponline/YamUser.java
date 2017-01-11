/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.lirmm.yamplusplus.yampponline;

import javax.servlet.http.HttpSession;

/**
 * Class for Yam Users. With: name, mail, password, matchCount and number of
 * possible matchs
 *
 * @author emonet
 */
public class YamUser {

  String apikey;
  String username;
  String mail;
  String role; // can be user or admin
  String passwordHash;
  String isAffiliateTo;
  String field;
  Integer matchCount;
  Integer canMatch;

  /**
   * YamUser constructor
   *
   * @param apikey
   * @param username
   * @param mail
   * @param passwordHash
   * @param isAffiliateTo
   * @param matchCount
   * @param canMatch
   * @param role
   */
  public YamUser(String apikey, String mail, String username, String passwordHash, String isAffiliateTo, String field, int matchCount, int canMatch, String role) {
    this.apikey = apikey;
    this.mail = mail;
    this.username = username;
    this.role = role;
    this.passwordHash = passwordHash;
    this.isAffiliateTo = isAffiliateTo;

    this.matchCount = matchCount;
    this.canMatch = canMatch;
  }

  /**
   * Build Yam User from HTTP session
   *
   * @param session
   */
  public YamUser(HttpSession session) {
    this.apikey = (String) session.getAttribute("apikey");
    this.mail = (String) session.getAttribute("mail");
    this.username = (String) session.getAttribute("username");
    this.role = (String) session.getAttribute("role");
    this.isAffiliateTo = (String) session.getAttribute("isAffiliateTo");
    this.field = (String) session.getAttribute("field");

    if (session.getAttribute("matchCount") != null) {
      this.matchCount = (int) session.getAttribute("matchCount");
    }
    if (session.getAttribute("canMatch") != null) {
      this.canMatch = (int) session.getAttribute("canMatch");
    }
  }

  /**
   * Add the YamUser param to the given session.
   *
   * @param session
   * @return HttpSession
   */
  public HttpSession addUserToSession(HttpSession session) {
    session.setAttribute("apikey", this.apikey);
    session.setAttribute("mail", this.mail);
    session.setAttribute("username", this.username);
    session.setAttribute("role", this.role);
    session.setAttribute("matchCount", this.matchCount);
    session.setAttribute("canMatch", this.canMatch);
    session.setAttribute("isAffiliateTo", this.isAffiliateTo);

    return session;
  }

  public String getApikey() {
    return apikey;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getMail() {
    return mail;
  }

  public void setMail(String mail) {
    this.mail = mail;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public String getField() {
    return field;
  }

  public void setField(String field) {
    this.field = field;
  }

  public String getIsAffiliateTo() {
    return isAffiliateTo;
  }

  public void setIsAffiliateTo(String isAffiliateTo) {
    this.isAffiliateTo = isAffiliateTo;
  }

  public Integer getMatchCount() {
    return matchCount;
  }

  public void setMatchCount(Integer matchCount) {
    this.matchCount = matchCount;
  }

  public Integer getCanMatch() {
    return canMatch;
  }

  public void setCanMatch(Integer canMatch) {
    this.canMatch = canMatch;
  }

}
