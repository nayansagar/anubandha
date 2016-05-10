package com.spouzee.server.api.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.spouzee.server.api.data.CreateUserResponse;
import com.spouzee.server.api.exception.*;
import com.spouzee.server.api.schema.*;
import com.spouzee.server.api.schema.common.UserSignUpData;
import com.spouzee.server.api.service.QuestionService;
import com.spouzee.server.api.service.UserService;
import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Sagar on 8/3/2015.
 */
@Path("/v1/users")
@Component
public class UsersResource {

    Logger logger = LoggerFactory.getLogger(UsersResource.class);

    @Autowired
    private UserService userService;

    @Autowired
    private QuestionService questionService;

    @POST
    @Consumes("application/json")
    public Response createUser(@HeaderParam("sid") String sid, String userDetailsWrapper){
        logger.debug("Create user request body : "+userDetailsWrapper);
        long userId=-1;
        try {
            UserToken userToken = convertTokenWrapperStringToJSON(userDetailsWrapper);
            logger.debug("Received request to create user for emailId {}", userToken.getEmail());
            CreateUserResponse createUserResponse = userService.createUser(userToken);
            String userUri = "/v1/users/" + createUserResponse.getUserId();
            String sessionId = userService.getSessionId(sid, createUserResponse.getUserId(), userToken);
            if(sessionId == null){
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }
            if(createUserResponse.isUserAlreadyExists() && createUserResponse.isProfileDataSubmitted()){
                return Response.ok().header("Location", userUri).header("sid", sessionId).build();
            }else if(createUserResponse.isUserAlreadyExists() && !createUserResponse.isProfileDataSubmitted()){
                return Response.noContent().header("Location", userUri).header("sid", sessionId).build();
            }else{
                return Response.created(new URI(userUri)).header("sid", sessionId).build();
            }
        } catch (IOException e) {
            logger.error("Error while converting input JSON to Object", e);
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (URISyntaxException e) {
            logger.error("Error while creating URI after creating user", e);
            userService.deleteUser(userId);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GET
    @Path("/{user_id}/search")
    public Response searchUser(@PathParam("user_id") String userId, @QueryParam("email") String email){
        logger.debug("Search request by user : "+userId+" for email "+email);
        Long searchUserId = userService.searchUser(email);
        if(searchUserId == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        String userUri = "/v1/users/" + searchUserId;
        return Response.ok().header("Location", userUri).build();
    }

    @POST
    @Path("/{user_id}/invite")
    public Response sendInvite(@PathParam("user_id") long userId, @QueryParam("email") String email){
        logger.debug("Invite request by user : "+userId+" for email "+email);
        boolean result = userService.sendInvite(userId, email);
        if(result){
            return Response.ok().build();
        }else{
            return Response.status(Response.Status.NOT_FOUND).build();
        }

    }

    @POST
    @Path("/signin")
    @Consumes("application/json")
    public Response verifyUser(String userSignUpDataWrapper){
        UserSignUpData userSignUpData = null;
        long userId=-1;
        try {
            userSignUpData = convertUserSignUpDataStringToJSON(userSignUpDataWrapper);
            logger.debug("Received request to create user for emailId {}", userSignUpData.getEmail());
            userId = userService.verifyUser(userSignUpData);
            return Response.created(new URI("/v1/users/" + userId)).build();
        } catch (UserNotFoundException e) {
            logger.error("Email {} not found", userSignUpData.getEmail(), e);
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (EmailOrPasswordInvalidException e) {
            logger.error("Invalid credentials for email {}", userSignUpData.getEmail(), e);
            return Response.status(Response.Status.UNAUTHORIZED).build();
        } catch (IOException e) {
            logger.error("Error while converting input JSON to Object", e);
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (URISyntaxException e) {
            logger.error("Error while creating URI after creating user", e);
            userService.deleteUser(userId);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @POST
    @Path("/{user_id}")
    @Consumes("application/json")
    public Response updateUserDetails(@PathParam("user_id") Long userId, String userDetailsStr){
        try {
            UserDetails userDetails = convertUserWrapperStringToJSON(userDetailsStr);
            logger.debug("Received request for user {}, token - {}", userId);
            userService.updateUser(userId, userDetails);
            return Response.ok().build();
        } catch (IOException e) {
            logger.error("Error while converting input JSON to Object for userID {}", userId, e);
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (UserNotFoundException e) {
            logger.error("User ID {} not found", userId, e);
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (ParseException e) {
            logger.error("Invalid Date of birth received for userID {}", userId, e);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @POST
    @Path("/{user_id}/token")
    @Consumes("application/json")
    public Response acceptToken(@PathParam("user_id") Long userId, String tokenWrapperStr){
        try {
            UserToken userToken = convertTokenWrapperStringToJSON(tokenWrapperStr);
            if(userToken.getCreatedTime() == null){
                userToken.setCreatedTime(Calendar.getInstance());
            }
            logger.debug("Received request for user {}, token - {}", userId, userToken.getAccessToken());
            userService.storeAccessTokenForUser(userId, userToken);
            return Response.ok().build();
        } catch (IOException e) {
            logger.error("Error while converting input JSON to Object for userID {}", userId, e);
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (UserNotFoundException e) {
            logger.error("User ID {} not found", userId, e);
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @POST
    @Path("/{user_id}/response")
    @Consumes("application/json")
    public Response acceptResponse(@PathParam("user_id") Long userId, String questionResponseJson){
        try {
            QuestionResponse questionResponse = convertQuestionResponseStringToQuestionResponseObj(questionResponseJson);
            logger.debug("Received request for user {}, question - {}, option - {}, comments - {}",
                    userId, questionResponse.getQuestion(), questionResponse.getOptions(), questionResponse.getComments());
            userService.storeResponseForUser(userId, questionResponse);
            return Response.ok().build();
        } catch (UserNotFoundException e) {
            logger.error("User ID {} not found", userId, e);
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (OptionNotFoundException e) {
            logger.error("Option ID not found", e);
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (QuestionNotFoundException e) {
            logger.error("Question ID not found");
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (IOException e) {
            logger.error("Error while converting input JSON to Object for userID {}, questionId {}", userId, e);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @POST
    @Path("/{user_id}/expectation")
    @Consumes("application/json")
    public Response acceptExpectation(@PathParam("user_id") Long userId, String questionResponseJson){
        try {
            QuestionResponse questionResponse = convertQuestionResponseStringToQuestionResponseObj(questionResponseJson);
            logger.debug("Received storeExpectation request for user {}, question - {}, option - {}, comments - {}",
                    userId, questionResponse.getQuestion(), questionResponse.getOptions(), questionResponse.getComments());
            userService.storeExpectationForUser(userId, questionResponse);
            return Response.ok().build();
        } catch (UserNotFoundException e) {
            logger.error("User ID {} not found", userId, e);
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (OptionNotFoundException e) {
            logger.error("Option ID not found", e);
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (QuestionNotFoundException e) {
            logger.error("Question ID not found");
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (IOException e) {
            logger.error("Error while converting input JSON to Object for userID {}, questionId {}", userId, e);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    private QuestionResponse convertQuestionResponseStringToQuestionResponseObj(String questionResponseJson) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(questionResponseJson.getBytes(), QuestionResponse.class);
    }

    @GET
    @Path("/{user_id}/profile")
    @Produces("application/json")
    public Response getUserProfile(@PathParam("user_id") Long userId, @QueryParam("ru") long ruId){

        try {
            UserDetails userDetails = userService.getProfile(userId, ruId);
            String userDetailsJSONStr = convertUserDetailsObjectToJSONString(userDetails);
            return Response.ok().type("application/json").entity(userDetailsJSONStr).build();
        } catch (IOException e) {
            logger.error("Error while converting JSON Object to string", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GET
    @Path("/{user_id}/picture")
    @Produces("image/jpeg")
    public Response getUserProfilePicture(@PathParam("user_id") Long userId, @QueryParam("ru") long ruId, @QueryParam("size") String size){
        try {
            if(size == null || size.isEmpty()){
                size = "normal";
            }
            byte[] profilePicture = userService.getProfilePicture(userId, size);
            return Response.ok().entity(profilePicture).build();
        } catch (AccountNotLinkedException e) {
            logger.error("{} account not linked for user {}", e.getMessage(), userId);
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }

    @POST
    @Path("/{user_id}/images")
    @Consumes("multipart/form-data")
    public Response saveUserImage(@PathParam("user_id") Long userId, MultipartFormDataInput input){
        try {
            userService.storeUserImage(userId, input);
            return Response.status(Response.Status.CREATED).build();
        } catch (IOException e) {
            logger.error("Error while parsing request body", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GET
    @Path("/{user_id}/images")
    @Produces("application/json")
    public Response getUserImages(@PathParam("user_id") Long userId, @QueryParam("ru") long ruId){
        try {
            List<UserImagePojo> userImages = userService.getUserImages(userId);
            return Response.ok().entity(convertUserImagePojoListToJSONString(userImages)).build();
        } catch (IOException e) {
            logger.error("Error while converting JSON Object to string", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GET
    @Path("/{user_id}/images/{image_id}")
    @Produces("application/json")
    public Response getUserImageContent(@PathParam("user_id") Long userId, @PathParam("image_id") Long imageId, @QueryParam("size") String size){
        ImagePojo imageContent = null;
        try {
            imageContent = userService.getUserImageContent(imageId, size);
        } catch (IOException e) {
            logger.error("Error while resizing image", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.ok().header("Content-Type", imageContent.getContentType()).entity(imageContent.getContent()).build();
    }

    @GET
    @Path(("/{user_id}/questions"))
    @Produces("application/json")
    public Response getQuestions(@PathParam("user_id") long userId, @QueryParam("lastQuestionId") long lastQuestionId, @QueryParam("pageSize") int pageSize){
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

    @GET
    @Path(("/{user_id}/responseSummary"))
    @Produces("application/json")
    public Response getResponse(@PathParam("user_id") long userId){
        try {
            List<QuestionResponse> responses = userService.getResponses(userId);
            if(responses == null || responses.isEmpty()){
                return Response.status(Response.Status.NO_CONTENT).build();
            }
            String questionsJSON = convertQuestionResponseListToJSONString(responses);
            return Response.ok().entity(questionsJSON).build();
        } catch (IOException e) {
            return Response.serverError().build();
        }
    }

    @GET
    @Path(("/{user_id}/responseDetail"))
    @Produces("application/json")
    public Response getResponseDetail(@PathParam("user_id") long userId, @QueryParam("lastQuestionId") long lastQuestionId, @QueryParam("pageSize") int pageSize){
        try {
            logger.debug("Received request for getResponseDetail for userId : "+userId+", lastQuestionId : "+lastQuestionId+", pageSize : "+pageSize);
            List<Question> questions = userService.getQuestionsWithResponse(userId, lastQuestionId, pageSize);
            if(questions == null || questions.isEmpty()){
                logger.debug("getResponseDetail :: no content available for userId : "+userId);
                return Response.status(Response.Status.NO_CONTENT).build();
            }
            String questionsJSON = convertQuestionsListToJSONString(questions);
            return Response.ok().entity(questionsJSON).build();
        } catch (IOException e) {
            return Response.serverError().build();
        }
    }

    @GET
    @Path(("/{user_id}/quesNext"))
    @Produces("application/json")
    public Response getNextQuestions(@PathParam("user_id") long userId, @QueryParam("lastQuestionId") long lastQuestionId, @QueryParam("pageSize") int pageSize){
        try {
            logger.debug("Received request for getResponseDetail for userId : "+userId+", lastQuestionId : "+lastQuestionId+", pageSize : "+pageSize);
            List<Question> questions = userService.getNextQuestionsForUser(userId, lastQuestionId, pageSize);
            if(questions == null || questions.isEmpty()){
                logger.debug("getResponseDetail :: no content available for userId : "+userId);
                return Response.status(Response.Status.NO_CONTENT).build();
            }
            String questionsJSON = convertQuestionsListToJSONString(questions);
            return Response.ok().entity(questionsJSON).build();
        } catch (IOException e) {
            return Response.serverError().build();
        }
    }

    @POST
    @Path(("/{user_id}/questions"))
    @Consumes("application/json")
    public Response createQuestion(@PathParam("user_id") long userId, String questionJson){
        logger.debug("Received request to create new question from user "+userId);
        try {
            UserQuestion userQuestion = covertUserQuestionStringToJsonObject(questionJson);
            long questionId = userService.createQuestion(userQuestion);
            return Response.ok().header("Location", questionId).build();
        } catch (IOException e) {
            return Response.serverError().build();
        }
    }

    @GET
    @Path("/{user_id}/match")
    @Produces("application/json")
    public Response getMatchingProfiles(@PathParam("user_id") Long userId){

        try {
            List<Match> matchList = userService.getMatch(userId);
            String matchListJSONStr = convertMatchListToJSONString(matchList);
            return Response.ok().type("application/json").entity(matchListJSONStr).build();
        } catch (IOException e) {
            logger.error("Error while converting JSON Object to string", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } catch (UserNotFoundException e) {
            logger.error("User ID {} not found", userId, e);
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @POST
    @Path("/{user_id}/interest")
    public Response saveInterestExpression(@PathParam("user_id") Long userId, @QueryParam("otherUserId") Long otherUserId){
        userService.saveInterestExpression(userId, otherUserId);
        return Response.ok().build();
    }

    @POST
    @Path("/{user_id}/interestResponse")
    public Response saveInterestResponse(@PathParam("user_id") Long userId, @QueryParam("otherUserId") Long otherUserId){
        String emailId = userService.saveInterestResponse(userId, otherUserId);
        return Response.ok().build();
    }

    @POST
    @Path("/{user_id}/requestContact")
    public Response saveContactRequest(@PathParam("user_id") Long userId, @QueryParam("otherUserId") Long otherUserId){
        userService.saveContactRequest(userId, otherUserId);
        return Response.ok().build();
    }

    @POST
    @Path("/{user_id}/revealContact")
    public Response saveContactResponse(@PathParam("user_id") Long userId, @QueryParam("otherUserId") Long otherUserId){
        String emailId = userService.saveContactResponse(userId, otherUserId);
        return Response.ok().build();
    }

    @POST
    @Path("/{user_id}/contact")
    public Response getUserContact(@PathParam("user_id") Long userId, @QueryParam("otherUserId") Long otherUserId){
        String emailId = userService.getContact(otherUserId);
        return Response.ok().entity(new String("{\"email\" : \""+emailId+"\"}")).build();
    }

    private String convertMatchListToJSONString(List<Match> matchList) throws JsonProcessingException {
        com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
        String matchesJSON = objectMapper.writeValueAsString(matchList);
        return matchesJSON;
    }

    private UserQuestion covertUserQuestionStringToJsonObject(String questionJson) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        UserQuestion userQuestion = objectMapper.readValue(questionJson.getBytes(), UserQuestion.class);
        return userQuestion;
    }

    private String convertQuestionResponseListToJSONString(List<QuestionResponse> responses) throws IOException {
        com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
        String questionJSON = objectMapper.writeValueAsString(responses);
        return questionJSON;
    }

    private String convertQuestionsListToJSONString(List<Question> questions) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String questionJSON = objectMapper.defaultPrettyPrintingWriter().writeValueAsString(questions);
        return questionJSON;
    }

    private String convertUserImagePojoListToJSONString(List<UserImagePojo> userImagePojos) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String userImageListJSON = objectMapper.defaultPrettyPrintingWriter().writeValueAsString(userImagePojos);
        return userImageListJSON;
    }

    private UserToken convertTokenWrapperStringToJSON(String tokenWrapperStr) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        UserToken userToken = objectMapper.readValue(tokenWrapperStr.getBytes(), UserToken.class);
        return userToken;
    }

    private UserSignUpData convertUserSignUpDataStringToJSON(String userWrapperStr) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        UserSignUpData userSignUpData = objectMapper.readValue(userWrapperStr.getBytes(), UserSignUpData.class);
        return userSignUpData;
    }

    private UserDetails convertUserWrapperStringToJSON(String userWrapperStr) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        UserDetails userDetails = objectMapper.readValue(userWrapperStr.getBytes(), UserDetails.class);
        return userDetails;
    }

    private String convertUserDetailsObjectToJSONString(UserDetails userDetails) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String userDetailsStr = objectMapper.writeValueAsString(userDetails);
        return userDetailsStr;
    }
}
