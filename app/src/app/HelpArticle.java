package app;

import java.util.Set;
import app.util.Group;
import java.util.HashSet;

/**
 * The HelpArticle class represents a help article in the application.
 * Each article has a unique identifier, title, description, body, level, keywords, and groups.
 * Articles can be created, viewed, edited, and deleted by admins and instructors.
 * Students can view articles based on their level.
 * Articles can belong to special access groups where the body is encrypted, and access is restricted.
 * 
 * Author:
 *     - Ayush Kaushik
 *     - Jaafar Abdeen
 */
public class HelpArticle {
    private long id;
    private String title;
    private String description;
    private String body;
    private String level; // Beginner, Intermediate, Advanced, Expert
    private Set<String> keywords = new HashSet<>();
    private String groupName; // null if not in any special access group
    private final String authorUsername;
    private Set<String> referenceLinks;
    private boolean isEncrypted; // New field to indicate if the article is encrypted

    // Constructor for creating a new article
    public HelpArticle(String title, String description, String body, String level, Set<String> keywords, Set<String> referenceLinks, String authorUsername, String groupName, boolean isEncrypted) {
        this(0, title, description, body, level, keywords, referenceLinks, authorUsername, groupName, isEncrypted);
    }

    // Updated constructor to include isEncrypted parameter
    public HelpArticle(long id, String title, String description, String body, String level, Set<String> keywords, Set<String> referenceLinks, String authorUsername, String groupName, boolean isEncrypted) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.body = body;
        this.level = level;
        this.keywords = keywords;
        this.referenceLinks = referenceLinks;
        this.authorUsername = authorUsername;
        this.groupName = groupName;
        this.isEncrypted = isEncrypted; // Initialize the new field
    }

    // Setters for all fields except immutable fields like authorUsername
    public void setId(long id) {
        this.id = id;
    }

    // Getters and setters for all fields
    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Updated getBody method to handle encryption
    public String getBody() {
        return body;
    }

    // New method to get decrypted body based on user access
    public String getDecryptedBody(User user, Group group) {
        if (isEncrypted) {
            // Check if user has rights to decrypt
            if (group != null && group.canUserViewDecryptedBody(user)) {
                return decryptBody(body);
            } else {
                return "You do not have access to view this article body.";
            }
        } else {
            return body;
        }
    }

    // Placeholder for decryption logic
    private String decryptBody(String encryptedBody) {
        // Implement decryption logic here
        return encryptedBody; // For now, just return as is
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Set<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(Set<String> keywords) {
        this.keywords = (keywords != null) ? keywords : new HashSet<>();
    }

    public Set<String> getReferenceLinks() {
        return referenceLinks;
    }

    public void setReferenceLinks(Set<String> referenceLinks) {
        this.referenceLinks = referenceLinks;
    }

    public String getAuthorUsername() {
        return authorUsername;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public boolean isEncrypted() {
        return isEncrypted;
    }

    public void setEncrypted(boolean isEncrypted) {
        this.isEncrypted = isEncrypted;
    }
}
