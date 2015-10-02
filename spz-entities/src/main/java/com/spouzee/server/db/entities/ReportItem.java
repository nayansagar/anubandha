package com.spouzee.server.db.entities;

/**
 * Created by Sagar on 9/12/2015.
 */
public class ReportItem {

    private long questionId;

    private String options;

    private String comments;

    public ReportItem(long questionId, String options, String comments) {
        this.questionId = questionId;
        this.options = options;
        this.comments = comments;
    }

    public long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(long questionId) {
        this.questionId = questionId;
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
