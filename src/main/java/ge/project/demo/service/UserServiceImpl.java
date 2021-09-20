package ge.project.demo.service;

import ge.project.demo.dto.user.*;
import ge.project.demo.entity.Token;
import ge.project.demo.entity.User;
import ge.project.demo.entity.UserActivity;
import ge.project.demo.enums.TokenType;
import ge.project.demo.exception.CustomException;
import ge.project.demo.repository.TokenRepository;
import ge.project.demo.repository.UserActivityRepository;
import ge.project.demo.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.ws.rs.core.Response;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

@Service
@Log4j2
public class UserServiceImpl implements UserService {

  private RestTemplate restTemplate;
  private UserRepository userRepository;
  private TokenRepository tokenRepository;
  private EmailService emailService;
  private Keycloak keycloak;
  private UserActivityRepository userActivityRepository;

  @Autowired
  public UserServiceImpl(RestTemplate restTemplate, UserRepository userRepository, EmailService emailService, Keycloak keycloak,
      TokenRepository tokenRepository, UserActivityRepository userActivityRepository) {
    this.restTemplate = restTemplate;
    this.userRepository = userRepository;
    this.emailService = emailService;
    this.keycloak = keycloak;
    this.tokenRepository = tokenRepository;
    this.userActivityRepository = userActivityRepository;
  }

  @Value("${keycloak.realm}")
  private String realm;
  @Value("${keycloak.resource}")
  private String resource;
  @Value("${keycloak.auth-server-url}")
  private String AUTHENTICATION_URL;
  @Value("${app.gui.url}")
  private String guiUrl;

  @Override
  public ResponseEntity<AuthorizationToken> authorization(UserCredentials userCredentials) {
    AuthorizationToken authorizationToken = new AuthorizationToken();
    ResponseEntity<AuthorizationToken> responseEntity = new ResponseEntity<>(authorizationToken, HttpStatus.OK);
    String authorizationUrl = AUTHENTICATION_URL + "/realms/" + realm + "/protocol/openid-connect/token";
    URI serviceUri = UriComponentsBuilder.fromUriString(authorizationUrl).build().toUri();
    MultiValueMap<String, Object> authForm = new LinkedMultiValueMap<>();
    authForm.add("client_id", resource);
    authForm.add("username", userCredentials.getUsername());
    authForm.add("password", userCredentials.getPassword());
    authForm.add("grant_type", "password");  // non-String form values supported as of 5.1.4

    AuthorizationDto authorizationDto = new AuthorizationDto(userCredentials);
    authorizationDto.setGrant_type("Password");
    authorizationDto.setClient_id(resource);
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    HttpEntity<MultiValueMap> request = new HttpEntity<>(authForm, httpHeaders);
    ResponseEntity<AuthorizationToken> response = restTemplate.postForEntity(serviceUri, request, AuthorizationToken.class);
    if (response != null) {
      authorizationToken = response.getBody();
      responseEntity = new ResponseEntity<>(authorizationToken, response.getStatusCode());
    }
    UserActivity userActivity = new UserActivity();
    User user = userRepository.findByEmailEquals(userCredentials.getUsername()).orElse(null);
    userActivity.setUser(user);
    userActivity.setActivityTime(LocalDateTime.now());
    userActivityRepository.save(userActivity);
    return responseEntity;
  }

  @Override
  @Transactional(rollbackFor = { Throwable.class })
  public ResponseEntity<String> register(RegistrationDto registrationDto) {
    User user = new User(registrationDto);
    user.setBudget(500);
    Token token = new Token();
    token.setTokenType(TokenType.REGISTER);
    token.setValue(UUID.randomUUID());
    token.setUser(user);
    user.addToken(token);
    userRepository.save(user);
    String text = "please go on the current url to complete registration: " + guiUrl + "/register/" + token.getValue();
    emailService.sendSimpleEmail(text, user.getEmail(), "complete registration");
    return new ResponseEntity<>("Success", HttpStatus.OK);
  }

  @Override
  @Transactional(rollbackFor = { Throwable.class })
  public ResponseEntity<String> confirmRegister(ResetPasswordDto resetPasswordDto) throws CustomException {
    Token token = tokenRepository.findByValueEqualsAndTokenTypeEquals(resetPasswordDto.getUserToken(), TokenType.REGISTER)
        .orElseThrow(() -> new CustomException("User not found with passed email and token"));
    User user = token.getUser();
    if(user == null || !user.getEmail().equals(resetPasswordDto.getEmail())){
      throw new CustomException("User not found with passed email and token");
    }
    user.setActivationTime(LocalDateTime.now());
    user.setActive(true);
    RealmResource realmResource = keycloak.realm(realm);
    Response response = registerUser(realmResource, user, resetPasswordDto.getPassword());
    String keycloakId = CreatedResponseUtil.getCreatedId(response);
    setUserRole(realmResource, keycloakId);
    user.setKeycloakId(keycloakId);
    user.deleteToken(token);
    userRepository.save(user);
    return new ResponseEntity<>("Success", HttpStatus.OK);
  }

  @Override
  @Transactional(rollbackFor = { Throwable.class })
  public ResponseEntity<String> forgotPassword(String personalNumber) throws CustomException {
    User user = userRepository.findByPersonalNumberEquals(personalNumber)
        .orElseThrow(() -> new CustomException("User not found with passed personalNumber"));
    Token token = new Token();
    token.setTokenType(TokenType.RESET_PASSWORD);
    token.setValue(UUID.randomUUID());
    token.setUser(user);
    tokenRepository.save(token);
    String text = "please go on the current url to reset password: " + guiUrl + "/reset/" + token.getValue();
    emailService.sendSimpleEmail(text, user.getEmail(), "reset password");
    return new ResponseEntity<>("Success", HttpStatus.OK);
  }

  @Override
  @Transactional(rollbackFor = { Throwable.class })
  public ResponseEntity<String> resetPassword(ResetPasswordDto resetPasswordDto) throws CustomException {
    Token token = tokenRepository.findByValueEqualsAndTokenTypeEquals(resetPasswordDto.getUserToken(), TokenType.RESET_PASSWORD)
        .orElseThrow(() -> new CustomException("User not found with passed email and token"));
    User user = token.getUser();
    if(user == null || !user.getEmail().equals(resetPasswordDto.getEmail())){
      throw new CustomException("User not found with passed email and token");
    }
    RealmResource realmResource = keycloak.realm(realm);
    resetPassword(realmResource, user.getKeycloakId(), resetPasswordDto.getPassword());

    user.deleteToken(token);
    userRepository.save(user);
    return new ResponseEntity<>("Success", HttpStatus.OK);
  }

  private void resetPassword(RealmResource realmResource, String userId, String newPassword) {
    UsersResource usersResource = realmResource.users();
    UserResource userResource = usersResource.get(userId);
    userResource.resetPassword(createPasswordCredentials(newPassword));
  }

  private void setUserRole(RealmResource realmResource, String userId) {
    UsersResource usersResource = realmResource.users();
    RoleRepresentation realmRole = realmResource.roles().get("user").toRepresentation();
    UserResource userResource = usersResource.get(userId);
    userResource.roles().realmLevel().add(Arrays.asList(realmRole));
  }

  private Response registerUser(RealmResource realmResource, User user, String password) {
    UserRepresentation userRepresentation = new UserRepresentation();
    CredentialRepresentation credentialRepresentation = createPasswordCredentials(password);
    userRepresentation.setUsername(user.getEmail());
    userRepresentation.setCredentials(Collections.singletonList(credentialRepresentation));
    userRepresentation.setFirstName(user.getFirstName());
    userRepresentation.setLastName(user.getLastName());
    userRepresentation.setEmail(user.getEmail());
    userRepresentation.setEnabled(true);
    userRepresentation.setEmailVerified(false);
    return realmResource.users().create(userRepresentation);
  }

  private CredentialRepresentation createPasswordCredentials(String password) {
    CredentialRepresentation passwordCredentials = new CredentialRepresentation();
    passwordCredentials.setTemporary(false);
    passwordCredentials.setType(CredentialRepresentation.PASSWORD);
    passwordCredentials.setValue(password);
    return passwordCredentials;
  }
}
