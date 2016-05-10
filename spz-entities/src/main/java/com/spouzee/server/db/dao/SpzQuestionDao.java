package com.spouzee.server.db.dao;

import com.spouzee.server.db.entities.Option;
import com.spouzee.server.db.entities.Question;
import com.spouzee.server.db.entities.Response;
import com.spouzee.server.db.entities.User;
import com.spouzee.server.db.exception.InvalidOptionException;
import com.spouzee.server.db.exception.InvalidQuestionException;
import com.spouzee.server.db.exception.InvalidUserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.HibernateTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Sagar on 8/20/2015.
 */
@Repository("spzQuestionDao")
public class SpzQuestionDao {

    private HibernateTemplate hibernateTemplate;
    private SpzUserDao spzUserDao;

    @Autowired
    public void SpzUserDao(HibernateTemplate hibernateTemplate, SpzUserDao spzUserDao){
        this.hibernateTemplate = hibernateTemplate;
        this.spzUserDao = spzUserDao;
    }

    @Transactional(propagation = Propagation.SUPPORTS, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public List<Question> getQuestions(long lastQuestionId, int pageSize){
        hibernateTemplate.setMaxResults(pageSize);
        List<Question> questions = (List<Question>)hibernateTemplate.find("from Question where id > " + lastQuestionId);
        if(questions == null || questions.isEmpty()){
            return null;
        }
        for(Question question : questions){
            hibernateTemplate.initialize(question.getOptions());
        }

        return questions;
    }

    public Question getQuestion(long questionId){
        return hibernateTemplate.get(Question.class, questionId);
    }

    public Option getOption(long optionId){
        return hibernateTemplate.get(Option.class, optionId);
    }

    public void saveResponse(long userId, long questionId, String optionIds, String comment) throws InvalidUserException, InvalidQuestionException, InvalidOptionException {

        User user = spzUserDao.getUserWithResponses(userId);
        if(user == null){
            throw new InvalidUserException();
        }
        Question question = getQuestion(questionId);
        if(question == null){
            throw new InvalidQuestionException();
        }

        Response response = getCurrentResponse(user, question.getId());
        if(response == null){
            response = new Response();
        }
        response.setUser(user);
        response.setQuestion(question);
        response.setOption(optionIds);
        response.setComment(comment);
        response.setAskCount(response.getAskCount() + 1);

        hibernateTemplate.saveOrUpdate(response);
    }

    private Response getCurrentResponse(User user, long questionId) {
        for(Response res : user.getResponses()){
            if(res.getQuestion().getId() == questionId){
                return res;
            }
        }
        return null;
    }

    private int getCurrentAskCount(List<Response> responses, long questionId) {
        for(Response res : responses){
            if(res.getQuestion().getId() == questionId){
                return res.getAskCount();
            }
        }
        return 0;
    }
}
