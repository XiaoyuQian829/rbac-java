// === src/main/java/utils/YamlLoader.java ===

package utils;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;

/**
 * YamlLoader is a utility class for loading and saving YAML files.
 * It uses SnakeYAML to handle structured data such as:
 *  - permission matrices (Map<String, Map<String, Boolean>>)
 *  - user registries (Map<String, Map<String, Object>>)
 *
 * This class provides:
 *  - consistent block formatting for output
 *  - error-handled generic loading methods
 *  - domain-specific helper for permission matrices
 */
public class YamlLoader {

    private static final DumperOptions OPTIONS = new DumperOptions();

    static {
        OPTIONS.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);  // Use key: value format
        OPTIONS.setPrettyFlow(true);
        OPTIONS.setIndent(2);
    }

    private static final Yaml yaml = new Yaml(OPTIONS);

    /**
     * Loads a YAML file and casts the result to the specified class type.
     *
     * @param path  file path to load
     * @param clazz expected return type (e.g., Map.class)
     * @param <T>   inferred type to return
     * @return loaded object, or null on failure
     */
    @SuppressWarnings("unchecked")
    public static <T> T load(String path, Class<T> clazz) {
        try (InputStream input = new FileInputStream(path)) {
            Object data = yaml.load(input);
            return (T) data;
        } catch (IOException e) {
            System.err.println("❌ Failed to load YAML: " + path);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Saves an object (Map or List) to a YAML file.
     *
     * @param path file path to write
     * @param data any serializable structure
     */
    public static void save(String path, Object data) {
        try (Writer writer = new FileWriter(path)) {
            yaml.dump(data, writer);
        } catch (IOException e) {
            System.err.println("❌ Failed to save YAML: " + path);
            e.printStackTrace();
        }
    }

    /**
     * Loads a permission matrix: Map<role, Map<permission, Boolean>>
     *
     * @param path path to RolePermissions.yaml
     * @return parsed permission matrix
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Map<String, Boolean>> loadPermissionMatrix(String path) {
        try (InputStream input = new FileInputStream(path)) {
            return (Map<String, Map<String, Boolean>>) yaml.load(input);
        } catch (IOException e) {
            System.err.println("❌ Failed to load permission matrix: " + path);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Loads all unique permission keys across all roles
     * from the permission matrix (boolean format).
     *
     * @return a set of all declared permissions
     */
    public static Set<String> loadAllPermissions() {
        Map<String, Map<String, Boolean>> matrix = loadPermissionMatrix("config/RolePermissions.yaml");
        Set<String> all = new HashSet<>();
        if (matrix != null) {
            for (Map<String, Boolean> perms : matrix.values()) {
                all.addAll(perms.keySet());
            }
        }
        return all;
    }
}
