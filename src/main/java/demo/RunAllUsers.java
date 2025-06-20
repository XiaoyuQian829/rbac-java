// === src/main/java/demo/RunAllUsers.java ===

package demo;

import context.ContextBuilder;
import core.PermissionsManager;
import model.UserContext;
import users.UserRegistryManager;
import utils.YamlLoader;

import java.util.*;

/**
 * RunAllUsers is a CLI utility for displaying a full user-permission matrix.
 *
 * It reads all users and all available permissions from YAML configuration,
 * builds a contextual permission scope for each user,
 * and checks whether each user has access to each permission.
 *
 * ✔️ Allowed permissions are printed in green.
 * ✘ Denied permissions are printed in red.
 *
 * This tool is useful for:
 * - Verifying YAML-based access control configurations
 * - Demonstrating role-permission mappings
 * - Auditing expected vs actual access outcomes
 */
public class RunAllUsers {

    private static final String GREEN = "\u001B[32m";
    private static final String RED = "\u001B[31m";
    private static final String RESET = "\u001B[0m";

    public static void main(String[] args) {
        PermissionsManager permissionsManager = new PermissionsManager();
        UserRegistryManager userRegistryManager = new UserRegistryManager();
        ContextBuilder contextBuilder = new ContextBuilder(permissionsManager, userRegistryManager);

        // Load all users and permissions from configuration
        Set<String> allUsers = userRegistryManager.getAllUserIds();       // <- requires method: getAllUserIds()
        Set<String> allPermissions = YamlLoader.loadAllPermissions();     // <- requires method: loadAllPermissions()

        System.out.println("========= RBAC Permission Matrix =========\n");

        for (String userId : allUsers) {
            System.out.println("👤 User: " + userId);

            UserContext context;
            try {
                context = contextBuilder.buildUserContext(userId);
            } catch (RuntimeException e) {
                printWithColor("  [Error] " + e.getMessage(), RED);
                continue;
            }

            for (String permission : allPermissions) {
                boolean allowed = context.hasPermission(permission);
                if (allowed) {
                    printWithColor("    ✔ " + permission, GREEN);
                } else {
                    printWithColor("    ✘ " + permission, RED);
                }
            }

            System.out.println();
        }

        System.out.println("==========================================");
    }

    /**
     * Prints a line to the console using ANSI color codes.
     *
     * @param message the content to print
     * @param color ANSI color code (e.g., GREEN, RED)
     */
    private static void printWithColor(String message, String color) {
        System.out.println(color + message + RESET);
    }
}
