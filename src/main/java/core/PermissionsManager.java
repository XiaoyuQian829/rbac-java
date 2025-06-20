// === src/main/java/core/PermissionsManager.java ===

package core;

import utils.YamlLoader;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PermissionsManager is responsible for managing the role-to-permission matrix.
 *
 * This class provides:
 * - Loading and saving permission mappings from YAML
 * - Querying whether a role has a specific permission
 * - Modifying permissions (grant/revoke)
 * - Logging permission changes for auditing
 *
 * The structure managed here is: role → { permission_key → boolean }
 */
public class PermissionsManager {

    private Map<String, Map<String, Boolean>> permissions = new HashMap<>();
    private final String path;
    private final String logPath = "rbac.log";

    /**
     * Constructs with default permissions file path.
     */
    public PermissionsManager() {
        this("config/RolePermissions.yaml");
    }

    /**
     * Constructs with a custom file path.
     *
     * @param path the YAML file to load/save permissions from
     */
    public PermissionsManager(String path) {
        this.path = path;
        load();
    }

    /**
     * Loads the role-permission matrix from disk.
     */
    public void load() {
        Map<String, Map<String, Boolean>> data = YamlLoader.loadPermissionMatrix(path);
        if (data != null) {
            permissions = data;
        } else {
            permissions = new HashMap<>();
        }
    }

    /**
     * Reloads the permission configuration from file.
     */
    public void reload() {
        load();
    }

    /**
     * Saves the current permission matrix to file.
     */
    public void save() {
        YamlLoader.save(path, permissions);
    }

    /**
     * Returns all defined role names.
     *
     * @return list of roles
     */
    public List<String> getAllRoles() {
        return permissions.keySet().stream().toList();
    }

    /**
     * Retrieves the permission map for a given role.
     *
     * @param role the role name
     * @return a map of permission keys to boolean values
     */
    public Map<String, Boolean> getRolePermissions(String role) {
        Map<String, Boolean> raw = permissions.get(role);
        if (raw == null) return new HashMap<>();
        return new HashMap<>(raw);
    }

    /**
     * Sets the permission map for a role.
     *
     * @param role  the role to update
     * @param perms the permission map to assign
     */
    public void setRolePermissions(String role, Map<String, Boolean> perms) {
        permissions.put(role, new HashMap<>(perms));
    }

    /**
     * Adds or updates a single permission value for a role.
     *
     * @param role the role name
     * @param key  the permission key
     * @param value true to grant, false to revoke
     */
    public void updatePermission(String role, String key, boolean value) {
        permissions.computeIfAbsent(role, r -> new HashMap<>()).put(key, value);
    }

    /**
     * Removes a specific permission from a role.
     *
     * @param role the role name
     * @param key  the permission key to remove
     */
    public void deletePermission(String role, String key) {
        Map<String, Boolean> perms = permissions.get(role);
        if (perms != null) {
            perms.remove(key);
        }
    }

    /**
     * Checks if a given role has a specific permission.
     *
     * @param role the role name
     * @param key  the permission key
     * @return true if the role has permission, false otherwise
     */
    public boolean hasPermission(String role, String key) {
        return permissions.getOrDefault(role, Map.of()).getOrDefault(key, false);
    }

    /**
     * Exports the full permission matrix.
     *
     * @return a copy of the full role-permission map
     */
    public Map<String, Map<String, Boolean>> export() {
        return new HashMap<>(permissions);
    }

    /**
     * Imports a new permission matrix and persists it.
     *
     * @param newData the new permission data to import
     */
    public void importPermissions(Map<String, Map<String, Boolean>> newData) {
        this.permissions = new HashMap<>(newData);
        save();
    }

    /**
     * Grants or revokes a specific permission with logging.
     *
     * @param role     the role name
     * @param key      permission key
     * @param value    true to grant, false to revoke
     * @param operator user performing the change
     */
    public void grant(String role, String key, boolean value, String operator) {
        permissions.computeIfAbsent(role, r -> new HashMap<>()).put(key, value);
        save();
        logGrantChange(role, key, value, operator);
    }

    /**
     * Logs a permission grant/revoke operation to a log file.
     *
     * @param role     affected role
     * @param key      permission key
     * @param value    true/false
     * @param operator who made the change
     */
    public void logGrantChange(String role, String key, boolean value, String operator) {
        try (FileWriter fw = new FileWriter(logPath, true)) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String msg = String.format("[%s] PERMISSION CHANGE: role=%s key=%s value=%s operator=%s\n",
                    timestamp, role, key, value, operator);
            fw.write(msg);
        } catch (IOException e) {
            System.err.println("Failed to write to log: " + e.getMessage());
        }
    }
}
