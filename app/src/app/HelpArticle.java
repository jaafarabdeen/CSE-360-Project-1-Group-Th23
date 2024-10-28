package app;

import java.util.Set;
import java.sql.SQLException;
import java.util.HashSet;

/**
 * The HelpArticle class represents a help article in the application.
 * Each article has a unique identifier, title, description, body, level, keywords, and groups.
 * Articles can be created, viewed, edited, and deleted by admins and instructors.
 * Students can view articles based on their level.
 * 
 * Author:
 *     - Ayush Kaushik
 */
public class HelpArticle {
    private long id;
    private String title;
    private String description;
    private String body;
    private String level; // Beginner, Intermediate, Advanced, Expert
    private Set<String> keywords;
    private Set<String> groups;
    private String authorUsername;
    private Set<String> referenceLinks;

    public HelpArticle(String title, String description, String body, String level, Set<String> keywords, Set<String> groups, Set<String> referenceLinks, String authorUsername) {
        this.title = title;
        this.description = description;
        this.body = body;
        this.level = level;
        this.keywords = keywords;
        this.groups = groups;
        this.referenceLinks = referenceLinks; // Initialize reference links
        this.authorUsername = authorUsername;
    }
    
    // For fetching
    public HelpArticle(long id, String title, String description, String body, String level, Set<String> keywords, Set<String> groups, Set<String> referenceLinks, String authorUsername) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.body = body;
        this.level = level;
        this.keywords = keywords;
        this.groups = groups;
        this.referenceLinks = referenceLinks; // Initialize reference links
        this.authorUsername = authorUsername;
    }
    
    public void setId(long id) {
        this.id = id;
        try {
            DatabaseHelper databaseHelper = new DatabaseHelper();
            databaseHelper.connectToDatabase();
            databaseHelper.updateArticle(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        this.keywords = keywords;
    }

    public Set<String> getGroups() {
        return groups;
    }

    public void setGroups(Set<String> groups) {
        this.groups = groups;
    }

    public String getAuthorUsername() {
        return authorUsername;
    }

    // Getter and Setter for referenceLinks
    public Set<String> getReferenceLinks() {
        return referenceLinks;
    }

    public void setReferenceLinks(Set<String> referenceLinks) {
        this.referenceLinks = referenceLinks;
    }
}
