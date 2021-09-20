package ge.project.demo.dto;

import ge.project.demo.dto.product.ProductReportDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportDto {
  private Long purchasedProductsCount;
  private Double purchasedProductsTotalPrice;
  private Double commissionPrice;
  private Long newProducts;
  private Long uniqueUsers;
  private Long userActivities;
  private List<ProductReportDto> productReport;


  public ReportDto(Long purchasedProductsCount,Double purchasedProductsPrice, Long newProducts) {
    this.purchasedProductsCount = purchasedProductsCount;
    this.purchasedProductsTotalPrice = purchasedProductsPrice;
    this.newProducts = newProducts;
    this.commissionPrice = purchasedProductsPrice == null ? null :purchasedProductsPrice / 10;
  }


}
