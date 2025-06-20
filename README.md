# 🛡️ Java RBAC System — Lightweight, Readable, Extensible

A minimal **Role-Based Access Control (RBAC)** system in Java — powered by YAML configs, strict user validation, and colorful CLI demos.

Designed to showcase:
- ✅ Role → Permission mapping via YAML
- ✅ Context construction: `user_id → role → permissions`
- ✅ Real-time permission checks
- ✅ CLI tools for verification and simulation

> 🔧 Originally part of **XQRiskCore**,this project strips away all trading logic, retaining only the RBAC skeleton for clean, reusable access control.
> ⚙️ No Spring, no database — just clean Java + config-driven logic.

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
