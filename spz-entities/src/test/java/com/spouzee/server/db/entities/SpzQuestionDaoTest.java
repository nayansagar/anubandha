package com.spouzee.server.db.entities;

import com.spouzee.server.db.dao.SpzQuestionDao;
import com.spouzee.server.db.dao.SpzUserDao;
import com.spouzee.server.db.exception.InvalidOptionException;
import com.spouzee.server.db.exception.InvalidQuestionException;
import com.spouzee.server.db.exception.InvalidUserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

/**
 * Created by Sagar on 8/20/2015.
 */
@ContextConfiguration(locations = "classpath:application-context.xml")
public class SpzQuestionDaoTest extends AbstractTestNGSpringContextTests{

    @Autowired
    private SpzQuestionDao spzQuestionDao;

    @Test
    public void getQuestionTest(){
        List<Question> questionList = spzQuestionDao.getQuestions(-1, 3);
        Assert.assertNotNull(questionList);
        Assert.assertFalse(questionList.isEmpty());
        //Assert.assertTrue(questionList.size() == 1);
        Question question = questionList.get(0);
        Assert.assertNotNull(question);
        Assert.assertNotNull(question.getOptions());
        Assert.assertNotNull(question.getScenario());
        System.out.println("*********** question id : " + question.getId());
        System.out.println("*********** question desc : " + question.getQuestionDescription());
        System.out.println("*********** question short desc : " + question.getShortDescription());
        System.out.println("*********** scenario id : "+question.getScenario().getId());
        System.out.println("*********** scenario desc : "+question.getScenario().getScenarioDescription());
        System.out.println("*********** scenario title : "+question.getScenario().getScenarioTitle());
        for(Option option : question.getOptions()){
            System.out.println("*********** option id : " + option.getId());
            System.out.println("*********** option desc : "+option.getOptionDescription());
            System.out.println("*********** option short desc : "+option.getShortDescription());
        }

    }

    @Test
    public void saveResponseTest() throws InvalidUserException, InvalidOptionException, InvalidQuestionException {
        spzQuestionDao.saveResponse(110,1,"1", "abcd");
    }
}
