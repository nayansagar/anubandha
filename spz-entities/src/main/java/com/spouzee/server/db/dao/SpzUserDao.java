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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

/**
 * Created by Sagar on 8/2/2015.
 */
@Repository("spzUserDao")
public class SpzUserDao {

    private HibernateTemplate hibernateTemplate;

    private SpzQuestionDao spzQuestionDao;

    private static final String GET_RESPONSES_NATIVE_QUERY = "select questionid, optionid, comment from response where userid = ?";
    private static final String GET_RESPONSE_USER_QUESTION_NATIVE_QUERY = "select questionid, optionid, comment from response where userid = ? and questionid = ?";

    @Autowired
    public void SpzUserDao(HibernateTemplate hibernateTemplate, SpzQuestionDao spzQuestionDao){
        this.hibernateTemplate = hibernateTemplate;
        this.spzQuestionDao = spzQuestionDao;
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
        return user;
    }

    @Transactional(propagation = Propagation.SUPPORTS, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public User getUserWithLinks(long userId){
        User user = getUser(userId);
        hibernateTemplate.initialize(user.getUserLinks());
        return user;
    }

    @Transactional(propagation = Propagation.SUPPORTS, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public User getUserWithExpectations(long userId){
        User user = getUser(userId);
        hibernateTemplate.initialize(user.getExpectations());
        return user;
    }

    @Transactional(propagation = Propagation.SUPPORTS, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public User getUserWithResponses(long userId){
        User user = getUser(userId);
        hibernateTemplate.initialize(user.getResponses());
        return user;
    }

    @Transactional(propagation = Propagation.SUPPORTS, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public User getUserWithExpectationsAndResponses(long userId){
        User user = getUser(userId);
        hibernateTemplate.initialize(user.getResponses());
        hibernateTemplate.initialize(user.getExpectations());
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

    public List<Response> getMatchingResponses(long userId){
        List<Response> matchingResponses = (List<Response>) hibernateTemplate.find("from Response where question in (select question from Expectation where user.id = ?) and user.role != (select role from User where id = ?)", userId, userId);
        return matchingResponses;
    }

    public void saveImage(long userId, byte[] imageContent, String contentType, String description){
        UserImage image = new UserImage();
        image.setContent(imageContent);
        image.setContentType(contentType);
        image.setDescription(description);
        image.setUser(getUser(userId));

        hibernateTemplate.save(image);
    }

    public User getUserWithImages(long userId){
        User user = getUser(userId);
        hibernateTemplate.initialize(user.getUserImages());
        return user;
    }

    public UserImage getImageContent(long imageId){
        return hibernateTemplate.get(UserImage.class, imageId);
    }

    public MatchEntity getMatch(long userId1, long userId2){
        /*User user1 = getUser(userId1);
        User user2 = getUser(userId2);*/
        List<MatchEntity> matchEntities = (List<MatchEntity>) hibernateTemplate.find("from MatchEntity where user1=? and user2=?", userId1, userId2);
        if(matchEntities == null || matchEntities.isEmpty()){
            matchEntities = (List<MatchEntity>) hibernateTemplate.find("from MatchEntity where user2=? and user1=?", userId1, userId2);
        }

        if(matchEntities != null && !matchEntities.isEmpty()){
            return matchEntities.get(0);
        }
        return null;
    }

    public List<MatchEntity> fetchAllMatches(long userId){
        List<MatchEntity> matchEntities = (List<MatchEntity>) hibernateTemplate.find("from MatchEntity me where me.user1 = ? or me.user2 = ?", userId, userId);
        return matchEntities;
    }

    public MatchEntity saveMatch(long userId1, double user1ExpectationMet, long userId2, double user2ExpectationMet){
        MatchEntity matchEntity = getMatch(userId1, userId2);
        if(matchEntity == null){
            matchEntity = new MatchEntity();
            matchEntity.setUserId1(userId1);
            matchEntity.setUserId2(userId2);
            matchEntity.setUser1Interested(false);
            matchEntity.setUser2Interested(false);
            matchEntity.setUser1Responded(false);
            matchEntity.setUser2Responded(false);
        }
        matchEntity.setUser1ExpectationMet(user1ExpectationMet);
        matchEntity.setUser2ExpectationMet(user2ExpectationMet);


        hibernateTemplate.saveOrUpdate(matchEntity);
        return matchEntity;
    }

    public void saveInterest(long interestedUser, long interestedIn){
        MatchEntity matchEntity = getMatch(interestedUser, interestedIn);

        if(matchEntity == null){
            return;
        }
        if(matchEntity.getUserId1() == interestedUser){
            matchEntity.setUser1Interested(true);
        }else if(matchEntity.getUserId2() == interestedUser){
            matchEntity.setUser2Interested(true);
        }else{
            return;
        }
    }

    public void saveInterestResponse(long respondedUserId, long respondedTo){
        MatchEntity matchEntity = getMatch(respondedUserId, respondedTo);

        if(matchEntity == null){
            return;
        }
        if(matchEntity.getUserId1() == respondedUserId){
            matchEntity.setUser1Responded(true);
        }else if(matchEntity.getUserId2() == respondedUserId){
            matchEntity.setUser2Responded(true);
        }else{
            return;
        }
    }

    public void saveContactRequest(long interestedUser, long interestedIn){
        MatchEntity matchEntity = getMatch(interestedUser, interestedIn);

        if(matchEntity == null){
            return;
        }
        if(matchEntity.getUserId1() == interestedUser){
            matchEntity.setUser1RequestedContact(true);
        }else if(matchEntity.getUserId2() == interestedUser){
            matchEntity.setUser2RequestedContact(true);
        }else{
            return;
        }
    }

    public void saveContactResponse(long respondedUserId, long respondedTo){
        MatchEntity matchEntity = getMatch(respondedUserId, respondedTo);

        if(matchEntity == null){
            return;
        }
        if(matchEntity.getUserId1() == respondedUserId){
            matchEntity.setUser1RevealedContact(true);
        }else if(matchEntity.getUserId2() == respondedUserId){
            matchEntity.setUser2RevealedContact(true);
        }else{
            return;
        }
    }

    public List<Question> getRandomQuestion(long userId, int pageSize){
        User user = getUser(userId);
        String role = user.getRole();
        int roleId = role.equals("Boy") ? 2 : 3;
        hibernateTemplate.setMaxResults(pageSize);
        List<Question> avblQuestions = (List<Question>)hibernateTemplate.find("from Question where questionTarget in (?, ?) order by RAND()", roleId, 1);
        hibernateTemplate.setMaxResults(0);
        return avblQuestions;
    }

    public MatchEntity getMatch(long matchId){
        return hibernateTemplate.get(MatchEntity.class, matchId);
    }

    public Scenario getScenario(long scenarioId) {
        return hibernateTemplate.get(Scenario.class, scenarioId);
    }

    @Transactional(propagation = Propagation.SUPPORTS, isolation = Isolation.READ_COMMITTED)
    public void addMessageToScenario(long userId, long scenarioId, long matchId, boolean complete, String message){
        ScenarioDiscussion scenarioDiscussion = new ScenarioDiscussion();
        MatchEntity match = getMatch(matchId);
        Scenario scenario = getScenario(scenarioId);
        User user = getUser(userId);
        if(match == null || scenario == null || user == null || message == null || message.isEmpty()){
            return;
        }
        scenarioDiscussion.setMatchEntity(match);
        scenarioDiscussion.setScenario(scenario);
        scenarioDiscussion.setUser(user);
        scenarioDiscussion.setMessage(message);
        scenarioDiscussion.setComplete(complete);
        hibernateTemplate.save(scenarioDiscussion);
    }

    public void markDiscussionCompleteForUser(long matchId, long scenarioId, long userId){
        hibernateTemplate.bulkUpdate("update ScenarioDiscussion set complete = 1 where matchid = ? and scenarioid = ? and userid = ?", matchId, scenarioId, userId);
    }

    @Transactional(propagation = Propagation.SUPPORTS, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public List<ScenarioDiscussion> getScenarioDiscussion(long matchId, long scenarioId){
        MatchEntity matchEntity = getMatch(matchId);
        Scenario scenario = getScenario(scenarioId);
        if(matchEntity == null || scenario == null){
            return null;
        }
        List<ScenarioDiscussion> scenarioDiscussionList = (List<ScenarioDiscussion>)
                hibernateTemplate.find("from ScenarioDiscussion where matchEntity = ? and scenario = ?", matchEntity, scenario);
        for(ScenarioDiscussion discussion : scenarioDiscussionList){
            hibernateTemplate.initialize(discussion.getScenario());
            hibernateTemplate.initialize(discussion.getUser());
            hibernateTemplate.initialize(discussion.getMatchEntity());
        }
        return scenarioDiscussionList;
    }

    public List<Scenario> getScenarioForMatch(long matchId, int pageSize){
        MatchEntity match = getMatch(matchId);
        List<Scenario> scenarios = getOngoingScenario(matchId);
        if(scenarios != null && !scenarios.isEmpty()){
            return scenarios.subList(0,1);
        }
        hibernateTemplate.setMaxResults(1);

        scenarios = (List<Scenario>) hibernateTemplate.find("from Scenario where id not in (select distinct scenario from ScenarioDiscussion where matchEntity=?) order by RAND()", match);
        hibernateTemplate.setMaxResults(0);
        return scenarios;
    }

    public List<Scenario> getOngoingScenario(long matchId) {
        MatchEntity me = getMatch(matchId);
        List<Scenario> scenarioList = (List<Scenario>) hibernateTemplate.find("select distinct scenario from ScenarioDiscussion where matchid = ? and complete != 1", matchId);
        return scenarioList;
    }

    public String createSession(long userId){
        User user = getUser(userId);
        if(user == null){
            return null;
        }
        String uuid = getNewUUID();
        com.spouzee.server.db.entities.Session session = new com.spouzee.server.db.entities.Session();
        session.setSessionId(uuid);
        session.setUser(user);
        session.setLoginTime(new Timestamp(Calendar.getInstance().getTime().getTime()));
        session.setLastRefreshTime(null);
        session.setLogoutTime(null);

        hibernateTemplate.saveOrUpdate(session);

        return uuid;
    }

    private String getNewUUID() {
        return UUID.randomUUID().toString();
    }

    public boolean isSessionValid(String sessionId, long userId){
        List<com.spouzee.server.db.entities.Session> sessions = (List<com.spouzee.server.db.entities.Session>) hibernateTemplate.find("from Session where sessionid = ? and user_id = ?", sessionId, userId);
        if(sessions == null || sessions.isEmpty()){
            return false;
        }
        com.spouzee.server.db.entities.Session session = sessions.get(0);

        if(session.getLogoutTime() != null){
            return false;
        }
        Timestamp now = new Timestamp(Calendar.getInstance().getTime().getTime());
        Timestamp then;
        if(session.getLastRefreshTime() != null){
            then = session.getLastRefreshTime();
        }else{
            then = session.getLoginTime();
        }

        long diff = now.getTime() - then.getTime();
        long diffMinutes = diff / (60 * 1000);
        if(diffMinutes > 10){
            return false;
        }
        return true;
    }

    @Transactional(propagation = Propagation.SUPPORTS, isolation = Isolation.READ_COMMITTED, readOnly = false)
    public String refreshSession(String sessionId){
        List<com.spouzee.server.db.entities.Session> sessions = (List<com.spouzee.server.db.entities.Session>) hibernateTemplate.find("from Session where sessionid = ?", sessionId);
        if(sessions == null || sessions.isEmpty()){
            return null;
        }
        com.spouzee.server.db.entities.Session session = sessions.get(0);
        if(session.getLogoutTime() != null){
            return null;
        }
        session.setLastRefreshTime(new Timestamp(Calendar.getInstance().getTime().getTime()));
        session.setSessionId(getNewUUID());
        hibernateTemplate.update(session);
        return session.getSessionId();
    }

    @Transactional(propagation = Propagation.SUPPORTS, isolation = Isolation.READ_COMMITTED)
    public void closeSession(String sessionId){
        List<com.spouzee.server.db.entities.Session> sessions =
                (List<com.spouzee.server.db.entities.Session>) hibernateTemplate.find("from Session where sessionid = ?", sessionId);
        com.spouzee.server.db.entities.Session session = sessions.get(0);
        if(session != null){
            session.setLogoutTime(new Timestamp(Calendar.getInstance().getTime().getTime()));
            hibernateTemplate.saveOrUpdate(session);
        }
    }
}
