/**
 * Copyright (C) 2021 European Spallation Source ERIC.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package org.phoebus.olog.email;

import org.phoebus.olog.entity.Log;
import org.phoebus.olog.notification.LogEntryNotifier;
import org.phoebus.email.EmailService;
import org.phoebus.email.EmailPreferences;
 
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.MessagingException;
 
public class EmailNotifier implements LogEntryNotifier {
 
    private Logger logger = Logger.getLogger(EmailNotifier.class.getName());
 
    public EmailNotifier(){
        
    }

    @Override
    public void notify(Log log) {
        Set tags = log.getTags();
        String logTitle = log.getTitle();
        String sender = EmailPreferences.username + "@" + EmailPreferences.mailhost;
        System.out.println(logTitle);
        try {
            if (EmailPreferences.isEmailSupported()) {
                EmailService.send("conorschofield@lbl.gov", sender, "Olog Email Notifier Test", logTitle);
            }
        } catch (MessagingException e) {
            logger.log(Level.SEVERE, "There was an error sending an email with the logbook", e);
        }
    }
 }