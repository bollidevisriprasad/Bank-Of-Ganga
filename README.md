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

> **Demo only:** This app is for portfolio/testing purposes, not real banking. Do not enter real personal or financial information. Demo accounts and their data are not private.

Demo logins: customer `1234567890` / PIN `1234` · admin `admin` / `admin123`

## Deploy a public demo on Render

This repository includes a Dockerfile and Render Blueprint for a free demo deployment. The app uses an external MySQL-compatible database so simulated accounts and transactions survive app restarts. For a free database, create a TiDB Cloud Starter cluster (free-tier availability and limits are set by the provider).

1. Sign in to [Render](https://render.com/).
2. Select **New** → **Blueprint** and connect the `bollidevisriprasad/Bank-Of-Ganga` GitHub repository.
3. Create a MySQL-compatible TiDB Cloud Starter cluster and copy its connection endpoint, database name, username, and password from the provider dashboard. Do not commit these credentials.
4. In the Blueprint setup, provide the three requested environment values:
   * `DB_URL`: `jdbc:mysql://<endpoint>:4000/<database>?sslMode=VERIFY_IDENTITY&serverTimezone=UTC` (replace the placeholders with values from the provider).
   * `DB_USER`: the database username from the provider.
   * `DB_PASSWORD`: the database password from the provider.
5. Deploy the Blueprint. Render builds the app and provides a public `https://` URL; open that URL in Chrome.

Render’s free web service can spin down when idle, but the external database keeps its data. The H2 console is disabled. Demo customer/admin accounts are seeded only when the database is empty. Free database limits and retention depend on the provider. This is for simulated portfolio data only, not production banking.

## Databases
* **Default**: in-memory H2 (no setup, data resets when the app restarts).
* **MySQL**: the MySQL profile connects to `localhost:3306/dspbank` by default. Set `DB_URL` to override the JDBC URL for a hosted MySQL-compatible server; set `DB_USER` and `DB_PASSWORD` for the connection. Hibernate `ddl-auto=update` creates or updates the entity tables at startup. You do not need to run `database/schema.sql` manually for normal startup; it documents the schema and can be used for manual setup if preferred.

## API (send the login token in header `X-Auth-Token`)
| Method | Path | Who |
|---|---|---|
| POST | /api/auth/register, /api/auth/login, /api/auth/admin/login, /api/auth/logout | public |
| GET | /api/account/me, /api/account/transactions?limit=50 | customer |
| POST | /api/account/deposit, /withdraw, /transfer · PUT /api/account/pin | customer |
| GET | /api/admin/stats, /api/admin/accounts, /api/admin/accounts/{no} | admin |
| PUT / DELETE | /api/admin/accounts/{no} | admin |
| POST | /api/admin/accounts/{no}/block, /unblock, /reset-pin | admin |
