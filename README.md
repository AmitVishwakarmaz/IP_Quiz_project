# Quizly — Quiz Platform

Quizly is a Spring Boot quiz app with separate student and instructor accounts.
Students can take quizzes and review their attempts. Instructors can create
quizzes and questions, view every quiz's answer key, and review student results.

## Run Quizly on Windows

### Prerequisites

- Java 21
- MySQL Server running locally

Maven does not need to be installed separately; the project includes the Maven
Wrapper (`mvnw.cmd`).

### 1. Configure MySQL

Quizly connects to MySQL at `localhost:3306` and uses the `quiz_platform`
database. It creates that database automatically if it does not exist. The
configured MySQL user must have permission to create databases.

The default MySQL username is `root`. If your MySQL credentials differ, set
them in the PowerShell session before starting Quizly:

```powershell
$env:DB_USERNAME = 'your-mysql-username'
$env:SPRING_DATASOURCE_PASSWORD = 'your-mysql-password'
```

You can also update `spring.datasource.url`, `spring.datasource.username`, and
`spring.datasource.password` in
`quizly/src/main/resources/application.properties`. Do not commit real
database passwords.

On startup, Hibernate creates or updates the database tables, then
[data.sql](./quizly/src/main/resources/data.sql) adds three sample quizzes and
nine questions without duplicating them on future startups.

### 2. Start the app

Open PowerShell in the repository folder (the folder containing `README.md`),
then choose either method.

**Quick demo — recommended:** from File Explorer, double-click
`quizly/run-demo.cmd`, or run:

```powershell
.\quizly\run-demo.cmd
```

This starts Quizly with the demo instructor invite code `quizly-demo`.

**Regular startup:** configure an instructor invite code in
`quizly/config/credentials.properties` (details below), then run:

```powershell
Set-Location .\quizly
.\mvnw.cmd spring-boot:run
```

Keep the terminal open while Quizly runs. On the first launch, Maven may
download itself and project dependencies. Wait for the `Started
QuizlyApplication` message, then open [http://localhost:8080](http://localhost:8080).
To stop the app, press **Ctrl+C** in its terminal.

If port 8080 is already in use, choose another port before starting:

```powershell
$env:SERVER_PORT = '8094'
.\quizly\run-demo.cmd
```

Then open [http://localhost:8094](http://localhost:8094).

If Maven is installed and available on your PATH, regular startup can also use
`mvn spring-boot:run` from inside the `quizly` folder.

## Create an account

1. Open Quizly in your browser and click **Sign in**.
2. Choose **Student** or **Instructor**.
3. Click **Create an account**, enter your name, email, and password, then
   submit the form. Passwords must be 8–72 bytes.
4. For instructor registration, also enter the invite code:
   - With `run-demo.cmd`, use `quizly-demo`.
   - With regular startup, use the value of `INSTRUCTOR_INVITE_CODE` in
     `quizly/config/credentials.properties`.
5. Registration signs you in automatically. Later, use the same account type,
   email, and password to sign in.

Students do not need an invite code. If an account already exists, choose
**Sign in** rather than trying to register it again. JWTs are created
automatically at sign-in; users do not need to create or paste a token.

### Set a private instructor invite code

For regular development, create or edit
`quizly/config/credentials.properties` with:

```properties
INSTRUCTOR_INVITE_CODE=choose-a-private-invite-code
```

Spring Boot loads this local file automatically when started from the `quizly`
folder. It is excluded by `.gitignore`; do not commit or share it. If the code
is blank or unset, new instructor registrations are disabled. Use the
`quizly-demo` code only for a local presentation, not for a deployed or shared
environment.

### JWT signing key (optional)

Quizly creates and signs JWTs automatically using a key generated when the app
starts. Tokens expire after 8 hours; with the default generated key, users must
sign in again after restarting the app. To keep tokens valid across restarts,
you may add a private `JWT_SECRET` of at least 32 bytes to
`quizly/config/credentials.properties`. Creating a JWT manually is never
required.

## Using Quizly

- **Students:** Explore quizzes, take one, see an answer-by-answer review after
  submission, and revisit attempts under **My progress**.
- **Instructors:** Create and manage your own quizzes, view questions and
  correct answers for all platform quizzes, and review attempts and answers
  under **Results**. The results page shows attempts from all quizzes by
  default; use its quiz filter to narrow the list. Instructors cannot take
  quizzes.

If a new or updated feature does not appear, stop the running app with
**Ctrl+C**, restart it, and refresh the browser with **Ctrl+F5**. Database
connection errors usually mean MySQL is not running or the connection settings
do not match your local MySQL setup. `DB_URL`, `DB_USERNAME`, and
`SPRING_DATASOURCE_PASSWORD` can override the database connection.
