# Quizly — Quiz Platform

## Run the project on Windows

### Prerequisites

- Java 21
- MySQL

Maven does not need to be installed separately. The project includes the Maven
Wrapper (`mvnw.cmd`).

### Automatic database setup

On startup, the application creates the `quiz_platform` database if it does not
exist (the configured MySQL user must have permission to create databases).
Hibernate creates or updates its tables, then
[data.sql](./quizly/src/main/resources/data.sql) seeds three sample quizzes
and nine questions. The seed script is safe to run on each startup and does not
duplicate those samples.

### Configure the database

1. Start MySQL.
2. (Optional) Create the database manually if your MySQL account cannot create
   databases automatically:

   ```sql
   CREATE DATABASE quiz_platform;
   ```

3. Open `quizly/src/main/resources/application.properties` and set the
   datasource URL, username, and password to match your MySQL installation:

   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/quiz_platform
   spring.datasource.username=root
   spring.datasource.password=your_mysql_password
   ```

   Do not commit your real database password to the repository.

### Start the application

Open PowerShell in the `quizly` directory (the directory containing
`mvnw.cmd`) and run:

```powershell
.\mvnw.cmd spring-boot:run
```

On the first run, the wrapper may download Maven and the project dependencies.
Keep the terminal open while the application is running. Look for a
`Started QuizlyApplication` message and Tomcat's port (default `8080`).

Then open [http://localhost:8080](http://localhost:8080) in a browser.

### Student and instructor accounts

Choose **Sign in**, select **Student** or **Instructor**, and create the
appropriate account. Students can take quizzes and review their own attempts.
Instructor signup requires an invite code; students cannot access instructor
features or register as instructors without that code. Instructors can manage
only the quizzes they own. Passwords are BCrypt-hashed and must be 8–72 bytes.

Set a private invite code in PowerShell before starting the app. There is no
default code:

```powershell
$env:INSTRUCTOR_INVITE_CODE = 'choose-a-private-invite-code'
.\mvnw.cmd spring-boot:run
```

If `INSTRUCTOR_INVITE_CODE` is unset, new instructor registration is disabled.
Previously created instructor accounts can still sign in. Do not commit or
share the invite code.

JWTs expire after 8 hours. For local development, a signing key is generated
automatically on startup, so users must sign in again after a server restart.
To keep tokens valid across restarts, set a private key:

```powershell
$env:JWT_SECRET = [Convert]::ToBase64String([Security.Cryptography.RandomNumberGenerator]::GetBytes(32))
```

For repeated local use, set both secrets in the same PowerShell session before
starting the app:

```powershell
$env:INSTRUCTOR_INVITE_CODE = 'choose-a-private-invite-code'
$env:JWT_SECRET = [Convert]::ToBase64String([Security.Cryptography.RandomNumberGenerator]::GetBytes(32))
.\mvnw.cmd spring-boot:run
```

Keep both values private and out of source control. You can also override the
database connection using `DB_URL`, `DB_USERNAME`, and
`SPRING_DATASOURCE_PASSWORD`.

If port `8080` is already in use, choose another port before starting:

```powershell
$env:SERVER_PORT = '8094'
.\mvnw.cmd spring-boot:run
```

Then open [http://localhost:8094](http://localhost:8094).

If Maven is installed and available on your PATH, this shorter command also
works from the `quizly` directory:

```powershell
mvn spring-boot:run
```

If startup fails, check the terminal output. Database connection errors
typically indicate that MySQL is not running or the connection settings in
`application.properties` do not match your MySQL setup.