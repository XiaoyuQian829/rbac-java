// === src/main/java/demo/RBACTestConsole.java ===

package demo;

import context.ContextBuilder;
import core.PermissionsManager;
import model.UserContext;
import users.UserRegistryManager;

import java.util.Scanner;

/**
 * RBACTestConsole simulates any role (auditor, risker, testengine, etc.)
 * within the RBAC system.
 *
 * Usage: java demo.RBACTestConsole <userId>
 * Or via Maven profile: mvn compile exec:java -Prisker
 */
public class RBACTestConsole {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        PermissionsManager permissionsManager = new PermissionsManager();
        UserRegistryManager userRegistryManager = new UserRegistryManager();
        ContextBuilder builder = new ContextBuilder(permissionsManager, userRegistryManager);

        String userId = args.length > 0 ? args[0].trim() : null;
        if (userId == null || userId.isEmpty()) {
            System.out.print("Enter test user ID: ");
            userId = scanner.nextLine().trim();
        }

        UserContext context = builder.buildUserContextWithLog(userId);
        System.out.println("👤 Context initialized for user: " + userId);

        while (true) {
            System.out.print("rbac> ");
            String input = scanner.nextLine().trim();

            if (input.equals("exit")) {
                System.out.println("👋 Exiting test console.");
                break;
            }

            if (input.equals("help")) {
                System.out.println("🔧 Commands:");
                System.out.println("  whoami            → show current user identity");
                System.out.println("  permissions       → show all granted permissions");
                System.out.println("  viewlog           → simulate audit log access");
                System.out.println("  simulate-action   → simulate behavior based on role");
                System.out.println("  can <perm>        → check permission explicitly");
                System.out.println("  switch <userId>   → switch context to another user");
                System.out.println("  help              → show available commands");
                System.out.println("  exit              → quit console");
                continue;
            }

            if (input.equals("whoami")) {
                System.out.printf("👤 You are: %s | role=%s | client_id=%s%n",
                        userId, context.getRole(), context.getClientId());
                continue;
            }

            if (input.equals("permissions")) {
                System.out.println("🔑 Permissions for " + userId + ":");
                context.getPermissions().forEach((k, v) -> {
                    System.out.printf("  %s = %s%n", k, v);
                });
                continue;
            }

            if (input.equals("viewlog")) {
                if (context.hasPermission("admin.view_action_logs") ||
                    context.hasPermission("auditor.view_audit_decisions")) {
                    System.out.println("📂 Simulated Logs:");
                    System.out.println("  [2025-06-18 12:00:01] ALICE added user bob (role=auditor)");
                    System.out.println("  [2025-06-18 12:03:45] BOB viewed portfolio A001");
                    System.out.println("  [2025-06-18 12:05:11] DIANA flagged risk exposure on A888");
                } else {
                    System.out.println("⛔ Access denied: no permission to view logs.");
                }
                continue;
            }

            if (input.equals("simulate-action")) {
                System.out.println("🎭 Simulating action based on permission:");
                if (context.hasPermission("risk.trigger_killswitch")) {
                    System.out.println("⚠️  Kill switch triggered.");
                } else if (context.hasPermission("auditor.view_risk_triggers")) {
                    System.out.println("📈 Viewing risk trigger timeline...");
                } else if (context.hasPermission("quant.run_backtest")) {
                    System.out.println("🔬 Backtest started on portfolio...");
                } else if (context.hasPermission("compliance.review_alerts")) {
                    System.out.println("🕵️ Reviewing compliance alerts.");
                } else if (context.hasPermission("testengine.run_scenario")) {
                    System.out.println("🧪 Test scenario executed.");
                } else {
                    System.out.println("🚫 No actionable permissions found.");
                }
                continue;
            }

            if (input.startsWith("can ")) {
                String perm = input.substring(4).trim();
                boolean allowed = context.hasPermission(perm);
                System.out.println(allowed ? "✅ ALLOWED" : "⛔ DENIED");
                continue;
            }

            if (input.startsWith("switch ")) {
                String newUser = input.substring(7).trim();
                if (newUser.isEmpty()) {
                    System.out.println("⚠️  Usage: switch <userId>");
                    continue;
                }
                context = builder.buildUserContextWithLog(newUser);
                userId = newUser;
                System.out.println("🔁 Switched context to: " + newUser);
                continue;
            }

            System.out.println("❓ Unknown command. Type 'help' for options.");
        }
    }
}
