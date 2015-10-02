package com.spouzee.server.api.schema;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Sagar on 8/4/2015.
 */
public class UserDetails {

    private String name;

    private Date dateOfBirth;

    private String religion;

    private String caste;

    private String subcaste;

    private String language;

    private String employment;

    private String qualification;

    private String role;

    private List<UserToken> userTokenList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getReligion() {
        return religion;
    }

    public void setReligion(String religion) {
        this.religion = religion;
    }

    public String getCaste() {
        return caste;
    }

    public void setCaste(String caste) {
        this.caste = caste;
    }

    public String getSubcaste() {
        return subcaste;
    }

    public void setSubcaste(String subcaste) {
        this.subcaste = subcaste;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getEmployment() {
        return employment;
    }

    public void setEmployment(String employment) {
        this.employment = employment;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<UserToken> getUserTokenList() {
        return userTokenList;
    }

    public void setUserTokenList(List<UserToken> userTokenList) {
        this.userTokenList = userTokenList;
    }

    public static void main(String[] args) throws IOException {
        String json = "{\"dateOfBirth\" : \"1987-06-20\"}";
        ObjectMapper objectMapper = new ObjectMapper();
        UserDetails userDetails = objectMapper.readValue(json.getBytes(), UserDetails.class);
        System.out.println(userDetails.getDateOfBirth().toString());
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        System.out.println(df.format(userDetails.getDateOfBirth()));
    }
}
