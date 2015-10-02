package com.spouzee.server.api.schema;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sagar on 9/24/2015.
 */
public class UserQuestion {

    private String question;

    private int responseType;

    private int targetRole;

    private List<String> options;

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public int getResponseType() {
        return responseType;
    }

    public void setResponseType(int responseType) {
        this.responseType = responseType;
    }

    public int getTargetRole() {
        return targetRole;
    }

    public void setTargetRole(int targetRole) {
        this.targetRole = targetRole;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    /*public static void main(String args[]) throws IOException {
        UserQuestion userQuestion = new UserQuestion();
        userQuestion.setQuestion("Question");
        userQuestion.setResponseType(2);
        userQuestion.setTargetRole(1);
        List<String> options = new ArrayList<>();
        options.add("Option 1");
        options.add("Option 2");
        options.add("Option 3");
        userQuestion.setOptions(options);

        ObjectMapper mapper = new ObjectMapper();
        System.out.println(mapper.writeValueAsString(userQuestion));
    }*/
}
