package com.spouzee;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spouzee.server.api.matcher.IMatcher;
import com.spouzee.server.api.schema.QuestionResponse;
import com.spouzee.server.api.service.QuestionService;
import com.spouzee.server.api.service.UserService;
import com.spouzee.server.api.social.EMailHelper;
import com.spouzee.server.api.social.FacebookHelper;
import com.spouzee.server.db.dao.SpzQuestionDao;
import com.spouzee.server.db.dao.SpzUserDao;
import com.spouzee.server.db.entities.Question;
import com.spouzee.server.db.entities.Response;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sagar on 9/5/2015.
 */
public class UserServiceTest {

    UserService userService;
    SpzUserDao spzUserDao;
    SpzQuestionDao spzQuestionDao;
    FacebookHelper facebookHelper;
    IMatcher matcher;
    QuestionService questionService;

    @BeforeTest
    public void setUp(){
        spzUserDao = Mockito.mock(SpzUserDao.class);
        spzQuestionDao = Mockito.mock(SpzQuestionDao.class);
        facebookHelper = Mockito.mock(FacebookHelper.class);
        EMailHelper eMailHelper = Mockito.mock(EMailHelper.class);
        matcher = Mockito.mock(IMatcher.class);
        userService = new UserService(spzUserDao, spzQuestionDao, facebookHelper, eMailHelper, matcher, questionService);
    }

    //@Test
    public void testGetResponse() throws IOException {
        Mockito.when(spzUserDao.getResponses(Mockito.anyLong())).thenReturn(getResponseEntityList(10));
        List<QuestionResponse> qrList = userService.getResponses(111);
        Assert.assertNotNull(qrList);
        Assert.assertFalse(qrList.isEmpty());
        for(QuestionResponse qr : qrList){
            System.out.println(qr.getQuestion());
        }
        ObjectMapper objectMapper = new ObjectMapper();
        String questionJSON = objectMapper.writeValueAsString(qrList);
        System.out.println(questionJSON);
    }

    private List<Response> getResponseEntityList(int count) {
        List<Response> responses = new ArrayList<>();
        for(int i=0; i<count; i++){
            Response res = new Response();
            res.setQuestion(createQuestion(10 + i));
            if(i==0){
                res.setOption("1,3,5");
            }else if(i == 1){
                res.setOption("1,3");
            }else if(i == 2){
                res.setOption("1");
            }else{
                res.setOption("1,3,5,7");
            }

            if(i%2 == 0){
                res.setComment(null);
            }else{
                res.setComment("");
            }
            responses.add(res);
        }
        return responses;
    }

    private Question createQuestion(int id) {
        Question question = new Question();
        question.setId(id);
        return question;
    }
}
