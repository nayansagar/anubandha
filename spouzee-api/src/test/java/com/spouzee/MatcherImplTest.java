package com.spouzee;

import com.spouzee.server.api.exception.UserNotFoundException;
import com.spouzee.server.api.schema.Match;
import com.spouzee.server.api.matcher.impl.MatcherImpl;
import com.spouzee.server.db.dao.SpzUserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

/**
 * Created by Sagar on 10/4/2015.
 */
@ContextConfiguration(locations = "classpath:application-context.xml")
public class MatcherImplTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private SpzUserDao spzUserDao;

    private MatcherImpl matcherImpl;

    @BeforeMethod
    public void createTestObject(){
        matcherImpl = new MatcherImpl(spzUserDao);
    }

    @Test
    public void testGetMatchingUsers() throws UserNotFoundException {
        List<Match> matchList = matcherImpl.getMatchingUsers(156);
        Assert.assertNotNull(matchList);
        for(Match match : matchList){
            System.out.println(match.getUserId() + " -- "+match.getSelfExpectationMatch()+" ++ "+match.getOtherExpectationMatch());
        }
    }
}
