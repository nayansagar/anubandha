package com.spouzee.server.db.entities;

import javax.persistence.*;

/**
 * Created by Sagar on 8/8/2015.
 */
@Entity
@Table(name = "user_creds")
public class UserCredential {

    @GeneratedValue
    @Column(name = "id")
    @Id
    long id;

    @Column(name = "pd_hash")
    String passwordHash;

    @Column(name = "pd_slt")
    String passwordSalt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getPasswordSalt() {
        return passwordSalt;
    }

    public void setPasswordSalt(String passwordSalt) {
        this.passwordSalt = passwordSalt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
