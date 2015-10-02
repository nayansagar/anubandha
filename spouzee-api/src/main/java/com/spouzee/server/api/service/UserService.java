package com.spouzee.server.api.service;

import com.spouzee.server.api.data.CreateUserResponse;
import com.spouzee.server.api.exception.*;
import com.spouzee.server.api.schema.*;
import com.spouzee.server.api.schema.common.Enums;
import com.spouzee.server.api.schema.common.UserSignUpData;
import com.spouzee.server.api.social.EMailHelper;
import com.spouzee.server.api.social.FacebookHelper;
import com.spouzee.server.db.dao.SpzQuestionDao;
import com.spouzee.server.db.dao.SpzUserDao;
import com.spouzee.server.db.entities.*;
import com.spouzee.server.db.entities.Option;
import com.spouzee.server.db.entities.Question;
import com.spouzee.server.db.entities.Scenario;
import com.spouzee.server.db.exception.InvalidOptionException;
import com.spouzee.server.db.exception.InvalidQuestionException;
import com.spouzee.server.db.exception.InvalidUserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Sagar on 8/3/2015.
 */
@Service
public class UserService {

    private SpzUserDao spzUserDao;
    private SpzQuestionDao spzQuestionDao;
    private FacebookHelper facebookHelper;
    private EMailHelper eMailHelper;

    Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    public UserService(SpzUserDao spzUserDao, SpzQuestionDao spzQuestionDao, FacebookHelper facebookHelper, EMailHelper emailHelper){
        this.spzUserDao = spzUserDao;
        this.spzQuestionDao = spzQuestionDao;
        this.facebookHelper = facebookHelper;
        this.eMailHelper = emailHelper;
    }

    public void storeAccessTokenForUser(long userId, UserToken token) throws UserNotFoundException {
        User user = spzUserDao.getUserWithLinks(userId);
        if(user == null){
            throw new UserNotFoundException("UserId "+userId+" not found");
        }
        user = populateUserLink(token, user);
        spzUserDao.updateUser(user);
    }

    public CreateUserResponse createUser(UserSignUpData userSignUpData) {
        User user = spzUserDao.getUserByEmail(userSignUpData.getEmail());
        if(user != null){
            logger.debug("EmailId " + userSignUpData.getEmail() + " already exists..");
            return new CreateUserResponse(user.getId(), true);
        }
        User userEntity = new User();
        userEntity.setEmail(userSignUpData.getEmail());
        UserCredential userCredential = new UserCredential();
        userCredential.setPasswordHash(userSignUpData.getPassword());
        userCredential.setPasswordSalt("passwordSalt");
        userCredential.setUser(userEntity);
        userEntity.setUserCredential(userCredential);
        spzUserDao.createUser(userEntity);
        return new CreateUserResponse(userEntity.getId(), false);
    }

    public long verifyUser(UserSignUpData userSignUpData) throws UserNotFoundException, EmailOrPasswordInvalidException {
        User user = spzUserDao.getUserByEmail(userSignUpData.getEmail());
        if(user == null){
            throw new UserNotFoundException("EmailId "+userSignUpData.getEmail()+" not found");
        }
        if(!userSignUpData.getPassword().equals(user.getUserCredential().getPasswordHash())){
            throw new EmailOrPasswordInvalidException("Invalid credentials : "+userSignUpData.getEmail());
        }
        return user.getId();
    }

    public void updateUser(long userId, UserDetails userDetails) throws UserNotFoundException {
        User user = spzUserDao.getUser(userId);
        if(user == null){
            throw new UserNotFoundException("UserId "+userId+" not found");
        }
        user = convertUserDetailsToUserEntity(userDetails, user);
        spzUserDao.updateUser(user);
    }

    private User convertUserDetailsToUserEntity(UserDetails userDetails, User user) {
        if(user == null){ user = new User();}
        if(userDetails.getEmployment() != null) user.setEmployment(userDetails.getEmployment());
        if(userDetails.getQualification() != null) user.setQualification(userDetails.getQualification());
        //if(userDetails.getDateOfBirth() != null) user.setDateOfBirth(userDetails.getDateOfBirth());
        if(userDetails.getLanguage() != null) user.setLanguage(userDetails.getLanguage());
        if(userDetails.getCaste() != null) user.setCaste(userDetails.getCaste());
        if(userDetails.getName() != null) user.setName(userDetails.getName());
        if(userDetails.getReligion() != null) user.setReligion(userDetails.getReligion());
        if(userDetails.getRole() != null) user.setRole(userDetails.getRole());
        if(userDetails.getSubcaste() != null) user.setSubcaste(userDetails.getSubcaste());
        List<UserToken> userTokenList = userDetails.getUserTokenList();
        if(userTokenList != null && !userTokenList.isEmpty()){
            for(UserToken token : userTokenList){
                user.addUserLink(createUserLinkFromUserToken(token, user));
            }
        }
        return user;
    }

    private UserLink createUserLinkFromUserToken(UserToken token, User user) {
        UserLink userLink = new UserLink();
        userLink.setLinkUserId(token.getLinkTypeUserID());
        userLink.setExpiryTime(calculateExpiryTime(token.getCreatedTime(), token.getTokenValidityInMillis()));
        userLink.setLinkType(token.getLinkType().name());
        userLink.setCreatedTime(token.getCreatedTime());
        userLink.setAccessToken(token.getAccessToken());
        userLink.setUser(user);
        return userLink;
    }

    private User populateUserLink(UserToken token, User user) {
        List<UserLink> userLinks = user.getUserLinks();
        for(UserLink link : userLinks){
            if(link.getLinkType().equals(token.getLinkType().name())){
                link.setCreatedTime(token.getCreatedTime());
                link.setAccessToken(token.getAccessToken());
                link.setLinkUserId(token.getLinkTypeUserID());
                link.setExpiryTime(calculateExpiryTime(token.getCreatedTime(), token.getTokenValidityInMillis()));
                return user;
            }
        }
        UserLink userLink = new UserLink();
        userLink.setAccessToken(token.getAccessToken());
        userLink.setCreatedTime(token.getCreatedTime());
        userLink.setLinkType(token.getLinkType().name());
        userLink.setLinkUserId(token.getLinkTypeUserID());
        userLink.setExpiryTime(calculateExpiryTime(token.getCreatedTime(), token.getTokenValidityInMillis()));
        userLink.setUser(user);
        user.addUserLink(userLink);
        return user;
    }

    private Calendar calculateExpiryTime(Calendar createdTime, int tokenValidityInMillis) {
        Calendar expiryTime = createdTime;
        expiryTime.add(Calendar.MILLISECOND, tokenValidityInMillis);
        return expiryTime;
    }

    public void deleteUser(long userId) {
        spzUserDao.deleteUser(userId);
    }

    public void storeResponseForUser(Long userId, QuestionResponse questionResponse)
            throws UserNotFoundException, QuestionNotFoundException, OptionNotFoundException {
        try {
            spzQuestionDao.saveResponse(userId, questionResponse.getQuestion(), questionResponse.getOptions(), questionResponse.getComments());
        } catch (InvalidUserException e) {
            throw new UserNotFoundException("UserID "+userId+" not found");
        } catch (InvalidQuestionException e) {
            throw new QuestionNotFoundException("QuestionID "+questionResponse.getQuestion()+" not found");
        } catch (InvalidOptionException e) {
            throw new OptionNotFoundException("OptionID "+questionResponse.getOptions()+" not found");
        }
    }

    public void storeExpectationForUser(Long userId, QuestionResponse questionResponse)
            throws UserNotFoundException, QuestionNotFoundException, OptionNotFoundException {
        try {
            spzUserDao.saveExpectation(userId, questionResponse.getQuestion(), questionResponse.getOptions());
        } catch (InvalidUserException e) {
            throw new UserNotFoundException("UserID "+userId+" not found");
        } catch (InvalidQuestionException e) {
            throw new QuestionNotFoundException("QuestionID "+questionResponse.getQuestion()+" not found");
        } catch (InvalidOptionException e) {
            throw new OptionNotFoundException("OptionID "+questionResponse.getOptions()+" not found");
        }
    }

    public UserDetails getProfile(long userId) {
        return convertUserEntityToUserDetails(spzUserDao.getUser(userId));
    }

    private UserDetails convertUserEntityToUserDetails(User user) {
        UserDetails userDetails = new UserDetails();
        if(user.getSubcaste() != null) userDetails.setSubcaste(user.getSubcaste());
        if(user.getRole() != null) userDetails.setRole(user.getRole());
        if(user.getReligion() != null) userDetails.setReligion(user.getReligion());
        if(user.getQualification() != null) userDetails.setQualification(user.getQualification());
        if(user.getCaste() != null) userDetails.setCaste(user.getCaste());
        //if(user.getDateOfBirth() != null) userDetails.setDateOfBirth(user.getDateOfBirth());
        if(user.getEmployment() != null) userDetails.setEmployment(user.getEmployment());
        if(user.getLanguage() != null) userDetails.setLanguage(user.getLanguage());
        if(user.getName() != null) userDetails.setName(user.getName());
        return userDetails;
    }

    public byte[] getProfilePicture(Long userId) throws AccountNotLinkedException {
        List<UserLink> userLinks = spzUserDao.getUserWithLinks(userId).getUserLinks();
        String linkUserId = null;
        for(UserLink userLink : userLinks){
            if(Enums.LinkType.FACEBOOK.name().equals(userLink.getLinkType())){
                linkUserId = userLink.getLinkUserId();
                break;
            }
        }
        if(linkUserId == null || linkUserId.isEmpty()){
            throw new AccountNotLinkedException(Enums.LinkType.FACEBOOK.name());
        }
        byte[] profilePic = facebookHelper.getUserProfilePicture(linkUserId);
        return profilePic;
    }

    public List<QuestionResponse> getResponses(long userId){
        List<ReportItem> reportItems = spzUserDao.getResponsesNative(userId);
        List<QuestionResponse> responses = new ArrayList<QuestionResponse>();
        for(ReportItem reportItem : reportItems){
            QuestionResponse response = new QuestionResponse();
            response.setQuestion(reportItem.getQuestionId());
            response.setOptions(reportItem.getOptions());
            response.setComments(reportItem.getComments());
            responses.add(response);
        }
        return responses;
    }

    public Long searchUser(String email) {
        User user = spzUserDao.getUserByEmail(email);
        if(user == null){
            return null;
        }
        return user.getId();
    }

    public List<com.spouzee.server.api.schema.Question> getQuestionsWithResponse(long userId, long lastQuestionId, int pageSize){
        List<com.spouzee.server.api.schema.Question> questions = new ArrayList<>();
        List<com.spouzee.server.db.entities.Question> questionEntities = spzQuestionDao.getQuestions(lastQuestionId, pageSize);
        for(com.spouzee.server.db.entities.Question questionEntity : questionEntities){
            com.spouzee.server.api.schema.Question question = new com.spouzee.server.api.schema.Question();
            question.setId(questionEntity.getId());
            question.setQuestionDescription(questionEntity.getQuestionDescription());
            question.setShortDescription(questionEntity.getShortDescription());
            question.setResponseType(questionEntity.getResponseType());
            question.setQuestionTarget(questionEntity.getQuestionTarget());

            ReportItem reportItem = spzUserDao.getResponseNative(userId, questionEntity.getId());
            if(reportItem == null){
                return null;
            }
            question.setResponses(reportItem.getOptions());
            question.setComments(reportItem.getComments());

            com.spouzee.server.db.entities.Scenario scenarioEntity = questionEntity.getScenario();
            com.spouzee.server.api.schema.Scenario scenario = new com.spouzee.server.api.schema.Scenario(scenarioEntity.getId(), scenarioEntity.getScenarioTitle(), scenarioEntity.getScenarioDescription());
            question.setScenario(scenario);

            List<Option> optionEntities = questionEntity.getOptions();
            for(Option optionEntity : optionEntities){
                com.spouzee.server.api.schema.Option option = new com.spouzee.server.api.schema.Option(optionEntity.getId(),
                        optionEntity.getOptionDescription(), optionEntity.getShortDescription());
                question.addOption(option);
            }
            questions.add(question);
        }
        return questions;
    }

    public boolean sendInvite(long userId, String email) {
        User user = spzUserDao.getUser(userId);
        if(user == null){
            return false;
        }
        spzUserDao.storeUserInvite(user, email);
        eMailHelper.sendInvite(user.getName(), email);
        return true;
    }

    public long createQuestion(UserQuestion userQuestion){
        Question questionEntity = new Question();

        questionEntity.setResponseType(userQuestion.getResponseType());
        questionEntity.setQuestionTarget(userQuestion.getTargetRole());
        questionEntity.setQuestionDescription(userQuestion.getQuestion());
        questionEntity.setOptions(new ArrayList<Option>());

        for(String optionStr : userQuestion.getOptions()){
            if(optionStr == null || optionStr.isEmpty()){
                continue;
            }
            Option optionEntity = new Option();
            optionEntity.setOptionDescription(optionStr);
            optionEntity.setQuestion(questionEntity);
            questionEntity.getOptions().add(optionEntity);
        }

        spzUserDao.addQuestion(questionEntity);
        return questionEntity.getId();
    }
}
