# SSLChatClient
Java chat client with SSL encryption

# Package and run
After maven package run client with following command:
```
java -jar target\SSLChatClient-0.0.1-SNAPSHOT-jar-with-dependencies.jar -host localhost -port 5441 -clientjks client.jks -trustjks trusted.jks  -clientpwd changeit -trustpwd changeit
```
