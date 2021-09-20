package ge.project.demo.dto.product;

import ge.project.demo.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
  private Integer productId;
  private String imageUrl;
  private String productName;
  private long count;
  private double price;

  public ProductDto(Product product) {
    this.productId = product.getProductId();
    this.imageUrl = product.getImageUrl();
    this.productName = product.getProductName();
    this.count = product.getCount();
    this.price = product.getPrice();
  }
}
