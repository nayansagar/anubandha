package com.spouzee.server.db.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Sagar on 8/2/2015.
 */
@Entity
@Table(name = "user", uniqueConstraints = @UniqueConstraint(columnNames = {"email"}))
public class User implements Serializable{

    @GeneratedValue
    @Column(name = "id")
    @Id
    private  long id;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "dob")
    private Date dateOfBirth;

    @Column(name = "religion")
    private String religion;

    @Column(name = "caste")
    private String caste;

    @Column(name = "subcaste")
    private String subcaste;

    @Column(name = "language")
    private String language;

    @Column(name = "employment")
    private String employment;

    @Column(name = "qualification")
    private String qualification;

    @Column(name = "role")
    private String role;

    @Column(name = "maritalStatus")
    private String maritalStatus;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL,fetch = FetchType.LAZY,orphanRemoval = true)
    private List<UserLink> userLinks;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL,fetch = FetchType.LAZY,orphanRemoval = true)
    private List<UserImage> userImages;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL,fetch = FetchType.LAZY,orphanRemoval = true)
    private List<Session> userSessions;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL,fetch = FetchType.LAZY,orphanRemoval = true)
    private List<Expectation> expectations;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL,fetch = FetchType.LAZY,orphanRemoval = true)
    private List<Response> responses;

    public List<UserLink> getUserLinks() {
        return userLinks;
    }

    public void addUserLink(UserLink userLink){
        if(userLinks == null){
            userLinks = new ArrayList<UserLink>();
        }
        userLinks.add(userLink);
    }

    public void setUserLinks(List<UserLink> userLinks) {
        this.userLinks = userLinks;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public List<Expectation> getExpectations() {
        return expectations;
    }

    public void setExpectations(List<Expectation> expectations) {
        this.expectations = expectations;
    }

    public List<Response> getResponses() {
        return responses;
    }

    public void setResponses(List<Response> responses) {
        this.responses = responses;
    }

    public List<UserImage> getUserImages() {
        return userImages;
    }

    public void setUserImages(List<UserImage> userImages) {
        this.userImages = userImages;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public List<Session> getUserSessions() {
        return userSessions;
    }

    public void setUserSessions(List<Session> userSessions) {
        this.userSessions = userSessions;
    }
}
