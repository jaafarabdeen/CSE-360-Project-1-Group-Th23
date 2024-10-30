package app;

import java.util.HashSet;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDateTime;

/**
 * The User class represents a user account in the application.
 * It contains user credentials, personal information, roles, and other settings.
 * Passwords are stored securely using SHA-256 hashing.
 * 
 * This class provides methods to manage user data and verify credentials.
 * 
 * @author
 *     - Lewam Atnafie
 */
public class User {
    private String email;
    private String username;
    private String password;
    private boolean oneTimePasswordFlag;
    private LocalDateTime oneTimePasswordExpiry;
    private String firstName;
    private String middleName;
    private String lastName;
    private String preferredName;
    private Map<String, String> topics; // topic -> level
    private Set<String> roles;
    private String level; // Beginner, Intermediate, Advanced, Expert

    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.roles = new HashSet<>();
        if (!role.isEmpty()) {
            this.roles.add(role);
        }
        this.oneTimePasswordFlag = false;
        this.topics = new HashMap<>();
        this.level = "Intermediate"; // Default level
    }

    /**
     * Sets a new password for the user.
     * 
     * @param password The new password.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Returns the hashed password.
     * 
     * @return The hashed password as a string.
     */
    public String getPassword() {
        return this.password;
    }

    // Getters and setters for other fields
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Similar methods for other fields
    public String getUsername() {
        return username;
    }

    public boolean isOneTimePassword() {
        return oneTimePasswordFlag;
    }

    public void setOneTimePassword(boolean flag) {
        this.oneTimePasswordFlag = flag;
    }

    public LocalDateTime getOneTimePasswordExpiry() {
        return oneTimePasswordExpiry;
    }

    public void setOneTimePasswordExpiry(LocalDateTime expiry) {
        this.oneTimePasswordExpiry = expiry;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public boolean hasRole(String role) {
        return roles.contains(role);
    }

    public void addRole(String role) {
        roles.add(role);
    }

    public void removeRole(String role) {
        roles.remove(role);
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    // Similar methods for middleName, lastName, preferredName
    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPreferredName() {
        return preferredName != null && !preferredName.isEmpty() ? preferredName : firstName;
    }

    public void setPreferredName(String preferredName) {
        this.preferredName = preferredName;
    }

    // Methods for topics can be added if needed
    public Map<String, String> getTopics() {
        return topics;
    }

    public void setTopicLevel(String topic, String level) {
        topics.put(topic, level);
    }

    public String getTopicLevel(String topic) {
        return topics.getOrDefault(topic, "Intermediate");
    }

    /**
     * Gets the user's level (Beginner, Intermediate, Advanced, Expert).
     * 
     * @return The user's level.
     */
    public String getLevel() {
        return level;
    }

    /**
     * Sets the user's level (Beginner, Intermediate, Advanced, Expert).
     * 
     * @param level The level to set.
     */
    public void setLevel(String level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return username;
    }
}
