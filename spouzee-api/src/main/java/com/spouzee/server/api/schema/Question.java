package com.spouzee.server.api.schema;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sagar on 8/20/2015.
 */
public class Question {

    private long id;

    private String questionDescription;

    private String shortDescription;

    private int responseType;

    private int questionTarget;

    private Scenario scenario;

    private List<Option> options = new ArrayList<>();

    private String responses;

    private String comments;

    private boolean isPartOfQuestionnaire=false;

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

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public Scenario getScenario() {
        return scenario;
    }

    public void setScenario(Scenario scenario) {
        this.scenario = scenario;
    }

    public List<Option> getOptions() {
        return options;
    }

    public void addOption(Option option) {
        this.options.add(option);
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

    public String getResponses() {
        return responses;
    }

    public void setResponses(String responses) {
        this.responses = responses;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public void setPartOfQuestionnaire(boolean b) {
        this.isPartOfQuestionnaire=b;
    }

    public boolean isPartOfQuestionnaire(){
        return isPartOfQuestionnaire;
    }
}
