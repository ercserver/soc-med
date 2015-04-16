package CommunicationModule.src.model;

import CommunicationModule.src.api.ICommToMail_model;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

/**
 * Created by NAOR on 06/04/2015.
 */
public class CommToMail_V1 implements ICommToMail_model {

    String emailAddress = null;
    String msgToSend = null;
    String subject = null;
    private final String username = "ercserver@gmail.com";
    private final String password = "serverpassword123";
    private final String host = "smtp.gmail.com";

    //C'tor
    public CommToMail_V1(String eml, String msg, String sub){
        emailAddress = eml;
        msgToSend = msg;
        subject = sub;
    }

    @Override
    public void sendEmail() {

        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.host",host);
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        props.put("mail.user", username);
        props.put("mail.password", password);

        Session session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try{
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(username, "ERC server"));
            InternetAddress receipientsAdress = new InternetAddress();
            receipientsAdress = new InternetAddress(emailAddress);
            msg.setRecipient(Message.RecipientType.TO, receipientsAdress);
            msg.setSubject(subject);
            msg.setText(msgToSend);

            Transport.send(msg);
        }catch (MessagingException mex){
            mex.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

}
