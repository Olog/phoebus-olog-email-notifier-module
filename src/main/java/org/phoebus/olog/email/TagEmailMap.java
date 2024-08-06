package org.phoebus.olog.email;

import java.util.List;
import java.util.Map;

public class TagEmailMap {
    private Map<String, List<String>> tagsToEmails;

    public Map<String, List<String>> getTagsToEmails() {
        return tagsToEmails;
    }

    public void setTags(Map<String, List<String>> tagsToEmails) {
        this.tagsToEmails = tagsToEmails;
    }
}
