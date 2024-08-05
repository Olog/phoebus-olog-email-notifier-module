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
import org.mockito.MockitoAnnotations;
import org.phoebus.olog.entity.Log;
import org.phoebus.olog.entity.Tag;
import org.phoebus.olog.entity.State;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;

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
        Log log = mock(Log.class);
        Tag activeTag = new Tag("important", State.Active);
        Set<Tag> tags = new HashSet<>(Collections.singletonList(activeTag));
        when(log.getTags()).thenReturn(tags);
        when(log.getTitle()).thenReturn("Test Log");

        Map<String, List<String>> tagEmailMap = new HashMap<>();
        tagEmailMap.put("important", Arrays.asList("test@example.com"));
        when(tagEmailMapPreferences.tagEmailMap()).thenReturn(tagEmailMap);

        // When
        emailNotifier.notify(log);

        // Then
        verify(mailer).sendMail(any(Email.class));
    }

    @Test
    void testNotify_NoEmailsForInactiveTag() throws Exception {
        // Given
        Log log = mock(Log.class);
        Tag inactiveTag = new Tag("trivial", State.Inactive);
        Set<Tag> tags = new HashSet<>(Collections.singletonList(inactiveTag));
        when(log.getTags()).thenReturn(tags);
        when(log.getTitle()).thenReturn("Test Log");

        // When
        emailNotifier.notify(log);

        // Then
        verify(mailer, never()).sendMail(any(Email.class));
    }

    @Test
    void testNotify_EmailListIsEmpty() throws Exception {
        // Given
        Log log = mock(Log.class);
        Tag activeTag = new Tag("nonexistent", State.Active);
        Set<Tag> tags = new HashSet<>(Collections.singletonList(activeTag));
        when(log.getTags()).thenReturn(tags);
        when(log.getTitle()).thenReturn("Test Log");

        Map<String, List<String>> tagEmailMap = new HashMap<>();
        when(tagEmailMapPreferences.tagEmailMap()).thenReturn(tagEmailMap);

        // When
        emailNotifier.notify(log);

        // Then
        verify(mailer, never()).sendMail(any(Email.class));
    }

    @Test
    void testNotify_ExceptionInSendingEmail() throws Exception {
        // Given
        Log log = mock(Log.class);
        Tag activeTag = new Tag("important", State.Active);
        Set<Tag> tags = new HashSet<>(Collections.singletonList(activeTag));
        when(log.getTags()).thenReturn(tags);
        when(log.getTitle()).thenReturn("Test Log");

        Map<String, List<String>> tagEmailMap = new HashMap<>();
        tagEmailMap.put("important", Arrays.asList("test@example.com"));
        when(tagEmailMapPreferences.tagEmailMap()).thenReturn(tagEmailMap);

        doThrow(new RuntimeException("Email sending failed")).when(mailer).sendMail(any(Email.class));

        // When
        emailNotifier.notify(log);

        // Then
        // Check that the exception was logged
    }
}
