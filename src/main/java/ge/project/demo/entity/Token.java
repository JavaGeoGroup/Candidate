package ge.project.demo.entity;

import ge.project.demo.enums.TokenType;
import lombok.Data;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "geo_token")
@Data
public class Token {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int tokenId;

  @Type(type = "uuid-char")
  private UUID value;

  @Enumerated(EnumType.STRING)
  private TokenType tokenType;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "user_id")
  private User user;
}
