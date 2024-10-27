package app;

import java.sql.*;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import Encryption.EncryptionHelper;
import Encryption.EncryptionUtils;
import app.HelpArticle;
import app.User;

/**
 * The DatabaseHelper class provides methods to interact with the HelpArticle and User databases.
 * It allows connecting to the database, adding, viewing, deleting articles, users, and handling backups.
 */
public class DatabaseHelper {

    // JDBC driver name and database URL 
    static final String JDBC_DRIVER = "org.h2.Driver";   
    static final String DB_URL = "jdbc:h2:~/appDatabase";  

    //  Database credentials 
    static final String USER = "sa"; 
    static final String PASS = ""; 

    private Connection connection = null;
    private Statement statement = null; 
    private EncryptionHelper encryptionHelper;

    /**
     * Constructor for DatabaseHelper.
     * Initializes the encryption helper.
     *
     * @throws Exception if an error occurs during initialization.
     */
    public DatabaseHelper() throws Exception {
        encryptionHelper = new EncryptionHelper();
    }

    /**
     * Connects to the database.
     *
     * @throws SQLException if an error occurs while connecting to the database.
     */
    public void connectToDatabase() throws SQLException {
        try {
            Class.forName(JDBC_DRIVER); // Load the JDBC driver
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            statement = connection.createStatement(); 
            createTables();  // Create the necessary tables if they don't exist
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver not found: " + e.getMessage());
        }
    }

    /**
     * Creates the necessary tables if they don't already exist.
     *
     * @throws SQLException if an error occurs during table creation.
     */
    private void createTables() throws SQLException {
        String userTable = "CREATE TABLE IF NOT EXISTS users ("
                + "username VARCHAR(255) PRIMARY KEY, "
                + "password_hash VARCHAR(255), "
                + "email VARCHAR(255), "
                + "first_name VARCHAR(255), "
                + "middle_name VARCHAR(255), "
                + "last_name VARCHAR(255), "
                + "preferred_name VARCHAR(255), "
                + "roles VARCHAR(255), "
                + "level VARCHAR(255))";
        statement.execute(userTable);

        String articleTable = "CREATE TABLE IF NOT EXISTS help_articles ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "title VARCHAR(255), "
                + "description VARCHAR(1000), "
                + "body TEXT, "
                + "level VARCHAR(255), "
                + "keywords VARCHAR(500), "
                + "groups VARCHAR(500), "
                + "author_username VARCHAR(255))";
        statement.execute(articleTable);

        String invitationTable = "CREATE TABLE IF NOT EXISTS invitations ("
                + "token VARCHAR(255) PRIMARY KEY, "
                + "roles VARCHAR(255))";
        statement.execute(invitationTable);
    }

    /**
     * Registers a new user in the database.
     *
     * @param user The user to be registered.
     * @throws Exception if an error occurs during user insertion.
     */
    public void registerUser(User user) throws Exception {
        String insertUser = "INSERT INTO users (username, password_hash, email, first_name, middle_name, last_name, preferred_name, roles, level) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPasswordHash());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getFirstName());
            pstmt.setString(5, user.getMiddleName());
            pstmt.setString(6, user.getLastName());
            pstmt.setString(7, user.getPreferredName());
            pstmt.setString(8, String.join(",", user.getRoles()));
            pstmt.setString(9, user.getLevel());
            pstmt.executeUpdate();
        }
    }

    /**
     * Retrieves a user by their username.
     *
     * @param username The username of the user.
     * @return The User object, or null if not found.
     * @throws SQLException if an error occurs during user retrieval.
     */
    public User getUser(String username) throws SQLException {
        String query = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User(
                            rs.getString("username"),
                            rs.getString("password_hash"),
                            rs.getString("roles")
                    );
                    user.setEmail(rs.getString("email"));
                    user.setFirstName(rs.getString("first_name"));
                    user.setMiddleName(rs.getString("middle_name"));
                    user.setLastName(rs.getString("last_name"));
                    user.setPreferredName(rs.getString("preferred_name"));
                    user.setLevel(rs.getString("level"));
                    return user;
                }
            }
        }
        return null;
    }

    /**
     * Checks if a user exists by their username.
     *
     * @param username The username to check.
     * @return True if the user exists, false otherwise.
     * @throws SQLException if an error occurs during the check.
     */
    public boolean doesUserExist(String username) throws SQLException {
        String query = "SELECT COUNT(*) AS count FROM users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count") > 0;
                }
            }
        }
        return false;
    }

    /**
     * Registers a new help article in the database.
     *
     * @param article The article to be registered.
     * @throws Exception if an error occurs during article insertion.
     */
    public void registerArticle(HelpArticle article) throws Exception {
        String insertArticle = "INSERT INTO help_articles (title, description, body, level, keywords, groups, author_username) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertArticle)) {
            pstmt.setString(1, article.getTitle());
            pstmt.setString(2, article.getDescription());
            pstmt.setString(3, article.getBody());
            pstmt.setString(4, article.getLevel());
            pstmt.setString(5, String.join(",", article.getKeywords()));
            pstmt.setString(6, String.join(",", article.getGroups()));
            pstmt.setString(7, article.getAuthorUsername());
            pstmt.executeUpdate();
        }
    }

    /**
     * Retrieves an article by its ID.
     *
     * @param id The ID of the article.
     * @return The HelpArticle object, or null if not found.
     * @throws SQLException if an error occurs during article retrieval.
     */
    public HelpArticle getArticle(long id) throws SQLException {
        String query = "SELECT * FROM help_articles WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    HelpArticle article = new HelpArticle(
                            rs.getString("title"),
                            rs.getString("description"),
                            rs.getString("body"),
                            rs.getString("level"),
                            Set.of(rs.getString("keywords").split(",")),
                            Set.of(rs.getString("groups").split(",")),
                            rs.getString("author_username")
                    );
                    return article;
                }
            }
        }
        return null;
    }

    /**
     * Deletes an article by its ID.
     *
     * @param id The ID of the article to delete.
     * @throws SQLException if an error occurs during article deletion.
     */
    public void deleteArticle(long id) throws SQLException {
        String deleteQuery = "DELETE FROM help_articles WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteQuery)) {
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        }
    }

    /**
     * Retrieves all articles from the database.
     *
     * @return A collection of HelpArticle objects.
     * @throws SQLException if an error occurs during article retrieval.
     */
    public Collection<HelpArticle> getAllArticles() throws SQLException {
        String query = "SELECT * FROM help_articles";
        Collection<HelpArticle> articles = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                HelpArticle article = new HelpArticle(
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("body"),
                        rs.getString("level"),
                        Set.of(rs.getString("keywords").split(",")),
                        Set.of(rs.getString("groups").split(",")),
                        rs.getString("author_username")
                );
                articles.add(article);
            }
        }
        return articles;
    }

    /**
     * Closes the database connection.
     */
    public void closeConnection() {
        try {
            if (statement != null) statement.close();
        } catch (SQLException se2) {
            se2.printStackTrace();
        }
        try {
            if (connection != null) connection.close();
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }
}