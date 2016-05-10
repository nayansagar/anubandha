package com.spouzee.server.api.schema;

import com.spouzee.server.api.schema.common.Enums.*;

import java.util.Calendar;

/**
 * Created by Sagar on 8/3/2015.
 */
public class UserToken {

    private String accessToken;

    private LinkType linkType;

    private String linkTypeUserID;

    private Calendar createdTime;

    private int tokenValidityInMillis;

    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public LinkType getLinkType() {
        return linkType;
    }

    public void setLinkType(LinkType linkType) {
        this.linkType = linkType;
    }

    public String getLinkTypeUserID() {
        return linkTypeUserID;
    }

    public void setLinkTypeUserID(String linkTypeUserID) {
        this.linkTypeUserID = linkTypeUserID;
    }

    public Calendar getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Calendar createdTime) {
        this.createdTime = createdTime;
    }

    public int getTokenValidityInMillis() {
        return tokenValidityInMillis;
    }

    public void setTokenValidityInMillis(int tokenValidityInMillis) {
        this.tokenValidityInMillis = tokenValidityInMillis;
    }
}
