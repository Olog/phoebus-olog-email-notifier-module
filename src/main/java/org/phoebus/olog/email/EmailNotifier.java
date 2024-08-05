package org.phoebus.olog.email;

import org.phoebus.olog.entity.Log;
import org.phoebus.olog.notification.LogEntryNotifier;
import org.simplejavamail.MailException;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.config.ConfigLoader;
import org.simplejavamail.config.ConfigLoader.Property;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.springsupport.SimpleJavaMailSpringSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

import com.google.auto.service.AutoService;

import org.phoebus.olog.entity.Tag;
import org.phoebus.olog.entity.State;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

@AutoService(LogEntryNotifier.class)
@Component
@Import(SimpleJavaMailSpringSupport.class)
public class EmailNotifier implements LogEntryNotifier {
 
    private Logger logger = Logger.getLogger(EmailNotifier.class.getName());

    @Autowired
    private TagEmailMapPreferences tagEmailMapPreferences;
    
    @Autowired
    private Mailer mailer;

    private Map<String,List<String>> tagEmailMap;

    @Override
    public void notify(Log log) {
        Set<Tag> tags = log.getTags();
        List<String> emailList = new ArrayList<>();
        List<String> tagNames = new ArrayList<>();

        String logTitle = log.getTitle();
        String senderEmail = ConfigLoader.getStringProperty(Property.DEFAULT_FROM_ADDRESS);
        String senderName = ConfigLoader.getStringProperty(Property.DEFAULT_FROM_NAME);

        if (senderEmail == null) {
            logger.log(Level.SEVERE, "Default sender email was not set in properties file");
        }
        if (senderName == null) {
            logger.log(Level.SEVERE, "Default sender name was not set in properties file");
        }

        logger.log(Level.INFO,"Email From " + senderName + " " + senderEmail);
        logger.log(Level.INFO,"Logbook Email Started");

        // Get Tag to Email Map
        try {
            tagEmailMap = tagEmailMapPreferences.tagEmailMap();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "There was an error reading tag email JSON", e);
        } 

        // Send Email
        try {
            logger.log(Level.INFO, tagEmailMap.toString());
            for (Tag tag : tags) {
                if (tag.getState() == State.Active) {
                    String tagName = tag.getName();
                    tagNames.add(tagName);
                    emailList.addAll(tagEmailMap.get(tagName));
                }
            }
            Email logbookEmail = createLogEmail(emailList, senderName, senderEmail, logTitle, log.getDescription(), tagNames);
            if (!emailList.isEmpty()) {
                logger.log(Level.INFO, "Email being sent to: " + emailList);
                mailer.sendMail(logbookEmail);
            } else {
                logger.log(Level.INFO, "Email list is empty");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "There was an error sending email", e);
        }
    }

    public Email createLogEmail(List<String> emailList, String senderName, String senderEmail, String subject, String body, List<String> tagNames) {
        return EmailBuilder.startingBlank()
            .from(senderName , senderEmail)
            .toMultiple(emailList)
            .withSubject(subject)
            .withPlainText(tagNames.toString())
            .withPlainText(body)
            .buildEmail();
    }
 }