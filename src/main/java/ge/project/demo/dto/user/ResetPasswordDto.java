package ge.project.demo.dto.user;

import lombok.Data;

import java.util.UUID;

@Data
public class ResetPasswordDto {
  private UUID userToken;
  private String email;
  private String password;
}
