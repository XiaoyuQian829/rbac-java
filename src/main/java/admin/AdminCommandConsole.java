// === src/main/java/admin/AdminCommandConsole.java ===

package admin;

import core.PermissionsManager;
import users.UserRegistryManager;

import java.util.Map;
import java.util.Scanner;

/**
 * AdminCommandConsole provides an interactive command-line interface
 * to manage users and role-permission assignments.
 *
 * This class supports both:
 * - main() for standalone launch
 * - mainWithOperator(String) for authenticated RBACCli injection
 */
public class AdminCommandConsole {

    /**
     * Standalone CLI entry — fallback if launched directly.
     * Prompts for operator ID.
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter operator ID: ");
        String operator = scanner.nextLine().trim();
        mainWithOperator(operator);
    }

    /**
     * Authenticated CLI entry with operator ID pre-injected.
     * Used by RBACCli.
     *
     * @param operator current logged-in user
     */
    public static void mainWithOperator(String operator) {
        Scanner scanner = new Scanner(System.in);
        PermissionsManager permissionsManager = new PermissionsManager();
        UserRegistryManager userRegistryManager = new UserRegistryManager();

        System.out.println("Welcome, " + operator + ". Type 'help' for commands.");

        while (true) {
            System.out.print("admin> ");
            String input = scanner.nextLine().trim();
            if (input.equals("exit")) break;

            if (input.equals("help")) {
                System.out.println("Commands:");
                System.out.println("  grant <role> <permission> <true/false>");
                System.out.println("  adduser <user_id> <role> <client_id> <true/false>");
                System.out.println("  listusers");
                System.out.println("  listperms <role>");
                System.out.println("  reload");
                System.out.println("  exit");
                continue;
            }

            if (input.startsWith("grant ")) {
                String[] parts = input.split("\\s+");
                if (parts.length == 4) {
                    String role = parts[1];
                    String perm = parts[2];
                    boolean value = Boolean.parseBoolean(parts[3]);
                    permissionsManager.grant(role, perm, value, operator);  // 🔁 一行搞定
                    System.out.printf("✅ Permission '%s' for role '%s' set to %s%n", perm, role, value);
                } else {
                    System.out.println("⚠️  Usage: grant <role> <permission> <true/false>");
                }
                continue;
            }
            
            if (input.startsWith("adduser ")) {
                String[] parts = input.split("\\s+");
                if (parts.length == 5) {
                    String userId = parts[1];
                    String role = parts[2];
                    String clientId = parts[3];
                    boolean active = Boolean.parseBoolean(parts[4]);
                    userRegistryManager.addUser(userId, role, clientId, active, operator);
                    System.out.printf("✅ User '%s' added with role '%s'%n", userId, role);
                } else {
                    System.out.println("⚠️  Usage: adduser <user_id> <role> <client_id> <true/false>");
                }
                continue;
            }

            if (input.equals("listusers")) {
                System.out.println("📋 Users:");
                for (String userId : userRegistryManager.getAllUsers().keySet()) {
                    Map<String, Object> user = userRegistryManager.getUser(userId);
                    System.out.printf("  - %s → %s (%s) active=%s%n",
                            userId,
                            user.get("role"),
                            user.get("client_id"),
                            user.get("active"));
                }
                continue;
            }

            if (input.startsWith("listperms ")) {
                String[] parts = input.split("\\s+");
                if (parts.length == 2) {
                    String role = parts[1];
                    Map<String, Boolean> perms = permissionsManager.getRolePermissions(role);
                    System.out.printf("📋 Permissions for role '%s':%n", role);
                    for (Map.Entry<String, Boolean> entry : perms.entrySet()) {
                        System.out.printf("  - %s = %s%n", entry.getKey(), entry.getValue());
                    }
                } else {
                    System.out.println("⚠️  Usage: listperms <role>");
                }
                continue;
            }

            if (input.equals("reload")) {
                permissionsManager.reload();
                userRegistryManager.reload();
                System.out.println("🔁 Reloaded permission matrix and user registry.");
                continue;
            }

            System.out.println("❓ Unknown command. Type 'help' for available commands.");
        }

        System.out.println("👋 Exiting admin console.");
    }
}

