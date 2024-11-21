package app.test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import app.User;

import java.time.LocalDateTime;

public class UserTest {

    @Test
    public void testUserCreation() {
        String username = "testuser";
        String password = "password";
        String role = "Student";
        User user = new User(username, password, role);

        assertEquals(username, user.getUsername());
        assertEquals(password, user.getPassword());
        assertTrue(user.getRoles().contains(role));
        assertFalse(user.isOneTimePassword());
        assertEquals("Intermediate", user.getLevel());
    }

    @Test
    public void testSetAndGetPassword() {
        User user = new User("user", "oldPass", "");
        user.setPassword("newPass");
        assertEquals("newPass", user.getPassword());
    }

    @Test
    public void testOneTimePasswordFlag() {
        User user = new User("user", "pass", "");
        user.setOneTimePassword(true);
        assertTrue(user.isOneTimePassword());
    }

    @Test
    public void testOneTimePasswordExpiry() {
        User user = new User("user", "pass", "");
        LocalDateTime expiry = LocalDateTime.now().plusDays(1);
        user.setOneTimePasswordExpiry(expiry);
        assertEquals(expiry, user.getOneTimePasswordExpiry());
    }

    @Test
    public void testRoles() {
        User user = new User("user", "pass", "Student");
        user.addRole("Admin");
        assertTrue(user.hasRole("Admin"));
        user.removeRole("Student");
        assertFalse(user.hasRole("Student"));
    }

    @Test
    public void testTopicLevels() {
        User user = new User("user", "pass", "");
        user.setTopicLevel("Math", "Advanced");
        assertEquals("Advanced", user.getTopicLevel("Math"));
        assertEquals("Intermediate", user.getTopicLevel("Science"));
    }

    @Test
    public void testSearchRequests() {
        User user = new User("user", "pass", "");
        user.addSearchRequest("search1");
        user.addSearchRequest("search2");
        assertEquals(2, user.getSearchRequests().size());
        assertEquals("search1", user.getSearchRequests().get(0));
    }

    @Test
    public void testContentLevelPreference() {
        User user = new User("user", "pass", "");
        user.setContentLevelPreference("Advanced");
        assertEquals("Advanced", user.getContentLevelPreference());
    }

    @Test
    public void testToString() {
        User user = new User("user", "pass", "");
        assertEquals("user", user.toString());
    }
}
