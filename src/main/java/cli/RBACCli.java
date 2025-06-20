// === src/main/java/cli/RBACCli.java ===

package cli;

import context.ContextBuilder;
import core.PermissionsManager;
import model.UserContext;
import users.UserRegistryManager;

import java.util.Scanner;

/**
 * RBACCli is a command-line interface for interactively testing the Role-Based Access Control system.
 *
 * It performs:
 * - Prompts for a user ID
 * - Builds a permission-aware UserContext
 * - Allows runtime permission checks via console input
 * - Supports admin console access for privileged users
 * - Supports :reload to hot-reload YAML configs
 *
 * This tool demonstrates the RBAC flow:
 *   user_id → role → permission matrix → runtime enforcement
 */
public class RBACCli {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter user ID: ");
        String userId = scanner.nextLine().trim();

        // Initialize permission system and user registry
        PermissionsManager permissionsManager = new PermissionsManager();
        UserRegistryManager userRegistryManager = new UserRegistryManager();
        ContextBuilder builder = new ContextBuilder(permissionsManager, userRegistryManager);

        // Attempt to build user context
        UserContext context;
        try {
            context = builder.buildUserContextWithLog(userId);
        } catch (Exception e) {
            System.out.println("❌ Failed to build context: " + e.getMessage());
            return;
        }

        System.out.println("✅ Permission check ready.");
        System.out.println("Type a permission key (e.g., 'admin.manage_users') to test access.");
        System.out.println("Type ':admin' to enter admin console (if authorized).");
        System.out.println("Type ':reload' to re-fetch YAML and rebuild context.");
        System.out.println("Type 'exit' to quit.\n");

        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("exit")) {
                System.out.println("👋 Exiting.");
                break;

            } else if (input.equals(":admin")) {
                if (context.hasPermission("admin.manage_users")) {
                    System.out.println("🔐 Entering AdminCommandConsole...");
                    try {
                        Class<?> adminConsole = Class.forName("admin.AdminCommandConsole");
                        adminConsole.getMethod("mainWithOperator", String.class)
                                    .invoke(null, userId);
                    } catch (Exception e) {
                        System.err.println("❌ Failed to launch admin console: " + e.getMessage());
                    }
                    break;
                } else {
                    System.out.println("⛔ You are not authorized to enter admin mode.");
                }

            } else if (input.equals(":reload")) {
                System.out.println("🔄 Reloading YAML and rebuilding user context...");
                permissionsManager.reload();
                userRegistryManager.reload();
                try {
                    context = builder.buildUserContextWithLog(userId);
                    System.out.println("✅ Reload successful.");
                } catch (Exception e) {
                    System.out.println("❌ Reload failed: " + e.getMessage());
                    break;
                }

            } else {
                boolean allowed = context.hasPermission(input);
                System.out.println(allowed ? "✅ ALLOWED" : "⛔ DENIED");
            }
        }
    }
}



/*
====================================
🧪 CLI Usage Example (RBACCli)
====================================

1. Compile and run:

   mvn clean compile
   mvn exec:java -Dexec.mainClass=cli.RBACCli

2. When prompted:

   Enter user ID: alice

   ✅ User 'alice' has role: admin
   🔑 Permissions:
      ✔ admin.manage_users
      ✔ admin.trigger_global_killswitch
      ...

3. Test access:

   >> admin.manage_users
   ✔️ ALLOWED

   >> trader.view_portfolio
   ❌ DENIED

   >> exit
   👋 Bye!
*/
