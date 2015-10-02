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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

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

    @Test
    public void createUserCredentialTest(){
        User user = createMinimalUserEntityObject("newuser");
        UserCredential userCredential = new UserCredential();
        userCredential.setUser(user);
        userCredential.setPasswordHash("passwordHash");
        userCredential.setPasswordSalt("passwordSalt");
        user.setUserCredential(userCredential);
        spzUserDao.createUser(user);

    }

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
        existingUser.setDateOfBirth(Calendar.getInstance());
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
        System.out.println(user.getId() + ", " + user.getEmail() + ", " + user.getUserCredential().getPasswordHash());
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
        user.setDateOfBirth(GregorianCalendar.getInstance());
        user.setQualification("TestQual");
        user.setRole("Boy");
        user.setEmployment("TestEmpl");
        return user;
    }
}
