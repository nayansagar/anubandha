package com.spouzee.server.db.entities;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by Sagar on 3/12/2016.
 */
@Entity
@Table(name = "matches")
public class MatchEntity implements Serializable{

    @GeneratedValue
    @Column(name = "id")
    @Id
    private long id;

    @Column(name = "userid1")
    private long user1;

    @Column(name = "userid2")
    private long user2;

    @Column(name = "userid1_expectation_met")
    private double user1ExpectationMet;

    @Column(name = "userid2_expectation_met")
    private double user2ExpectationMet;

    @Column(columnDefinition = "TINYINT", name = "userid1_interested")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    public boolean user1Interested = true;

    @Column(columnDefinition = "TINYINT", name = "userid2_interested")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    public boolean user2Interested = true;

    @Column(columnDefinition = "TINYINT", name = "userid1_responded")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    public boolean user1Responded = true;

    @Column(columnDefinition = "TINYINT", name = "userid2_responded")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    public boolean user2Responded = true;




    @Column(columnDefinition = "TINYINT", name = "userid1_requested_contact")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    public boolean user1RequestedContact = true;

    @Column(columnDefinition = "TINYINT", name = "userid2_requested_contact")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    public boolean user2RequestedContact = true;

    @Column(columnDefinition = "TINYINT", name = "userid1_revealed_contact")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    public boolean user1RevealedContact = true;

    @Column(columnDefinition = "TINYINT", name = "userid2_revealed_contact")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    public boolean user2RevealedContact = true;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId1() {
        return user1;
    }

    public void setUserId1(long user1) {
        this.user1 = user1;
    }

    public long getUserId2() {
        return user2;
    }

    public void setUserId2(long user2) {
        this.user2 = user2;
    }

    public double getUser1ExpectationMet() {
        return user1ExpectationMet;
    }

    public void setUser1ExpectationMet(double user1ExpectationMet) {
        this.user1ExpectationMet = user1ExpectationMet;
    }

    public double getUser2ExpectationMet() {
        return user2ExpectationMet;
    }

    public void setUser2ExpectationMet(double user2ExpectationMet) {
        this.user2ExpectationMet = user2ExpectationMet;
    }

    public boolean isUser1Interested() {
        return user1Interested;
    }

    public void setUser1Interested(boolean user1Interested) {
        this.user1Interested = user1Interested;
    }

    public boolean isUser2Interested() {
        return user2Interested;
    }

    public void setUser2Interested(boolean user2Interested) {
        this.user2Interested = user2Interested;
    }

    public boolean isUser1Responded() {
        return user1Responded;
    }

    public void setUser1Responded(boolean user1Responded) {
        this.user1Responded = user1Responded;
    }

    public boolean isUser2Responded() {
        return user2Responded;
    }

    public void setUser2Responded(boolean user2Responded) {
        this.user2Responded = user2Responded;
    }

    public boolean isUser1RequestedContact() {
        return user1RequestedContact;
    }

    public void setUser1RequestedContact(boolean user1RequestedContact) {
        this.user1RequestedContact = user1RequestedContact;
    }

    public boolean isUser2RequestedContact() {
        return user2RequestedContact;
    }

    public void setUser2RequestedContact(boolean user2RequestedContact) {
        this.user2RequestedContact = user2RequestedContact;
    }

    public boolean isUser1RevealedContact() {
        return user1RevealedContact;
    }

    public void setUser1RevealedContact(boolean user1RevealedContact) {
        this.user1RevealedContact = user1RevealedContact;
    }

    public boolean isUser2RevealedContact() {
        return user2RevealedContact;
    }

    public void setUser2RevealedContact(boolean user2RevealedContact) {
        this.user2RevealedContact = user2RevealedContact;
    }
}
