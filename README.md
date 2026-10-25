# DSP Bank – Spring Boot + REST API + web UI

Convert of the original console/JDBC bank app into a Spring Boot REST service with a browser frontend.

## Run locally on Windows

The full Spring Boot application requires **Java 17** and **Maven**. Spring Boot 3.3.5 does not run on Java 8. This project does not include a Maven wrapper, so Maven must already be available on your PATH.

In VS Code, open a PowerShell terminal in the `dsp-bank` project folder. Check that the selected Java and Maven are available:

```powershell
java -version
mvn -version
```

Set the MySQL credentials in that terminal. The password prompt is hidden, and these variables are only set for the current PowerShell process:

```powershell
$env:DB_USER = 'root'
$securePassword = Read-Host 'MySQL password' -AsSecureString
$env:DB_PASSWORD = [System.Net.NetworkCredential]::new('', $securePassword).Password
```

Start the app with the MySQL profile:

```powershell
mvn "-Dspring-boot.run.profiles=mysql" spring-boot:run
```

Open <http://localhost:8080>. Stop the app with `Ctrl+C`. When finished, remove the credentials from the terminal environment:

```powershell
Remove-Item Env:DB_USER
Remove-Item Env:DB_PASSWORD
```

Demo logins: customer `1234567890` / PIN `1234` · admin `admin` / `admin123`

## Databases
* **Default**: in-memory H2 (no setup, data resets when the app restarts).
* **MySQL**: the MySQL profile connects to `localhost:3306/dspbank`. The configured JDBC URL creates the database if it does not exist (the MySQL user must have permission), and Hibernate `ddl-auto=update` creates or updates the entity tables at startup. You do not need to run `database/schema.sql` manually for normal startup; it documents the schema and can be used for manual setup if preferred. Both `DB_USER` and `DB_PASSWORD` must be set in the environment before starting with the MySQL profile.

## API (send the login token in header `X-Auth-Token`)
| Method | Path | Who |
|---|---|---|
| POST | /api/auth/register, /api/auth/login, /api/auth/admin/login, /api/auth/logout | public |
| GET | /api/account/me, /api/account/transactions?limit=50 | customer |
| POST | /api/account/deposit, /withdraw, /transfer · PUT /api/account/pin | customer |
| GET | /api/admin/stats, /api/admin/accounts, /api/admin/accounts/{no} | admin |
| PUT / DELETE | /api/admin/accounts/{no} | admin |
| POST | /api/admin/accounts/{no}/block, /unblock, /reset-pin | admin |
