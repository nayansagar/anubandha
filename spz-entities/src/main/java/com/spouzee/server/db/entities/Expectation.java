package com.spouzee.server.db.entities;

import javax.persistence.*;

/**
 * Created by Sagar on 8/20/2015.
 */
@Entity
@Table(name = "expectation")
public class Expectation {

    @GeneratedValue
    @Column(name = "id")
    @Id
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userid" , nullable = false)
    private User user;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "questionid", nullable = false)
    private Question question;

    @Column(name = "optionid")
    private String option;

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

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }
}
