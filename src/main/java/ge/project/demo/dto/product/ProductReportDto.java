package ge.project.demo.dto.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductReportDto {
  private String productName;
  private String purchaserEmail;
  private String purchaserPersonalNumber;
}
