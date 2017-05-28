package fk.retail.ip.core;

/**
 * Created by nidhigupta.m on 27/04/17.
 */
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;

@Slf4j
public class MailSender {
    public static void sendMail(String to, String from, String cc, String host,
                                String subject, String body,
                                String attachmentPath) throws Exception{

        Properties properties = System.getProperties();
        properties.setProperty("mail.smtp.host", host);

        Session session = Session.getDefaultInstance(properties);

        if(to == null || from == null || host ==null){
            throw new IllegalArgumentException(
                    "to, from or host can't be null");
        }

        try {
            //log.info("Sending mail to : {} with cc : {}", to, cc);

            MimeMessage mimeMessage = new MimeMessage(session);
            mimeMessage.setFrom(new InternetAddress(from));
            mimeMessage.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(to));
            mimeMessage.addRecipient(Message.RecipientType.CC,
                    new InternetAddress(cc));
            mimeMessage.setSubject(subject);

            Multipart multipart = new MimeMultipart();

            //text body
            BodyPart bodyPart = new MimeBodyPart();
            bodyPart.setText(body);

            multipart.addBodyPart(bodyPart);

            //attachment
            if (attachmentPath != null) {
                log.info("Path of attachment is : {}", attachmentPath);
                bodyPart = new MimeBodyPart();
                DataSource source = new FileDataSource(attachmentPath);
                bodyPart.setDataHandler(new DataHandler(source));
                String[] filePathArr = attachmentPath.split("/");
                bodyPart.setFileName(filePathArr[filePathArr.length - 1]);
                multipart.addBodyPart(bodyPart);
                mimeMessage.setContent(multipart);
            }
            Transport.send(mimeMessage);
            log.info("Mail sent");
        }catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }

}
