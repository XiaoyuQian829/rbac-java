// === src/main/java/context/ContextBuilder.java ===

package context;

import core.PermissionsManager;
import model.UserContext;
import users.UserRegistryManager;

import java.util.Map;
import java.util.Set;

/**
 * ContextBuilder is responsible for constructing a permission-aware
 * UserContext object by combining:
 *
 * - User metadata from registry (role, client_id, active)
 * - Role-permission mapping from PermissionsManager
 *
 * It performs critical validation steps:
 * - Ensures user exists in registry
 * - Checks active status
 * - Verifies role is defined in permission matrix
 *
 * This class is the key link between user identity and policy enforcement.
 */
public class ContextBuilder {

    private final PermissionsManager permissionsManager;
    private final UserRegistryManager userRegistry;

    /**
     * Constructor for ContextBuilder.
     *
     * @param pm  the permissions manager (resolves role → permissions)
     * @param urm the user registry manager (resolves userId → metadata)
     */
    public ContextBuilder(PermissionsManager pm, UserRegistryManager urm) {
        this.permissionsManager = pm;
        this.userRegistry = urm;
    }

    /**
     * Builds a UserContext for the given user ID with full validation.
     *
     * @param userId the ID of the user
     * @return UserContext object with resolved permissions
     * @throws RuntimeException if user is missing, inactive, or has an invalid role
     */
    public UserContext buildUserContext(String userId) {
        // 1. Load user metadata from registry
        Map<String, Object> userEntry = userRegistry.getUser(userId);
        if (userEntry == null) {
            throw new RuntimeException("User not found: " + userId);
        }

        // 2. Check active flag
        boolean isActive = Boolean.TRUE.equals(userEntry.get("active"));
        if (!isActive) {
            throw new RuntimeException("User account is deactivated: " + userId);
        }

        // 3. Get and validate role
        String role = (String) userEntry.get("role");
        if (!isValidRole(role)) {
            throw new RuntimeException("Invalid role: " + role);
        }

        // 4. Optional client_id
        String clientId = (String) userEntry.getOrDefault("client_id", null);

        // 5. Load permission map
        Map<String, Boolean> perms = permissionsManager.getRolePermissions(role);

        return new UserContext(userId, role, perms, clientId);
    }

    /**
     * Builds a UserContext with verbose logging to stdout.
     *
     * @param userId the user ID
     * @return fully resolved UserContext
     * @throws RuntimeException if user is invalid
     */
    public UserContext buildUserContextWithLog(String userId) {
        System.out.println("🔍 Building context for user: " + userId);

        Map<String, Object> userEntry = userRegistry.getUser(userId);
        if (userEntry == null) {
            throw new RuntimeException("❌ User not found: " + userId);
        }

        boolean isActive = Boolean.TRUE.equals(userEntry.get("active"));
        if (!isActive) {
            throw new RuntimeException("❌ User is deactivated: " + userId);
        }

        String role = (String) userEntry.get("role");
        if (!isValidRole(role)) {
            throw new RuntimeException("❌ Invalid role: " + role);
        }

        String clientId = (String) userEntry.getOrDefault("client_id", null);
        Map<String, Boolean> perms = permissionsManager.getRolePermissions(role);

        System.out.println("✅ User '" + userId + "' has role: " + role);
        System.out.println("✅ Assigned client_id: " + clientId);
        System.out.println("🔑 Granted permissions:");
        perms.forEach((key, val) -> {
            if (Boolean.TRUE.equals(val)) {
                System.out.println("   ✔ " + key);
            }
        });

        return new UserContext(userId, role, perms, clientId);
    }

    /**
     * Checks if a role is valid (exists in the permission matrix).
     *
     * @param role the role to validate
     * @return true if defined; false otherwise
     */
    private boolean isValidRole(String role) {
        Set<String> definedRoles = Set.copyOf(permissionsManager.getAllRoles());
        return definedRoles.contains(role);
    }
}
