package org.phoebus.olog.email;

import java.io.File;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;

/** Preference settings for tag to email list
 *  @author Conor Schofield
 */

@Component("tagEmailMapPreferences")
public class TagEmailMapPreferences
{
    private final String tagsToEmailsFilePath;
    private Logger logger = Logger.getLogger(TagEmailMapPreferences.class.getName());

    public TagEmailMapPreferences(@Value("${olog.tagsToEmailsFilePath:src/main/resources/tagsToEmail.json}") String tagsToEmailsFilePath) {
        this.tagsToEmailsFilePath = tagsToEmailsFilePath;
    }

    public final Map<String,List<String>> tagEmailMap() throws Exception
    {
        logger.log(Level.INFO, "Tag email file: " + tagsToEmailsFilePath);
        File tagsToEmailsFile = new File(tagsToEmailsFilePath);
        ObjectMapper objectMapper = new ObjectMapper();
        TagEmailMap tagEmailMap = objectMapper.readValue(tagsToEmailsFile, TagEmailMap.class);
        return tagEmailMap.getTagsToEmails();
    }
}