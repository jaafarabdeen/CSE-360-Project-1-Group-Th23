package app.test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import app.HelpArticle;

import java.util.HashSet;
import java.util.Set;

public class HelpArticleTest {

    @Test
    public void testHelpArticleCreation() {
        String title = "Sample Title";
        String description = "Sample Description";
        String body = "Sample Body";
        String level = "Intermediate";
        Set<String> keywords = new HashSet<>();
        keywords.add("sample");
        keywords.add("article");
        Set<String> referenceLinks = new HashSet<>();
        referenceLinks.add("http://example.com");
        String authorUsername = "author1";
        String groupName = "group1";
        boolean isEncrypted = false;

        HelpArticle article = new HelpArticle(title, description, body, level, keywords, referenceLinks, authorUsername, groupName, isEncrypted);

        assertEquals(title, article.getTitle());
        assertEquals(description, article.getDescription());
        assertEquals(body, article.getBody());
        assertEquals(level, article.getLevel());
        assertEquals(keywords, article.getKeywords());
        assertEquals(referenceLinks, article.getReferenceLinks());
        assertEquals(authorUsername, article.getAuthorUsername());
        assertEquals(groupName, article.getGroupName());
        assertEquals(isEncrypted, article.isEncrypted());
    }

    @Test
    public void testSettersAndGetters() {
        HelpArticle article = new HelpArticle("Title", "Desc", "Body", "Beginner", new HashSet<>(), new HashSet<>(), "author", null, false);
        article.setId(123);
        article.setTitle("New Title");
        article.setDescription("New Description");
        article.setBody("New Body");
        article.setLevel("Advanced");
        Set<String> newKeywords = new HashSet<>();
        newKeywords.add("test");
        article.setKeywords(newKeywords);
        Set<String> newReferences = new HashSet<>();
        newReferences.add("http://newlink.com");
        article.setReferenceLinks(newReferences);
        article.setGroupName("NewGroup");
        article.setEncrypted(true);

        assertEquals(123, article.getId());
        assertEquals("New Title", article.getTitle());
        assertEquals("New Description", article.getDescription());
        assertEquals("New Body", article.getBody());
        assertEquals("Advanced", article.getLevel());
        assertEquals(newKeywords, article.getKeywords());
        assertEquals(newReferences, article.getReferenceLinks());
        assertEquals("NewGroup", article.getGroupName());
        assertTrue(article.isEncrypted());
    }
}
