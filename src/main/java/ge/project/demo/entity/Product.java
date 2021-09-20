package ge.project.demo.entity;

import ge.project.demo.dto.product.ProductDto;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "geo_product")
@Data
public class Product {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int productId;
  private String imageUrl;
  private String productName;
  private long count;
  private double price;
  private LocalDateTime creationDate;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  public Product() {
  }

  public void update(ProductDto productDto){
    this.imageUrl = productDto.getImageUrl();
    this.productName = productDto.getProductName();
    this.count = productDto.getCount();
    this.price = productDto.getPrice();
  }

  public void sellProduct(int count){
    if(this.count >= count){
      this.count -= count;
    }
  }
}
