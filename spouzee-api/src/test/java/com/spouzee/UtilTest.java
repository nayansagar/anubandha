package com.spouzee;

import com.spouzee.server.api.schema.UserDetails;
import com.spouzee.server.api.schema.UserToken;
import com.spouzee.server.api.schema.common.Enums;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Sagar on 8/3/2015.
 */
public class UtilTest {

    public static void main2(String[] args) throws IOException {
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

    public static void main(String[] args) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        try
        {
            SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            System.out.println(dateFormat1.format(dateFormat2.parse("2015-10-04 00:09:22")));

            /*java.util.Date date = simpleDateFormat.parse("20/06/1987");

            System.out.println("date : "+date);*/
        }
        catch (ParseException ex)
        {
            System.out.println("Exception "+ex);
        }

    }
}
