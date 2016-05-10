package com.spouzee.server.api.service;

import com.spouzee.server.api.schema.Question;
import com.spouzee.server.api.schema.Scenario;
import com.spouzee.server.db.dao.SpzQuestionDao;
import com.spouzee.server.db.dao.SpzUserDao;
import com.spouzee.server.db.entities.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sagar on 8/20/2015.
 */
@Service
public class QuestionService {

    SpzQuestionDao spzQuestionDao;

    SpzUserDao spzUserDao;

    Logger logger = LoggerFactory.getLogger(QuestionService.class);

    @Autowired
    public QuestionService(SpzQuestionDao spzQuestionDao, SpzUserDao spzUserDao){
        this.spzQuestionDao = spzQuestionDao;
        this.spzUserDao = spzUserDao;
    }

    public List<Question> getQuestions(long lastQuestionId, int pageSize){
        List<Question> questions = new ArrayList<>();
        List<com.spouzee.server.db.entities.Question> questionEntities = spzQuestionDao.getQuestions(lastQuestionId, pageSize);
        for(com.spouzee.server.db.entities.Question questionEntity : questionEntities){
            Question question = createQuestionSchemaObjectFromEntity(questionEntity);
            questions.add(question);
        }
        return questions;
    }

    private Question createQuestionSchemaObjectFromEntity(com.spouzee.server.db.entities.Question questionEntity) {
        Question question = new Question();
        question.setId(questionEntity.getId());
        question.setQuestionDescription(questionEntity.getQuestionDescription());
        question.setResponseType(questionEntity.getResponseType());
        question.setQuestionTarget(questionEntity.getQuestionTarget());

        List<Option> optionEntities = questionEntity.getOptions();
        for(Option optionEntity : optionEntities){
            com.spouzee.server.api.schema.Option option = new com.spouzee.server.api.schema.Option(optionEntity.getId(),
                    optionEntity.getOptionDescription(), optionEntity.getShortDescription());
            question.addOption(option);
        }
        return question;
    }

    public List<Question> getQuestions(long userId, long lastQuestionId, int pageSize){
        List<Question> questions = new ArrayList<>();
        User user = spzUserDao.getUserWithResponses(userId);
        addQuestionsForExistingMatch(user, questions, pageSize);
        if(questions.size() < pageSize){
            getRandomApplicableQuestion(user, questions, pageSize - questions.size());
        }

        return questions;
    }

    private void getRandomApplicableQuestion(User user, List<Question> questions, int pageSize) {
        List<com.spouzee.server.db.entities.Question> questionEntities = spzUserDao.getRandomQuestion(user.getId(), pageSize);
        int count = 0;
        while(count < pageSize){
            for(com.spouzee.server.db.entities.Question questionEntity : questionEntities){
                if(!repeatCountExceedsLimit(questionEntity.getId(), user.getResponses())){
                    questions.add(createQuestionSchemaObjectFromEntity(questionEntity));
                    count++;
                }
            }
        }
    }

    private boolean repeatCountExceedsLimit(long questionEntityId, List<Response> responses) {
        for(Response response : responses){
            if(response.getQuestion().getId() == questionEntityId){
                if(response.getAskCount() >= 3){
                    return true;
                }else{
                    return false;
                }
            }
        }
        return false;
    }

    private void addQuestionsForExistingMatch(User user, List<Question> questions, int pageSize){

        if(user == null){
            return;
        }

        List<MatchEntity> userMatches = spzUserDao.fetchAllMatches(user.getId());
        if(userMatches == null || userMatches.isEmpty()){
            return;
        }

        int questionsAdded = 0;

            for(MatchEntity match : userMatches){
                if(questionsAdded >= pageSize){
                    return;
                }
                long otherUserId = match.getUserId1() == user.getId() ? match.getUserId2() : match.getUserId1();
                User otherUser = spzUserDao.getUserWithExpectations(otherUserId);

                for(Expectation expectation : otherUser.getExpectations()){
                    long questionId = expectation.getQuestion().getId();
                    if(!responseContains(user.getResponses(), questionId) && !listContains(questions, questionId)){
                        Question question = createQuestionSchemaObjectFromEntity(spzQuestionDao.getQuestion(questionId));
                        questions.add(question);
                        questionsAdded++;
                    }
                }
            }
    }

    private boolean listContains(List<Question> questions, long questionId) {
        for(Question question : questions){
            if(question.getId() == questionId){
                return true;
            }
        }
        return false;
    }

    private boolean responseContains(List<Response> responses, long questionId) {
        for(Response response : responses){
            if(response.getQuestion().getId() == questionId){
                return true;
            }
        }
        return false;
    }

}
