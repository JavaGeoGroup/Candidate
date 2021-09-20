package ge.project.demo.repository;

import ge.project.demo.dto.ReportDto;
import ge.project.demo.dto.product.ProductReportDto;
import ge.project.demo.entity.ProductActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface ProductActivityRepository extends JpaRepository<ProductActivity, Integer> {
  @Query("select new ge.project.demo.dto.ReportDto(sum(pa.productCount), sum(cast(pa.productCount as double) * pa.currentPrice), count(distinct p.productId))"
      + "from ProductActivity pa inner join pa.product p "
      + "where pa.activityTime between ?1 and ?2")
  ReportDto getProductReport(LocalDateTime activityTimeStart, LocalDateTime activityTimeEnd);

  @Query("select new ge.project.demo.dto.product.ProductReportDto(pr.productName, p.email, p.personalNumber) from ProductActivity pa"
      + " inner join pa.purchaser p inner join pa.product pr where pa.activityTime between ?1 and ?2")
  List<ProductReportDto> getProductsReport(LocalDateTime activityTimeStart, LocalDateTime activityTimeEnd);

}
