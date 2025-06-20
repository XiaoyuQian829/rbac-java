// === src/main/java/model/Role.java ===

package model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Role represents a named role in the RBAC system (e.g., admin, trader),
 * along with its assigned set of permissions.
 *
 * Permissions are stored as a map from permission key to boolean value.
 * Example:
 *   "admin.manage_users" → true
 *   "trader.view_portfolio" → false
 *
 * This class is typically used for:
 * - role editing tools
 * - initializing RolePermissions.yaml
 * - UI visualization or policy updates
 */
public class Role {

    private final String name;
    private final Map<String, Boolean> permissions;

    /**
     * Constructs an empty role with no permissions.
     *
     * @param name the role name (e.g., "admin", "auditor")
     */
    public Role(String name) {
        this.name = name;
        this.permissions = new HashMap<>();
    }

    /**
     * Constructs a role with an existing permission map.
     *
     * @param name the role name
     * @param permissions full permission map
     */
    public Role(String name, Map<String, Boolean> permissions) {
        this.name = name;
        this.permissions = new HashMap<>(permissions);
    }

    /**
     * Adds or updates a permission.
     *
     * @param permissionKey the permission string
     */
    public void addPermission(String permissionKey) {
        permissions.put(permissionKey, true);
    }

    /**
     * Removes a permission key (if it exists).
     *
     * @param permissionKey the permission string
     */
    public void removePermission(String permissionKey) {
        permissions.remove(permissionKey);
    }

    /**
     * Explicitly sets a permission value (true or false).
     *
     * @param permissionKey the permission string
     * @param value true to grant; false to deny
     */
    public void setPermission(String permissionKey, boolean value) {
        permissions.put(permissionKey, value);
    }

    /**
     * Checks if the role has the given permission granted.
     *
     * @param permissionKey the permission to check
     * @return true if granted; false otherwise
     */
    public boolean hasPermission(String permissionKey) {
        return permissions.getOrDefault(permissionKey, false);
    }

    /**
     * Returns an unmodifiable map of permissions.
     *
     * @return permission map
     */
    public Map<String, Boolean> getPermissions() {
        return Collections.unmodifiableMap(permissions);
    }

    /**
     * @return role name
     */
    public String getName() {
        return name;
    }

    /**
     * Prints summary of role.
     */
    @Override
    public String toString() {
        return "Role{name='" + name + "', permissions=" + permissions + "}";
    }
}
