package com.spouzee.server.db.entities;

import javax.persistence.*;

/**
 * Created by Sagar on 9/18/2015.
 */
@Entity
@Table(name = "user_invites")
public class UserInvite {

    @GeneratedValue
    @Column(name = "id")
    @Id
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userid", nullable = false)
    private User user;

    @Column(name = "email")
    private String email;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
