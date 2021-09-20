package ge.project.demo.entity;

import ge.project.demo.dto.user.RegistrationDto;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "geo_user")
@Data
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int userId;
  private String email;
  private String firstName;
  private String lastName;
  private String personalNumber;
  private String accountNumber;
  private boolean active;
  private LocalDateTime activationTime;
  private String keycloakId;
  @ColumnDefault("500")
  private double budget;

  @OneToMany(cascade=CascadeType.ALL, mappedBy="user", orphanRemoval = true)
  private List<Token> tokens;

  public User(RegistrationDto registrationDto) {
    this.email = registrationDto.getEmail();
    this.firstName = registrationDto.getFirstName();
    this.lastName = registrationDto.getLastName();
    this.personalNumber = registrationDto.getPersonalNumber();
    this.accountNumber = registrationDto.getAccountNumber();
    this.active = false;
  }

  public User() {
  }

  public void addToken(Token token){
    if(this.tokens == null){
      this.tokens = new ArrayList<>();
    }
    this.tokens.add(token);
  }

  public void deleteToken(Token token){
    if(this.tokens != null){
      this.tokens.remove(token);
    }
  }

  public void purchaseProduct(double totalPrice){
    if(this.budget >= totalPrice){
      this.budget -= totalPrice;
    }
  }

  public void sellProduct(double totalPrice){
    this.budget += totalPrice;
  }
}
