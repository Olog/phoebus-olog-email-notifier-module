package org.phoebus.olog.email;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TagEmailMapPreferencesTest {

    private static final String TEST_JSON_FILE_PATH = "src/test/resources/tagsToEmailTest.json";

    @BeforeEach
    public void setup() {
        TagEmailMapPreferences.tagsToEmailsFile = new File(TEST_JSON_FILE_PATH);
    }

    @Test
    public void testTagEmailMap_Success() throws Exception {
        // Act
        Map<String, List<String>> tagEmailMap = TagEmailMapPreferences.tagEmailMap();

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
        TagEmailMapPreferences.tagsToEmailsFile = new File("nonexistent_file.json");

        // Act & Assert
        assertThrows(Exception.class, () -> TagEmailMapPreferences.tagEmailMap());
    }
}
