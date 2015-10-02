package com.spouzee.server.api.social;

/**
 * Created by Sagar on 9/17/2015.
 */
import com.spouzee.server.api.config.SpzConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Component
public class EMailHelper {

    private Session session;
    private static String username;
    private static String password;
    private static String inviteURL;
    private static final String emailBody;

    static{
        emailBody = "<div>" +
                        "<h4>Greetings from Spouzee!</h4>" +
                        "<p>%s has invited you to try Spouzee. Click the below link to start your quest for a happy future with a truly compatible life partner!</p>" +
                        "<a href=\"%s\" target=\"_blank\">%s</a>" +
                        "<br>" +
                        "<h4>Spouzee provides insights into the many aspects of a prospective match,<br>so you can make informed decisions about your life partner.</h4>" +
                    "</div>";
    }

    @Autowired
    public EMailHelper(SpzConfig spzConfig){
        inviteURL = spzConfig.getStringProperty("mail.inviteURL");
        Properties props = new Properties();
        props.put("mail.smtp.auth", spzConfig.getStringProperty("mail.smtp.auth"));
        props.put("mail.smtp.starttls.enable", spzConfig.getStringProperty("mail.smtp.starttls.enable"));
        props.put("mail.smtp.host", spzConfig.getStringProperty("mail.smtp.host"));
        props.put("mail.smtp.port", spzConfig.getStringProperty("mail.smtp.port"));
        username = spzConfig.getStringProperty("mail.username");
        password = spzConfig.getStringProperty("mail.password");

        session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });
    }

    public void sendInvite(String fromUserName, String toEmailId){
        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(toEmailId));
            message.setSubject("Spouzee Invite from " + fromUserName);
            String url = inviteURL + toEmailId;
            String htmlBody = String.format(emailBody, fromUserName, url, url);
            message.setContent(htmlBody, "text/html");

            Transport.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        SpzConfig spzConfig = new SpzConfig();
        EMailHelper eMailHelper = new EMailHelper(spzConfig);
        eMailHelper.sendInvite("Maaaaaya", "nayansagar008@gmail.com");
    }
}
