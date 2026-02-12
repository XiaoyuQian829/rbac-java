# Java RBAC System

A Role-Based Access Control (RBAC) engine implemented in Java.

Designed for systems that require clear separation of duties, explicit permission control, runtime configuration, and auditable authorization logic.

Extracted from XQRiskCore, this module functions as a standalone policy enforcement layer.

---

## Scope

This system does not handle authentication.

Identity verification must be performed upstream.  
This module focuses exclusively on authorization:

- Resolve user → role  
- Resolve role → permissions  
- Validate whether a requested action is allowed  

All decisions are configuration-driven.

---

## Core Properties

- Strict role isolation  
- Explicit permission grants only  
- No implicit inheritance  
- External YAML configuration  
- Runtime reload capability  
- Deterministic permission validation  

No permission logic is embedded in business code.

---

## Architecture Call Flow

The following diagram outlines the runtime structure and permission validation flow.

```text
           ┌────────────────────────────┐
           │        RBACCli.java        │
           │────────────────────────────│
           │ Prompts for user_id        │
           │ Prompts for permission_key │
           └────────────┬───────────────┘
                        │ calls
                        ▼
           ┌────────────────────────────┐
           │      ContextBuilder        │
           │────────────────────────────│
           │ buildUserContext(user_id)  │
           └────────────┬───────────────┘
                        │
        ┌───────────────┴────────────────────────────┐
        │                                            │
        ▼                                            ▼
┌────────────────────────────┐        ┌────────────────────────────┐
│   UserRegistryManager      │        │     PermissionsManager     │
│────────────────────────────│        │────────────────────────────│
│ Loads: UserRegistry.yaml   │        │ Loads: RolePermissions.yaml │
│ Resolves user → role       │        │ Resolves role → permissions │
└────────────┬───────────────┘        └────────────┬────────────────┘
             │ returns role                        │ returns perm set
             └────────────┬───────────────┬────────┘
                          ▼               ▼
                ┌──────────────────────────────────┐
                │         UserContext              │
                │──────────────────────────────────│
                │ user_id                          │
                │ role                             │
                │ permission_map (resolved perms)  │
                └───────────────┬──────────────────┘
                                │
                      ┌─────────▼─────────┐
                      │ checkPermission() │
                      └─────────┬─────────┘
                                │
                   ┌────────────▼────────────┐
                   │   PermissionsManager     │
                   │ hasPermission(user, key) │
                   └────────────┬─────────────┘
                                │
                                ▼
                 ┌────────────────────────────────────────┐
                 │ PermissionsManager (internal logging)  │
                 │────────────────────────────────────────│
                 │ logGrantChange(user, key, value, ts)   │
                 └────────────────────────────────────────┘


(Admin Console Path: Grant / Revoke / Save / Reload)

                ┌────────────────────────────┐
                │ AdminCommandConsole.java   │
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

## Project Structure

```bash
rbac-java/
├── pom.xml
├── config/
│   ├── RolePermissions.yaml
│   └── UserRegistry.yaml
├── src/main/java/
│   ├── admin/
│   ├── cli/
│   ├── context/
│   ├── core/
│   ├── model/
│   ├── users/
│   └── utils/
└── src/test/java/
```

---

## Configuration Model

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

Permissions are enforced exactly as declared.

---

## Build

```bash
mvn clean compile
```

## Run CLI

```bash
mvn exec:java -Dexec.mainClass=cli.RBACCli
```

---

## License

MIT

---

## Author

Xiaoyu Qian  
https://github.com/XiaoyuQian829
