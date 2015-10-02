package com.spouzee;

import com.spouzee.server.api.schema.UserDetails;
import com.spouzee.server.api.schema.UserToken;
import com.spouzee.server.api.schema.common.Enums;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Sagar on 8/3/2015.
 */
public class UtilTest {

    public static void main(String[] args) throws IOException {
        UserDetails userDetails = new UserDetails();

        userDetails.setCaste("abc");
        //userDetails.setDateOfBirth(Calendar.getInstance());
        userDetails.setEmployment("ghi");
        userDetails.setLanguage("Kannada");
        userDetails.setName("haha");
        userDetails.setQualification("sslc");
        userDetails.setReligion("adu");
        userDetails.setRole("idu");
        userDetails.setSubcaste("innondu");

        UserToken tw = new UserToken();
        tw.setCreatedTime(Calendar.getInstance());
        tw.setAccessToken("FirstAccessToken");
        tw.setLinkType(Enums.LinkType.FACEBOOK);
        tw.setLinkTypeUserID("fb:123");
        tw.setTokenValidityInMillis(2000000);

        List<UserToken> userTokenList = new ArrayList<UserToken>();
        userTokenList.add(tw);
        userDetails.setUserTokenList(userTokenList);

        ObjectMapper mapper = new ObjectMapper();
        System.out.println(mapper.writeValueAsString(userDetails));
    }
}
