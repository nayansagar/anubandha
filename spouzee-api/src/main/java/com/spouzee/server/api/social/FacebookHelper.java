package com.spouzee.server.api.social;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.exception.FacebookOAuthException;
import com.restfb.types.User;
import com.spouzee.server.api.config.SpzConfig;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.ImageType;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

/**
 * Created by Sagar on 8/23/2015.
 */
@Component
public class FacebookHelper {

    public enum FBImageType{small, large};

    public static class ExtendedToken{
        private String accessToken;
        private Date expiryDate;

        public ExtendedToken(String accessToken, Date expiryDate) {
            this.accessToken = accessToken;
            this.expiryDate = expiryDate;
        }

        public String getAccessToken() {
            return accessToken;
        }

        public Date getExpiryDate() {
            return expiryDate;
        }
    }

    private SpzConfig spzConfig;
    private FacebookClient appFacebookClient;
    private String appId;
    private String appSecret;

    @Autowired
    public FacebookHelper(SpzConfig spzConfig){
        this.spzConfig = spzConfig;
    }

    @PostConstruct
    public void getFBAccessToken() {
        appId = spzConfig.getStringProperty("facebook.appId");
        appSecret = spzConfig.getStringProperty("facebook.appSecret");
        FacebookClient facebookClient = new DefaultFacebookClient(Version.VERSION_2_4);
        FacebookClient.AccessToken accessToken = null;
        do{
            accessToken = facebookClient.obtainAppAccessToken(appId, appSecret);
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                //Do Nothing
            }
        }while(accessToken == null);
        appFacebookClient = new DefaultFacebookClient(accessToken.getAccessToken(), Version.VERSION_2_4);
    }

    public byte[] getProfilePictureSpringImpl(String accessToken){
        Facebook facebook = new FacebookTemplate(accessToken);
        return facebook.userOperations().getUserProfileImage(ImageType.LARGE);
    }

    public byte[] getProfilePicture(String accessToken){
        FacebookClient facebookClient = new DefaultFacebookClient(accessToken, Version.VERSION_2_4);
        try{
            User fbUser = (User)facebookClient.fetchObject("me", User.class);
            User.Picture picture = fbUser.getPicture();
            return fetchPicture(picture.getUrl());
        }catch (FacebookOAuthException e){
            return null;
        }
    }

    public ExtendedToken getLongLivedUserToken(String token){
        FacebookClient.AccessToken accessTokenObj = appFacebookClient.obtainExtendedAccessToken(appId, appSecret, token);
        String accessToken = accessTokenObj.getAccessToken();
        Date tokenExpiry = accessTokenObj.getExpires();
        return new ExtendedToken(accessToken, tokenExpiry);
    }

    public String getUserEmail(String accessToken){
        FacebookClient facebookClient = new DefaultFacebookClient(accessToken, Version.VERSION_2_4);
        try{
            User fbUser = (User)facebookClient.fetchObject("me", User.class, Parameter.with("fields", "email"));
            return fbUser.getEmail();
        }catch (FacebookOAuthException e){
            return null;
        }
    }

    public byte[] getUserProfilePicture(String userId, String fbImageType){

        try{
            User fbUser = appFacebookClient.fetchObject(userId, User.class, Parameter.with("fields", "email,picture.type("+fbImageType+")"));
            User.Picture picture = fbUser.getPicture();
            return fetchPicture(picture.getUrl());
        }catch (FacebookOAuthException e){
            return null;
        }

    }

    private byte[] fetchPicture(String urlStr) {
        try {
            URL url = new URL(urlStr);
            URLConnection connection = url.openConnection();
            InputStream is = connection.getInputStream();
            return IOUtils.toByteArray(is);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*public static void main(String[] args) throws IOException {
        SpzConfig spzConfig = new SpzConfig();
        FacebookHelper facebookHelper = new FacebookHelper(spzConfig);
        byte[] content = facebookHelper.getUserProfilePicture("10153483247871390");

        FileOutputStream fos = new FileOutputStream("C:/projects/better_place/Dumps/pic.jpg");
        fos.write(content);
    }*/
}
