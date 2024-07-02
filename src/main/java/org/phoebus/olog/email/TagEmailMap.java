package org.phoebus.olog.email;

import java.util.List;
import java.util.Map;

public class TagEmailMap {
    private Map<String, List<String>> tagToEmails;

    public Map<String, List<String>> getTagsToEmails() {
        return tagToEmails;
    }

    public void setTags(Map<String, List<String>> tagToEmails) {
        this.tagToEmails = tagToEmails;
    }
}
