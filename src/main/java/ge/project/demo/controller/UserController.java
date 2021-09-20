package ge.project.demo.controller;

import ge.project.demo.dto.user.AuthorizationToken;
import ge.project.demo.dto.user.RegistrationDto;
import ge.project.demo.dto.user.ResetPasswordDto;
import ge.project.demo.dto.user.UserCredentials;
import ge.project.demo.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpServerErrorException;

@Log4j2
@RestController
@RequestMapping("/user")
public class UserController {

  private UserService userService;

  @Autowired
  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping("/auth")
  public ResponseEntity<AuthorizationToken> authorization(@RequestBody UserCredentials userCredentials) {
    ResponseEntity<AuthorizationToken> responseEntity;
    try {
      responseEntity = userService.authorization(userCredentials);
    } catch (HttpServerErrorException e) {
      log.error(e.getMessage(), e);
      responseEntity = new ResponseEntity<>(new AuthorizationToken(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      responseEntity = new ResponseEntity<>(new AuthorizationToken(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return responseEntity;
  }

  @PostMapping("/register")
  public ResponseEntity<String> registerUser(@RequestBody RegistrationDto registrationDto) {
    ResponseEntity<String> responseEntity;
    try {
      responseEntity = userService.register(registrationDto);
    } catch (HttpServerErrorException e) {
      log.error(e.getMessage(), e);
      responseEntity = new ResponseEntity<>("error:" + e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      responseEntity = new ResponseEntity<>("error:" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return responseEntity;
  }

  @PostMapping("/confirm-registration")
  public ResponseEntity<String> confirmRegistration(@RequestBody ResetPasswordDto confirmRegistration) {
    ResponseEntity<String> responseEntity;
    try {
      responseEntity = userService.confirmRegister(confirmRegistration);
    } catch (HttpServerErrorException e) {
      log.error(e.getMessage(), e);
      responseEntity = new ResponseEntity<>("error:" + e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      responseEntity = new ResponseEntity<>("error:" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return responseEntity;
  }

  @PutMapping("/forgot-password/{personalNumber}")
  public ResponseEntity<String> confirmRegistration(@PathVariable String personalNumber) {
    ResponseEntity<String> responseEntity;
    try {
      responseEntity = userService.forgotPassword(personalNumber);
    } catch (HttpServerErrorException e) {
      log.error(e.getMessage(), e);
      responseEntity = new ResponseEntity<>("error:" + e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      responseEntity = new ResponseEntity<>("error:" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return responseEntity;
  }

  @PutMapping("/reset-password")
  public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordDto resetPasswordDto) {
    ResponseEntity<String> responseEntity;
    try {
      responseEntity = userService.resetPassword(resetPasswordDto);
    } catch (HttpServerErrorException e) {
      log.error(e.getMessage(), e);
      responseEntity = new ResponseEntity<>("error:" + e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      responseEntity = new ResponseEntity<>("error:" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return responseEntity;
  }

}
