package com.spouzee.server.api.resources;

import com.spouzee.server.api.schema.DisussionMessage;
import com.spouzee.server.api.schema.DisussionUpload;
import com.spouzee.server.api.schema.Scenario;
import com.spouzee.server.api.service.MatchService;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;

/**
 * Created by Sagar on 4/27/2016.
 */
@Path("/v1/matches")
@Component
public class MatchResource {

    Logger logger = LoggerFactory.getLogger(UsersResource.class);

    @Autowired
    private MatchService matchService;

    @POST
    @Path("/{match_id}/discussion")
    public Response addToDiscussion(@PathParam("match_id") long matchId, String message){
        try {
            DisussionUpload disussionUpload = convertStringToDiscussionUpload(message);
            matchService.addToDiscussion(matchId, disussionUpload);
            return Response.ok().build();
        } catch (IOException e) {
            logger.error("Error while converting JSON Object to string", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    private DisussionUpload convertStringToDiscussionUpload(String message) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(message, DisussionUpload.class);
    }

    @GET
    @Path("/{match_id}/discussion")
    public Response getDiscussion(@PathParam("match_id") long matchId, @QueryParam("scenarioId") long scenarioId){
        try {
            List<DisussionMessage> disussionMessages = matchService.getDiscussion(matchId, scenarioId);
            String discussionStr = convertDiscussionListToString(disussionMessages);
            return Response.ok().type("application/json").entity(discussionStr).build();
        } catch (IOException e) {
            logger.error("Error while converting JSON Object to string", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GET
    @Path("/{match_id}/scenario")
    public Response getScenario(@PathParam("match_id") long matchId, @QueryParam("pageSize") int pageSize){
        try {
            List<Scenario> scenarios = matchService.getScenarios(matchId, pageSize);
            String scenarioStr = convertScenarioListToString(scenarios);
            return Response.ok().type("application/json").entity(scenarioStr).build();
        } catch (IOException e) {
            logger.error("Error while converting JSON Object to string", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    private String convertScenarioListToString(List<Scenario> scenarios) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(scenarios);
    }

    private String convertDiscussionListToString(List<DisussionMessage> disussionMessages) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(disussionMessages);
    }
}
