package ge.project.demo.repository;

import ge.project.demo.dto.product.ProductDto;
import ge.project.demo.entity.Product;
import ge.project.demo.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer>, ProductRepositoryCustom{
  Optional<Product> findByProductIdEqualsAndProductNameEquals(int productId, String productName);

  @Query("select new ge.project.demo.dto.product.ProductDto(p.productId, p.imageUrl, p.productName, p.count, p.price) " + " from Product p "
      + " inner join p.user u" + " where  u.keycloakId = :keycloakId")
  List<ProductDto> getUserProducts(String keycloakId);

  @Query("select count(t) from Product t")
  int getTotalProducts();
}
