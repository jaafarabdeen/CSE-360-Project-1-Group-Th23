package app.test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;

import app.util.HelpArticleDatabase;
import app.HelpArticle;
import app.User;

import java.util.Set;
import java.util.HashSet;
import java.util.Collection;

public class HelpArticleDatabaseTest {

    static User testUser;

    @BeforeAll
    public static void setup() throws Exception {
        // Setup the test user
        testUser = new User("testuser", "password", "Student");
    }

    @Test
    public void testAddAndGetArticle() {
        HelpArticle article = new HelpArticle("Test Title", "Test Description", "Test Body", "Intermediate",
                new HashSet<>(Set.of("keyword1", "keyword2")), new HashSet<>(Set.of("http://link.com")),
                "testuser", null, false);

        HelpArticleDatabase.addArticle(article);

        Collection<HelpArticle> articles = HelpArticleDatabase.getArticles(testUser);
        assertNotNull(articles);
        boolean found = false;
        for (HelpArticle art : articles) {
            if (art.getTitle().equals("Test Title")) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

    @Test
    public void testUpdateArticle() {
        // Assuming we have an article added
        HelpArticle article = new HelpArticle("Update Title", "Description", "Body", "Intermediate",
                new HashSet<>(), new HashSet<>(), "testuser", null, false);
        HelpArticleDatabase.addArticle(article);

        // Update article
        article.setDescription("Updated Description");
        HelpArticleDatabase.updateArticle(article);

        // Retrieve and verify
        Collection<HelpArticle> articles = HelpArticleDatabase.getArticles(testUser);
        boolean foundUpdated = false;
        for (HelpArticle art : articles) {
            if (art.getTitle().equals("Update Title") && art.getDescription().equals("Updated Description")) {
                foundUpdated = true;
                break;
            }
        }
        assertTrue(foundUpdated);
    }

    @Test
    public void testRemoveArticle() {
        HelpArticle article = new HelpArticle("Delete Title", "Description", "Body", "Intermediate",
                new HashSet<>(), new HashSet<>(), "testuser", null, false);
        HelpArticleDatabase.addArticle(article);

        // Assume the article gets an ID assigned after addition
        long id = article.getId();
        HelpArticleDatabase.removeArticle(id);

        HelpArticle retrievedArticle = HelpArticleDatabase.getArticle(id, testUser);
        assertNull(retrievedArticle);
    }
}
