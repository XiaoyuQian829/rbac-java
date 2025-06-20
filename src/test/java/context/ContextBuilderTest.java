// === src/test/java/context/ContextBuilderTest.java ===

package context;

import core.PermissionsManager;
import model.UserContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import users.UserRegistryManager;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ContextBuilderTest validates the correct construction of UserContext
 * based on user identity, role validity, activation status, and permissions.
 *
 * Uses test config from:
 *   - config/UserRegistry.yaml
 *   - config/RolePermissions.yaml
 */
public class ContextBuilderTest {

    private ContextBuilder builder;

    /**
     * Initializes a fresh ContextBuilder before each test.
     * Loads from default config paths.
     */
    @BeforeEach
    public void setUp() {
        PermissionsManager pm = new PermissionsManager("config/RolePermissions.yaml");
        UserRegistryManager urm = new UserRegistryManager("config/UserRegistry.yaml");
        builder = new ContextBuilder(pm, urm);
    }

    /**
     * Validates that an active user with a valid role is loaded correctly.
     */
    @Test
    public void testValidUserContext() {
        UserContext ctx = builder.buildUserContext("alice");
        assertEquals("alice", ctx.getUserId());
        assertEquals("admin", ctx.getRole());
        assertTrue(ctx.hasPermission("admin.manage_users"));
    }

    /**
     * Ensures an inactive user triggers a RuntimeException.
     */
    @Test
    public void testInactiveUserThrowsException() {
        Exception exception = assertThrows(RuntimeException.class, () ->
                builder.buildUserContext("inactive_user"));
        assertTrue(exception.getMessage().contains("deactivated"));
    }

    /**
     * Ensures a non-existent user triggers a RuntimeException.
     */
    @Test
    public void testUnknownUserThrowsException() {
        Exception exception = assertThrows(RuntimeException.class, () ->
                builder.buildUserContext("nonexistent"));
        assertTrue(exception.getMessage().contains("not found"));
    }

    /**
     * Ensures a user with invalid role fails to load.
     */
    @Test
    public void testInvalidRoleThrowsException() {
        Exception exception = assertThrows(RuntimeException.class, () ->
                builder.buildUserContext("bad_role_user"));
        assertTrue(exception.getMessage().contains("Invalid role"));
    }

    /**
     * Verifies that permission check returns false for disallowed actions.
     */
    @Test
    public void testPermissionDeniedForUnknownAction() {
        UserContext ctx = builder.buildUserContext("alice");
        assertFalse(ctx.hasPermission("unauthorized.action"));
    }

    /**
     * Verifies that all granted permissions are true in the returned context.
     */
    @Test
    public void testPermissionSnapshotContents() {
        UserContext ctx = builder.buildUserContext("alice");
        Map<String, Boolean> perms = ctx.getPermissions();
        assertTrue(perms.containsKey("admin.manage_users"));
        assertTrue(perms.get("admin.manage_users"));
    }
}
