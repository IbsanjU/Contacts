package com.ibsanju.contact.Data;

public
class ContactData {

    private String conFirstName;
    private String conLastName;
    private String conPhone;
    private String conEmail;
    private String conStreet;
    private String conCity;
    private String conState;
    private String conZip;

    public void
    contactData(String conFirstName, String conLastName, String conPhone, String conEmail, String conStreet, String conCity, String conState, String conZip) {
        this.conFirstName = conFirstName;
        this.conLastName  = conLastName;
        this.conPhone     = conPhone;
        this.conEmail     = conEmail;
        this.conStreet    = conStreet;
        this.conCity      = conCity;
        this.conState     = conState;
        this.conZip       = conZip;
    }

    public
    String getConFirstName() {
        return conFirstName;
    }

    public
    void setConFirstName(String conFirstName) {
        this.conFirstName = conFirstName;
    }

    public
    String getConLastName() {
        return conLastName;
    }

    public
    void setConLastName(String conLastName) {
        this.conLastName = conLastName;
    }

    public
    String getConPhone() {
        return conPhone;
    }

    public
    void setConPhone(String conPhone) {
        this.conPhone = conPhone;
    }

    public
    String getConEmail() {
        return conEmail;
    }

    public
    void setConEmail(String conEmail) {
        this.conEmail = conEmail;
    }

    public
    String getConStreet() {
        return conStreet;
    }

    public
    void setConStreet(String conStreet) {
        this.conStreet = conStreet;
    }

    public
    String getConCity() {
        return conCity;
    }

    public
    void setConCity(String conCity) {
        this.conCity = conCity;
    }

    public
    String getConState() {
        return conState;
    }

    public
    void setConState(String conState) {
        this.conState = conState;
    }

    public
    String getConZip() {
        return conZip;
    }

    public
    void setConZip(String conZip) {
        this.conZip = conZip;
    }
}
