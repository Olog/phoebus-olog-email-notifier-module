package org.phoebus.olog.email;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TagEmailMapPreferencesTest {

    private static final String TEST_JSON_FILE_PATH = "src/test/resources/tagsToEmailTest.json";
    TagEmailMapPreferences testEmailMapPreferences = new TagEmailMapPreferences(TEST_JSON_FILE_PATH);

    @Test
    public void testTagEmailMap_Success() throws Exception {
        // Act
        Map<String, List<String>> tagEmailMap = testEmailMapPreferences.tagEmailMap();

        // Assert
        assertNotNull(tagEmailMap);
        assertEquals(2, tagEmailMap.size());
        assertTrue(tagEmailMap.containsKey("tag1"));
        assertTrue(tagEmailMap.containsKey("tag2"));
        assertEquals(List.of("email1@example.com"), tagEmailMap.get("tag1"));
        assertEquals(List.of("email2@example.com", "email3@example.com"), tagEmailMap.get("tag2"));
    }

    @Test
    public void testTagEmailMap_FileNotFound() {
        // Arrange
        TagEmailMapPreferences  tagEmailMapPreferencesNoFile = new TagEmailMapPreferences("nonexistent_file.json");

        // Act & Assert
        assertThrows(Exception.class, () -> tagEmailMapPreferencesNoFile.tagEmailMap());
    }
}
