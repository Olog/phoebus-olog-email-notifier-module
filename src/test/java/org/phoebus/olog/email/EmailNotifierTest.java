package org.phoebus.olog.email;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.phoebus.olog.entity.Log;
import org.phoebus.olog.entity.Tag;
import org.phoebus.olog.entity.State;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.config.ConfigLoader;
import org.simplejavamail.config.ConfigLoader.Property;
import org.junit.Assert; 

import org.simplejavamail.MailException;

public class EmailNotifierTest {

    @InjectMocks
    private EmailNotifier emailNotifier;

    @Mock
    private TagEmailMapPreferences tagEmailMapPreferences;

    @Mock
    private Mailer mailer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testNotify_SuccessfulEmailSent() throws Exception {
        // Given
        Log mockLog = mock(Log.class);
        Tag activeTag = new Tag("important", State.Active);
        Set<Tag> tags = new HashSet<>(Collections.singletonList(activeTag));
        when(mockLog.getTags()).thenReturn(tags);
        when(mockLog.getTitle()).thenReturn("Test Log");
        Map<String, List<String>> tagEmailMap = new HashMap<>();
        tagEmailMap.put("important", Arrays.asList("test@example.com"));
        when(tagEmailMapPreferences.tagEmailMap()).thenReturn(tagEmailMap);

        // When
        try (MockedStatic<ConfigLoader> mockConfigLoader = mockStatic(ConfigLoader.class)) {
            mockConfigLoader.when(() -> ConfigLoader.getStringProperty(Property.DEFAULT_FROM_ADDRESS)).thenReturn("from@example.com");
            mockConfigLoader.when(() -> ConfigLoader.getStringProperty(Property.DEFAULT_FROM_NAME)).thenReturn("from");
            emailNotifier.notify(mockLog);
        }

        // Then
        verify(mailer).sendMail(any(Email.class));
    }

    @Test
    void testNotify_NoEmailsForInactiveTag() throws Exception {
        // Given
        Log mockLog = mock(Log.class);
        Tag inactiveTag = new Tag("trivial", State.Inactive);
        Set<Tag> tags = new HashSet<>(Collections.singletonList(inactiveTag));
        when(mockLog.getTags()).thenReturn(tags);
        when(mockLog.getTitle()).thenReturn("Test Log");

        // When
        try (MockedStatic<ConfigLoader> mockConfigLoader = mockStatic(ConfigLoader.class)) {
            mockConfigLoader.when(() -> ConfigLoader.getStringProperty(Property.DEFAULT_FROM_ADDRESS)).thenReturn("from@example.com");
            mockConfigLoader.when(() -> ConfigLoader.getStringProperty(Property.DEFAULT_FROM_NAME)).thenReturn("from");
            emailNotifier.notify(mockLog);
        }

        // Then
        verify(mailer, never()).sendMail(any(Email.class));
    }

    @Test
    void testNotify_EmailListIsEmpty() throws Exception {
        // Given
        Log mockLog = mock(Log.class);
        Tag activeTag = new Tag("nonexistent", State.Active);
        Set<Tag> tags = new HashSet<>(Collections.singletonList(activeTag));
        when(mockLog.getTags()).thenReturn(tags);
        when(mockLog.getTitle()).thenReturn("Test Log");

        Map<String, List<String>> tagEmailMap = new HashMap<>();
        when(tagEmailMapPreferences.tagEmailMap()).thenReturn(tagEmailMap);

        // When
        try (MockedStatic<ConfigLoader> mockConfigLoader = mockStatic(ConfigLoader.class)) {
            mockConfigLoader.when(() -> ConfigLoader.getStringProperty(Property.DEFAULT_FROM_ADDRESS)).thenReturn("from@example.com");
            mockConfigLoader.when(() -> ConfigLoader.getStringProperty(Property.DEFAULT_FROM_NAME)).thenReturn("from");
            emailNotifier.notify(mockLog);
        }

        // Then
        verify(mailer, never()).sendMail(any(Email.class));
    }

    @Test
    void testNotify_ExceptionInSendingEmailisCaught() throws Exception {
        // Given
        Log mockLog = mock(Log.class);
        Tag activeTag = new Tag("important", State.Active);
        Set<Tag> tags = new HashSet<>(Collections.singletonList(activeTag));
        when(mockLog.getTags()).thenReturn(tags);
        when(mockLog.getTitle()).thenReturn("Test Log");

        Map<String, List<String>> tagEmailMap = new HashMap<>();
        tagEmailMap.put("important", Arrays.asList("test@example.com"));
        when(tagEmailMapPreferences.tagEmailMap()).thenReturn(tagEmailMap);

        doThrow(mock(MailException.class)).when(mailer).sendMail(any(Email.class));

        // When
        try {
            try (MockedStatic<ConfigLoader> mockConfigLoader = mockStatic(ConfigLoader.class)) {
                mockConfigLoader.when(() -> ConfigLoader.getStringProperty(Property.DEFAULT_FROM_ADDRESS)).thenReturn("from@example.com");
                mockConfigLoader.when(() -> ConfigLoader.getStringProperty(Property.DEFAULT_FROM_NAME)).thenReturn("from");
                emailNotifier.notify(mockLog);
            }
        } catch (Exception ex) {
            Assert.fail("Exception was supposed to be caught.");
        }
    }
}
