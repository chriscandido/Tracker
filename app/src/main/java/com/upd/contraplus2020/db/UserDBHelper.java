package com.upd.contraplus2020.db;

public class UserDBHelper {

    private String unique_Id;
    private String firstName;
    private String lastName;
    private Integer age;
    private String contactNumber;
    private String permanentAddress;
    private String municipalityOrCity;
    private String presentAddress;
    private String present_municipalityOrCity;
    private Integer isSuspectedOfContact;
    private Integer isPUI;
    private String gender;
    private String testResult;

    public UserDBHelper(String unique_Id,
                        String firstName,
                        String lastName,
                        Integer age,
                        String contactNumber,
                        String permanentAddress,
                        String municipalityOrCity,
                        String presentAddress,
                        String present_municipalityOrCity,
                        Integer isSuspectedOfContact,
                        Integer isPUI,
                        String gender,
                        String testResult) {
        this.unique_Id = unique_Id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.contactNumber = contactNumber;
        this.permanentAddress = permanentAddress;
        this.municipalityOrCity = municipalityOrCity;
        this.presentAddress = presentAddress;
        this.present_municipalityOrCity = present_municipalityOrCity;
        this.isSuspectedOfContact = isSuspectedOfContact;
        this.isPUI = isPUI;
        this.gender = gender;
        this.testResult = testResult;
    }

    public void setUnique_Id(String unique_Id){
        this.unique_Id = unique_Id;
    }

    public String getUnique_Id() {
        return unique_Id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getPermanentAddress() {
        return permanentAddress;
    }

    public void setPermanentAddress(String permanentAddress) {
        this.permanentAddress = permanentAddress;
    }

    public String getMunicipalityOrCity() {
        return municipalityOrCity;
    }

    public void setMunicipalityOrCity(String municipalityOrCity) {
        this.municipalityOrCity = municipalityOrCity;
    }

    public String getPresentAddress() {
        return presentAddress;
    }

    public void setPresentAddress(String presentAddress) {
        this.presentAddress = presentAddress;
    }

    public String getPresent_municipalityOrCity() {
        return present_municipalityOrCity;
    }

    public void setPresent_municipalityOrCity(String present_municipalityOrCity) {
        this.present_municipalityOrCity = present_municipalityOrCity;
    }

    public Integer getIsSuspectedOfContact() {
        return isSuspectedOfContact;
    }

    public void setIsSuspectedOfContact(Integer isSuspectedOfContact) {
        this.isSuspectedOfContact = isSuspectedOfContact;
    }

    public Integer getIsPUI() {
        return isPUI;
    }

    public void setIsPUI(Integer isPUI) {
        this.isPUI = isPUI;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getTestResult() {
        return testResult;
    }

    public void setTestResult(String testResult) {
        this.testResult = testResult;
    }
}

