package com.spouzee.server.db.entities;

import javax.persistence.*;
import java.util.Calendar;

/**
 * Created by Sagar on 8/2/2015.
 */
@Entity
@Table(name = "user_links")
public class UserLink {

    @GeneratedValue
    @Column(name = "id")
    @Id
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Column(name = "link_type")
    private String linkType;

    @Column(name = "access_token")
    private String accessToken;

    @Column(name = "link_user_id")
    private String linkUserId;

    @Column(name = "created_time")
    private Calendar createdTime;

    @Column(name = "expiry_time")
    private Calendar expiryTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLinkType() {
        return linkType;
    }

    public void setLinkType(String linkType) {
        this.linkType = linkType;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getLinkUserId() {
        return linkUserId;
    }

    public void setLinkUserId(String linkUserId) {
        this.linkUserId = linkUserId;
    }

    public Calendar getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Calendar createdTime) {
        this.createdTime = createdTime;
    }

    public Calendar getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(Calendar expiryTime) {
        this.expiryTime = expiryTime;
    }
}
