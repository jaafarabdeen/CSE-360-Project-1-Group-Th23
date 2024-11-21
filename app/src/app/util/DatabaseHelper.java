package app.util;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
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
    static final String USER = "CSE360Project1"; 
    static final String PASS = "GroupTh23"; 

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
                + "reference_links VARCHAR(1000), "
                + "author_username VARCHAR(255), "
                + "group_name VARCHAR(255))";
        statement.execute(articleTable);

        String invitationTable = "CREATE TABLE IF NOT EXISTS invitations ("
                + "token VARCHAR(255) PRIMARY KEY, "
                + "roles VARCHAR(255))";
        statement.execute(invitationTable);

        String groupTable = "CREATE TABLE IF NOT EXISTS groups ("
                + "name VARCHAR(255) PRIMARY KEY, "
                + "admins VARCHAR(1000), "
                + "instructors VARCHAR(1000), "
                + "instructor_admins VARCHAR(1000), "
                + "students VARCHAR(1000), "
                + "article_ids VARCHAR(1000))";
        statement.execute(groupTable);
        
        String messagesTable = "CREATE TABLE IF NOT EXISTS help_messages ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "username VARCHAR(255), "
                + "message_type VARCHAR(50), "
                + "message_content TEXT, "
                + "search_terms VARCHAR(255), "
                + "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
        statement.execute(messagesTable);
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
     * Stores a help message sent by a user into the database.
     *
     * @param username      The username of the user sending the message.
     * @param messageType   The type of the message ("Generic" or "Specific").
     * @param messageContent The content of the message.
     * @param searchTerms   The search terms used by the user (if any).
     */
    public void storeHelpMessage(String username, String messageType, String messageContent, String searchTerms) {
        String insertMessage = "INSERT INTO help_messages (username, message_type, message_content, search_terms) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertMessage)) {
            pstmt.setString(1, username);
            pstmt.setString(2, messageType);
            pstmt.setString(3, messageContent);
            pstmt.setString(4, searchTerms);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Retrieves all help messages from the database.
     *
     * @return A list of formatted help messages.
     */
    public List<String> getAllHelpMessages() {
        List<String> messages = new ArrayList<>();
        String query = "SELECT * FROM help_messages ORDER BY timestamp DESC";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                String message = "[" + rs.getTimestamp("timestamp") + "] "
                        + rs.getString("username") + " (" + rs.getString("message_type") + "): "
                        + rs.getString("message_content");
                String searchTerms = rs.getString("search_terms");
                if (searchTerms != null && !searchTerms.isEmpty()) {
                    message += " | Search Terms: " + searchTerms;
                }
                messages.add(message);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    /**
     * Creates a new group in the database.
     *
     * @param group The Group object to be created.
     * @throws SQLException if an error occurs during group insertion.
     */
    public void createGroup(Group group) throws SQLException {
        String insertGroup = "INSERT INTO groups (name, admins, instructors, instructor_admins, students, article_ids) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertGroup)) {
            pstmt.setString(1, group.getName());
            pstmt.setString(2, String.join(",", group.getAdmins()));
            pstmt.setString(3, String.join(",", group.getInstructors()));
            pstmt.setString(4, String.join(",", group.getInstructorAdmins()));
            pstmt.setString(5, String.join(",", group.getStudents()));
            pstmt.setString(6, group.getArticleIds().stream().map(Object::toString).reduce((a,b)->a+","+b).orElse(""));
            pstmt.executeUpdate();
        }
    }

    /**
     * Retrieves a group by its name.
     *
     * @param name The name of the group.
     * @return The Group object, or null if not found.
     * @throws SQLException if an error occurs during group retrieval.
     */
    public Group getGroup(String name) throws SQLException {
        String query = "SELECT * FROM groups WHERE name = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, name);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Group group = new Group(name);
                    String admins = rs.getString("admins");
                    String instructors = rs.getString("instructors");
                    String instructorAdmins = rs.getString("instructor_admins");
                    String students = rs.getString("students");
                    String articleIds = rs.getString("article_ids");

                    if (admins != null && !admins.isEmpty()) {
                        group.getAdmins().addAll(Set.of(admins.split(",")));
                    }
                    if (instructors != null && !instructors.isEmpty()) {
                        group.getInstructors().addAll(Set.of(instructors.split(",")));
                    }
                    if (instructorAdmins != null && !instructorAdmins.isEmpty()) {
                        group.getInstructorAdmins().addAll(Set.of(instructorAdmins.split(",")));
                    }
                    if (students != null && !students.isEmpty()) {
                        group.getStudents().addAll(Set.of(students.split(",")));
                    }
                    if (articleIds != null && !articleIds.isEmpty()) {
                        Set<Long> ids = new HashSet<>();
                        for (String idStr : articleIds.split(",")) {
                            ids.add(Long.parseLong(idStr));
                        }
                        group.getArticleIds().addAll(ids);
                    }
                    return group;
                }
            }
        }
        return null;
    }

    /**
     * Retrieves all groups from the database.
     *
     * @return A set of all groups.
     * @throws SQLException if an error occurs during group retrieval.
     */
    public Set<Group> getAllGroups() throws SQLException {
        String query = "SELECT * FROM groups";
        Set<Group> groups = new HashSet<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                String name = rs.getString("name");
                Group group = new Group(name);
                String admins = rs.getString("admins");
                String instructors = rs.getString("instructors");
                String instructorAdmins = rs.getString("instructor_admins");
                String students = rs.getString("students");
                String articleIds = rs.getString("article_ids");

                if (admins != null && !admins.isEmpty()) {
                    group.getAdmins().addAll(Set.of(admins.split(",")));
                }
                if (instructors != null && !instructors.isEmpty()) {
                    group.getInstructors().addAll(Set.of(instructors.split(",")));
                }
                if (instructorAdmins != null && !instructorAdmins.isEmpty()) {
                    group.getInstructorAdmins().addAll(Set.of(instructorAdmins.split(",")));
                }
                if (students != null && !students.isEmpty()) {
                    group.getStudents().addAll(Set.of(students.split(",")));
                }
                if (articleIds != null && !articleIds.isEmpty()) {
                    Set<Long> ids = new HashSet<>();
                    for (String idStr : articleIds.split(",")) {
                        ids.add(Long.parseLong(idStr));
                    }
                    group.getArticleIds().addAll(ids);
                }
                groups.add(group);
            }
        }
        return groups;
    }
    
    public List<Group> getGroupsForUser(User user) throws SQLException {
        List<Group> userGroups = new ArrayList<>();
        Set<Group> allGroups = getAllGroups();
        String username = user.getUsername();
        for (Group group : allGroups) {
            if (group.getAdmins().contains(username) ||
                group.getInstructorAdmins().contains(username) ||
                group.getInstructors().contains(username) ||
                group.getStudents().contains(username)) {
                userGroups.add(group);
            }
        }
        return userGroups;
    }

    /**
     * Updates an existing group in the database.
     *
     * @param group The Group object with updated information.
     * @throws SQLException if an error occurs during group update.
     */
    public void updateGroup(Group group) throws SQLException {
        String updateGroup = "UPDATE groups SET admins = ?, instructors = ?, instructor_admins = ?, students = ?, article_ids = ? WHERE name = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(updateGroup)) {
            pstmt.setString(1, String.join(",", group.getAdmins()));
            pstmt.setString(2, String.join(",", group.getInstructors()));
            pstmt.setString(3, String.join(",", group.getInstructorAdmins()));
            pstmt.setString(4, String.join(",", group.getStudents()));
            pstmt.setString(5, group.getArticleIds().stream().map(Object::toString).reduce((a,b)->a+","+b).orElse(""));
            pstmt.setString(6, group.getName());
            pstmt.executeUpdate();
        }
    }

    /**
     * Deletes a group from the database.
     *
     * @param name The name of the group to delete.
     * @throws SQLException if an error occurs during group deletion.
     */
    public void deleteGroup(String name) throws SQLException {
        String deleteGroup = "DELETE FROM groups WHERE name = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteGroup)) {
            pstmt.setString(1, name);
            pstmt.executeUpdate();
        }
    }

    // Modify methods to handle articles with group_name and encrypted body

    /**
     * Registers a new help article in the database.
     *
     * @param article The article to be registered.
     * @throws Exception if an error occurs during article insertion.
     */
    public void registerArticle(HelpArticle article) throws Exception {
        String insertArticle = "INSERT INTO help_articles (title, description, body, level, keywords, reference_links, author_username, group_name) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertArticle, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, article.getTitle());
            pstmt.setString(2, article.getDescription());

            String bodyToStore = article.getBody();
            if (article.getGroupName() != null) {
                // Encrypt the body
                byte[] iv = new byte[16];
                SecureRandom random = new SecureRandom();
                random.nextBytes(iv);
                byte[] encryptedBody = encryptionHelper.encrypt(bodyToStore.getBytes(StandardCharsets.UTF_8), iv);
                String ivEncoded = Base64.getEncoder().encodeToString(iv);
                String encryptedBodyEncoded = Base64.getEncoder().encodeToString(encryptedBody);
                bodyToStore = ivEncoded + ":" + encryptedBodyEncoded;
            }

            pstmt.setString(3, bodyToStore);
            pstmt.setString(4, article.getLevel());
            pstmt.setString(5, String.join(",", article.getKeywords()));
            pstmt.setString(6, String.join(",", article.getReferenceLinks()));
            pstmt.setString(7, article.getAuthorUsername());
            pstmt.setString(8, article.getGroupName());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    article.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating help article failed, no ID obtained.");
                }
            }

            // If the article is in a group, add it to the group's article list
            if (article.getGroupName() != null) {
                Group group = getGroup(article.getGroupName());
                if (group != null) {
                    group.addArticleId(article.getId());
                    updateGroup(group);
                }
            }
        }
    }

    /**
     * Updates an existing help article in the database.
     *
     * @param article The article with updated information.
     * @throws Exception if an error occurs during article update.
     */
    public void updateArticle(HelpArticle article) throws Exception {
        String updateQuery = "UPDATE help_articles SET title = ?, description = ?, body = ?, level = ?, keywords = ?, reference_links = ?, group_name = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
            pstmt.setString(1, article.getTitle());
            pstmt.setString(2, article.getDescription());

            String bodyToStore = article.getBody();
            if (article.getGroupName() != null) {
                // Encrypt the body
                byte[] iv = new byte[16];
                SecureRandom random = new SecureRandom();
                random.nextBytes(iv);
                byte[] encryptedBody = encryptionHelper.encrypt(bodyToStore.getBytes(StandardCharsets.UTF_8), iv);
                String ivEncoded = Base64.getEncoder().encodeToString(iv);
                String encryptedBodyEncoded = Base64.getEncoder().encodeToString(encryptedBody);
                bodyToStore = ivEncoded + ":" + encryptedBodyEncoded;
            }

            pstmt.setString(3, bodyToStore);
            pstmt.setString(4, article.getLevel());
            pstmt.setString(5, String.join(",", article.getKeywords()));
            pstmt.setString(6, String.join(",", article.getReferenceLinks()));
            pstmt.setString(7, article.getGroupName());
            pstmt.setLong(8, article.getId());
            pstmt.executeUpdate();

            // Handle group article lists if group changed
            // Implementation omitted for brevity
        }
    }

    /**
     * Retrieves an article by its ID.
     *
     * @param id The ID of the article.
     * @param user The user requesting the article (needed for access control and decryption)
     * @return The HelpArticle object, or null if not found or access denied.
     * @throws Exception if an error occurs during article retrieval.
     */
    public HelpArticle getArticle(long id, User user) throws Exception {
        String query = "SELECT * FROM help_articles WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String groupName = rs.getString("group_name");
                    String body = rs.getString("body");
                    boolean isEncrypted = groupName != null;
                    boolean hasAccess = true;

                    if (groupName != null) {
                        // Article is in a special access group
                        Group group = getGroup(groupName); // Use getGroup instead of getGroupByName
                        if (group == null) {
                            // Group not found, deny access
                            return null;
                        }

                        if (!hasAccessToGroupArticle(user, group)) {
                            // User does not have access to view the body
                            return null;
                        }

                        // Decrypt the body
                        String[] parts = body.split(":");
                        if (parts.length != 2) {
                            return null; // Invalid encrypted body format
                        }
                        byte[] iv = Base64.getDecoder().decode(parts[0]);
                        byte[] encryptedBody = Base64.getDecoder().decode(parts[1]);
                        byte[] decryptedBodyBytes = encryptionHelper.decrypt(encryptedBody, iv);
                        body = new String(decryptedBodyBytes, StandardCharsets.UTF_8);
                    }

                    HelpArticle article = new HelpArticle(
                            rs.getLong("id"),
                            rs.getString("title"),
                            rs.getString("description"),
                            body,
                            rs.getString("level"),
                            new HashSet<>(Set.of(rs.getString("keywords").split(","))),
                            new HashSet<>(Set.of(rs.getString("reference_links").split(","))),
                            rs.getString("author_username"),
                            groupName,
                            isEncrypted // Include the isEncrypted parameter
                    );
                    return article;
                }
            }
        }
        return null;
    }

    /**
     * Retrieves all articles accessible to the user.
     *
     * @param user The user requesting the articles.
     * @return A collection of HelpArticle objects.
     * @throws Exception if an error occurs during article retrieval.
     */
    public Collection<HelpArticle> getAllArticles(User user) throws Exception {
        String query = "SELECT * FROM help_articles";
        Collection<HelpArticle> articles = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                String groupName = rs.getString("group_name");
                String body = rs.getString("body");
                boolean isEncrypted = groupName != null;
                boolean hasAccess = true;

                if (groupName != null) {
                    // Article is in a special access group
                    Group group = getGroup(groupName); // Use getGroup instead of getGroupByName
                    if (group == null) {
                        // Group not found, deny access
                        hasAccess = false;
                    } else {
                        hasAccess = hasAccessToGroupArticle(user, group);
                    }
                }

                if (hasAccess) {
                    if (groupName != null) {
                        // Decrypt the body
                        String[] parts = body.split(":");
                        if (parts.length != 2) {
                            continue; // Invalid encrypted body format
                        }
                        byte[] iv = Base64.getDecoder().decode(parts[0]);
                        byte[] encryptedBody = Base64.getDecoder().decode(parts[1]);
                        byte[] decryptedBodyBytes = encryptionHelper.decrypt(encryptedBody, iv);
                        body = new String(decryptedBodyBytes, StandardCharsets.UTF_8);
                    }
                    HelpArticle article = new HelpArticle(
                            rs.getLong("id"),
                            rs.getString("title"),
                            rs.getString("description"),
                            body,
                            rs.getString("level"),
                            new HashSet<>(Set.of(rs.getString("keywords").split(","))),
                            new HashSet<>(Set.of(rs.getString("reference_links").split(","))),
                            rs.getString("author_username"),
                            groupName,
                            isEncrypted // Include the isEncrypted parameter
                    );
                    articles.add(article);
                }
            }
        }
        return articles;
    }

    // Add this method if getGroupByName is being called elsewhere
    /**
     * Retrieves a group by its name.
     *
     * @param name The name of the group.
     * @return The Group object, or null if not found.
     * @throws SQLException if an error occurs during group retrieval.
     */
    public Group getGroupByName(String name) throws SQLException {
        // Since getGroup(String name) already exists, we can simply call it
        return getGroup(name);
    }

    /**
     * Checks if a user has access to view the body of articles in a group.
     *
     * @param user The user requesting access.
     * @param group The group in question.
     * @return True if the user has access, false otherwise.
     */
    public boolean hasAccessToGroupArticle(User user, Group group) {
        String username = user.getUsername();

        if (group.getAdmins().contains(username)) {
            return true;
        }
        if (group.getInstructorAdmins().contains(username)) {
            return true;
        }
        if (group.getInstructors().contains(username)) {
            return true;
        }
        if (group.getStudents().contains(username)) {
            return true;
        }
        return false;
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
     * Backs up all articles to a specified file, encrypting the entire file.
     *
     * @param fileName The name of the backup file.
     * @throws Exception if an error occurs during the backup process.
     */
    public void backupArticles(String fileName) throws Exception {
        List<HelpArticle> articles = new ArrayList<>();
        String query = "SELECT * FROM help_articles";

        // Retrieve all articles from the database
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
                        new HashSet<>(Set.of(rs.getString("reference_links").split(","))),
                        rs.getString("author_username"),
                        rs.getString("group_name"), // Group name can be null
                        rs.getString("group_name") != null // Is encrypted if part of a group
                );
                articles.add(article);
            }
        }

        // Serialize articles into a single string
        StringBuilder serializedData = new StringBuilder();
        for (HelpArticle article : articles) {
            serializedData.append(article.getId()).append("§")
                    .append(article.getTitle()).append("§")
                    .append(article.getDescription()).append("§")
                    .append(article.getBody()).append("§")
                    .append(article.getLevel()).append("§")
                    .append(String.join(",", article.getKeywords())).append("§")
                    .append(String.join(",", article.getReferenceLinks())).append("§")
                    .append(article.getAuthorUsername()).append("§")
                    .append(article.getGroupName() == null ? "null" : article.getGroupName()) // Handle null group names
                    .append("\n");
        }

        // Encrypt the serialized data
        byte[] iv = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        byte[] encryptedData = encryptionHelper.encrypt(serializedData.toString().getBytes(StandardCharsets.UTF_8), iv);

        // Write the IV and encrypted data to the file
        try (FileWriter writer = new FileWriter(fileName)) {
            String encodedIv = Base64.getEncoder().encodeToString(iv);
            String encodedData = Base64.getEncoder().encodeToString(encryptedData);
            writer.write(encodedIv + "\n" + encodedData);
        }
    }

    /**
     * Restores articles from an encrypted backup file.
     *
     * @param fileName The name of the backup file.
     * @param merge If true, merges backup entries with current entries; otherwise, deletes current entries before restoring.
     * @param group The group name to filter by; if null, restores all articles.
     * @throws Exception if an error occurs during the restore process.
     */
    public void restoreArticles(String fileName, boolean merge, String group) throws Exception {
        String encodedIv;
        String encodedData;

        // Read the IV and encrypted data from the file
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            encodedIv = reader.readLine();
            encodedData = reader.readLine();
        }

        // Decode and decrypt the data
        byte[] iv = Base64.getDecoder().decode(encodedIv);
        byte[] encryptedData = Base64.getDecoder().decode(encodedData);
        byte[] decryptedData = encryptionHelper.decrypt(encryptedData, iv);

        // Deserialize the decrypted data
        String[] articlesData = new String(decryptedData, StandardCharsets.UTF_8).split("\n");

        if (!merge) {
            String clearQuery = "DELETE FROM help_articles";
            try (Statement stmt = connection.createStatement()) {
                stmt.executeUpdate(clearQuery);
            }
        }

        // Insert each article into the database, filtering by group if specified
        for (String articleData : articlesData) {
            String[] fields = articleData.split("§", 9);
            if (fields.length != 9) continue;

            String articleGroupName = fields[8].equals("null") ? null : fields[8]; // Interpret "null" as actual null

            // Check if the article should be restored based on the group filter
            if (group != null && (articleGroupName == null || !group.equals(articleGroupName))) {
                continue; // Skip articles not in the specified group
            }

            // Insert the article if it doesn't exist or if merge is false
            if (!merge || !doesArticleExist(fields[1])) { // Check if the article exists by title
                String insertArticle = "INSERT INTO help_articles (id, title, description, body, level, keywords, reference_links, author_username, group_name) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement pstmt = connection.prepareStatement(insertArticle)) {
                    pstmt.setLong(1, Long.parseLong(fields[0]));
                    pstmt.setString(2, fields[1]);
                    pstmt.setString(3, fields[2]);
                    pstmt.setString(4, fields[3]);
                    pstmt.setString(5, fields[4]);
                    pstmt.setString(6, fields[5]);
                    pstmt.setString(7, fields[6]);
                    pstmt.setString(8, fields[7]);
                    pstmt.setString(9, articleGroupName); // Handle null group names correctly
                    pstmt.executeUpdate();
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
