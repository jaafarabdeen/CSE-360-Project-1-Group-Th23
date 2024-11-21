package app.test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import app.util.Group;
import app.User;

public class GroupTest {

    @Test
    public void testGroupCreation() {
        String groupName = "TestGroup";
        Group group = new Group(groupName);

        assertEquals(groupName, group.getName());
        assertTrue(group.getAdmins().isEmpty());
        assertTrue(group.getInstructors().isEmpty());
        assertTrue(group.getInstructorAdmins().isEmpty());
        assertTrue(group.getStudents().isEmpty());
        assertTrue(group.getArticleIds().isEmpty());
    }

    @Test
    public void testAddRemoveAdmin() {
        Group group = new Group("Group");
        group.addAdmin("admin1");
        assertTrue(group.getAdmins().contains("admin1"));

        group.addAdmin("admin2");
        group.removeAdmin("admin1");
        assertFalse(group.getAdmins().contains("admin1"));
        assertTrue(group.getAdmins().contains("admin2"));

        // Attempt to remove last admin should throw exception
        assertThrows(IllegalStateException.class, () -> {
            group.removeAdmin("admin2");
        });
    }

    @Test
    public void testAddRemoveInstructor() {
        Group group = new Group("Group");
        group.addInstructor("instructor1");
        assertTrue(group.getInstructors().contains("instructor1"));
        // The first instructor should also be added as instructorAdmin
        assertTrue(group.getInstructorAdmins().contains("instructor1"));

        group.addInstructor("instructor2");
        assertTrue(group.getInstructors().contains("instructor2"));

        group.removeInstructor("instructor1");
        assertFalse(group.getInstructors().contains("instructor1"));
    }

    @Test
    public void testAddRemoveInstructorAdmin() {
        Group group = new Group("Group");
        group.addInstructorAdmin("instructorAdmin1");
        assertTrue(group.getInstructorAdmins().contains("instructorAdmin1"));

        group.addInstructorAdmin("instructorAdmin2");
        group.removeInstructorAdmin("instructorAdmin1");
        assertFalse(group.getInstructorAdmins().contains("instructorAdmin1"));
        assertTrue(group.getInstructorAdmins().contains("instructorAdmin2"));

        // Attempt to remove last instructor admin should throw exception
        assertThrows(IllegalStateException.class, () -> {
            group.removeInstructorAdmin("instructorAdmin2");
        });
    }

    @Test
    public void testAddRemoveStudent() {
        Group group = new Group("Group");
        group.addStudent("student1");
        assertTrue(group.getStudents().contains("student1"));

        group.removeStudent("student1");
        assertFalse(group.getStudents().contains("student1"));
    }

    @Test
    public void testAddRemoveArticleId() {
        Group group = new Group("Group");
        group.addArticleId(1L);
        assertTrue(group.getArticleIds().contains(1L));

        group.removeArticleId(1L);
        assertFalse(group.getArticleIds().contains(1L));
    }

    @Test
    public void testCanUserViewDecryptedBody() {
        Group group = new Group("Group");
        group.addAdmin("admin1");
        group.addInstructorAdmin("instructorAdmin1");
        group.addInstructor("instructor1");
        group.addStudent("student1");

        User adminUser = new User("admin1", "pass", "");
        User instructorAdminUser = new User("instructorAdmin1", "pass", "");
        User instructorUser = new User("instructor1", "pass", "");
        User studentUser = new User("student1", "pass", "");
        User outsiderUser = new User("outsider", "pass", "");

        assertTrue(group.canUserViewDecryptedBody(adminUser));
        assertTrue(group.canUserViewDecryptedBody(instructorAdminUser));
        assertTrue(group.canUserViewDecryptedBody(instructorUser));
        assertTrue(group.canUserViewDecryptedBody(studentUser));
        assertFalse(group.canUserViewDecryptedBody(outsiderUser));
    }
}
