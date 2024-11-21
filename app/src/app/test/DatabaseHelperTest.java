package app.test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;
import app.util.DatabaseHelper;
import app.User;
import app.HelpArticle;
import app.util.Group;
import app.util.Invitation;

import java.util.Set;
import java.util.HashSet;
import java.util.List;

public class DatabaseHelperTest {

    static DatabaseHelper dbHelper;

    @BeforeAll
    public static void setUpClass() throws Exception {
        dbHelper = new DatabaseHelper();
        dbHelper.connectToDatabase();
    }

    @AfterAll
    public static void tearDownClass() {
        dbHelper.closeConnection();
    }

    @Test
    public void testRegisterAndRetrieveUser() throws Exception {
        User user = new User("testuser", "password", "Student");
        user.setEmail("test@example.com");
        dbHelper.registerUser(user);

        User retrievedUser = dbHelper.getUser("testuser");
        assertNotNull(retrievedUser);
        assertEquals("testuser", retrievedUser.getUsername());
        assertEquals("test@example.com", retrievedUser.getEmail());
    }

    @Test
    public void testDeleteUser() throws Exception {
        dbHelper.deleteUser("testuser");
        User user = dbHelper.getUser("testuser");
        assertNull(user);
    }

    @Test
    public void testDoesUserExist() throws Exception {
        User user = new User("existuser", "password", "Student");
        dbHelper.registerUser(user);
        assertTrue(dbHelper.doesUserExist("existuser"));
        dbHelper.deleteUser("existuser");
        assertFalse(dbHelper.doesUserExist("existuser"));
    }

    @Test
    public void testRegisterAndRetrieveArticle() throws Exception {
        HelpArticle article = new HelpArticle("Test Article", "Description", "Body", "Beginner",
                new HashSet<>(Set.of("test")), new HashSet<>(Set.of("http://example.com")),
                "author1", null, false);
        dbHelper.registerArticle(article);

        HelpArticle retrievedArticle = dbHelper.getArticle(article.getId(), new User("author1", "password", "Student"));
        assertNotNull(retrievedArticle);
        assertEquals("Test Article", retrievedArticle.getTitle());
    }

    @Test
    public void testCreateAndRetrieveGroup() throws Exception {
        Group group = new Group("TestGroup");
        group.addAdmin("admin1");
        group.addStudent("student1");

        Group retrievedGroup = dbHelper.getGroup("TestGroup");
        assertNotNull(retrievedGroup);
        assertTrue(retrievedGroup.getAdmins().contains("admin1"));
        assertTrue(retrievedGroup.getStudents().contains("student1"));
    }

    @Test
    public void testRegisterAndRetrieveInvitation() throws Exception {
        Set<String> roles = new HashSet<>();
        roles.add("Student");
        Invitation invitation = new Invitation("invitationToken", roles);
        dbHelper.registerInvitation(invitation);

        Invitation retrievedInvitation = dbHelper.getInvitation("invitationToken");
        assertNotNull(retrievedInvitation);
        assertEquals(roles, retrievedInvitation.getRoles());

        assertTrue(dbHelper.doesInvitationExist("invitationToken"));
        dbHelper.deleteInvitation("invitationToken");
        assertFalse(dbHelper.doesInvitationExist("invitationToken"));
    }

    @Test
    public void testStoreAndRetrieveHelpMessages() throws Exception {
        dbHelper.storeHelpMessage("user1", "Generic", "Need help with X", "search terms");
        List<String> messages = dbHelper.getAllHelpMessages();
        assertFalse(messages.isEmpty());
        assertTrue(messages.get(0).contains("Need help with X"));
    }
}
