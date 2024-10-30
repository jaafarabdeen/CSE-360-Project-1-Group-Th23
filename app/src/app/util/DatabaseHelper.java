package app.util;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Base64;
import Encryption.EncryptionHelper;
import app.HelpArticle;
import app.User;

/**
 * The DatabaseHelper class provides methods to interact with the HelpArticle and User databases.
 * It allows connecting to the database, adding, viewing, deleting articles, users, and handling backups.
 *  
 * Author:
 *     - Jaafar Abdeen
 */
public class DatabaseHelper {

    // JDBC driver name and database URL 
    static final String JDBC_DRIVER = "org.h2.Driver";   
    static final String DB_URL = "jdbc:h2:./database/appDatabase";  

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
                + "reference_links VARCHAR(1000), "
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
            pstmt.setString(2, user.getPassword());
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
     * Retrieves all users from the database.
     *
     * @return A collection of User objects.
     * @throws SQLException if an error occurs during user retrieval.
     */
    public Collection<User> getAllUsers() throws SQLException {
        String query = "SELECT * FROM users";
        Collection<User> users = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
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
                users.add(user);
            }
        }
        return users;
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
     * Updates an existing user in the database.
     *
     * @param user The user with updated information.
     * @throws SQLException if an error occurs during user update.
     */
    public void updateUser(User user) throws SQLException {
        String updateQuery = "UPDATE users SET password_hash = ?, email = ?, first_name = ?, middle_name = ?, last_name = ?, preferred_name = ?, roles = ?, level = ? WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
            pstmt.setString(1, user.getPassword());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getFirstName());
            pstmt.setString(4, user.getMiddleName());
            pstmt.setString(5, user.getLastName());
            pstmt.setString(6, user.getPreferredName());
            pstmt.setString(7, String.join(",", user.getRoles()));
            pstmt.setString(8, user.getLevel());
            pstmt.setString(9, user.getUsername());
            pstmt.executeUpdate();
        }
    }
    
    /**
     * Deletes a user by their username.
     *
     * @param username The username of the user to delete.
     * @throws SQLException if an error occurs during user deletion.
     */
    public void deleteUser(String username) throws SQLException {
        String deleteQuery = "DELETE FROM users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteQuery)) {
            pstmt.setString(1, username);
            pstmt.executeUpdate();
        }
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
    public void registerArticle(HelpArticle article) throws SQLException {
        String insertArticle = "INSERT INTO help_articles (title, description, body, level, keywords, groups, reference_links, author_username) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertArticle, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, article.getTitle());
            pstmt.setString(2, article.getDescription());
            pstmt.setString(3, article.getBody());
            pstmt.setString(4, article.getLevel());
            pstmt.setString(5, String.join(",", article.getKeywords()));
            pstmt.setString(6, String.join(",", article.getGroups()));
            pstmt.setString(7, String.join(",", article.getReferenceLinks()));
            pstmt.setString(8, article.getAuthorUsername());
            pstmt.executeUpdate();
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    article.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating help article failed, no ID obtained.");
                }
            }
        }
    }

    /**
     * Updates an existing help article in the database.
     *
     * @param article The article with updated information.
     * @throws SQLException if an error occurs during article update.
     */
    public void updateArticle(HelpArticle article) throws SQLException {
        String updateQuery = "UPDATE help_articles SET title = ?, description = ?, body = ?, level = ?, keywords = ?, groups = ?, reference_links = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
            pstmt.setString(1, article.getTitle());
            pstmt.setString(2, article.getDescription());
            pstmt.setString(3, article.getBody());
            pstmt.setString(4, article.getLevel());
            pstmt.setString(5, String.join(",", article.getKeywords()));
            pstmt.setString(6, String.join(",", article.getGroups()));
            pstmt.setString(7, String.join(",", article.getReferenceLinks()));
            pstmt.setLong(8, article.getId());
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
                            rs.getLong("id"),
                            rs.getString("title"),
                            rs.getString("description"),
                            rs.getString("body"),
                            rs.getString("level"),
                            new HashSet<>(Set.of(rs.getString("keywords").split(","))),
                            new HashSet<>(Set.of(rs.getString("groups").split(","))),
                            new HashSet<>(Set.of(rs.getString("reference_links").split(","))),
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
                        rs.getLong("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("body"),
                        rs.getString("level"),
                        new HashSet<>(Set.of(rs.getString("keywords").split(","))),
                        new HashSet<>(Set.of(rs.getString("groups").split(","))),
                        new HashSet<>(Set.of(rs.getString("reference_links").split(","))),
                        rs.getString("author_username")
                );
                articles.add(article);
            }
        }
        return articles;
    }
    
    /**
     * Backs up articles to a specified backup file.
     *
     * @param fileName The name of the backup file.
     * @throws SQLException if an error occurs during article backup.
     * @throws IOException  if an error occurs during file writing.
     */
    public void backupArticles(String fileName) throws SQLException, IOException, Exception {
        String query = "SELECT * FROM help_articles";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query);
             FileWriter writer = new FileWriter(fileName)) {

            while (rs.next()) {
                String articleData = rs.getString("title") + "§" + rs.getString("description") + "§" +
                                     rs.getString("body") + "§" + rs.getString("level") + "§" +
                                     rs.getString("keywords") + "§" + rs.getString("groups") + "§" +
                                     rs.getString("reference_links") + "§" +
                                     rs.getString("author_username");

                // Generate a random IV
                byte[] iv = new byte[16];
                SecureRandom random = new SecureRandom();
                random.nextBytes(iv);

                // Encrypt the article data with the random IV
                byte[] encryptedData = encryptionHelper.encrypt(articleData.getBytes(StandardCharsets.UTF_8), iv);

                // Encode IV and encrypted data to Base64 and store them in the backup file
                String encodedIv = Base64.getEncoder().encodeToString(iv);
                String encodedData = Base64.getEncoder().encodeToString(encryptedData);
                writer.write(encodedIv + "," + encodedData + "\n");
            }
        }
    }

    /**
     * Restores articles from a specified backup file.
     *
     * @param fileName The name of the backup file.
     * @param merge If true, merges backup entries with current entries; otherwise, deletes current entries before restoring.
     * @param group The group name to filter by; if null, restores all articles.
     * @throws SQLException if an error occurs during article restoration.
     * @throws IOException if an error occurs during file reading.
     */
    public void restoreArticles(String fileName, boolean merge, String group) throws SQLException, IOException, Exception {
        if (!merge) {
            String clearQuery = "DELETE FROM help_articles";
            try (Statement stmt = connection.createStatement()) {
                stmt.executeUpdate(clearQuery);
            }
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 2); // Separate IV and encrypted data
                if (parts.length != 2) continue;

                byte[] iv = Base64.getDecoder().decode(parts[0]);
                byte[] encryptedData = Base64.getDecoder().decode(parts[1]);

                // Decrypt article data using the extracted IV
                byte[] decryptedData = encryptionHelper.decrypt(encryptedData, iv);

                String articleData = new String(decryptedData, StandardCharsets.UTF_8);
                String[] fields = articleData.split("§", 8);

                if (fields.length != 8) continue;

                // Check if the article belongs to the specified group (if group filtering is enabled)
                Set<String> groups = Set.of(fields[5].split(","));
                if (group != null && !groups.contains(group)) {
                    continue; // Skip articles not in the specified group
                }

                // Insert the article into the database if it doesn't exist already (use title for uniqueness in this example)
                if (!merge || !doesArticleExist(fields[0])) {
                    String insertArticle = "INSERT INTO help_articles (title, description, body, level, keywords, groups, reference_links, author_username) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement pstmt = connection.prepareStatement(insertArticle)) {
                        pstmt.setString(1, fields[0]);
                        pstmt.setString(2, fields[1]);
                        pstmt.setString(3, fields[2]);
                        pstmt.setString(4, fields[3]);
                        pstmt.setString(5, fields[4]);
                        pstmt.setString(6, fields[5]);
                        pstmt.setString(7, fields[6]);
                        pstmt.setString(8, fields[7]);
                        pstmt.executeUpdate();
                    }
                }
            }
        }
    }

    /**
     * Checks if an article exists by its title.
     *
     * @param title The title of the article.
     * @return True if the article exists, false otherwise.
     * @throws SQLException if an error occurs during the check.
     */
    private boolean doesArticleExist(String title) throws SQLException {
        String query = "SELECT COUNT(*) AS count FROM help_articles WHERE title = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, title);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count") > 0;
                }
            }
        }
        return false;
    }
    
    /**
     * Registers a new invitation in the database.
     *
     * @param invitation The invitation to be registered.
     * @throws SQLException if an error occurs during invitation insertion.
     */
    public void registerInvitation(Invitation invitation) throws SQLException {
        String insertInvitation = "INSERT INTO invitations (token, roles) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertInvitation)) {
            pstmt.setString(1, invitation.getToken());
            pstmt.setString(2, String.join(",", invitation.getRoles()));
            pstmt.executeUpdate();
        }
    }

    /**
     * Checks if an invitation exists by its token.
     *
     * @param token The token of the invitation.
     * @return True if the invitation exists, false otherwise.
     * @throws SQLException if an error occurs during the check.
     */
    public boolean doesInvitationExist(String token) throws SQLException {
        String query = "SELECT COUNT(*) AS count FROM invitations WHERE token = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, token);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count") > 0;
                }
            }
        }
        return false;
    }

    /**
     * Retrieves an invitation by its token.
     *
     * @param token The token of the invitation.
     * @return The Invitation object, or null if not found.
     * @throws SQLException if an error occurs during invitation retrieval.
     */
    public Invitation getInvitation(String token) throws SQLException {
        String query = "SELECT * FROM invitations WHERE token = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, token);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Set<String> roles = Set.of(rs.getString("roles").split(","));
                    return new Invitation(token, roles);
                }
            }
        }
        return null;
    }

    /**
     * Deletes an invitation by its token.
     *
     * @param token The token of the invitation to delete.
     * @throws SQLException if an error occurs during invitation deletion.
     */
    public void deleteInvitation(String token) throws SQLException {
        String deleteQuery = "DELETE FROM invitations WHERE token = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteQuery)) {
            pstmt.setString(1, token);
            pstmt.executeUpdate();
        }
    }

    /**
     * Checks if the database is empty.
     *
     * @return True if the user table is empty, false otherwise.
     * @throws SQLException if an error occurs during the check.
     */
    public boolean isDatabaseEmpty() throws SQLException {
        String query = "SELECT COUNT(*) AS count FROM users";
        try (ResultSet resultSet = statement.executeQuery(query)) {
            if (resultSet.next()) {
                return resultSet.getInt("count") == 0;
            }
        }
        return true;
    }

    /**
     * Closes the database connection.
     */
    public void closeConnection() {
        try {
            if (statement != null) statement.close();
            if (connection != null) connection.close();
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }
}