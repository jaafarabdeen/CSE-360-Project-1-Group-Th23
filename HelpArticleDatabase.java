// HelpArticleDatabase.java
import java.util.HashMap;
import java.util.Map;
import java.util.Collection;

/**
 * The HelpArticleDatabase class serves as an in-memory database for help articles.
 * It provides methods to add, retrieve, update, and delete articles.
 * 
 * Author:
 *     - Jaafar Abdeen
 */
public class HelpArticleDatabase {
    private static Map<Long, HelpArticle> articles = new HashMap<>();

    /**
     * Adds a new article to the database.
     * 
     * @param article The article to add.
     */
    public static void addArticle(HelpArticle article) {
        articles.put(article.getId(), article);
    }

    /**
     * Retrieves an article by its ID.
     * 
     * @param id The ID of the article.
     * @return The HelpArticle object, or null if not found.
     */
    public static HelpArticle getArticle(long id) {
        return articles.get(id);
    }

    /**
     * Removes an article from the database.
     * 
     * @param id The ID of the article to remove.
     */
    public static void removeArticle(long id) {
        articles.remove(id);
    }

    /**
     * Updates an existing article.
     * 
     * @param article The article with updated information.
     */
    public static void updateArticle(HelpArticle article) {
        articles.put(article.getId(), article);
    }

    /**
     * Retrieves all articles.
     * 
     * @return A collection of all help articles.
     */
    public static Collection<HelpArticle> getArticles() {
        return articles.values();
    }
}
