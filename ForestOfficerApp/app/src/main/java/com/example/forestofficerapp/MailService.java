package com.example.forestofficerapp;

import android.os.AsyncTask;

import java.util.Properties;

import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.activation.MailcapCommandMap;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class MailService extends AsyncTask<String, Void, Void> {

    private boolean check = false;
    private static final String username = "pratiknaik4799@gmail.com";
    private static final String password = "uzumakinaruto@4799";

    private void sendEmail(String OTP) {

        String textMessage = "Please enter this OTP in the Android application\n"+"OTP - "+OTP;

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });
        try {
            Thread.currentThread().setContextClassLoader( getClass().getClassLoader() );

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("pratiknaik4799@gmail.com"));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse("siddesh.pn23@gmail.com"));
            message.setSubject("OTP for Vanrakshak");
            message.setText(textMessage);

            MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
            mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
            mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
            mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
            mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
            mc.addMailcap("message/rfc822;; x-java-content- handler=com.sun.mail.handlers.message_rfc822");

//            MimeBodyPart messageBodyPart = new MimeBodyPart();
//
//            Multipart multipart = new MimeMultipart();
//
//            messageBodyPart = new MimeBodyPart();
//            String file = "path of file to be attached";
//            String fileName = "attachmentName";
//            FileDataSource source = new FileDataSource(file);
//            messageBodyPart.setDataHandler(new DataHandler((javax.activation.DataSource) source));
//            messageBodyPart.setFileName(fileName);
//            multipart.addBodyPart(messageBodyPart);
//
//            message.setContent(multipart);

            Transport.send(message);

            System.out.println("Done");

            check = true;

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected Void doInBackground(String[] strings) {
        sendEmail(strings[0]);
        return null;
    }

    public boolean getEmailStatus() {
        return check;
    }
}
