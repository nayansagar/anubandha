package com.spouzee.server.api.matcher.impl;

import com.spouzee.server.api.exception.UserNotFoundException;
import com.spouzee.server.api.matcher.IMatcher;
import com.spouzee.server.api.schema.Match;
import com.spouzee.server.db.dao.SpzUserDao;
import com.spouzee.server.db.entities.Expectation;
import com.spouzee.server.db.entities.Response;
import com.spouzee.server.db.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Sagar on 10/3/2015.
 */
@Component
public class MatcherImpl implements IMatcher {

    //Get all users who have answered the questions in current user's expectations
    //Max score = number of selected options in current user's expectation set + 1 for each question
    //
    //If response from a user for a question contains all the expected options of the current user (n),
    //then the match gets n/n points for this question.
    //
    //If response from a user for a question contains m out of n expected options of the current user (m<n),
    //then the match gets m/n points for this question (Loses n-m points).
    //
    //If a user has not responded to a question with n expected options from the current user,
    //then the match gets 0/n points for this question (Loses n points).

    private SpzUserDao spzUserDao;

    @Autowired
    public MatcherImpl(SpzUserDao spzUserDao){
        this.spzUserDao = spzUserDao;
    }

    @Override
    public List<Match> getMatchingUsers(long userId) throws UserNotFoundException {

        User currentUser = spzUserDao.getUserWithExpectationsAndResponses(userId);

        if(currentUser == null){
            throw new UserNotFoundException(""+userId);
        }

        List<Match> matchList = new ArrayList<>();

        if(currentUser.getExpectations() != null && !currentUser.getExpectations().isEmpty()){

            List<Response> responsesMatchingUserExpectation = spzUserDao.getMatchingResponses(userId);

            if(responsesMatchingUserExpectation != null){
                Set<Long> matchingUserIDs = getDistinctUserIDsList(responsesMatchingUserExpectation);

                for(long uId : matchingUserIDs){
                    if(uId != currentUser.getId()){
                        Match match = matchForUser(currentUser, responsesMatchingUserExpectation, uId);
                        if (match.getSelfExpectationMatch() == 0 && match.getOtherExpectationMatch() == 0) {
                            continue;
                        }
                        matchList.add(match);
                    }
                }
            }
        }
        return matchList;
    }

    private Match matchForUser(User currentUser, List<Response> responsesMatchingUserExpectation, long uId) {
        Match match = calculateMatch(responsesMatchingUserExpectation, uId, currentUser.getExpectations());
        User matchedUser = spzUserDao.getUserWithExpectations(uId);
        match.setName(matchedUser.getName());
        List<Expectation> matchExpectations;
        if(matchedUser != null && matchedUser.getExpectations()!= null && !matchedUser.getExpectations().isEmpty()){
            matchExpectations = matchedUser.getExpectations();
            Match reverseMatch = calculateMatch(currentUser.getResponses(), currentUser.getId(), matchExpectations);
            match.setOtherExpectationMatch(reverseMatch.getSelfExpectationMatch());
        }
        return match;
    }

    private Match calculateMatch(List<Response> responsesMatchingUserExpectation, long uId, List<Expectation> expectations) {
        int maxScore = getMaxScore(expectations);
        int score = 0;
        for(Response response : responsesMatchingUserExpectation){
            if(response.getUser().getId() == uId){
                String[] selectedOptions = response.getOption().split(",");
                String expectedOptionsStr = getExpectedOptionsForQuestion(expectations, response.getQuestion().getId());
                if(expectedOptionsStr == null || expectedOptionsStr.isEmpty()){
                    continue;
                }
                String[] expectedOptions = expectedOptionsStr.split(",");
                for(int i=0; i<expectedOptions.length; i++){
                    for(int j=0; j<selectedOptions.length; j++){
                        if(expectedOptions[i].equals(selectedOptions[j])){
                            score++;
                        }
                    }
                }
            }
        }
        int matchPercentage = (score*100/maxScore);
        return new Match(uId, matchPercentage, -1);
    }

    private String getExpectedOptionsForQuestion(List<Expectation> expectations, long questionId) {
        for(Expectation expectation : expectations){
            if(expectation.getQuestion().getId() == questionId){
                return expectation.getOption();
            }
        }
        return null;
    }

    private int getMaxScore(List<Expectation> expectations) {
        int maxScore = 0;
        for(Expectation expectation : expectations){
            String optionsSelected = expectation.getOption();
            String[] optionsArr = optionsSelected.split(",");
            maxScore += optionsArr.length;
        }
        return maxScore;
    }

    private Set<Long> getDistinctUserIDsList(List<Response> responsesMatchingUserExpectation) {
        Set<Long> userIDsList = new HashSet<>();
        for(Response res : responsesMatchingUserExpectation){
            userIDsList.add(res.getUser().getId());
        }
        return userIDsList;
    }
}
