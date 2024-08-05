package org.phoebus.olog.email;

import org.phoebus.olog.entity.Log;
import org.phoebus.olog.notification.LogEntryNotifier;
import org.simplejavamail.MailException;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
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

    @Override
    public void notify(Log log) {
        Set<Tag> tags = log.getTags();
        List<String> emailList = new ArrayList<>();

        String logTitle = log.getTitle();
        logger.log(Level.INFO,"Logbook Email Started");
        try {
            Map<String,List<String>> tagEmailMap = tagEmailMapPreferences.tagEmailMap();
            logger.log(Level.INFO, tagEmailMap.toString());
            for (Tag tag : tags) {
                if (tag.getState() == State.Active) {
                    String tagName = tag.getName();
                    emailList.addAll(tagEmailMap.get(tagName));
                }
            }
            Email logbookEmail = createEmail(emailList, logTitle, "test");
            if (!emailList.isEmpty()) {
                mailer.sendMail(logbookEmail);
            } else {
                logger.log(Level.INFO, "Email list is empty");
            }
        } catch (MailException e) {
            logger.log(Level.SEVERE, "There was an error sending email", e);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "There was an error reading tag email JSON", e);
        } 
    }

    public Email createEmail(List<String> emailList, String subject, String body) {
        return EmailBuilder.startingBlank().from("conorschofield@lbl.gov")
            .toMultiple(emailList)
            .withSubject(subject)
            .withPlainText(body)
            .buildEmail();
    }
 }