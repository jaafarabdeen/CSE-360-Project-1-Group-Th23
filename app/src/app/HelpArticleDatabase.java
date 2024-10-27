package app;

import java.util.Collection;
import app.DatabaseHelper;
import java.sql.SQLException;

/**
 * The HelpArticleDatabase class serves as a database interface for help articles.
 * It provides methods to add, retrieve, update, and delete articles.
 * 
 * Author:
 *     - Jaafar Abdeen
 */
public class HelpArticleDatabase {
    private static DatabaseHelper databaseHelper;

    static {
        try {
            databaseHelper = new DatabaseHelper();
            databaseHelper.connectToDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds a new article to the database.
     * 
     * @param article The article to add.
     */
    public static void addArticle(HelpArticle article) {
        try {
            databaseHelper.registerArticle(article);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves an article by its ID.
     * 
     * @param id The ID of the article.
     * @return The HelpArticle object, or null if not found.
     */
    public static HelpArticle getArticle(long id) {
        try {
            return databaseHelper.getArticle(id);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Removes an article from the database.
     * 
     * @param id The ID of the article to remove.
     */
    public static void removeArticle(long id) {
        try {
            databaseHelper.deleteArticle(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates an existing article.
     * 
     * @param article The article with updated information.
     */
    public static void updateArticle(HelpArticle article) {
        try {
            databaseHelper.updateArticle(article);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves all articles.
     * 
     * @return A collection of all help articles.
     */
    public static Collection<HelpArticle> getArticles() {
        try {
            return databaseHelper.getAllArticles();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}