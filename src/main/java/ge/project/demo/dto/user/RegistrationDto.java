package ge.project.demo.dto.user;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
public class RegistrationDto {
  @Email
  @NotNull
  private String email;
  @NotNull
  private String firstName;
  @NotNull
  private String lastName;
  @NotNull
  private String personalNumber;
  @NotNull
  private String accountNumber;
}
