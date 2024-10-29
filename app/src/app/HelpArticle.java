package app;

import java.util.Set;
import java.util.HashSet;

/**
 * The HelpArticle class represents a help article in the application.
 * Each article has a unique identifier, title, description, body, level, keywords, and groups.
 * Articles can be created, viewed, edited, and deleted by admins and instructors.
 * Students can view articles based on their level.
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
    private Set<String> groups = new HashSet<>();
    private final String authorUsername;

    public HelpArticle(String title, String description, String body, String level, Set<String> keywords, Set<String> groups, String authorUsername) {
        this(0, title, description, body, level, keywords, groups, authorUsername);
    }
    
    // Constructor for fetching existing article with ID
    public HelpArticle(long id, String title, String description, String body, String level, Set<String> keywords, Set<String> groups, String authorUsername) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.body = body;
        this.level = level;
        this.keywords = (keywords != null) ? keywords : new HashSet<>();
        this.groups = (groups != null) ? groups : new HashSet<>();
        this.authorUsername = authorUsername;
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

    public Set<String> getGroups() {
        return groups;
    }

    public void setGroups(Set<String> groups) {
        this.groups = (groups != null) ? groups : new HashSet<>();
    }

    public String getAuthorUsername() {
        return authorUsername;
    }
}
