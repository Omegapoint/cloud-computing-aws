# Cloud Computing w/ AWS

1. [Setup project](#setup)
2. [Kafka Installation Instructions for Ubuntu 16.04 LTS](#install)
3. [Command line Exercises](#CommandExercises)
4. [Java Exercises](#JavaExercises)


<a name="setup"></a>
## 1. Setup project
 1. Download [Spring Boot 1.5.8](http://start.spring.io/) with gradle. Pick the _web_ and _postgreSQL_ dependencies. Use `group ID = se.omegapoint` and `artifact ID = <application-name>`.
 2. Install [PostgreSQL](https://www.postgresql.org/download/) on your local machine.
 3. Install [Java 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) on your local machine.
 4. Create a [Github](https://github.com/Omegapoint) repository at `https://github.com/Omegapoint/<application-name>`
 5. Install [PgAdmin](https://www.postgresql.org/ftp/pgadmin/pgadmin4/v2.0/) locally	

### Application endpoints
Start the application with `./gradlew bootRun`.

The application has two endpoints: **ping** and **reverse**. Reverse takes some data and reverses it. The reverse function should use a database cache, so if the application has recevied the data before, it should not compute it - the result should be feteched from the database. 


Your application should handle the following scenarios:

### Scenario 1
Call `http://localhost:8080/<application-name>/ping/`

Response:

```json
pong
```

### Scenario 2
Call `http://localhost:8080/<application-name>/reverse/<data>`

Response:

```json
{
	"timeStamp": "<yyyy-MM-dd HH:mm:ss>",
	"applicationName": "<appication-name>",
	"data": "<reversed-data>"
}
```

For example, if `<data> = "omegapoint"` the application should return:

```json
{
	"timeStamp": "2017-10-28 11:16:58",
	"applicationName": "reversing-richard",
	"data": "tniopagemo"
}
```


## 2. Application profiles
In order to be able to have different configurations for different environments, you need to create profile Add two properties files to the project: `application-production.properties` and `applications-local.properties`. Add `spring.profiles.active=local` at the very top of the file `applications-local.properties`. You also need to add the following code to `build.gradle`:

```javascript
sourceCompatibility = 1.8
targetCompatibility = 1.8

bootRun {
	systemProperties System.properties
}
```

You can now run your application with `./gradlew bootRun -Dspring.profiles.active=local` and vice versa.


## 3. Connecting your application to a PostgreSQL database

### Local DB

#### Configure `application-local.properties`

```bash
spring.datasource.url=jdbc:postgresql://localhost:5432/<db-name> 
spring.datasource.username=<username>
spring.datasource.password=<password>   
```

If you want to save time and not manually create your tables, also add the following config to the file:

```bash
spring.jpa.hibernate.ddl-auto=create-drop
```

#### Configure `application.properties`

```bash
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
```

#### Configure `build.gradle`
Make sure you have the following dependencies:

```json
dependencies {
	compile('org.springframework.boot:spring-boot-starter-data-jpa')
	compile('org.springframework.boot:spring-boot-starter-web')
	runtime('org.postgresql:postgresql')
	testCompile('org.springframework.boot:spring-boot-starter-test')
}
```


#### Suggested database model
| id        | data           | reversed_data  |
| ------------- |:-------------:| -----:|
| 1     | omegapoint | tniopagemo |
| 2     | bullen | nellub |

### Cloud DB

#### Create a new RDS instance
Log in to AWS and create a new RDS instance called `<application-name>-db`. Modify your profile configuaration file `application-production.properties` so that the application can connect to the new RDS instance. 

At the very top of the configuration file, put `spring.profiles.active=production`.

## 4. Manual deploy on EC2




