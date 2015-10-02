package com.spouzee.server.api.service;

import com.spouzee.server.api.schema.Question;
import com.spouzee.server.api.schema.Scenario;
import com.spouzee.server.db.dao.SpzQuestionDao;
import com.spouzee.server.db.entities.Option;
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

    Logger logger = LoggerFactory.getLogger(QuestionService.class);

    @Autowired
    public QuestionService(SpzQuestionDao spzQuestionDao){
        this.spzQuestionDao = spzQuestionDao;
    }

    public List<Question> getQuestions(long lastQuestionId, int pageSize){
        List<Question> questions = new ArrayList<>();
        List<com.spouzee.server.db.entities.Question> questionEntities = spzQuestionDao.getQuestions(lastQuestionId, pageSize);
        for(com.spouzee.server.db.entities.Question questionEntity : questionEntities){
            Question question = new Question();
            question.setId(questionEntity.getId());
            question.setQuestionDescription(questionEntity.getQuestionDescription());
            question.setShortDescription(questionEntity.getShortDescription());
            question.setResponseType(questionEntity.getResponseType());
            question.setQuestionTarget(questionEntity.getQuestionTarget());

            com.spouzee.server.db.entities.Scenario scenarioEntity = questionEntity.getScenario();
            if(scenarioEntity != null){
                Scenario scenario = new Scenario(scenarioEntity.getId(), scenarioEntity.getScenarioTitle(), scenarioEntity.getScenarioDescription());
                question.setScenario(scenario);
            }

            List<Option> optionEntities = questionEntity.getOptions();
            for(Option optionEntity : optionEntities){
                com.spouzee.server.api.schema.Option option = new com.spouzee.server.api.schema.Option(optionEntity.getId(),
                        optionEntity.getOptionDescription(), optionEntity.getShortDescription());
                question.addOption(option);
            }
            questions.add(question);
        }
        return questions;
    }

    public List<Question> getResponse(String userId, long lastQuestionId, int pageSize) {
        return null;
    }
}
