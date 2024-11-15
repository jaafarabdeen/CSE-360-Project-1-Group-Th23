package app;

import java.util.Set;
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

    // Constructor for creating a new article
    public HelpArticle(String title, String description, String body, String level, Set<String> keywords, Set<String> referenceLinks, String authorUsername, String groupName) {
        this(0, title, description, body, level, keywords, referenceLinks, authorUsername, groupName);
    }

    public HelpArticle(long id, String title, String description, String body, String level, Set<String> keywords, Set<String> referenceLinks, String authorUsername, String groupName) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.body = body;
        this.level = level;
        this.keywords = keywords;
        this.referenceLinks = referenceLinks;
        this.authorUsername = authorUsername;
        this.groupName = groupName;
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

    public String getBody() {
        return body;
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
}
