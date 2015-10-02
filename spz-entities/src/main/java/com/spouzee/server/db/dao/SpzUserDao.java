package com.spouzee.server.db.dao;

import com.spouzee.server.db.entities.*;
import com.spouzee.server.db.exception.InvalidOptionException;
import com.spouzee.server.db.exception.InvalidQuestionException;
import com.spouzee.server.db.exception.InvalidUserException;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.HibernateCallback;
import org.springframework.orm.hibernate4.HibernateTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sagar on 8/2/2015.
 */
@Repository("spzUserDao")
public class SpzUserDao {

    private HibernateTemplate hibernateTemplate;

    private static final String GET_RESPONSES_NATIVE_QUERY = "select questionid, optionid, comment from response where userid = ?";
    private static final String GET_RESPONSE_USER_QUESTION_NATIVE_QUERY = "select questionid, optionid, comment from response where userid = ? and questionid = ?";

    @Autowired
    public void SpzUserDao(HibernateTemplate hibernateTemplate){
        this.hibernateTemplate = hibernateTemplate;
    }

    public void createUser(User user){
        hibernateTemplate.persist(user);
        hibernateTemplate.flush();
    }

    public void updateUser(User user){
        hibernateTemplate.merge(user);
    }

    public void updateUserLinks(UserLink userLink){
        hibernateTemplate.merge(userLink);
    }

    public User getUser(long userId){
        return hibernateTemplate.get(User.class, userId);
    }

    @Transactional(propagation = Propagation.SUPPORTS, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public User getUserByEmail(String email){
        List<User> users = (List<User>)hibernateTemplate.find("from User where email =" + "'" + email + "'");
        if(users == null || users.isEmpty()){
            return null;
        }
        User user = users.get(0);
        hibernateTemplate.initialize(user.getUserCredential());
        return user;
    }

    @Transactional(propagation = Propagation.SUPPORTS, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public User getUserWithLinks(long userId){
        User user = getUser(userId);
        hibernateTemplate.initialize(user.getUserLinks());
        return user;
    }

    public void deleteUser(long userId){
        User user = getUser(userId);
        hibernateTemplate.delete(user);
    }

    @Transactional(propagation = Propagation.SUPPORTS, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public List<Response> getResponses(long userId){
        User user = getUser(userId);
        List<Response> responses = (List<Response>) hibernateTemplate.findByNamedQuery("GET_RESPONSES_FOR_USER_NATIVE", user);
        return responses;
    }

    public List<ReportItem> getResponsesNative(final long userId){
        List<Object[]> resultList = (List)hibernateTemplate.execute(
                new HibernateCallback() {
                    public Object doInHibernate(Session session) throws HibernateException {
                        SQLQuery sq = session.createSQLQuery(GET_RESPONSES_NATIVE_QUERY);
                        sq.setLong(0, userId);
                        return sq.list();
                    }
                });

        List<ReportItem> reportItems = new ArrayList<ReportItem>();
        if(resultList != null && resultList.size() > 0){
            for(Object[] objarr : resultList){
                int questionId = (Integer)objarr[0];
                String options = (String)objarr[1];
                String comments = (String)objarr[2];
                ReportItem reportItem = new ReportItem(questionId, options, comments);
                reportItems.add(reportItem);
            }
        }
        return reportItems;
    }

    public ReportItem getResponseNative(final long userId, final long questionIdInput){
        List<Object[]> resultList = (List)hibernateTemplate.execute(
                new HibernateCallback() {
                    public Object doInHibernate(Session session) throws HibernateException {
                        SQLQuery sq = session.createSQLQuery(GET_RESPONSE_USER_QUESTION_NATIVE_QUERY);
                        sq.setLong(0, userId);
                        sq.setLong(1, questionIdInput);
                        return sq.list();
                    }
                });
        ReportItem reportItem = null;
        if(resultList != null && resultList.size() > 0){
            Object[] objarr = resultList.get(0);
            int questionId = (Integer)objarr[0];
            String options = (String)objarr[1];
            String comments = (String)objarr[2];
            reportItem = new ReportItem(questionId, options, comments);
        }
        return reportItem;
    }

    @Transactional(propagation = Propagation.SUPPORTS, isolation = Isolation.READ_COMMITTED)
    public void storeUserInvite(User user, String emailId){

        UserInvite invite = new UserInvite();
        invite.setUser(user);
        invite.setEmail(emailId);

        hibernateTemplate.persist(invite);
        hibernateTemplate.flush();
    }

    public Question getQuestion(long questionId){
        return hibernateTemplate.get(Question.class, questionId);
    }

    public void saveExpectation(long userId, long questionId, String optionIds) throws InvalidUserException, InvalidQuestionException, InvalidOptionException {

        User user = getUser(userId);
        if(user == null){
            throw new InvalidUserException();
        }
        Question question = getQuestion(questionId);
        if(question == null){
            throw new InvalidQuestionException();
        }

        Expectation expectation = new Expectation();
        expectation.setUser(user);
        expectation.setQuestion(question);
        expectation.setOption(optionIds);

        hibernateTemplate.save(expectation);
    }

    @Transactional(propagation = Propagation.SUPPORTS, isolation = Isolation.READ_COMMITTED)
    public void addQuestion(Question question){
        hibernateTemplate.saveOrUpdate(question);
        hibernateTemplate.flush();
    }
}
