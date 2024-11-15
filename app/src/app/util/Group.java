package app.util;

import java.util.Set;
import java.util.HashSet;

/**
 * The Group class represents a special access group in the application.
 * Each group has a name, a list of admins, instructors, instructor admins, students, and articles.
 * The group manages access rights to its articles.
 * 
 * Author:
 *     - Jaafar Abdeen
 */
public class Group {
    private String name;
    private Set<String> admins; // usernames of group admins (including instructors with admin rights for the group)
    private Set<String> instructors; // usernames of instructors with viewing rights
    private Set<String> instructorAdmins; // usernames of instructors with admin rights for the group
    private Set<String> students; // usernames of students with viewing rights
    private Set<Long> articleIds; // IDs of articles in the group

    public Group(String name) {
        this.name = name;
        this.admins = new HashSet<>();
        this.instructors = new HashSet<>();
        this.instructorAdmins = new HashSet<>();
        this.students = new HashSet<>();
        this.articleIds = new HashSet<>();
    }

    // Getters and setters

    public String getName() {
        return name;
    }

    public Set<String> getAdmins() {
        return admins;
    }

    public Set<String> getInstructors() {
        return instructors;
    }

    public Set<String> getInstructorAdmins() {
        return instructorAdmins;
    }

    public Set<String> getStudents() {
        return students;
    }

    public Set<Long> getArticleIds() {
        return articleIds;
    }

    // Methods to add/remove users and articles

    public void addAdmin(String username) {
        admins.add(username);
    }

    public void removeAdmin(String username) {
        admins.remove(username);
    }

    public void addInstructor(String username) {
        instructors.add(username);
    }

    public void removeInstructor(String username) {
        instructors.remove(username);
    }

    public void addInstructorAdmin(String username) {
        instructorAdmins.add(username);
    }

    public void removeInstructorAdmin(String username) {
        instructorAdmins.remove(username);
    }

    public void addStudent(String username) {
        students.add(username);
    }

    public void removeStudent(String username) {
        students.remove(username);
    }

    public void addArticleId(Long articleId) {
        articleIds.add(articleId);
    }

    public void removeArticleId(Long articleId) {
        articleIds.remove(articleId);
    }
}
