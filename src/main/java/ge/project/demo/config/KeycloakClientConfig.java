package ge.project.demo.config;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakClientConfig {
  @Value("${keycloak.resource}")
  private String clientId;
  @Value("${keycloak.auth-server-url}")
  private String authUrl;
  @Value("${keycloak.realm}")
  private String realm;
  @Value("${admin.keystore.username}")
  private String userName;
  @Value("${admin.keystore.password}")
  private String password;

  @Bean
  public Keycloak keycloak(){
    return KeycloakBuilder.builder()
        .serverUrl(authUrl)
        .realm(realm)
        .grantType(OAuth2Constants.PASSWORD)
        .username(userName)
        .password(password)
        .clientId(clientId)
        .resteasyClient(new ResteasyClientBuilder()
            .connectionPoolSize(10)
            .build())
        .build();
  }
}
