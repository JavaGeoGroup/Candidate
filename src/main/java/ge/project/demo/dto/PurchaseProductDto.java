package ge.project.demo.dto;

import lombok.Data;

@Data
public class PurchaseProductDto {
  private CardDto card;
  private int productId;
  private String productName;
  private int count;
}
