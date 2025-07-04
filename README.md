# Java RBAC System

A governance-grade **Role-Based Access Control (RBAC)** engine in Java — built for systems where **separation of duties**, **explicit approval**, **live reconfiguration**, and **auditable enforcement** are non-negotiable.

> Extracted from **XQRiskCore**, this module serves as a standalone policy enforcement layer — ideal for testing, simulating, and governing permission-controlled execution environments.

---

## 🛡️ Designed for Governance, Not Login

This system **does not handle authentication** (e.g., JWT, OAuth, login sessions).  
It assumes identity is verified upstream — and focuses entirely on **what a user is allowed to do**, under what role, under what conditions, and whether that action can be simulated and explained.

---

## 🔍 What Makes It Different

While most RBAC examples focus on access toggles, this engine focuses on **governance clarity** — built around four operational pillars:

- ✅ **Isolation** — strict role boundaries, no hardcoded bypass  
- ✅ **Approval** — every action must be explicitly granted in config  
- ✅ **Hot Configuration** — roles and users updated via reloadable YAML  
- ✅ **Auditability** — CLI-based simulation, matrix dumps, and future log hooks

> Simple to use. Hard to misuse. Built for systems where trust must be earned through structure.

---

## 🔧 Architecture Call Flow

```text
           ┌────────────────────────────┐
           │        RBACCli.java        │
           └────────────┬───────────────┘
                        │
           ┌────────────▼───────────────┐
           │      ContextBuilder        │
           └────────────┬───────────────┘
        ┌───────────────┴───────────────┐
        ▼                               ▼
┌──────────────────────┐     ┌────────────────────────┐
│ UserRegistryManager  │     │  PermissionsManager     │
└────────────┬─────────┘     └────────────┬────────────┘
             ▼                            ▼
     ┌──────────────┐           ┌───────────────────────┐
     │ UserContext  │◄──────────┤ permission_map loader │
     └──────┬───────┘           └────────────┬──────────┘
            ▼                                ▼
     ┌──────────────┐              ┌────────────────────┐
     │ checkPermission() │◄────────┤ hasPermission()     │
     └──────────────┘              └────────────────────┘


## 🔧 Architecture Call Flow

The following diagram outlines the core runtime structure and permission validation flow — showing how users, roles, and permissions are loaded, resolved, and enforced.

```text
           ┌────────────────────────────┐
           │        RBACCli.java        │  ← (User-facing CLI tool)
           │────────────────────────────│
           │ Prompts for user_id        │
           │ Prompts for permission_key │
           └────────────┬───────────────┘
                        │ calls
                        ▼
           ┌────────────────────────────┐
           │      ContextBuilder        │  ← (Session assembler)
           │────────────────────────────│
           │ buildUserContext(user_id)  │
           └────────────┬───────────────┘
                        │
        ┌───────────────┴────────────────────────────┐
        │                                            │
        ▼                                            ▼
┌────────────────────────────┐        ┌────────────────────────────┐
│   UserRegistryManager       │        │     PermissionsManager     │
│────────────────────────────│        │────────────────────────────│
│ Loads: UserRegistry.yaml   │        │ Loads: RolePermissions.yaml │
│ Resolves user → role       │        │ Resolves role → permissions │
└────────────┬───────────────┘        └────────────┬────────────────┘
             │ returns role                        │ returns perm set
             └────────────┬───────────────┬────────┘
                          ▼               ▼
                ┌──────────────────────────────────┐
                │         UserContext              │  ← (Runtime access object)
                │──────────────────────────────────│
                │ user_id                          │
                │ role                             │
                │ permission_map (resolved perms)  │
                └───────────────┬──────────────────┘
                                │
                      ┌─────────▼─────────┐
                      │ checkPermission() │  ← From RBACCli
                      └─────────┬─────────┘
                                │
                   ┌────────────▼────────────┐
                   │   PermissionsManager     │
                   │ hasPermission(user, key) │
                   └────────────┬─────────────┘
                                │
                                ▼
                 ┌────────────────────────────────────────┐
                 │ PermissionsManager (logging internal)  │
                 │────────────────────────────────────────│
                 │ logGrantChange(user, key, value, ts)   │ ← current logger
                 │ (future: delegate to AuditLogger.log)  │
                 └────────────────────────────────────────┘

(Admin Console Path: Grant / Revoke / Save / Reload)

                ┌────────────────────────────┐
                │ AdminCommandConsole.java   │  ← Admin CLI
                │────────────────────────────│
                │ grant / revoke / reload    │
                └────────────┬───────────────┘
                             ▼
                ┌────────────────────────────┐
                │     PermissionsManager     │
                │────────────────────────────│
                │ reload(), save()           │
                └────────────┬───────────────┘
                             ▼
                ┌────────────────────────────┐
                │       YamlLoader.java      │
                └────────────────────────────┘

```
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

## 🧪 Try It (Command Line)

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
