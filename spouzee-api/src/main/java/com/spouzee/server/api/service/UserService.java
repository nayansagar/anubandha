package com.spouzee.server.api.service;

import com.spouzee.server.api.data.CreateUserResponse;
import com.spouzee.server.api.exception.*;
import com.spouzee.server.api.matcher.IMatcher;
import com.spouzee.server.api.schema.*;
import com.spouzee.server.api.schema.common.Enums;
import com.spouzee.server.api.schema.common.UserSignUpData;
import com.spouzee.server.api.social.EMailHelper;
import com.spouzee.server.api.social.FacebookHelper;
import com.spouzee.server.api.util.ImageUtils;
import com.spouzee.server.db.dao.SpzQuestionDao;
import com.spouzee.server.db.dao.SpzUserDao;
import com.spouzee.server.db.entities.*;
import com.spouzee.server.db.entities.Option;
import com.spouzee.server.db.entities.Question;
import com.spouzee.server.db.exception.InvalidOptionException;
import com.spouzee.server.db.exception.InvalidQuestionException;
import com.spouzee.server.db.exception.InvalidUserException;
import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.HibernateJdbcException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * Created by Sagar on 8/3/2015.
 */
@Service
public class UserService {

    public static final String ANON_NAME_STR = "";
    private SpzUserDao spzUserDao;
    private SpzQuestionDao spzQuestionDao;
    private FacebookHelper facebookHelper;
    private EMailHelper eMailHelper;
    private IMatcher matcherImpl;
    private QuestionService questionService;

    Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    public UserService(SpzUserDao spzUserDao,
                       SpzQuestionDao spzQuestionDao,
                       FacebookHelper facebookHelper,
                       EMailHelper emailHelper,
                       IMatcher matcherImpl,
                       QuestionService questionService) {
        this.spzUserDao = spzUserDao;
        this.spzQuestionDao = spzQuestionDao;
        this.facebookHelper = facebookHelper;
        this.eMailHelper = emailHelper;
        this.matcherImpl = matcherImpl;
        this.questionService = questionService;
    }

    public void storeAccessTokenForUser(long userId, UserToken token) throws UserNotFoundException {
        User user = spzUserDao.getUserWithLinks(userId);
        if (user == null) {
            throw new UserNotFoundException("UserId " + userId + " not found");
        }
        user = populateUserLink(token, user);
        spzUserDao.updateUser(user);
    }

    public CreateUserResponse createUser(UserToken userSignUpData) {
        User user = spzUserDao.getUserByEmail(userSignUpData.getEmail());
        if (user != null) {
            logger.debug("EmailId " + userSignUpData.getEmail() + " already exists..");
            if (user.getName() != null && !user.getName().isEmpty()
                    && user.getDateOfBirth() != null
                    && user.getRole() != null && !user.getRole().isEmpty()
                    && user.getMaritalStatus() != null && !user.getMaritalStatus().isEmpty()) {
                return new CreateUserResponse(user.getId(), true, true);
            } else {
                return new CreateUserResponse(user.getId(), true, false);
            }
        }
        User userEntity = new User();
        userEntity.setEmail(userSignUpData.getEmail());
        userEntity = populateUserLink(userSignUpData, userEntity);
        /*UserCredential userCredential = new UserCredential();
        userCredential.setPasswordHash(userSignUpData.getPassword());
        userCredential.setPasswordSalt("passwordSalt");
        userCredential.setUser(userEntity);
        userEntity.setUserCredential(userCredential);*/
        spzUserDao.createUser(userEntity);
        return new CreateUserResponse(userEntity.getId(), false, false);
    }

    public long verifyUser(UserSignUpData userSignUpData) throws UserNotFoundException, EmailOrPasswordInvalidException {
        User user = spzUserDao.getUserByEmail(userSignUpData.getEmail());
        if (user == null) {
            throw new UserNotFoundException("EmailId " + userSignUpData.getEmail() + " not found");
        }
        /*if (!userSignUpData.getPassword().equals(user.getUserCredential().getPasswordHash())) {
            throw new EmailOrPasswordInvalidException("Invalid credentials : " + userSignUpData.getEmail());
        }*/
        return user.getId();
    }

    public void updateUser(long userId, UserDetails userDetails) throws UserNotFoundException, ParseException {
        User user = spzUserDao.getUser(userId);
        if (user == null) {
            throw new UserNotFoundException("UserId " + userId + " not found");
        }
        user = convertUserDetailsToUserEntity(userDetails, user);
        spzUserDao.updateUser(user);
        eMailHelper.notifyUserCreationToAdmin(user.getName());
    }

    private User convertUserDetailsToUserEntity(UserDetails userDetails, User user) throws ParseException {
        if (user == null) {
            user = new User();
        }
        if (userDetails.getEmployment() != null) user.setEmployment(userDetails.getEmployment());
        if (userDetails.getQualification() != null) user.setQualification(userDetails.getQualification());
        if (userDetails.getDateOfBirth() != null) user.setDateOfBirth(createDateObject(userDetails.getDateOfBirth()));
        if (userDetails.getLanguage() != null) user.setLanguage(userDetails.getLanguage());
        if (userDetails.getCaste() != null) user.setCaste(userDetails.getCaste());
        if (userDetails.getName() != null) user.setName(userDetails.getName());
        if (userDetails.getReligion() != null) user.setReligion(userDetails.getReligion());
        if (userDetails.getRole() != null) user.setRole(userDetails.getRole());
        if (userDetails.getSubcaste() != null) user.setSubcaste(userDetails.getSubcaste());
        if (userDetails.getMaritalStatus() != null) user.setMaritalStatus(userDetails.getMaritalStatus());
        List<UserToken> userTokenList = userDetails.getUserTokenList();
        if (userTokenList != null && !userTokenList.isEmpty()) {
            for (UserToken token : userTokenList) {
                user.addUserLink(createUserLinkFromUserToken(token, user));
            }
        }
        return user;
    }

    private Date createDateObject(String dateOfBirth) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = simpleDateFormat.parse(dateOfBirth);
        return date;
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
        if(userLinks != null){
            for (UserLink link : userLinks) {
                if (link.getLinkType().equals(token.getLinkType().name())) {
                    link.setCreatedTime(token.getCreatedTime());
                    link.setAccessToken(token.getAccessToken());
                    link.setLinkUserId(token.getLinkTypeUserID());
                    link.setExpiryTime(calculateExpiryTime(token.getCreatedTime(), token.getTokenValidityInMillis()));
                    return user;
                }
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
        if(expiryTime != null){
            expiryTime.add(Calendar.MILLISECOND, tokenValidityInMillis);
        }
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
            throw new UserNotFoundException("UserID " + userId + " not found");
        } catch (InvalidQuestionException e) {
            throw new QuestionNotFoundException("QuestionID " + questionResponse.getQuestion() + " not found");
        } catch (InvalidOptionException e) {
            throw new OptionNotFoundException("OptionID " + questionResponse.getOptions() + " not found");
        }
    }

    public void storeExpectationForUser(Long userId, QuestionResponse questionResponse)
            throws UserNotFoundException, QuestionNotFoundException, OptionNotFoundException {
        try {
            spzUserDao.saveExpectation(userId, questionResponse.getQuestion(), questionResponse.getOptions());
        } catch (InvalidUserException e) {
            throw new UserNotFoundException("UserID " + userId + " not found");
        } catch (InvalidQuestionException e) {
            throw new QuestionNotFoundException("QuestionID " + questionResponse.getQuestion() + " not found");
        } catch (InvalidOptionException e) {
            throw new OptionNotFoundException("OptionID " + questionResponse.getOptions() + " not found");
        }
    }

    public UserDetails getProfile(long userId, long ruId) {
        if(userId != ruId){
            MatchEntity me = spzUserDao.getMatch(userId, ruId);
            if(me != null){
                UserDetails userDetails = convertUserEntityToUserDetails(spzUserDao.getUser(userId));
                if(( (me.isUser1Interested() && me.isUser2Responded()) || (me.isUser2Interested() && me.isUser1Responded()) )){
                    return userDetails;
                }else{
                    userDetails.setName(ANON_NAME_STR);
                    return userDetails;
                }
            }else{
                return null;
            }
        }else{
            return convertUserEntityToUserDetails(spzUserDao.getUser(userId));
        }
    }

    private UserDetails convertUserEntityToUserDetails(User user) {
        UserDetails userDetails = new UserDetails();
        if (user.getSubcaste() != null) userDetails.setSubcaste(user.getSubcaste());
        if (user.getRole() != null) userDetails.setRole(user.getRole());
        if (user.getReligion() != null) userDetails.setReligion(user.getReligion());
        if (user.getQualification() != null) userDetails.setQualification(user.getQualification());
        if (user.getCaste() != null) userDetails.setCaste(user.getCaste());
        if (user.getDateOfBirth() != null) userDetails.setDateOfBirth(convertDateToString(user.getDateOfBirth()));
        if (user.getEmployment() != null) userDetails.setEmployment(user.getEmployment());
        if (user.getLanguage() != null) userDetails.setLanguage(user.getLanguage());
        if (user.getName() != null) userDetails.setName(user.getName());
        if (user.getMaritalStatus() != null) userDetails.setMaritalStatus(user.getMaritalStatus());
        return userDetails;
    }

    private String convertDateToString(Date dateOfBirth) {
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd/MM/yyyy");
        return dateFormat1.format(dateOfBirth);
    }

    public byte[] getProfilePicture(Long userId, String imageSize) throws AccountNotLinkedException {
        List<UserLink> userLinks = spzUserDao.getUserWithLinks(userId).getUserLinks();
        String linkUserId = null;
        for (UserLink userLink : userLinks) {
            if (Enums.LinkType.FACEBOOK.name().equals(userLink.getLinkType())) {
                linkUserId = userLink.getLinkUserId();
                break;
            }
        }
        if (linkUserId == null || linkUserId.isEmpty()) {
            throw new AccountNotLinkedException(Enums.LinkType.FACEBOOK.name());
        }
        byte[] profilePic = facebookHelper.getUserProfilePicture(linkUserId, imageSize);
        return profilePic;
    }

    public List<QuestionResponse> getResponses(long userId) {
        List<ReportItem> reportItems = spzUserDao.getResponsesNative(userId);
        List<QuestionResponse> responses = new ArrayList<QuestionResponse>();
        for (ReportItem reportItem : reportItems) {
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
        if (user == null) {
            return null;
        }
        return user.getId();
    }

    public List<com.spouzee.server.api.schema.Question> getQuestionsWithResponse(long userId, long lastQuestionId, int pageSize) {
        List<com.spouzee.server.api.schema.Question> questions = new ArrayList<>();
        List<com.spouzee.server.db.entities.Question> questionEntities = spzQuestionDao.getQuestions(lastQuestionId, pageSize);
        User user = spzUserDao.getUserWithExpectationsAndResponses(userId);

        for (com.spouzee.server.db.entities.Question questionEntity : questionEntities) {
            com.spouzee.server.api.schema.Question question = new com.spouzee.server.api.schema.Question();
            question.setId(questionEntity.getId());
            question.setQuestionDescription(questionEntity.getQuestionDescription());
            question.setResponseType(questionEntity.getResponseType());
            question.setQuestionTarget(questionEntity.getQuestionTarget());

            if (contains(user.getExpectations(), questionEntity.getId())) {
                question.setPartOfQuestionnaire(true);
            }

            ReportItem reportItem = spzUserDao.getResponseNative(userId, questionEntity.getId());

            if (reportItem != null) {
                question.setResponses(reportItem.getOptions());
                question.setComments(reportItem.getComments());
            }

            List<Option> optionEntities = questionEntity.getOptions();
            for (Option optionEntity : optionEntities) {
                com.spouzee.server.api.schema.Option option = new com.spouzee.server.api.schema.Option(optionEntity.getId(),
                        optionEntity.getOptionDescription(), optionEntity.getShortDescription());
                question.addOption(option);
            }
            questions.add(question);
        }
        return questions;
    }

    private boolean contains(List<Expectation> expectations, long id) {
        for (Expectation e : expectations) {
            if (e.getQuestion().getId() == id) {
                return true;
            }
        }
        return false;
    }

    public boolean sendInvite(long userId, String email) {
        User user = spzUserDao.getUser(userId);
        if (user == null) {
            return false;
        }
        spzUserDao.storeUserInvite(user, email);
        eMailHelper.sendInvite(user.getName(), email);
        return true;
    }

    public long createQuestion(UserQuestion userQuestion) {
        Question questionEntity = new Question();

        questionEntity.setResponseType(userQuestion.getResponseType());
        questionEntity.setQuestionTarget(userQuestion.getTargetRole());
        questionEntity.setQuestionDescription(userQuestion.getQuestion());
        questionEntity.setOptions(new ArrayList<Option>());

        for (String optionStr : userQuestion.getOptions()) {
            if (optionStr == null || optionStr.isEmpty()) {
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

    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.DEFAULT)
    public List<Match> getMatch(Long userId) throws UserNotFoundException {
        List<Match> matchList = matcherImpl.getMatchingUsers(userId);
        for (Match match : matchList) {
            MatchEntity matchEntity = spzUserDao.getMatch(userId, match.getUserId());
            if (matchEntity == null) {
                User user = spzUserDao.getUser(userId);
                User otherUser = spzUserDao.getUser(match.getUserId());
                eMailHelper.notifyMatch(otherUser.getEmail(), user.getName(), userId);
            } else {
                match.setInterestExpressedByMe(matchEntity.isUser1Interested());
                match.setOtherUserRespondedToMyInterest(matchEntity.isUser2Responded());
            }
            matchEntity = spzUserDao.saveMatch(userId, match.getSelfExpectationMatch(), match.getUserId(), match.getOtherExpectationMatch());
            match.setMatchId(matchEntity.getId());

            if(( (matchEntity.isUser1Interested() && matchEntity.isUser2Responded())
                    || (matchEntity.isUser2Interested() && matchEntity.isUser1Responded()) )){
                //Do nothing - name already set...
            }else{
                match.setName(ANON_NAME_STR);
            }
        }

        List<MatchEntity> otherMatches = spzUserDao.fetchAllMatches(userId);
        for (MatchEntity matchEntity : otherMatches) {
            long otherUserId = matchEntity.getUserId1() == userId ?
                    matchEntity.getUserId2() : matchEntity.getUserId1();
            double userExpectationMet = matchEntity.getUserId1() == userId ?
                    matchEntity.getUser1ExpectationMet() : matchEntity.getUser2ExpectationMet();
            double otherUserExpectationMet = matchEntity.getUserId1() == userId ?
                    matchEntity.getUser2ExpectationMet() : matchEntity.getUser1ExpectationMet();
            boolean haveIExpressedInterest = matchEntity.getUserId1() == userId ?
                    matchEntity.isUser1Interested() : matchEntity.isUser2Interested();
            boolean hasOtherUserExpressedInterest = matchEntity.getUserId1() == userId ?
                    matchEntity.isUser2Interested() : matchEntity.isUser1Interested();
            boolean hasTheOtherUserRespondedToMyInterest = matchEntity.getUserId1() == userId ?
                    matchEntity.isUser2Responded() : matchEntity.isUser1Responded();
            boolean haveIRespondedToThisUsersInterest = matchEntity.getUserId1() == userId ?
                    matchEntity.isUser1Responded() : matchEntity.isUser2Responded();

            boolean haveIRequestedContact = matchEntity.getUserId1() == userId ?
                    matchEntity.isUser1RequestedContact() : matchEntity.isUser2RequestedContact();
            boolean hasOtherUserRequestedContact = matchEntity.getUserId1() == userId ?
                    matchEntity.isUser2RequestedContact() : matchEntity.isUser1RequestedContact();
            boolean haveIRevealedContact = matchEntity.getUserId1() == userId ?
                    matchEntity.isUser1RevealedContact() : matchEntity.isUser2RevealedContact();
            boolean hasOtherUserRevealedContact = matchEntity.getUserId1() == userId ?
                    matchEntity.isUser2RevealedContact() : matchEntity.isUser1RevealedContact();

            if (!matchAdded(matchList, otherUserId)) {
                Match match = new Match(otherUserId, userExpectationMet, otherUserExpectationMet);

                User otherUser = spzUserDao.getUser(otherUserId);
                if(( (matchEntity.isUser1Interested() && matchEntity.isUser2Responded())
                        || (matchEntity.isUser2Interested() && matchEntity.isUser1Responded()) )){
                    match.setIdentityExchanged(true);
                    match.setName(otherUser == null ? null : otherUser.getName());
                }else{
                    match.setName(ANON_NAME_STR);
                }

                match.setInterestExpressedByMe(haveIExpressedInterest);
                match.setOtherUserExpressedInterest(hasOtherUserExpressedInterest);
                match.setOtherUserRespondedToMyInterest(hasTheOtherUserRespondedToMyInterest);
                match.setIRespondedToOtherUsersInterest(haveIRespondedToThisUsersInterest);
                match.setIRequestedContact(haveIRequestedContact);
                match.setOtherUserRequestedContact(hasOtherUserRequestedContact);
                match.setIRevealedContact(haveIRevealedContact);
                match.setOtherUserRevealedContact(hasOtherUserRevealedContact);

                match.setMatchId(matchEntity.getId());
                matchList.add(match);
            }
        }
        return matchList;
    }

    private boolean matchAdded(List<Match> matchList, long otherUserId) {
        for (Match match : matchList) {
            if (match.getUserId() == otherUserId) {
                return true;
            }
        }
        return false;
    }

    public List<UserImagePojo> getUserImages(Long userId) {
        User user = spzUserDao.getUserWithImages(userId);
        if (user == null) {
            return null;
        }
        List<UserImage> userImageEntities = user.getUserImages();
        List<UserImagePojo> userImagePojos = new ArrayList<>();
        for (UserImage userImage : userImageEntities) {
            userImagePojos.add(new UserImagePojo(userImage.getId(), userImage.getDescription()));
        }
        return userImagePojos;
    }

    public void storeUserImage(Long userId, MultipartFormDataInput input) throws IOException {
        Map<String, List<InputPart>> uploadForm = input.getFormDataMap();

        List<InputPart> inputParts = uploadForm.get("attachment");

        List<InputPart> descParts = uploadForm.get("description");

        String description = "";
        for (InputPart inputPart : descParts) {
            InputStream inputStream = null;
            try {
                inputStream = inputPart.getBody(InputStream.class, null);
                byte[] bytes = IOUtils.toByteArray(inputStream);
                description = new String(bytes);
            }finally {
                if(inputStream != null){
                    inputStream.close();
                }
            }
        }

        for (InputPart inputPart : inputParts) {
            String contentType = inputPart.getMediaType().getType() + "/" + inputPart.getMediaType().getSubtype();
            InputStream inputStream = null;
            byte[] bytes;
            try {
                inputStream = inputPart.getBody(InputStream.class, null);
                bytes = IOUtils.toByteArray(inputStream);
            }finally {
                if(inputStream != null){
                    inputStream.close();
                }
            }
            float quality = 0.6f;
            do {
                try {
                    compressAndSave(userId, description, inputPart, contentType, new ByteArrayInputStream(bytes), quality);
                    break;
                } catch (HibernateJdbcException e) {
                    quality = quality - 0.1f;
                }
            } while (quality >= 0.3f);

        }
    }

    private void compressAndSave(Long userId, String description, InputPart inputPart, String contentType, InputStream inputStream, float quality) throws IOException {
        byte[] bytes = ImageUtils.compressImage(inputStream, inputPart.getMediaType().getSubtype(), quality);
        spzUserDao.saveImage(userId, bytes, contentType, description);
    }

    public ImagePojo getUserImageContent(Long imageId, String size) throws IOException {
        UserImage userImage = spzUserDao.getImageContent(imageId);
        byte[] resizedImage;
        if (size != null && size.equals("large")) {
            resizedImage = resizeImage(userImage.getContent(), userImage.getContentType().split("/")[1], 700, 300);
        } else if (size != null && size.equals("small")) {
            resizedImage = resizeImage(userImage.getContent(), userImage.getContentType().split("/")[1], 100, 100);
        } else {
            resizedImage = resizeImage(userImage.getContent(), userImage.getContentType().split("/")[1], 500, 250);
        }
        return new ImagePojo(userImage.getContentType(), resizedImage);
    }

    private byte[] resizeImage(byte[] originalImage, String imageType, int width, int height) throws IOException {
        BufferedImage originalBufferedImage = ImageIO.read(new ByteArrayInputStream(originalImage));
        int type = originalBufferedImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalBufferedImage.getType();

        BufferedImage resizedImage = new BufferedImage(width, height, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalBufferedImage, 0, 0, width, height, null);
        g.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, imageType, baos);
        baos.flush();
        byte[] imageInByte = baos.toByteArray();
        baos.close();

        return imageInByte;
    }

    public void saveInterestExpression(Long userId, Long otherUserId) {
        spzUserDao.saveInterest(userId, otherUserId);
        User fromUser = spzUserDao.getUser(userId);
        User toUser = spzUserDao.getUser(otherUserId);
        eMailHelper.notifyIdentityRequest(toUser.getEmail(), fromUser.getName(), fromUser.getId());
    }

    public String saveInterestResponse(Long userId, Long otherUserId) {
        spzUserDao.saveInterestResponse(userId, otherUserId);
        User otherUser = spzUserDao.getUser(otherUserId);
        User user = spzUserDao.getUser(userId);
        eMailHelper.notifyIdentityRevealed(otherUser.getEmail(), user.getName(), user.getEmail(), user.getId());
        return otherUser.getEmail();
    }

    public String getContact(Long otherUserId) {
        User user = spzUserDao.getUser(otherUserId);
        return user.getEmail();
    }

    public List<com.spouzee.server.api.schema.Question> getNextQuestionsForUser(long userId, long lastQuestionId, int pageSize) {
        List<com.spouzee.server.api.schema.Question> questions = questionService.getQuestions(userId, lastQuestionId, pageSize);
        return questions;
    }

    public String getSessionId(String sid, long userId, UserToken userToken) {
        String emailFromFB = facebookHelper.getUserEmail(userToken.getAccessToken());
        String sessionId = null;
        if(emailFromFB != null && emailFromFB.equals(userToken.getEmail())){
            if(sid == null || sid.isEmpty()){
                sessionId = spzUserDao.createSession(userId);
            }else{
                sessionId = spzUserDao.refreshSession(sid);
            }
        }
        return sessionId;
    }

    public void saveContactRequest(Long userId, Long otherUserId) {
        spzUserDao.saveContactRequest(userId, otherUserId);
        User fromUser = spzUserDao.getUser(userId);
        User toUser = spzUserDao.getUser(otherUserId);
        eMailHelper.notifyContactRequest(toUser.getEmail(), fromUser.getName(), fromUser.getId());
    }

    public String saveContactResponse(Long userId, Long otherUserId) {
        spzUserDao.saveContactResponse(userId, otherUserId);
        User otherUser = spzUserDao.getUser(otherUserId);
        User user = spzUserDao.getUser(userId);
        eMailHelper.notifyContactRevealed(otherUser.getEmail(), user.getName(), user.getEmail(), user.getId());
        return otherUser.getEmail();
    }
}
