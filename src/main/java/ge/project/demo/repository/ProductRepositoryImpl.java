package ge.project.demo.repository;

import ge.project.demo.dto.product.ProductDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Log4j2
@Repository
public class ProductRepositoryImpl implements ProductRepositoryCustom{
  @PersistenceContext
  EntityManager entityManager;

  @Override
  public List<ProductDto> getProductList(int firstResult, int size) {
    return entityManager.createQuery("select new ge.project.demo.dto.product.ProductDto(p.productId, p.imageUrl, p.productName, p.count, p.price) " +
            " from Product p ", ProductDto.class)
        .setFirstResult(firstResult)
        .setMaxResults(size)
        .getResultList();
  }

}
