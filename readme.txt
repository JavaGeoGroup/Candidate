1. Create "files" folder in project(where src is placed) or where .jar file will be placed
2. Replace spring.datasource.username, spring.datasource.password and spring.datasource.url values with correct oracle database's credentials in Applications.properties
3. Replace spring.datasource.username and spring.datasource.password values with correct oracle database's credentials
4. Download and run Keycload standalone
5. Replace keycloak.auth-server-url in application.properties with correct keycload address
6. Create realm in keycloak with name "GeoBankDemo"(if you create different name replace keycloak.realm in application properties )
7. Create client in keycloak with name "login-app" (if you create different name replace keycloak.resource in application properties)
8. Create role "user" in keycloak under created realm
9. Create user in keycload and grant it  "manage-clients", "manage-users" and "realm-admin" roles  under "realm-management"
	9.1) Go to users
	9.2) Select created user
	9.3) Go to Role Mappings
	9.4) select Realm-management under "Client Roles" and add described roles
10) Replace admin.keystore.username and admin.keystore.password with created user's credentials
11) Replace spring.mail.username and spring.mail.password with the emails credentials from where will be sent emails 
	11.1) Set "Less secure app to true" for sender gmail account
12) Replace report.receiver.email with the email where reports will be sent