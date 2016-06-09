/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.lirmm.opendata.yamgui;

/**
 *
 * @author emonet
 */
public class YamUser {
    String name;
    String mail;
    String passwordHash;
    String isAffiliateTo;
    Integer canMatch;

    /**
     * YamUser constructor
     * 
     * @param name
     * @param mail
     * @param passwordHash
     * @param isAffiliateTo
     * @param canMatch 
     */
    public YamUser(String name, String mail, String passwordHash, String isAffiliateTo, String canMatch) {
        this.name = name;
        this.mail = mail;
        this.passwordHash = passwordHash;
        this.isAffiliateTo = isAffiliateTo;
        
        // If canMatch is null we set it to 5 by default
        if (canMatch == null) {
            this.canMatch = 5;
        } else {
            this.canMatch = Integer.parseInt(canMatch);
        }
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

    public Integer getCanMatch() {
        return canMatch;
    }

    public void setCanMatch(Integer canMatch) {
        this.canMatch = canMatch;
    }
    
    
    
    
}
