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
  String name;
  String mail;
  String passwordHash;
  String isAffiliateTo;
  Integer matchCount;
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
  public YamUser(String apikey, String mail, String name, String passwordHash, String isAffiliateTo, int matchCount, int canMatch) {
    this.apikey = apikey;
    this.mail = mail;
    this.name = name;
    this.passwordHash = passwordHash;
    this.isAffiliateTo = isAffiliateTo;

    this.matchCount = matchCount;
    this.canMatch = canMatch;
  }

  /**
   * Build YAM from HTTP session
   *
   * @param session
   */
  public YamUser(HttpSession session) {
    this.apikey = (String) session.getAttribute("apikey");
    this.mail = (String) session.getAttribute("mail");
    this.name = (String) session.getAttribute("name");
    this.isAffiliateTo = (String) session.getAttribute("isAffiliateTo");

    this.matchCount = (int) session.getAttribute("matchCount");
    this.canMatch = (int) session.getAttribute("canMatch");
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
    session.setAttribute("name", this.name);
    session.setAttribute("matchCount", this.matchCount);
    session.setAttribute("canMatch", this.canMatch);
    session.setAttribute("isAffiliateTo", this.isAffiliateTo);

    return session;
  }

  public String getApikey() {
    return apikey;
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
