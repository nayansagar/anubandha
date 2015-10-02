package com.spouzee.server.api.resources;

import com.spouzee.server.api.schema.Question;
import com.spouzee.server.api.schema.UserDetails;
import com.spouzee.server.api.service.QuestionService;
import com.spouzee.server.api.service.UserService;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;

/**
 * Created by Sagar on 8/20/2015.
 */
@Path("/v1/questions")
@Component
public class QuestionResource {
    Logger logger = LoggerFactory.getLogger(QuestionResource.class);

    @Autowired
    private QuestionService questionService;

    @GET
    @Produces("application/json")
    public Response getQuestions(@QueryParam("lastQuestionId") long lastQuestionId, @QueryParam("pageSize") int pageSize){
        if(pageSize <= 0) pageSize = 1;
        try {
            List<Question> questions = questionService.getQuestions(lastQuestionId, pageSize);
            if(questions == null || questions.isEmpty()){
                return Response.status(Response.Status.NO_CONTENT).build();
            }
            String questionsJSON = convertQuestionsListToJSONString(questions);
            return Response.ok().entity(questionsJSON).build();
        } catch (IOException e) {
            return Response.serverError().build();
        }
    }

    private String convertQuestionsListToJSONString(List<Question> questions) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String questionJSON = objectMapper.defaultPrettyPrintingWriter().writeValueAsString(questions);
        return questionJSON;
    }
}
