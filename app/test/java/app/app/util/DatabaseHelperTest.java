package app.util;

import app.HelpArticle;
import app.Invitation;
import app.User;
import Encryption.EncryptionHelper;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseHelperTest {

    private static DatabaseHelper dbHelper;

    @BeforeAll
    static void setup() throws Exception {
        dbHelper = new DatabaseHelper();
        dbHelper.connectToDatabase();
    }

    @AfterAll
    static void teardown() {
        dbHelper.closeConnection();
    }

    @BeforeEach
    void resetDatabase() throws SQLException {
        dbHelper.deleteUser("testUser");
        dbHelper.deleteArticle(1);
    }

    @Test
    void testRegisterUser() throws Exception {
        User user = new User("testUser", "hashed_password", "USER_ROLE");
        user.setEmail("test@example.com");
        user.setFirstName("Test");
        user.setLastName("User");
        dbHelper.registerUser(user);
        
        assertTrue(dbHelper.doesUserExist("testUser"));
    }

    @Test
    void testGetUser() throws Exception {
        User user = dbHelper.getUser("testUser");
        assertNotNull(user);
        assertEquals("testUser", user.getUsername());
    }

    @Test
    void testUpdateUser() throws SQLException {
        User user = dbHelper.getUser("testUser");
        assertNotNull(user);

        user.setEmail("updated@example.com");
        dbHelper.updateUser(user);

        User updatedUser = dbHelper.getUser("testUser");
        assertEquals("updated@example.com", updatedUser.getEmail());
    }

    @Test
    void testDeleteUser() throws SQLException {
        dbHelper.deleteUser("testUser");
        assertFalse(dbHelper.doesUserExist("testUser"));
    }

    @Test
    void testRegisterArticle() throws SQLException {
        HelpArticle article = new HelpArticle();
        article.setTitle("Sample Article");
        article.setDescription("This is a sample description.");
        article.setBody("Sample body text.");
        article.setAuthorUsername("testUser");
        
        dbHelper.registerArticle(article);
        HelpArticle retrievedArticle = dbHelper.getArticle(article.getId());
        assertNotNull(retrievedArticle);
        assertEquals("Sample Article", retrievedArticle.getTitle());
    }

    @Test
    void testGetAllArticles() throws SQLException {
        Collection<HelpArticle> articles = dbHelper.getAllArticles();
        assertFalse(articles.isEmpty());
    }

    @Test
    void testUpdateArticle() throws SQLException {
        HelpArticle article = dbHelper.getArticle(1);
        assertNotNull(article);

        article.setDescription("Updated description");
        dbHelper.updateArticle(article);

        HelpArticle updatedArticle = dbHelper.getArticle(1);
        assertEquals("Updated description", updatedArticle.getDescription());
    }

    @Test
    void testDeleteArticle() throws SQLException {
        dbHelper.deleteArticle(1);
        assertNull(dbHelper.getArticle(1));
    }

    @Test
    void testBackupAndRestoreArticles() throws Exception {
        String backupFile = "articles_backup.txt";
        dbHelper.backupArticles(backupFile);

        // Reset the table and restore from backup
        dbHelper.restoreArticles(backupFile, false, null);
        Collection<HelpArticle> restoredArticles = dbHelper.getAllArticles();
        assertFalse(restoredArticles.isEmpty());
    }

    @Test
    void testRegisterInvitation() throws SQLException {
        Set<String> roles = new HashSet<>();
        roles.add("ADMIN");
        Invitation invitation = new Invitation("testToken", roles);

        dbHelper.registerInvitation(invitation);
        assertTrue(dbHelper.doesInvitationExist("testToken"));
    }

    @Test
    void testDeleteInvitation() throws SQLException {
        dbHelper.deleteInvitation("testToken");
        assertFalse(dbHelper.doesInvitationExist("testToken"));
    }

    @Test
    void testIsDatabaseEmpty() throws SQLException {
        boolean isEmpty = dbHelper.isDatabaseEmpty();
        assertFalse(isEmpty);
    }
}