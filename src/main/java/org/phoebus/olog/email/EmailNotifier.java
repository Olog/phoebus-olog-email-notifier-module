package org.phoebus.olog.email;

import org.phoebus.olog.entity.Log;
import org.phoebus.olog.notification.LogEntryNotifier;

import org.phoebus.email.EmailService;
import org.phoebus.email.EmailPreferences;
import org.phoebus.olog.email.TagEmailMapPreferences;
import org.phoebus.olog.entity.Tag;
import org.phoebus.olog.entity.State;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.MessagingException;
 
public class EmailNotifier implements LogEntryNotifier {
 
    private Logger logger = Logger.getLogger(EmailNotifier.class.getName());

    @Override
    public void notify(Log log) {
        Set<Tag> tags = log.getTags();
        List<String> emailList = new ArrayList<>();

        String logTitle = log.getTitle();
        String sender = EmailPreferences.username + "@" + EmailPreferences.mailhost;
        System.out.println(logTitle);
        try {
            Map<String,List<String>> tagEmailMap = TagEmailMapPreferences.tagEmailMap();
            for (Tag tag : tags) {
                if (tag.getState() == State.Active) {
                    String tagName = tag.getName();
                    emailList.addAll(tagEmailMap.get(tagName));
                }
            }
            if (EmailPreferences.isEmailSupported()) {
                for (String emailStr : emailList) {
                    EmailService.send(emailStr, sender, "Olog Email Notifier Test", logTitle);
                }
            }
        } catch (MessagingException e) {
            logger.log(Level.SEVERE, "There was an error sending an email with the logbook", e);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "There was an error reading tag email JSON", e);
        }
    }
 }