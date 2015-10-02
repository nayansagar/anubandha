package com.spouzee.server.db.entities;

import javax.persistence.*;

/**
 * Created by Sagar on 8/20/2015.
 */
@Entity
@Table(name = "opt")
public class Option {

    @GeneratedValue
    @Column(name = "id")
    @Id
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(name = "option_description")
    private String optionDescription;

    @Column(name = "short_description")
    private String shortDescription;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public String getOptionDescription() {
        return optionDescription;
    }

    public void setOptionDescription(String optionDescription) {
        this.optionDescription = optionDescription;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }
}
