package app.util;

import java.util.Collection;
import app.HelpArticle;

import java.sql.SQLException;
import app.User;

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
     * Retrieves an article by its ID for the specified user.
     * 
     * @param id   The ID of the article.
     * @param user The user requesting the article.
     * @return The HelpArticle object, or null if not found or access denied.
     */
    public static HelpArticle getArticle(long id, User user) {
        try {
            return databaseHelper.getArticle(id, user);
        } catch (Exception e) {
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
     * Retrieves all articles accessible to the specified user.
     * 
     * @param user The user requesting the articles.
     * @return A collection of accessible help articles.
     */
    public static Collection<HelpArticle> getArticles(User user) {
        try {
            return databaseHelper.getAllArticles(user);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
