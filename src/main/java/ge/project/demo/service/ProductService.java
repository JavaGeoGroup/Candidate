package ge.project.demo.service;

import ge.project.demo.dto.PagingResponseDto;
import ge.project.demo.dto.PurchaseProductDto;
import ge.project.demo.dto.product.ProductDto;
import ge.project.demo.exception.CustomException;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ProductService {
  ResponseEntity<String> createUpdateProduct(ProductDto productDto, String userId) throws CustomException;
  ResponseEntity<List<ProductDto>> getUserProducts(String userId);
  ResponseEntity<PagingResponseDto> getProductList(int page, int limit);
  ResponseEntity<ProductDto> purchaseProduct(PurchaseProductDto purchaseProduct, String userId) throws CustomException;
}
