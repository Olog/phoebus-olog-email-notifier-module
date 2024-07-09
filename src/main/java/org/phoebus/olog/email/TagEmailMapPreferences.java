package org.phoebus.olog.email;

import java.io.File;
import java.util.Map;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.phoebus.framework.preferences.AnnotatedPreferences;
import org.phoebus.framework.preferences.Preference;

/** Preference settings for tag to email list
 *  @author Conor Schofield
 */
public class TagEmailMapPreferences
{
    @Preference public static File tagsToEmailsFile;

    public static final Map<String,List<String>> tagEmailMap() throws Exception
    {
        ObjectMapper objectMapper = new ObjectMapper();
        TagEmailMap tagEmailMap = objectMapper.readValue(tagsToEmailsFile, TagEmailMap.class);
        return tagEmailMap.getTagsToEmails();
    }

    static
    {
    	AnnotatedPreferences.initialize(TagEmailMap.class, "/tag_emails.properties");
    }
}