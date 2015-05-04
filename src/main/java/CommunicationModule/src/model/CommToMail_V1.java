package CommunicationModule.src.model;

import java.io.*;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

/**
 * Created by NAOR on 06/04/2015.
 */
public class CommToMail_V1 extends CommOfficial_V1  {

    String emailAddress = null;

    private final String username = "ercserver@gmail.com";
    private final String password = "serverpassword123";
    private final String host = "smtp.gmail.com";

    //C'tor
    public CommToMail_V1(HashMap<String,String> data){
        super(data);
        emailAddress = data.get("Email");
    }

    public void sendMessage() {

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
