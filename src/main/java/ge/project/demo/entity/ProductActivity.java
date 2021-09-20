package ge.project.demo.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "product_activity"/*, indexes = {
    @Index(columnList = "activity_time", name = "product_activity_ind01")
}*/)
@Data
public class ProductActivity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int activityId;

  private LocalDateTime activityTime;

  private double currentPrice;

  private int productCount;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "seller_id")
  private User seller;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "purchaser_id")
  private User purchaser;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id")
  private Product product;


}
