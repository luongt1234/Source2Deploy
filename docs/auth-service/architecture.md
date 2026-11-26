# Architecture

The Java 21 Maven parent manages Spring Boot 3.3.5, Spring Cloud 2023.0.3 and their dependency versions. The existing Gateway uses reactive Spring Cloud Gateway. The eight business services use Spring MVC and separate PostgreSQL databases initialized by Flyway. Kafka and the existing `shared/event-contracts` module carry business events. The business APIs previously used permissive Spring Security chains. The Gateway now uses a reactive JWT decoder and each business service uses its own servlet JWT decoder.

| Layer | Auth Service ownership |
| --- | --- |
| Domain | User, role and permission types. |
| Application | Registration, account views, authorization queries and recovery, using repository and password-hashing ports. |
| Infrastructure | JPA adapters, OAuth JDBC repositories, security filters, RSA signing, PostgreSQL rate limits and SMTP. |
| Presentation | JSON request validation, safe response DTOs and account error mapping. |

`shared/resource-security` holds JWT validation, authority mapping, and reusable API permission rules. It contains no account models or authorization-server storage classes.

`V1__identity.sql` installs the account, RBAC, attempt and recovery tables. `V2__oauth.sql` adapts Spring Authorization Server's JDBC schema for PostgreSQL, adds token lookup indexes and records spent refresh-token hashes. The Auth Service uses its own database in Compose. Registration assigns the default role in the same transaction as account creation. Token mutations use a PostgreSQL transaction and serialize exchanges, rotations and revocations per OAuth client across replicas.

The parent uses the existing Boot plugin to package runnable service JARs. The compiler retains controller parameter names for Spring MVC request binding. Each Dockerfile builds its selected Maven reactor module and dependencies using the repository root as the build context.
