// HelpArticle.java
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
 */
public class HelpArticle {
    private static long idCounter = 0;
    private long id;
    private String title;
    private String description;
    private String body;
    private String level; // Beginner, Intermediate, Advanced, Expert
    private Set<String> keywords;
    private Set<String> groups;
    private String authorUsername;

    public HelpArticle(String title, String description, String body, String level, Set<String> keywords, Set<String> groups, String authorUsername) {
        this.id = ++idCounter;
        this.title = title;
        this.description = description;
        this.body = body;
        this.level = level;
        this.keywords = keywords;
        this.groups = groups;
        this.authorUsername = authorUsername;
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
}
