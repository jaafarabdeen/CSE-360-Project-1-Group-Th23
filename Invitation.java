// Invitation.java
import java.util.Set;

/**
 * The Invitation class represents an invitation token used for registering new users.
 * Each invitation contains a token string and a set of roles assigned to the new user.
 * 
 * Author:
 *     - Pragya Kumari
 *     - Aaryan Gaur
 */
public class Invitation {
    private String token;
    private Set<String> roles;

    public Invitation(String token, Set<String> roles) {
        this.token = token;
        this.roles = roles;
    }

    public String getToken() {
        return token;
    }

    public Set<String> getRoles() {
        return roles;
    }
}
