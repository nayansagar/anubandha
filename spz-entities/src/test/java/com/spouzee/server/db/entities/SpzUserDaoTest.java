package com.spouzee.server.db.entities;

import com.spouzee.server.db.dao.SpzUserDao;
import com.spouzee.server.db.exception.InvalidOptionException;
import com.spouzee.server.db.exception.InvalidQuestionException;
import com.spouzee.server.db.exception.InvalidUserException;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import java.io.*;
import java.util.*;

/**
 * Created by Sagar on 8/2/2015.
 */
@ContextConfiguration(locations = "classpath:application-context.xml")
public class SpzUserDaoTest extends AbstractTestNGSpringContextTests{

    @Autowired
    private SpzUserDao spzUserDao;

    @Test
    public void createUserTest(){
        User user = createSampleUserEntityObject();
        Assert.assertTrue(user.getId() == 0);
        spzUserDao.createUser(user);
        Assert.assertTrue(user.getId() > 0);
        spzUserDao.deleteUser(user.getId());
    }

    /*@Test
    public void createUserCredentialTest(){
        User user = createMinimalUserEntityObject("newuser");
        UserCredential userCredential = new UserCredential();
        userCredential.setUser(user);
        userCredential.setPasswordHash("passwordHash");
        userCredential.setPasswordSalt("passwordSalt");
        user.setUserCredential(userCredential);
        spzUserDao.createUser(user);

    }*/

    @Test
    public void createUserWithLinkTest(){
        User user = createSampleUserEntityObject();
        UserLink userLink = getSampleUserLinkEntityObject(user, "fb");
        user.addUserLink(userLink);
        Assert.assertTrue(user.getId() == 0);
        Assert.assertTrue(user.getUserLinks().get(0).getId() == 0);
        spzUserDao.createUser(user);
        Assert.assertTrue(user.getId() > 0);
        Assert.assertTrue(user.getUserLinks().get(0).getId() > 0);
        spzUserDao.deleteUser(user.getId());
    }

    @Test
    public void updateUserTest(){
        User user = createMinimalUserEntityObject("justLoggedIn");
        spzUserDao.createUser(user);
        User existingUser = spzUserDao.getUser(user.getId());
        existingUser.setSubcaste("sc");
        existingUser.setRole("Boy");
        existingUser.setReligion("rel");
        existingUser.setQualification("qu");
        existingUser.setCaste("cs");
        existingUser.setDateOfBirth(new Date());
        existingUser.setEmployment("empl");
        existingUser.setLanguage("lang");
        existingUser.setName("nma");
        spzUserDao.updateUser(existingUser);
    }

    @Test
    public void updateUserLinkTest(){
        User user = createSampleUserEntityObject();
        spzUserDao.createUser(user);
        UserLink userLink1 = getSampleUserLinkEntityObject(user, "TW");
        spzUserDao.updateUserLinks(userLink1);
        UserLink userLink2 = getSampleUserLinkEntityObject(user, "LI");
        spzUserDao.updateUserLinks(userLink2);
        User userFromDB = spzUserDao.getUserWithLinks(user.getId());
        Assert.assertTrue(userFromDB.getId() == user.getId());
        List<UserLink> userLinkList = userFromDB.getUserLinks();
        Assert.assertNotNull(userLinkList);
        Assert.assertFalse(userLinkList.isEmpty());
        Assert.assertTrue(userLinkList.size() == 2);
    }

    @Test
    public void getUserByEmailTest(){
        User user = spzUserDao.getUserByEmail("df@dfbgb");
        System.out.println(user.getId() + ", " + user.getEmail());
    }

    @Test
    public void getResponsesTest(){
        for(int i=0; i<1; i++){
            List<Response> responses = spzUserDao.getResponses(111);
            Assert.assertNotNull(responses);
            Assert.assertFalse(responses.isEmpty());
            //Assert.assertEquals(responses.size(), 39);
            System.out.println("$$$$$$$$$$$$$$$$ attempt : " +i+", size : "+ responses.size());
        for(Response response : responses){
            System.out.println(response.getOption());
            System.out.println(response.getComment());
            System.out.println(response.getQuestion().getId());
            System.out.println("---------------------------------------------");
        }
        }
    }

    @Test
    public void getReportItemsTest(){
        for(int i=0; i<1; i++){
            List<ReportItem> reportItems = spzUserDao.getResponsesNative(111);
            Assert.assertNotNull(reportItems);
            Assert.assertFalse(reportItems.isEmpty());
            //Assert.assertEquals(responses.size(), 39);
            System.out.println("$$$$$$$$$$$$$$$$ attempt : " +i+", size : "+ reportItems.size());
            for(ReportItem reportItem : reportItems){
                System.out.println(reportItem.getOptions());
                System.out.println(reportItem.getComments());
                System.out.println(reportItem.getQuestionId());
                System.out.println("---------------------------------------------");
            }
        }
    }

    @Test
    public void getSingleReportItemTest(){
        for(int i=0; i<1; i++){
            ReportItem reportItem = spzUserDao.getResponseNative(113, 1);
            Assert.assertNotNull(reportItem);
            System.out.println(reportItem.getOptions());
            System.out.println(reportItem.getComments());
            System.out.println(reportItem.getQuestionId());
            System.out.println("---------------------------------------------");
        }
    }

    @Test
    public void testStoreUserInvite(){
        User user = spzUserDao.getUser(113);
        spzUserDao.storeUserInvite(user, "abcd");
    }

    @Test
    public void testSaveExpectation() throws InvalidUserException, InvalidOptionException, InvalidQuestionException {
        spzUserDao.saveExpectation(120, 4, "9,10,11,12");
    }

    @Test
    public void testAddQuestion(){
        Question question = new Question();
        question.setOptions(new ArrayList<Option>());
        question.setQuestionDescription("Question description");
        question.setQuestionTarget(1);
        question.setResponseType(1);

        for(int i=0; i<3; i++){
            Option option = new Option();
            option.setQuestion(question);
            option.setOptionDescription("option desc : " + i);
            question.getOptions().add(option);
        }
        spzUserDao.addQuestion(question);
    }

    @Test
    public void testGetUserWithExpectations(){
        User user = spzUserDao.getUserWithExpectations(155);
        System.out.println(user.getId());
        System.out.println(user.getExpectations().size());
        System.out.println(user.getExpectations().get(0).getQuestion().getId() + " : " + user.getExpectations().get(0).getOption());
    }

    @Test
    public void testGetUserWithResponses(){
        User user = spzUserDao.getUserWithResponses(154);
        System.out.println(user.getId());
        System.out.println(user.getResponses().size());
        System.out.println(user.getResponses().get(0).getQuestion().getId() + " : " + user.getResponses().get(0).getOption());
    }

    @Test
    public void testGetUserWithExpectationsAndResponses(){
        User user = spzUserDao.getUserWithExpectationsAndResponses(158);
        System.out.println(user.getId());

        System.out.println(user.getExpectations().size());
        System.out.println(user.getExpectations().get(0).getQuestion().getId() + " : " + user.getExpectations().get(0).getOption());

        System.out.println(user.getResponses().size());
        System.out.println(user.getResponses().get(0).getQuestion().getId() +" : "+user.getResponses().get(0).getOption());
    }

    @Test
    public void testGetMatchingResponses(){
        User u = spzUserDao.getUserWithExpectationsAndResponses(158);
        u.getExpectations();
        List<Response> matchingResponses = spzUserDao.getMatchingResponses(158);
        System.out.println(matchingResponses.size());
        for(Response res : matchingResponses){
            System.out.println(res.getUser().getId()+" -- "+res.getQuestion().getId() +" : "+res.getOption());
        }
    }

    @Test
    public void testGetResponseNative(){
        ReportItem ri = spzUserDao.getResponseNative(160, 1);
        System.out.println("OPTS :::: " + ri.getOptions());
    }

    @Test
    public void testSaveImage() throws IOException {
        spzUserDao.saveImage(160, getImageContent(), "", "Mirror");
    }

    @Test
    public void testGetImageContent() throws IOException {
        UserImage userImage = spzUserDao.getImageContent(1);
        writeImageContent(userImage.getContent());
    }

    @Test
    public void testGetUserWithImages(){
        User user = spzUserDao.getUserWithImages(160);
        org.testng.Assert.assertTrue(user.getUserImages().size() == 1);
        org.testng.Assert.assertTrue(user.getUserImages().get(0).getId() == 1);
        Assert.assertTrue(user.getUserImages().get(0).getDescription().equals("Mirror"));
    }

    @Test
    public void saveMatchTest(){
        MatchEntity me = spzUserDao.saveMatch(158, 66.6, 156, 92.3);
        System.out.println(me);
    }

    @Test
    public void saveInterestTest(){
        spzUserDao.saveInterest(160, 158);
    }

    @Test
    public void saveInterestResponseTest(){
        spzUserDao.saveInterestResponse(158, 160);
    }

    @Test
    public void getAllMatchesTest(){
        List<MatchEntity> matchEntities = spzUserDao.fetchAllMatches(158);
        for(MatchEntity matchEntity : matchEntities){
            System.out.println(matchEntity.getUserId1() + " : " + matchEntity.getUserId2());
        }
    }

    @Test
    public void getRandomQuestionTest(){
        List<Question> questions = spzUserDao.getRandomQuestion(158, 2);
        for(Question q : questions){
            System.out.println(q.getId()+" -- "+q.getQuestionDescription());
        }
    }

    @Test
    public void testGetMatch(){
        MatchEntity matchEntity = spzUserDao.getMatch(158, 160);
        System.out.println("UID1 : "+matchEntity.getUserId1());
        System.out.println("UID2 : "+matchEntity.getUserId2());
        System.out.println("UID1EM : "+matchEntity.getUser1ExpectationMet());
        System.out.println("UID2EM : "+matchEntity.getUser2ExpectationMet());
    }

    @Test
    public void testAddMessageToScenario(){
        spzUserDao.addMessageToScenario(160, 13, 4, true, "Test Message................");
    }

    @Test
    public void testGetScenarioDiscussion(){
        List<ScenarioDiscussion> scenarioDiscussionList = spzUserDao.getScenarioDiscussion(4, 13);
        System.out.println(scenarioDiscussionList);
    }

    @Test
    public void testGetOngoingScenario(){
        List<Scenario> scenarios = spzUserDao.getOngoingScenario(4);
        for(Scenario s : scenarios){
            System.out.println(s.getId()+"  "+s.getScenarioDescription());
        }
    }

    @Test
    public void testGetScenarioForMatch(){
        List<Scenario> scenarios = spzUserDao.getScenarioForMatch(4, 31);
        for(Scenario scenario : scenarios){
            System.out.println(scenario.getId()+" "+scenario.getScenarioDescription());
        }
    }

    @Test
    public void testMarkDiscussionCompleteForUser(){
        spzUserDao.markDiscussionCompleteForUser(4, 13, 158);
    }

    @Test
    public void testCreateSession(){
        String sessionId = spzUserDao.createSession(158);
        System.out.println("SessionId : " + sessionId);
    }

    @Test
    public void testCloseSession(){
        spzUserDao.closeSession("760fff05-771a-4f76-8722-d52c8be95a13");
    }

    @Test
    public void testIsSessionValid(){
        System.out.println(spzUserDao.isSessionValid("0dd40f97-4510-454f-9332-88f228df9396", 158));
    }

    @Test
    public void testRefreshSession(){
        String sessionId = spzUserDao.refreshSession("a5318810-8b65-484b-bb62-d606b3f8ddb2");
        System.out.println(sessionId);
    }

    private byte[] getImageContent() throws IOException {
        FileInputStream fis = new FileInputStream("C:\\Users\\Sagar\\Desktop\\wallpaper.jpg");
        byte[] b = new byte[fis.available()];
        fis.read(b);
        return b;
    }

    private void writeImageContent(byte[] bytes) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream("C:\\Users\\Sagar\\Desktop\\out.jpg");
        fileOutputStream.write(bytes);
        fileOutputStream.flush();
        fileOutputStream.close();
    }

    private UserLink getSampleUserLinkEntityObject(User user, String linkType) {
        UserLink userLink = new UserLink();
        userLink.setAccessToken("SampleAccessToken");
        userLink.setCreatedTime(Calendar.getInstance());
        userLink.setExpiryTime(Calendar.getInstance());
        userLink.setLinkType(linkType);
        userLink.setLinkUserId("abc");
        userLink.setUser(user);
        return userLink;
    }

    private User createMinimalUserEntityObject(String justLoggedIn) {
        User user = new User();
        user.setEmail(justLoggedIn);
        return user;
    }

    private User createSampleUserEntityObject() {
        User user = new User();
        user.setName("Test");
        user.setEmail(Math.random()+"");
        user.setReligion("TestReligion");
        user.setCaste("TestCaste");
        user.setSubcaste("TestSubcaste");
        user.setLanguage("Kannada");
        user.setDateOfBirth(new Date());
        user.setQualification("TestQual");
        user.setRole("Boy");
        user.setEmployment("TestEmpl");
        return user;
    }
}
