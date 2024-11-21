package app.test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import app.util.Invitation;

import java.util.Set;
import java.util.HashSet;

public class InvitationTest {

    @Test
    public void testInvitationCreation() {
        String token = "invitationToken";
        Set<String> roles = new HashSet<>();
        roles.add("Student");
        roles.add("Instructor");

        Invitation invitation = new Invitation(token, roles);

        assertEquals(token, invitation.getToken());
        assertEquals(roles, invitation.getRoles());
    }
}
