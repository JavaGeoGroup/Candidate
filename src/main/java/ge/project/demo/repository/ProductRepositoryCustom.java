package ge.project.demo.repository;

import ge.project.demo.dto.product.ProductDto;

import java.util.List;

public interface ProductRepositoryCustom {
  List<ProductDto> getProductList(int firstResult, int size);
}
