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

### Quick demo setup

For a presentation or local demo, run `run-demo.cmd` from the `quizly` folder
(or double-click it in File Explorer). It starts Quizly with the instructor
invite code `quizly-demo`; enter that code when creating an instructor account.
Students can create accounts without an invite code.

Quizly creates JWTs automatically when users sign up or sign in. You do not
need to create or paste a JWT. The signing key is generated automatically
unless you choose to configure one.

### Student and instructor accounts

Choose **Sign in**, select **Student** or **Instructor**, and create the
appropriate account. Students can take quizzes and review their own attempts.
Instructor signup requires an invite code; students cannot access instructor
features or register as instructors without that code. Instructors can manage
only the quizzes they own, and can browse every platform quiz with its questions
and answer key in **Manage quizzes → View questions & answers**. The **Results &
insights** page shows attempts for all quizzes by default, with an optional quiz
filter and detailed answers. Students see
their answer-by-answer review immediately after submitting a quiz and can revisit
it under **My progress**. Passwords are BCrypt-hashed and must be 8–72 bytes.

For regular local development, keep your own instructor invite code in the untracked
`quizly/config/credentials.properties` file. Copy
`quizly/config/credentials.example.properties` to that filename (or edit the
local file if it already exists):

```properties
INSTRUCTOR_INVITE_CODE=choose-a-private-invite-code
```

Spring Boot loads this file automatically when you start the app from the
`quizly` project directory. `config/credentials.properties` is excluded by
`.gitignore`; do not commit or share it. If `INSTRUCTOR_INVITE_CODE` is blank or
unset, new instructor registration is disabled. Previously created instructor
accounts can still sign in. The `quizly-demo` code is only for demonstrations;
choose a private code for any shared or deployed environment.

JWTs expire after 8 hours. A signing key is generated at startup, so users sign
in again after a restart. Optionally, set a private `JWT_SECRET` (at least 32
bytes) in `credentials.properties` to keep tokens valid across restarts. You
can override database connection settings using `DB_URL`, `DB_USERNAME`, and
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