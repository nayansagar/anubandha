package com.spouzee.server.db.entities;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by Sagar on 5/2/2016.
 */
@Entity
@Table(name = "user_session")
public class Session {

    @GeneratedValue
    @Column(name = "id")
    @Id
    private  long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "sessionid")
    private String sessionId;

    @Column(name = "loginTime")
    private Timestamp loginTime;

    @Column(name = "lastRefreshTime")
    private Timestamp lastRefreshTime;

    @Column(name = "logoutTime")
    private Timestamp logoutTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Timestamp getLoginTime() {
        return loginTime;
    }

    public Timestamp getLogoutTime() {
        return logoutTime;
    }

    public Timestamp getLastRefreshTime() {
        return lastRefreshTime;
    }

    public void setLastRefreshTime(Timestamp lastRefreshTime) {
        this.lastRefreshTime = lastRefreshTime;
    }

    public void setLoginTime(Timestamp loginTime) {
        this.loginTime = loginTime;
    }

    public void setLogoutTime(Timestamp logoutTime) {
        this.logoutTime = logoutTime;
    }
}
