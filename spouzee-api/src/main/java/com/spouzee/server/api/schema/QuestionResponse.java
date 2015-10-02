package com.spouzee.server.api.schema;

/**
 * Created by Sagar on 8/30/2015.
 */
public class QuestionResponse {

    private long question;

    private String options;

    private String comments;

    public long getQuestion() {
        return question;
    }

    public void setQuestion(long question) {
        this.question = question;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
