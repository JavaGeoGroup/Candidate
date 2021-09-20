package ge.project.demo.service;

import ge.project.demo.dto.user.ResetPasswordDto;
import ge.project.demo.dto.user.RegistrationDto;
import ge.project.demo.dto.user.AuthorizationToken;
import ge.project.demo.dto.user.UserCredentials;
import ge.project.demo.exception.CustomException;
import org.springframework.http.ResponseEntity;

public interface UserService {
  ResponseEntity<AuthorizationToken> authorization(UserCredentials userCredentials);
  ResponseEntity<String> register(RegistrationDto registrationDto);
  ResponseEntity<String> confirmRegister(ResetPasswordDto resetPasswordDto) throws CustomException;
  ResponseEntity<String> forgotPassword(String email) throws CustomException;
  ResponseEntity<String> resetPassword(ResetPasswordDto resetPasswordDto) throws CustomException;
}
