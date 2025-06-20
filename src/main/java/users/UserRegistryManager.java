// === src/main/java/users/UserRegistryManager.java ===

package users;

import utils.YamlLoader;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * UserRegistryManager is responsible for loading and managing user records
 * from the user registry YAML file (UserRegistry.yaml).
 *
 * Each user entry includes:
 *   - role: assigned role name
 *   - client_id: associated account or client scope (optional)
 *   - active: whether the account is enabled
 *
 * Example YAML:
 *   alice:
 *     role: admin
 *     client_id: A001
 *     active: true
 */
public class UserRegistryManager {

    private final String path;
    private final String logPath = "rbac.log";
    private Map<String, Map<String, Object>> registry;

    /**
     * Constructs with default file path: config/UserRegistry.yaml
     */
    public UserRegistryManager() {
        this("config/UserRegistry.yaml");
    }

    /**
     * Constructs with custom path to the user registry file.
     *
     * @param path location of the YAML file
     */
    public UserRegistryManager(String path) {
        this.path = path;
        load();
    }

    /**
     * Loads user registry from disk into memory.
     * Overwrites any existing in-memory data.
     */
    public void load() {
        registry = YamlLoader.load(path, Map.class);
        if (registry == null) {
            registry = new HashMap<>();
        }
    }

    /**
     * Manually forces reload from file.
     */
    public void reload() {
        load();
    }

    /**
     * Persists the current in-memory user registry to YAML.
     */
    public void save() {
        YamlLoader.save(path, registry);
    }

    /**
     * Returns metadata map for a specific user ID.
     *
     * @param userId the user to look up
     * @return map of fields like role, client_id, active
     */
    public Map<String, Object> getUser(String userId) {
        return registry.get(userId);
    }

    /**
     * Adds or updates a user record.
     *
     * @param userId   user ID
     * @param role     assigned role
     * @param clientId associated client (optional)
     * @param active   account status
     * @param operator actor who made the change
     */
    public void addUser(String userId, String role, String clientId, boolean active, String operator) {
        Map<String, Object> user = new HashMap<>();
        user.put("role", role);
        user.put("client_id", clientId);
        user.put("active", active);
        registry.put(userId, user);
        save();
        logUserAdd(userId, role, clientId, active, operator);
    }

    /**
     * Returns all user records.
     *
     * @return user ID → metadata map
     */
    public Map<String, Map<String, Object>> getAllUsers() {
        return registry;
    }

    /**
     * Returns all user IDs from the registry.
     *
     * @return a set of user IDs
     */
    public Set<String> getAllUserIds() {
        return registry.keySet();
    }

    /**
     * Checks whether a user account is active.
     *
     * @param userId the user to check
     * @return true if active; false otherwise
     */
    public boolean isActive(String userId) {
        Map<String, Object> user = getUser(userId);
        if (user == null) return false;
        return Boolean.TRUE.equals(user.get("active"));
    }

    /**
     * Toggles the active status of a user.
     *
     * @param userId   the user to update
     * @param active   new active status
     * @param operator actor who made the change
     */
    public void toggleActive(String userId, boolean active, String operator) {
        Map<String, Object> user = registry.get(userId);
        if (user != null) {
            user.put("active", active);
            save();
            logToggleStatus(userId, active, operator);
        }
    }

    /**
     * Logs user creation or update with metadata.
     */
    private void logUserAdd(String userId, String role, String clientId, boolean active, String operator) {
        try (FileWriter fw = new FileWriter(logPath, true)) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String msg = String.format("[%s] USER ADD: user=%s role=%s client_id=%s active=%s operator=%s\n",
                    timestamp, userId, role, clientId, active, operator);
            fw.write(msg);
        } catch (IOException e) {
            System.err.println("Failed to write user add log: " + e.getMessage());
        }
    }

    /**
     * Logs active status toggle with metadata.
     */
    private void logToggleStatus(String userId, boolean active, String operator) {
        try (FileWriter fw = new FileWriter(logPath, true)) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String msg = String.format("[%s] USER STATUS TOGGLE: user=%s active=%s operator=%s\n",
                    timestamp, userId, active, operator);
            fw.write(msg);
        } catch (IOException e) {
            System.err.println("Failed to write status toggle log: " + e.getMessage());
        }
    }
}

