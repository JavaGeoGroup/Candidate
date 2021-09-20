package ge.project.demo.dto.user;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AuthorizationDto {
  private String client_id;
  private String username;
  private String password;
  private String grant_type;

  public AuthorizationDto(UserCredentials userCredentials) {
    this.username = userCredentials.getUsername();
    this.password = userCredentials.getPassword();
  }
}
