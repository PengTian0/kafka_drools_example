
# Kafka + drools

## Dependencies
1. java
2. maven

## current version 45

## Config maven
   ```
   sudo vi /etc/maven/settings.xml
   # Add server
    <server>
      <id>guvnor-m2-repo</id>
      <username>admin</username>
      <password>admin</password>
    </server>

   # Add profile
    <profile>
        <activation>
            <activeByDefault>true</activeByDefault>
        </activation>
        <repositories>
             <repository>
                 <id>guvnor-m2-repo</id>
                 <name>Guvnor M2 Repo</name>
                 <url>http://10.62.59.179:8080/drools-wb/maven2/</url>
                 <layout>default</layout>
                 <releases>
                     <enabled>true</enabled>
                     <updatePolicy>always</updatePolicy>
                 </releases>
             </repository>
        </repositories>
    </profile>
   ```

## Build and Run
   ```
   git clone https://github.com/PengTian0/kafka_drools_example
   cd kafka_drools_example
   mvn clean install -DskipTests
   mvn exec:java
   ```

