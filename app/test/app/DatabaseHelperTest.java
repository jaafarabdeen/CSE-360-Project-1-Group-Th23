package app;

import org.junit.jupiter.api.*;
import java.sql.SQLException;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DatabaseHelperTest {

    private DatabaseHelper dbHelper;

    @BeforeAll
    public void setUp() throws Exception {
        // Initialize DatabaseHelper with in-memory database
        dbHelper = new DatabaseHelper();
        dbHelper.connectToDatabase();
    }

    @AfterAll
    public void tearDown() throws Exception {
        // Close the connection after all tests
        dbHelper.closeConnection();
    }

    @Test
    public void testRegisterUser() throws Exception {
        User user = new User("testUser", "passwordHash", "testRole");
        user.setEmail("test@example.com");
        dbHelper.registerUser(user);

        // Verify user exists
        assertTrue(dbHelper.doesUserExist("testUser"));

        // Verify retrieval of user data
        User retrievedUser = dbHelper.getUser("testUser");
        assertEquals("testUser", retrievedUser.getUsername());
        assertEquals("test@example.com", retrievedUser.getEmail());
    }

    @Test
    public void testUpdateUser() throws SQLException {
        User user = dbHelper.getUser("testUser");
        assertNotNull(user);

        // Update user details
        user.setFirstName("UpdatedName");
        dbHelper.updateUser(user);

        // Verify updates
        User updatedUser = dbHelper.getUser("testUser");
        assertEquals("UpdatedName", updatedUser.getFirstName());
    }

    @Test
    public void testDeleteUser() throws SQLException {
        // Delete user and check if removed
        dbHelper.deleteUser("testUser");
        assertFalse(dbHelper.doesUserExist("testUser"));
    }

    @Test
    public void testRegisterAndRetrieveArticle() throws SQLException {
        HelpArticle article = new HelpArticle(
            "Test Article", "A test description", "This is the body",
            "Basic", Set.of("test"), Set.of("group1"), Set.of("link1"), "authorUser"
        );
        dbHelper.registerArticle(article);

        // Verify article exists
        HelpArticle retrievedArticle = dbHelper.getArticle(article.getId());
        assertNotNull(retrievedArticle);
        assertEquals("Test Article", retrievedArticle.getTitle());
    }

    @Test
    public void testUpdateArticle() throws SQLException {
        HelpArticle article = dbHelper.getAllArticles().iterator().next();
        article.setTitle("Updated Title");
        dbHelper.updateArticle(article);

        // Verify article updates
        HelpArticle updatedArticle = dbHelper.getArticle(article.getId());
        assertEquals("Updated Title", updatedArticle.getTitle());
    }

    @Test
    public void testDeleteArticle() throws SQLException {
        HelpArticle article = dbHelper.getAllArticles().iterator().next();
        dbHelper.deleteArticle(article.getId());

        // Verify deletion
        HelpArticle deletedArticle = dbHelper.getArticle(article.getId());
        assertNull(deletedArticle);
    }

    @Test
    public void testRegisterAndRetrieveInvitation() throws SQLException {
        Invitation invitation = new Invitation("testToken", Set.of("ROLE_USER"));
        dbHelper.registerInvitation(invitation);

        // Verify invitation exists
        Invitation retrievedInvitation = dbHelper.getInvitation("testToken");
        assertNotNull(retrievedInvitation);
        assertEquals("testToken", retrievedInvitation.getToken());
    }

    @Test
    public void testDeleteInvitation() throws SQLException {
        dbHelper.deleteInvitation("testToken");

        // Verify deletion
        assertFalse(dbHelper.doesInvitationExist("testToken"));
    }

    @Test
    public void testBackupAndRestoreArticles() throws Exception {
        // Backup articles to a file
        String backupFile = "backup.txt";
        dbHelper.backupArticles(backupFile);

        // Clear articles table and verify it's empty
        dbHelper.getAllArticles().forEach(article -> {
            try {
                dbHelper.deleteArticle(article.getId());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        assertTrue(dbHelper.getAllArticles().isEmpty());

        // Restore articles from backup and verify articles exist
        dbHelper.restoreArticles(backupFile);
        assertFalse(dbHelper.getAllArticles().isEmpty());
    }
}