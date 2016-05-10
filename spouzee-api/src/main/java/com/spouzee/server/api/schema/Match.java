package com.spouzee.server.api.schema;

/**
 * Created by Sagar on 10/4/2015.
 */
public class Match {

    private long matchId;

    private long userId;

    private String name;

    private double selfExpectationMatch;

    private double otherExpectationMatch;

    private boolean interestExpressed = false;

    private  boolean respondedToInterest = false;
    private boolean otherUserExpressedInterest;
    private boolean IRespondedToOtherUsersInterest;
    private boolean IRequestedContact;
    private boolean otherUserRequestedContact;
    private boolean IRevealedContact;
    private boolean otherUserRevealedContact;
    private boolean identityExchanged = false;

    public Match(long userId, double selfExpectationMatch, double otherExpectationMatch) {
        this.userId = userId;
        this.selfExpectationMatch = selfExpectationMatch;
        this.otherExpectationMatch = otherExpectationMatch;
    }

    public long getMatchId() {
        return matchId;
    }

    public void setMatchId(long matchId) {
        this.matchId = matchId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getSelfExpectationMatch() {
        return selfExpectationMatch;
    }

    public void setSelfExpectationMatch(double selfExpectationMatch) {
        this.selfExpectationMatch = selfExpectationMatch;
    }

    public double getOtherExpectationMatch() {
        return otherExpectationMatch;
    }

    public void setOtherExpectationMatch(double otherExpectationMatch) {
        this.otherExpectationMatch = otherExpectationMatch;
    }

    public boolean isInterestExpressed() {
        return interestExpressed;
    }

    public void setInterestExpressedByMe(boolean interestExpressed) {
        this.interestExpressed = interestExpressed;
    }

    public boolean isRespondedToInterest() {
        return respondedToInterest;
    }

    public void setOtherUserRespondedToMyInterest(boolean respondedToInterest) {
        this.respondedToInterest = respondedToInterest;
    }

    public void setOtherUserExpressedInterest(boolean otherUserExpressedInterest) {
        this.otherUserExpressedInterest = otherUserExpressedInterest;
    }

    public boolean isOtherUserExpressedInterest() {
        return otherUserExpressedInterest;
    }

    public void setIRespondedToOtherUsersInterest(boolean IRespondedToOtherUsersInterest) {
        this.IRespondedToOtherUsersInterest = IRespondedToOtherUsersInterest;
    }

    public boolean isIRespondedToOtherUsersInterest() {
        return IRespondedToOtherUsersInterest;
    }

    public void setIRequestedContact(boolean IRequestedContact) {
        this.IRequestedContact = IRequestedContact;
    }

    public boolean isIRequestedContact() {
        return IRequestedContact;
    }

    public boolean isOtherUserRequestedContact() {
        return otherUserRequestedContact;
    }

    public void setOtherUserRequestedContact(boolean otherUserRequestedContact) {
        this.otherUserRequestedContact = otherUserRequestedContact;
    }

    public void setIRevealedContact(boolean IRevealedContact) {
        this.IRevealedContact = IRevealedContact;
    }

    public boolean isIRevealedContact() {
        return IRevealedContact;
    }

    public void setOtherUserRevealedContact(boolean otherUserRevealedContact) {
        this.otherUserRevealedContact = otherUserRevealedContact;
    }

    public boolean isOtherUserRevealedContact() {
        return otherUserRevealedContact;
    }

    public void setIdentityExchanged(boolean identityExchanged) {
        this.identityExchanged = identityExchanged;
    }

    public boolean isIdentityExchanged() {
        return identityExchanged;
    }
}
