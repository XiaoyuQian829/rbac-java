# 🔐 RBAC-Java

A minimal yet powerful **Role-Based Access Control (RBAC)** engine — built in Java, powered by YAML config, tested via CLI.

> ✅ Permission-aware sessions  
> ✅ Hot-editable role/permission YAMLs  
> ✅ CLI tools for validation, demos, and audits

---

## 📦 Project Structure

```
rbac-java/
├── pom.xml                          ← ✅ Maven build descriptor
│                                      - Declares dependencies, entry point
│                                      - Configures CLI execution
│
├── config/                          ← ✅ External YAML configs (hot-editable)
│   ├── RolePermissions.yaml         ← Role → permission matrix (true/false per action)
│   └── UserRegistry.yaml            ← User → role map + client_id + active status
│
├── src/
│   ├── main/java/
│   │   ├── admin/                   ← Admin-only console: modify users, roles, permissions
│   │   ├── cli/                     ← CLI tool to login, check permissions
│   │   ├── demo/                    ← CLI batch runner for permission matrix (RunAllUsers)
│   │   ├── context/                 ← Builds contextual permission scope
│   │   ├── core/                    ← Core RBAC logic (grant checks, permission loader)
│   │   ├── users/                   ← User registry loader
│   │   ├── utils/                   ← YAML utilities
│   │   └── model/                   ← Domain objects: UserContext, Role, Permission
│
│   └── test/java/                   ← ✅ JUnit tests
│       └── context/ContextBuilderTest.java
```

---

## 🚀 Quick Start

```bash
# Compile the project
mvn compile

# Run CLI as a specific user (e.g. 'alice')
mvn exec:java -Dexec.mainClass="cli.RBACCli"

# Run full permission matrix test for all users
mvn exec:java -Dexec.mainClass="demo.RunAllUsers"
```

---

## 🧪 Demo Output: Permission Matrix

```text
========= RBAC Permission Matrix =========

👤 User: alice
    ✔ admin.modify_role_permission
    ✔ admin.view_action_logs
    ✘ trader.view_portfolio
    ✘ observer.inspect_config
    ...

👤 User: bob
    ✘ admin.modify_role_permission
    ...
```

- ✔ = Allowed (green in terminal)  
- ✘ = Denied (red in terminal)

---

## ⚙️ Core Features

- 🔑 Role-to-permission mapping via `RolePermissions.yaml`
- 👤 User-to-role mapping via `UserRegistry.yaml`
- 🔍 Permission resolution via `PermissionsManager`
- 🧠 Session context via `ContextBuilder`
- 🛠️ Live modification via `AdminCommandConsole`
- ✅ Ready-to-run CLI tools for testing + demos

---

## 🧾 Example Configs

<details>
<summary><code>config/RolePermissions.yaml</code></summary>

```yaml
admin:
  modify_role_permission: true
  view_action_logs: true
  trigger_global_killswitch: true

trader:
  submit_manual_trade: true
  view_portfolio: true
```
</details>

<details>
<summary><code>config/UserRegistry.yaml</code></summary>

```yaml
alice:
  role: admin
  client_id: A1
  active: true

bob:
  role: trader
  client_id: B2
  active: true
```
</details>

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
