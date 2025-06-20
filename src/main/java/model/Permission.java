// === src/main/java/model/Permission.java ===

package model;

/**
 * Permission represents a single atomic action that can be granted or denied
 * to a role or user in the RBAC system.
 *
 * Example:
 *   Key:         "admin.manage_users"
 *   Description: "Allows user management operations"
 *   Module:      "admin"
 *
 * This class is useful for:
 * - rendering permission catalogs
 * - documenting permission usage
 * - mapping permission keys to descriptions for UI
 */
public class Permission {

    private final String key;         // e.g., "admin.manage_users"
    private String description;       // e.g., "Allows managing users"
    private String module;            // e.g., "admin"

    /**
     * Constructs a basic permission with key only.
     *
     * @param key the permission string identifier
     */
    public Permission(String key) {
        this.key = key;
    }

    /**
     * Constructs a permission with key, description, and module.
     *
     * @param key         the permission string (must be unique)
     * @param description optional human-readable description
     * @param module      optional grouping/category
     */
    public Permission(String key, String description, String module) {
        this.key = key;
        this.description = description;
        this.module = module;
    }

    public String getKey() {
        return key;
    }

    public String getDescription() {
        return description != null ? description : "";
    }

    public String getModule() {
        return module != null ? module : "";
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setModule(String module) {
        this.module = module;
    }

    @Override
    public String toString() {
        return "Permission{" +
                "key='" + key + '\'' +
                ", description='" + description + '\'' +
                ", module='" + module + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Permission)) return false;
        Permission other = (Permission) o;
        return this.key.equals(other.key);
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }
}
