# Java RBAC System

A minimal, clean, and extensible **Role-Based Access Control (RBAC)** engine in Java — powered by YAML configs, permission validation, and command-line interfaces.

> Originally extracted from **XQRiskCore**, this module provides a standalone RBAC layer, ideal for auditing, testing, and permission-controlled execution.

---

## 🔍 Why This Matters

Most open-source RBAC examples skip traceability, validation, or real-time usage simulation.

This project delivers:

- Real-time permission enforcement
- Human-readable YAML configs
- Command-line driven validation
- Clean separation of concerns

---

## ✅ Key Features

### Hot-reloadable Configs
All access logic is driven by two YAML files: `UserRegistry.yaml` and `RolePermissions.yaml`. These can be updated and reloaded at runtime without restarting the application.

### Structured Permission Checks
Every permission is validated explicitly and can be logged or audited. No hidden assumptions — everything is resolved through the permission matrix.

### Role-Scoped Contexts
Permissions are tied to `UserContext` objects resolved from `ContextBuilder`, ensuring all access flows are permission-scoped.

### CLI-Based Testing
Includes two CLI interfaces:
- `RBACCli.java`: interactive permission testing
- `RunAllUsers.java`: permission matrix dump for all users

### Zero Framework Overhead
No Spring, no database. Built with plain Java, SnakeYAML, and Maven — easy to inspect, modify, or embed.

---

## 📁 Project Structure

```bash
rbac-java/
├── pom.xml                          ← ✅ Maven build descriptor
│                                      - Declares dependencies, Java version, entry point
│                                      - Configures exec plugin to launch RBACCli

├── config/                          ← ✅ External YAML configs (hot-editable)
│   ├── RolePermissions.yaml         ← Role-to-permission matrix (true/false per action)
│   └── UserRegistry.yaml            ← User-to-role map + client_id + activation status

├── src/
│   ├── main/
│   │   └── java/
│   │       ├── admin/               ← ✅ Admin-only command console (permission editor)
│   │       │   └── AdminCommandConsole.java
│   │       │      - Interactive console for modifying users, roles, permissions
│   │       │      - Supports commands like: grant, revoke, add_user, reload, save

│   │       ├── cli/                 ← ✅ Command-line interface for RBAC testing
│   │       │   └── RBACCli.java
│   │       │      - Login as any user
│   │       │      - View granted permissions
│   │       │      - Test any permission key interactively

│   │       ├── context/             ← ✅ Constructs UserContext objects
│   │       │   └── ContextBuilder.java
│   │       │      - Combines user + role info into permission-aware session context

│   │       ├── core/                ← ✅ Core RBAC engine
│   │       │   └── PermissionsManager.java
│   │       │      - Loads, manages, saves RolePermissions.yaml
│   │       │      - Validates permissions, enforces grant logic

│   │       ├── model/               ← ✅ Core domain objects
│   │       │   ├── Permission.java         ← Represents single permission key + granted flag
│   │       │   ├── Role.java              ← Represents role name + permissions
│   │       │   └── UserContext.java       ← Runtime container for a user’s resolved context

│   │       ├── users/               ← ✅ User registry loader
│   │       │   └── UserRegistryManager.java
│   │       │      - Loads UserRegistry.yaml
│   │       │      - Provides user metadata by ID

│   │       └── utils/               ← ✅ Utilities shared across modules
│   │           └── YamlLoader.java
│   │              - Reads and writes YAML files generically
│   │              - Used by both user and permission managers

│
│   └── resources/                   ← (Optional) for internalized configs, templates, docs
│

└── src/
    └── test/
        └── java/
            └── context/
                └── ContextBuilderTest.java ← ✅ JUnit tests for ContextBuilder & permission logic
```

---

## ⚙️ How It Works

- `UserRegistry.yaml` defines **users → roles**
- `RolePermissions.yaml` defines **roles → permissions**
- `ContextBuilder` resolves a `UserContext` object
- CLI scripts simulate runtime access control

---

## 🧪 Try It Live (Command Line)

### 1. Compile

```bash
mvn clean compile
```

### 2. Run: Interactive Mode

```bash
mvn exec:java -Dexec.mainClass=cli.RBACCli
```

```text
Enter user ID: alice
✅ Role: admin
🔑 Permissions:
   ✔ admin.manage_users
   ✔ admin.trigger_global_killswitch
   ...

> trader.view_portfolio
⛔ DENIED

> admin.manage_users
✅ ALLOWED
```

### 3. Run: Permission Matrix Audit

```bash
mvn exec:java -Dexec.mainClass=demo.RunAllUsers
```

```text
========= RBAC Permission Matrix =========

👤 User: alice
    ✔ admin.manage_users
    ✘ trader.submit_manual_trade
    ✔ admin.edit_asset_config
    ...

👤 User: bob
    ✘ admin.manage_users
    ✘ trader.submit_manual_trade
    ...
==========================================
```

---

## 🔐 Sample Config (YAML)

### RolePermissions.yaml

```yaml
admin:
  - admin.manage_users
  - admin.edit_asset_config

trader:
  - trader.submit_manual_trade
  - trader.view_portfolio
```

### UserRegistry.yaml

```yaml
alice:
  role: admin
  active: true

bob:
  role: trader
  active: true
```

---

## 💡 Design Philosophy

> "A trade can only be executed by a user — and a user can only act within their defined scope."

This system was built with **clarity, traceability, and testability** as top priorities — ideal for demos, audits, or as a foundation for production-grade access layers.

---

## 🛠️ Future Roadmap

- [ ] Spring Boot REST API interface
- [ ] PostgreSQL integration for persistent config
- [ ] Web Admin Panel (React/Spring Boot)
- [ ] Multi-client permission scoping
- [ ] Audit logging with timestamps

---

## 📄 License

MIT

---

## 👤 Author

**Xiaoyu Qian**  
[github.com/XiaoyuQian829](https://github.com/XiaoyuQian829)
