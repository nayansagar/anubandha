package com.spouzee.server.db.entities;

import javax.persistence.*;
import java.util.List;

/**
 * Created by Sagar on 8/20/2015.
 */
@Entity
@Table(name = "question")
public class Question {

    @GeneratedValue
    @Column(name = "id")
    @Id
    private long id;

    @Column(name = "question_description")
    private String questionDescription;

    @Column(name = "response_type")
    private int responseType;

    @Column(name = "question_target")
    private int questionTarget;

    @OneToMany(mappedBy = "question",cascade = CascadeType.ALL,fetch = FetchType.EAGER,orphanRemoval = true)
    private List<Option> options;

    public List<Option> getOptions() {
        return options;
    }

    public void setOptions(List<Option> options) {
        this.options = options;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getQuestionDescription() {
        return questionDescription;
    }

    public void setQuestionDescription(String questionDescription) {
        this.questionDescription = questionDescription;
    }

    public int getResponseType() {
        return responseType;
    }

    public void setResponseType(int responseType) {
        this.responseType = responseType;
    }

    public int getQuestionTarget() {
        return questionTarget;
    }

    public void setQuestionTarget(int questionTarget) {
        this.questionTarget = questionTarget;
    }
}
