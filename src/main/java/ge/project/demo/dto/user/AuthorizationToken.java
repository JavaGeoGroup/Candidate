package ge.project.demo.dto.user;

import lombok.Data;

@Data
public class AuthorizationToken {
  private String access_token;
  private long expires_in;
  private long refresh_expires_in;
  private String refresh_token;
  private String session_state;
  private String token_type;
}
