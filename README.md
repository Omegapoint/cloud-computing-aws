# Cloud Computing w/ AWS

1. [Setup project](#setup)
2. [Application profiles](#application_profiles)
3. [Connecting your application to a PostgreSQL database](#database)
4. [Manual deploy on EC2](#manualdeploy)


<a name="setup"></a>
## 1. Setup project
 1. Download [Spring Boot 1.5.8](http://start.spring.io/) with gradle. Pick the _web_, _JPA_ and _postgreSQL_ dependencies. Use `group ID = se.omegapoint` and `artifact ID = <application-name>`.
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

<a name="application_profiles"></a>
## 2. Application profiles
In order to be able to have different configurations for different environments, you need to create profile Add two properties files to the project: `application-production.properties` and `applications-local.properties`. Add `spring.profiles.active=local` at the very top of the file `applications-local.properties`. You also need to add the following code to `build.gradle`:

```javascript
archivesBaseName = '<application-name>'

sourceCompatibility = 1.8
targetCompatibility = 1.8

bootRun {
	systemProperties System.properties
}
```

You can now run your application with `./gradlew bootRun -Dspring.profiles.active=local` and vice versa.

<a name="database"></a>
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

[Login to AWS](https://sts.omegapoint.se/adfs/ls/IdpInitiatedSignOn.aspx). Go to **RDS** under _"Database"_. Click on the orange button _"Launch a DB Instance"_ and choose _PostgreSQL_. 


Make the following choices:

 1. **Choose use case**
  - Dev/Test
 
 2. **Specify DB details** 
  - DB engine version: _PostgreSQL 9.6.2-R1_
  - DB instance class: _db.t2.micro_
  - Allocated storage: `20 GB`
  - DB instance identifier: `<application-name>-db-instance`
  - Master username: `<username>`
  - Master password: `<password>`
 3. **Configure advanced settings**
  - Virtual Private Cloud (VPC): _Default VPC_
  - Public accessibility: _Yes_
  - Databasename: `<application-name>-db`
  - Databaseport: `5432`
  - Backup: _No preference_
  - Monitoring: _Disable_
  - Maintenance: _Disable_
  - Maintenance window: _No preference_
 
 On your RDS overview, choose _"Instances"_ and collect the following info:
 
  * Endpoint
  * DB Name
  * Username

Insert the information into `application-production.properties` in your project. At the very top of the configuration file, put `spring.profiles.active=production`.

You should now be able to run `./gradlew bootRun -Dspring.profiles.active=production` **on your local machine** and connect your application to the RDS instance.  
<a name="manualdeploy"></a>
## 4. Manual deploy on EC2

### Setup instance
[Login to AWS](https://sts.omegapoint.se/adfs/ls/IdpInitiatedSignOn.aspx). 

 1. **Go to the Service -> EC2 -> Key pairs.** 
 2. **Create a new key** and download an ssh key. You will use this to connect to instances that you provision during this lab. The key should be called `<application-name>-key` 
 3. **Choose an Instance Type**
  - Go to the Service -> EC2 -> Instances. 
  - Launch an EC2-instance using Amazon Linux on a *t2.micro* instance. Click _Next: Configure Instance Details_
 5. **Configure Instance Details** - Leave all fields as default
 6. **Add Storage** - Leave all fields as default 
 7. **Add Tags** 
  - Key: `Name`
  - Value `<application-name>`
 8. **Configure Security Group 1**
  - Security group name: `<application-name>-security-group`
  - Type: _SSH_
  - Protocol: _TCP_
  - Port Range: `22`
  - Source: `My IP`
  - Description: `SSH for admin`
 9. **Configure Security Group 2**
  - Type: _Custom TCP_
  - Protocol: _TCP_
  - Port Range: `8080` (or whichever port your application is running)
  - Source: `My IP`
  - Description `<insert-something-smart>`
 10. **Review Instance Launch** - Leave all fields as default
 11. **Select an existing key pair or create a new key pair**
  - _Choose an existing key pair_
  - `<application-name>-key`
 12. Verify that your EC2 is working by SSH to your EC2 instance. The username is **ec2-user**.

You have now created a new EC2 instance in the cloud, yay!

### Manual deploy

 1. Upload your jar to to the instance with scp.
 2. Install java with `yes | sudo yum install java-1.8.0` and remove Java 1.7 with `yes | sudo yum remove java-1.7.0`
 3. Start the application by using the command `java -jar <app-name> -Dspring.profiles.active=production`.
 4. Verify that your app is accessible from the internet by browsing the public IP address of the instance.
 





