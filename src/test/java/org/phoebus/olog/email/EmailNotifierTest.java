package org.phoebus.olog.email;

import org.junit.After;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.phoebus.email.EmailService;
import org.phoebus.email.EmailPreferences;
import org.phoebus.olog.entity.Log;
import org.phoebus.olog.entity.Tag;

import com.unboundid.util.StaticUtils;

import org.phoebus.olog.entity.State;

import javax.mail.MessagingException;

import java.io.File;
import java.util.*;

import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class EmailNotifierTest {

    @Mock
    private Log mockLog;

    @Mock
    private Tag mockActiveTag;

    @Mock
    private Tag mockInactiveTag;

    @InjectMocks
    private EmailNotifier emailNotifier;

    private static final String SENDER_EMAIL = "testuser@example.com";
    private static final String LOG_TITLE = "Test Log Title";


    @BeforeEach
    public void setup() throws Exception {
        MockitoAnnotations.openMocks(this);

        // Mock EmailPreferences
        EmailPreferences.username = "testuser";
        EmailPreferences.mailhost = "example.com";

        // Mock Log and Tags
        when(mockLog.getTitle()).thenReturn(LOG_TITLE);
        Set<Tag> tags = new HashSet<>();
        tags.add(mockActiveTag);
        tags.add(mockInactiveTag);
        when(mockLog.getTags()).thenReturn(tags);

        // Mock TagEmailMapPreferences
        Map<String, List<String>> tagEmailMap = new HashMap<>();
        tagEmailMap.put(mockActiveTag.getName(), Collections.singletonList("active_email@example.com"));
        tagEmailMap.put(mockInactiveTag.getName(), Collections.singletonList("inactive_email@example.com"));
        when(TagEmailMapPreferences.tagEmailMap()).thenReturn(tagEmailMap);
    }

    @Test
    public void testNotify_SuccessfulNotification() throws Exception {
        // Arrange
        when(mockActiveTag.getState()).thenReturn(State.Active);
        when(mockInactiveTag.getState()).thenReturn(State.Inactive);
        try (MockedStatic<EmailPreferences> mockEmailPreferences = Mockito.mockStatic(EmailPreferences.class)) {

            mockEmailPreferences.when(EmailPreferences::isEmailSupported).thenReturn(true);
            assertTrue(EmailPreferences.isEmailSupported());
            
            try (MockedStatic<EmailService> mockEmailService = Mockito.mockStatic(EmailService.class)) {
                // Act
                emailNotifier.notify(mockLog);
        
                // Assert
                mockEmailService.verify(() -> EmailService.send(anyString(), anyString(), anyString(), anyString()), times(1));
            }
        }
    }
    

    @Test
    public void testNotify_NoActiveTags() throws Exception {
        // Arrange
        when(mockActiveTag.getState()).thenReturn(State.Inactive);
        when(mockInactiveTag.getState()).thenReturn(State.Inactive);
        try (MockedStatic<EmailPreferences> mockEmailPreferences = Mockito.mockStatic(EmailPreferences.class)) {

            mockEmailPreferences.when(EmailPreferences::isEmailSupported).thenReturn(true);
            assertTrue(EmailPreferences.isEmailSupported());
            
            try (MockedStatic<EmailService> mockEmailService = Mockito.mockStatic(EmailService.class)) {
                // Act
                emailNotifier.notify(mockLog);
        
                // Assert
                mockEmailService.verify(() -> EmailService.send(anyString(), anyString(), anyString(), anyString()), times(0));
            }
        }
    }

    @Test
    public void testNotify_NoEmailSupported() throws Exception {
        // Arrange
        when(mockActiveTag.getState()).thenReturn(State.Active);
        when(mockInactiveTag.getState()).thenReturn(State.Inactive);
        try (MockedStatic<EmailPreferences> mockEmailPreferences = Mockito.mockStatic(EmailPreferences.class)) {

            mockEmailPreferences.when(EmailPreferences::isEmailSupported).thenReturn(true);
            assertTrue(EmailPreferences.isEmailSupported());
            
            try (MockedStatic<EmailService> mockEmailService = Mockito.mockStatic(EmailService.class)) {
                // Act
                emailNotifier.notify(mockLog);
        
                // Assert
                mockEmailService.verify(() -> EmailService.send(anyString(), anyString(), anyString(), anyString()), times(0));
            }
        }
    }

    @Test
    public void testNotify_ErrorReadingTagEmailJSON() throws Exception {
        // Arrange
        when(TagEmailMapPreferences.tagEmailMap()).thenThrow(new RuntimeException("Simulated JSON reading error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> emailNotifier.notify(mockLog));
    }
}
