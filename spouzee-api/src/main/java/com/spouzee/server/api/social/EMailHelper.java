package com.spouzee.server.api.social;

/**
 * Created by Sagar on 9/17/2015.
 */
import com.spouzee.server.api.config.SpzConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.URLDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

@Component
public class EMailHelper {

    private Session session;
    private static String username;
    private static String password;
    private static String inviteURL;
    private static String siteURL;
    private static String adminEmail;
    private static final String inviteEmailBody;
    private static final String profileMatchedEmailBody;

    private static final String identityRequestedEmailBody;
    private static final String identityRevealedEmailBody;
    private static final String contactRequestedEmailBody;
    private static final String contactRevealedEmailBody;
    private static final String solicitationEmailBody;

    static{
        inviteEmailBody = "<div>" +
                        "<h4>Greetings from Spouzee!</h4>" +
                        "<p>%s has invited you to try Spouzee. Click the below link to start your quest for a happy future with a truly compatible life partner!</p>" +
                        "<a href=\"%s\" target=\"_blank\">%s</a>" +
                        "<br>" +
                        "<h4>Spouzee provides insights into the many aspects of a prospective match,<br>so you can make informed decisions about your life partner.</h4>" +
                    "</div>";

        identityRequestedEmailBody = "<div>" +
                "<h4>Greetings from Spouzee!</h4>" +
                "<p>Someone has expressed interest in your profile on Spouzee and has requested to reveal your identity.</p>" +
                "<p>Please visit <a href=\"%s\" target=\"_blank\">spouzee.com</a> for details.</p>" +
                "<br>" +
                "<h4>Spouzee provides insights into the many aspects of a prospective match,<br>so you can make informed decisions about your life partner.</h4>" +
                "</div>";

        identityRevealedEmailBody = "<div>" +
                "<h4>Greetings from Spouzee!</h4>" +
                "<p>%s has revealed identity to you.</p>" +
                /*"<img src=\"cid:image\">" +*/
                "<p>Please visit <a href=\"%s\" target=\"_blank\">spouzee.com</a> for details.</p>" +
                "<br>" +
                "<h4>Spouzee provides insights into the many aspects of a prospective match,<br>so you can make informed decisions about your life partner.</h4>" +
                "</div>";

        contactRequestedEmailBody = "<div>" +
                "<h4>Greetings from Spouzee!</h4>" +
                "<p>%s wants to view your contact info!</p>" +
                /*"<img src=\"cid:image\">" +*/
                "<p>Please visit <a href=\"%s\" target=\"_blank\">spouzee.com</a> for details.</p>" +
                "<br>" +
                "<h4>Spouzee provides insights into the many aspects of a prospective match,<br>so you can make informed decisions about your life partner.</h4>" +
                "</div>";

        contactRevealedEmailBody = "<div>" +
                "<h4>Greetings from Spouzee!</h4>" +
                "<p>%s has shared contact details with you!</p>" +
                /*"<img src=\"cid:image\">" +*/
                "<h3>You can email %s at %s</h3>" +
                "<br>" +
                "<p>Best,</p>" +
                "<p>Spouzee</p>" +
                "</div>";

        profileMatchedEmailBody = "<div>" +
                "<h4>Greetings from Spouzee!</h4>" +
                "<p>We've found a match for you!</p>" +
                "<h3>%s</h3>" +
                /*"<img src=\"cid:image\">" +*/
                "<p>Please visit <a href=\"%s\" target=\"_blank\">spouzee.com</a> for details.</p>" +
                "<br>" +
                "<h4>Spouzee provides insights into the many aspects of a prospective match,<br>so you can make informed decisions about your life partner.</h4>" +
                "</div>";

        solicitationEmailBody = "<div>" +
                "<h4><font color=\"#1e8eb8\" size=\"2\">Greetings from Spouzee!</font></h4>" +
                "<p><font color=\"#1e8eb8\" size=\"2\">We are writing to invite you to try Spouzee.</font></p>" +
                "<h3><font color=\"#1e8eb8\" size=\"2\">Spouzee provides insights into many aspects of a prospective match,\n" +
                "so you can make informed decisions about your life partner</font></h3>" +
                "<h4><font color=\"#1e8eb8\" size=\"2\">Here is why you should give it a try</font></h4>" +
                "<ul>"+
                "<li><font color=\"#1e8eb8\" size=\"2\">Create your questionnaire and get all the information you need from a prospective match</font></li>"+
                "<li><font color=\"#1e8eb8\" size=\"2\">Answer questions to help prospective matches get an idea about you</font></li>"+
                "<li><font color=\"#1e8eb8\" size=\"2\">Get match suggestions from our unique matching algorithm</font></li>"+
                "<li><font color=\"#1e8eb8\" size=\"2\">Be in control of when and with whom you share your contact information</font></li>"+
                "<li><font color=\"#1e8eb8\" size=\"2\">No random browsing of profiles</font></li>"+
                "<li><font color=\"#1e8eb8\" size=\"2\">Invite alliances you receive offline to Spouzee and avoid all the awkwardness/dilemma involved in eliciting information</font></li>"+
                "<li><font color=\"#1e8eb8\" size=\"2\">Benefit from the collective intelligence - Get access to questions created by all our users</font></li>"+
                "</ul>"+
                "<h4><font color=\"#1e8eb8\" size=\"2\">Hope to see you soon!</font></h4>" +
                "<a href=\"https://spouzee.com\" target=\"_blank\"><font color=\"#1e8eb8\" size=\"3\"><b>Visit Spouzee</b></font></a>" +
                "<br>" +
                "<p><font color=\"#1e8eb8\" size=\"2\">Best,</font></p>" +
                "<p><font color=\"#1e8eb8\" size=\"2\">Team Spouzee</font></p>" +
                "</div>";
    }

    @Autowired
    public EMailHelper(SpzConfig spzConfig){
        inviteURL = spzConfig.getStringProperty("mail.inviteURL");
        siteURL = spzConfig.getStringProperty("mail.siteURL");
        Properties props = new Properties();
        props.put("mail.smtp.auth", spzConfig.getStringProperty("mail.smtp.auth"));
        props.put("mail.smtp.starttls.enable", spzConfig.getStringProperty("mail.smtp.starttls.enable"));
        props.put("mail.smtp.host", spzConfig.getStringProperty("mail.smtp.host"));
        props.put("mail.smtp.port", spzConfig.getStringProperty("mail.smtp.port"));
        username = spzConfig.getStringProperty("mail.username");
        password = spzConfig.getStringProperty("mail.password");
        adminEmail = spzConfig.getStringProperty("mail.adminEmail");

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
            String htmlBody = String.format(inviteEmailBody, fromUserName, url, url);
            message.setContent(htmlBody, "text/html");

            Transport.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public void notifyIdentityRequest(String email, String name, long id) {
        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(email));
            message.setSubject("Someone has expressed interest in your Spouzee profile");
            String htmlBody = String.format(identityRequestedEmailBody, name, siteURL);


            MimeMultipart multipart = new MimeMultipart("related");

            // first part  (the html)
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(htmlBody, "text/html");

            // add it
            multipart.addBodyPart(messageBodyPart);

            // second part (the image)
            //messageBodyPart = new MimeBodyPart();
            //messageBodyPart.setDataHandler(new DataHandler(new URLDataSource(new URL(siteURL + "/v1/users/" + id + "/picture"))));
            //messageBodyPart.setDataHandler(new DataHandler(new URLDataSource(new URL("https://spouzee.com/spouzee-api/v1/users/155/picture"))));
            //messageBodyPart.setHeader("Content-ID", "<image>");
            //messageBodyPart.setDisposition(MimeBodyPart.INLINE);

            // add it
            //multipart.addBodyPart(messageBodyPart);

            // put everything together
            message.setContent(multipart);

            //message.setContent(htmlBody, "text/html");

            Transport.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public void notifyMatch(String email, String name, long id) {
        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(email));
            message.setSubject("You have a match on Spouzee");
            String htmlBody = String.format(profileMatchedEmailBody, name, siteURL);


            MimeMultipart multipart = new MimeMultipart("related");

            // first part  (the html)
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(htmlBody, "text/html");

            // add it
            multipart.addBodyPart(messageBodyPart);

            // second part (the image)
            /*messageBodyPart = new MimeBodyPart();
            messageBodyPart.setDataHandler(new DataHandler(new URLDataSource(new URL(siteURL + "/v1/users/" + id + "/picture"))));
            //messageBodyPart.setDataHandler(new DataHandler(new URLDataSource(new URL("https://spouzee.com/spouzee-api/v1/users/155/picture"))));
            messageBodyPart.setHeader("Content-ID", "<image>");
            messageBodyPart.setDisposition(MimeBodyPart.INLINE);

            // add it
            multipart.addBodyPart(messageBodyPart);*/

            // put everything together
            message.setContent(multipart);

            //message.setContent(htmlBody, "text/html");

            Transport.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public void notifyContactRevealed(String toAddress, String name, String emailId, long id) {
        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(toAddress));
            message.setSubject(name+ " has shared contact info");
            String htmlBody = String.format(contactRevealedEmailBody, name, name, emailId);


            MimeMultipart multipart = new MimeMultipart("related");

            // first part  (the html)
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(htmlBody, "text/html");

            // add it
            multipart.addBodyPart(messageBodyPart);

            // second part (the image)
            /*messageBodyPart = new MimeBodyPart();
            messageBodyPart.setDataHandler(new DataHandler(new URLDataSource(new URL(siteURL + "/v1/users/" + id + "/picture"))));
            //messageBodyPart.setDataHandler(new DataHandler(new URLDataSource(new URL("https://spouzee.com/spouzee-api/v1/users/155/picture"))));
            messageBodyPart.setHeader("Content-ID", "<image>");
            messageBodyPart.setDisposition(MimeBodyPart.INLINE);

            // add it
            multipart.addBodyPart(messageBodyPart);*/

            // put everything together
            message.setContent(multipart);

            //message.setContent(htmlBody, "text/html");

            Transport.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public void notifyUserCreationToAdmin(String name){
        try {
            String emailBody = "<div>" +
                    "<h4>"+name+" has created account</h4>"+
                    "</div>";
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(adminEmail));
            message.setSubject(name + " has created account");
            message.setContent(emailBody, "text/html");

            Transport.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        SpzConfig spzConfig = new SpzConfig();
        EMailHelper eMailHelper = new EMailHelper(spzConfig);
        //eMailHelper.notifyInterest("nayansagar008@gmail.com", "Maiya", 158);
        eMailHelper.sendSolicitationEmail("nayansagar008@gmail.com");
        //eMailHelper.sendInvite("maiya", "nayansagar008@gmail.com");
    }

    public void sendSolicitationEmail(String emailId){
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(adminEmail));
            message.setSubject("You are invited to Spouzee");
            message.setContent(solicitationEmailBody, "text/html");

            Transport.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendSolicitationEmail1(String emailId){
        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(emailId));
            message.setSubject("You are invited to Spouzee");
            String htmlBody = String.format(solicitationEmailBody, siteURL);


            MimeMultipart multipart = new MimeMultipart("related");

            // first part  (the html)
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(htmlBody, "text/html");

            // add it
            multipart.addBodyPart(messageBodyPart);

            // second part (the image)
            /*messageBodyPart = new MimeBodyPart();
            messageBodyPart.setDataHandler(new DataHandler(new URLDataSource(new URL(siteURL))));
            //messageBodyPart.setDataHandler(new DataHandler(new URLDataSource(new URL("https://spouzee.com/spouzee-api/v1/users/155/picture"))));
            messageBodyPart.setHeader("Content-ID", "<image>");
            messageBodyPart.setDisposition(MimeBodyPart.INLINE);

            // add it
            multipart.addBodyPart(messageBodyPart);*/

            // put everything together
            message.setContent(multipart);

            //message.setContent(htmlBody, "text/html");

            Transport.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public void notifyIdentityRevealed(String toAddress, String name, String emailId, long id) {
        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(toAddress));
            message.setSubject(name+ " has revealed identity");
            String htmlBody = String.format(identityRevealedEmailBody, name, name, emailId);


            MimeMultipart multipart = new MimeMultipart("related");

            // first part  (the html)
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(htmlBody, "text/html");

            // add it
            multipart.addBodyPart(messageBodyPart);

            // second part (the image)
            /*messageBodyPart = new MimeBodyPart();
            messageBodyPart.setDataHandler(new DataHandler(new URLDataSource(new URL(siteURL + "/v1/users/" + id + "/picture"))));
            //messageBodyPart.setDataHandler(new DataHandler(new URLDataSource(new URL("https://spouzee.com/spouzee-api/v1/users/155/picture"))));
            messageBodyPart.setHeader("Content-ID", "<image>");
            messageBodyPart.setDisposition(MimeBodyPart.INLINE);

            // add it
            multipart.addBodyPart(messageBodyPart);*/

            // put everything together
            message.setContent(multipart);

            //message.setContent(htmlBody, "text/html");

            Transport.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public void notifyContactRequest(String email, String name, long id) {
        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(email));
            message.setSubject(name + " wants to contact you!");
            String htmlBody = String.format(contactRequestedEmailBody, name, siteURL);


            MimeMultipart multipart = new MimeMultipart("related");

            // first part  (the html)
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(htmlBody, "text/html");

            // add it
            multipart.addBodyPart(messageBodyPart);

            // second part (the image)
            /*messageBodyPart = new MimeBodyPart();
            messageBodyPart.setDataHandler(new DataHandler(new URLDataSource(new URL(siteURL + "/v1/users/" + id + "/picture"))));
            //messageBodyPart.setDataHandler(new DataHandler(new URLDataSource(new URL("https://spouzee.com/spouzee-api/v1/users/155/picture"))));
            messageBodyPart.setHeader("Content-ID", "<image>");
            messageBodyPart.setDisposition(MimeBodyPart.INLINE);

            // add it
            multipart.addBodyPart(messageBodyPart);*/

            // put everything together
            message.setContent(multipart);

            //message.setContent(htmlBody, "text/html");

            Transport.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
