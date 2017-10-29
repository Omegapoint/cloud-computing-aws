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
 


## 5. Deployment with CodePipeline
CodePipeline is a managed service that connects together several other managed services. We will be using CodeBuild and CodeDeploy.
In this lab CodeBuild will be configured to test and build our application. CodeDeploy will then copy the built artifact to an EC2 instance and start it.

### Setup a new EC2 instance
CodeDeploy requires the CodeDeploy Agent to be running on an EC2 instance.

 1. Terminate the EC2 instance you provisioned in the previous lab
 2. Download this CloudFormation template: http://s3-eu-west-1.amazonaws.com/aws-codedeploy-eu-west-1/templates/latest/CodeDeploy_SampleCF_Template.json
 3. Open the template in an editor. After line 247 we want to install Java 8 and uninstall Java 7 as we did with our previous instance. Add the following lines:

```bash
"yum install java-1.8.0 -y \n",
"yum remove java-1.7.0 -y \n",
```
4. Go to the AWS service CloudFormation and click Create stack.
5. Choose _Upload a template to Amazon S3_ and choose your modified template .json file
6. Specify details and parameters
 - Stack name: `<application-name>`
 - InstanceCount: `1`
 - InstanceType: `t2.micro`
 - KeyPairName: `<application-name>-key`
 - OperatingSystem: `Linux`
 - SSHLocation: `My IP`
 - TagKey: `Name`
 - TagValue: `<application-name>`
7.  Click Next, then Next again,
8. Tick the checkbox _I acknowledge that AWS CloudFormation might create IAM resources._ and click Create.
9. While the instance is being provisioned move on to the next step

### Source code modifications
CodeBuild requires a buildspec.yml file to be in the root of your application. Example file:

buildspec.yml

```yml
version: 0.2
phases:
  build:
    commands:
      - echo Build started on `date`
      - sh gradlew clean assemble
artifacts:
  files:
    - appspec.yml
    - 'build/libs/*.jar'
    - start_application.sh
  discard-paths: yes
```

CodeDeploy requires two files, examples:

appspec.yml

```yml
version: 0.0
os: linux
files:
  - source: cloud-reverser-1.0-SNAPSHOT.jar
    destination: /tmp
hooks:
  ApplicationStart:
    - location: start_application.sh
      timeout: 500
      runas: root
```

start_application.sh

```bash
#!/bin/bash

touch app.log
nohup java -jar /tmp/*.jar -Dspring.profiles.active=production > app.log 2>&1 &
```
You may have to modify these files to fit your application.

### Create a CodePipeline
 1. Go to the service CodePipeline and click _Create Pipeline_
 2. Name it `<application-name>-CodePipeline` and click next step
 3. For _Source provider_ chooce Github and click _Connect to Github_ and authorize AWS to access your Github resources
 4. In _Repository_ choose your application repository, then select the branch on which the version of the application that you want to deploy is (typiclly _master_). Then click _next_.
 
 #### CodeBuild
  - Build provider: `AWS CodeBuild`
  - Configure your project -> Create a new build project
  - Project Name: `<application-name>-CodeBuild`
  - **Environment: How to build**
  - Environment image: `Use an image managed by AWS CodeBuild`
  - Operating system: `Ubuntu`
  - Runtime: `Java`
  - Version: `aws/codebuild/java:openjdk-8`
  - Build specification: `Use the buildspec.yml in the source code root directory`
  - **AWS CodeBuild service role**
  - Select _Create a service role in your account_
  - Role name: Leave as default
  - Click _Save build project_
  - After the build project is saved click _Next step_

  #### CodeDeploy
  - Deployment provider: `AWS CodeDeploy`
  - **AWS CodeDeploy**
  - Click the link _create a new one in AWS CodeDeploy_
  - Application name: `<application-name>-Application`
  - Deployment group: `<application-name>-DeploymentGroup`
  - Deployment type: In-place deployment
  - **Environment configuration**
  - Choose Amazon EC2 instances
  - Key: `Name`
  - Value: `<application-name>` make sure you see the EC2 instance created by the CloudFormation template in the _Matching instances_ section
  - Do not tick the box _Enable load balancing_
  ** Deployment configuration**
  - Leave as default
  ** Service role**
  - Service role ARN: Select the role named `BlueGreenCodeDeployServiceRole`
  - Click create application

  #### CodePipeline
  - Go back to the pipeline tab
  - **AWS CodeDeploy**
  - Application name: `<application-name>-Application`
  - Deployment group: `<application-name>-DeploymentGroup`
  - Click _Next step_
  - Role name: `AWS-CodePipeline-Service`
  - Click _Next step_
  - Review your pipeline, then click _Create pipeline_

  At this point your pipeline should start and build whatever is that latest commit. Follow what happend in CodePipeline and make sure that all steps pass.
  
### Verify everything is working
Browse the public ip of your instance at the port your application is running. As in the manual deploy lab you should be able to access your application.

## 6. Load balancing and subdomain
### Elastic Load Balaner
 1. Go to EC2 -> Load Balancers and click _Create Load Balancer_
 2. _Create_ an Application Load Balancer
 3. **Basic Configuration**
    - Name: `<application-name>-ALB`
    - Scheme: _internet-facing_
    - IP address type: _ipv4_
 4. **Listeners**
    - Leave as default
 5. **Availability Zones**
    - VPC: Select whichever VPC is marked _default_
    - Select all availability zones
    - Click _Next: Configure Security Settings
 6. **Conigure Security Settings**
    - Leave as defualt, click _Next: Configure Security Groups_
 7. **Configure Security Groups**
    - Choose _Select an **existing** seurity group_
    - Select the group previously created: `<application-name>-security-group`
    - Click _Next: Configure Routing_
 8. **Configure Routing**
    - Target group: New target group
    - Name: `<application-name>-TargetGroup`
    - Protocol: HTTP
    - Port: 8080 (or the custom port on which your application is running)
    - Target type: instance
    - **Healtch Checks**
    - Protocol: HTTP
    - Path: any valid path where your application responds with `HTTP 200`
    - **Advanced healthcheck settings**
    - Port: traffic port
    - Healthy treshold: 2
    - Unhealthy treshold: 2
    - Timeout: 5
    - Interval: 10
    - Success codes: 200-299
    - Click _Next: Register Targets_
 9. **Register Targets**
    - Search for your EC2 instance in the _Instances_ section
    - Select your instance and click _Add to registered_
    - Click _Next: Review_
 10. **Review*
    - Review your Load Balancer configuration and click _Create_
 11. **Verify your ALB is working**
    - Go to EC2 -> Load Balancers and find your Load Balancer
    - Wait until the state of the Load Balancer changes from _provisioning_ to _active_
    - Go to EC2 -> Target groups and find the target group containing your instance
    - Click the _Targets_ tab and verify that your instance has the status healthy. If properly configured it will take up to 20 seconds for the instance to pass its healthcheck.
    - Browse to the DNS name of the Load Balancer (including request mapping) and verify that you reach your application

### Subdomain with Route53
 1. Go to Route53 -> Hosted zones and click _lab.omegapint.academy_.
 2. Click _Create record set_
 3. **Creat record set**
    - Name: choose a subdomain (e.g. test.lab.omegapoint.academy)
    - Type: A - IPv4 address
    - Alias: Yes
    - Alias target: select your ALB
    - Routing policy: Simple
    - Evaluate Targe Health: No
    - Click: _Save Record Set_
 4. Try browsing your new subdomain to reach your application