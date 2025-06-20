// === src/main/java/model/UserContext.java ===

package model;

import java.util.Map;

/**
 * UserContext represents the runtime identity of a user during a request or session.
 * It contains:
 * - userId: the username or login ID
 * - role: the role assigned to the user (e.g., admin, trader)
 * - clientId: (optional) the client or account the user is operating under
 * - permissions: the resolved map of permission keys → boolean (granted or not)
 *
 * This class is the core object for access control checks during execution.
 */
public class UserContext {

    private final String userId;
    private final String role;
    private final Map<String, Boolean> permissions;
    private final String clientId;

    /**
     * Constructor for a complete user context snapshot.
     *
     * @param userId     the user ID
     * @param role       the assigned role
     * @param permissions resolved permissions for this user (copied from role)
     * @param clientId   client/account context (optional)
     */
    public UserContext(String userId, String role, Map<String, Boolean> permissions, String clientId) {
        this.userId = userId;
        this.role = role;
        this.permissions = permissions;
        this.clientId = clientId;
    }

    /**
     * Checks whether this user has a given permission.
     *
     * @param key the permission key (e.g., "admin.manage_users")
     * @return true if granted; false otherwise
     */
    public boolean hasPermission(String key) {
        return permissions.getOrDefault(key, false);
    }

    // === Getters ===

    /**
     * @return user ID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @return role name
     */
    public String getRole() {
        return role;
    }

    /**
     * @return permission map (read-only usage)
     */
    public Map<String, Boolean> getPermissions() {
        return permissions;
    }

    /**
     * @return client ID (can be null)
     */
    public String getClientId() {
        return clientId;
    }

    /**
     * Debug print of user context.
     *
     * @return string summary
     */
    @Override
    public String toString() {
        return "UserContext{" +
                "userId='" + userId + '\'' +
                ", role='" + role + '\'' +
                ", clientId='" + clientId + '\'' +
                ", permissions=" + permissions.keySet() +
                '}';
    }
}
