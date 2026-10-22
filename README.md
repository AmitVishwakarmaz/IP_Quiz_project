# IP Quiz Project

## Run the project on Windows

### Prerequisites

- Java 21
- MySQL

Maven does not need to be installed separately. The project includes the Maven
Wrapper (`mvnw.cmd`).

### Configure the database

1. Start MySQL.
2. Create the database if it does not already exist:

   ```sql
   CREATE DATABASE quiz_platform;
   ```

3. Open `ipProject/src/main/resources/application.properties` and set the
   datasource URL, username, and password to match your MySQL installation:

   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/quiz_platform
   spring.datasource.username=root
   spring.datasource.password=your_mysql_password
   ```

   Do not commit your real database password to the repository.

### Start the application

Open PowerShell in the `ipProject` directory (the directory containing
`mvnw.cmd`) and run:

```powershell
.\mvnw.cmd spring-boot:run
```

On the first run, the wrapper may download Maven and the project dependencies.
Keep the terminal open while the application is running. Look for a
`Started IpProjectApplication` message and Tomcat's port (default `8080`).

Then open [http://localhost:8080](http://localhost:8080) in a browser.

If Maven is installed and available on your PATH, this shorter command also
works from the `ipProject` directory:

```powershell
mvn spring-boot:run
```

If startup fails, check the terminal output. Database connection errors
typically indicate that MySQL is not running or the connection settings in
`application.properties` do not match your MySQL setup.