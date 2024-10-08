// User.java
import java.util.HashSet;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDateTime;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

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
    private byte[] passwordHash;
    private boolean oneTimePasswordFlag;
    private LocalDateTime oneTimePasswordExpiry;
    private String firstName;
    private String middleName;
    private String lastName;
    private String preferredName;
    private Map<String, String> topics; // topic -> level
    private Set<String> roles;

    public User(String username, String password, String role) {
        this.username = username;
        this.passwordHash = hashPassword(password);
        this.roles = new HashSet<>();
        if (!role.isEmpty()) {
            this.roles.add(role);
        }
        this.oneTimePasswordFlag = false;
        this.topics = new HashMap<>();
    }

    // Password hashing method using SHA-256
    private byte[] hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(password.getBytes());
        } catch (NoSuchAlgorithmException e) {
            // Handle exception
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Checks if the provided password matches the stored password hash.
     * 
     * @param password The password to check.
     * @return True if the password matches, false otherwise.
     */
    public boolean checkPassword(String password) {
        byte[] hash = hashPassword(password);
        return Arrays.equals(this.passwordHash, hash);
    }

    /**
     * Sets a new password for the user.
     * 
     * @param password The new password.
     */
    public void setPassword(String password) {
        this.passwordHash = hashPassword(password);
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

    @Override
    public String toString() {
        return username;
    }
}
