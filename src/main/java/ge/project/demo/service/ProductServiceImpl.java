package ge.project.demo.service;

import ge.project.demo.dto.PagingResponseDto;
import ge.project.demo.dto.PurchaseProductDto;
import ge.project.demo.dto.product.ProductDto;
import ge.project.demo.dto.user.ResetPasswordDto;
import ge.project.demo.entity.Product;
import ge.project.demo.entity.ProductActivity;
import ge.project.demo.entity.Token;
import ge.project.demo.entity.User;
import ge.project.demo.enums.TokenType;
import ge.project.demo.exception.CustomException;
import ge.project.demo.repository.ProductActivityRepository;
import ge.project.demo.repository.ProductRepository;
import ge.project.demo.repository.UserRepository;
import org.keycloak.admin.client.resource.RealmResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

  private ProductRepository productRepository;
  private UserRepository userRepository;
  private ProductActivityRepository productActivityRepository;

  @Autowired
  public ProductServiceImpl(ProductRepository productRepository, UserRepository userRepository,
      ProductActivityRepository productActivityRepository) {
    this.productRepository = productRepository;
    this.userRepository = userRepository;
    this.productActivityRepository = productActivityRepository;
  }

  @Override
  @Transactional(rollbackFor = { Throwable.class })
  public ResponseEntity<String> createUpdateProduct(ProductDto productDto, String userId) throws CustomException {
    String message = "Product created successfully";

    Product product = new Product();
    if(productDto.getProductId() != null){
      product = productRepository.findByProductIdEqualsAndProductNameEquals(productDto.getProductId(), productDto.getProductName())
          .orElseThrow(() -> new CustomException("Can not find produced with passed id and name"));
      message = "Product updated successfully";
    } else {
      User currentUser = userRepository.findByKeycloakIdEquals(userId)
          .orElseThrow(() -> new CustomException("user does not exist in database"));
      product.setUser(currentUser);
      product.setCreationDate(LocalDateTime.now());
    }
    product.update(productDto);
    productRepository.save(product);
    return new ResponseEntity<>(message, HttpStatus.OK);
  }

  @Override
  @Transactional(readOnly = true)
  public ResponseEntity<List<ProductDto>> getUserProducts(String userId){
    List<ProductDto> userProducts = productRepository.getUserProducts(userId);
    return new ResponseEntity<>(userProducts, HttpStatus.OK);
  }

  @Override
  public ResponseEntity<PagingResponseDto> getProductList(int page, int limit) {
    int firstResults = page * limit;
    int maxResults = firstResults + limit;
    List<ProductDto> userProducts = productRepository.getProductList(firstResults, maxResults);
    int totalObjects = productRepository.getTotalProducts();
    PagingResponseDto pagingResponseDto = new PagingResponseDto();
    pagingResponseDto.setPage(page);
    pagingResponseDto.setLimit(limit);
    pagingResponseDto.setObjects(userProducts);
    pagingResponseDto.setTotalObjects(totalObjects);

    return new ResponseEntity<>(pagingResponseDto, HttpStatus.OK);
  }

  @Override
  public ResponseEntity<ProductDto> purchaseProduct(PurchaseProductDto purchaseProduct, String userId) throws CustomException {
    User currentUser = userRepository.findByKeycloakIdEquals(userId)
        .orElseThrow(() -> new CustomException("user does not exist in database"));
    Product product = productRepository.findByProductIdEqualsAndProductNameEquals(purchaseProduct.getProductId(), purchaseProduct.getProductName())
        .orElseThrow(() -> new CustomException("Can not find produced with passed id and name"));
    User seller = product.getUser();
    ProductActivity productActivity = new ProductActivity();
    productActivity.setProduct(product);
    productActivity.setProductCount(purchaseProduct.getCount());
    productActivity.setActivityTime(LocalDateTime.now());
    productActivity.setPurchaser(currentUser);
    productActivity.setSeller(seller);
    productActivity.setCurrentPrice(product.getPrice());
    double totalPrice = purchaseProduct.getCount() * product.getPrice();
    validatePurchase(purchaseProduct, currentUser, product, totalPrice);
    product.sellProduct(purchaseProduct.getCount());
    currentUser.purchaseProduct(totalPrice);
    seller.sellProduct(totalPrice - (totalPrice/10));
    userRepository.save(seller);
    userRepository.save(currentUser);
    productActivityRepository.save(productActivity);
    ProductDto productDto = new ProductDto(product);
    return new ResponseEntity<>(productDto, HttpStatus.OK);
  }

  private void validatePurchase(PurchaseProductDto purchaseProduct, User currentUser, Product product, double totalPrice) throws CustomException {
    if(product.getCount() < purchaseProduct.getCount()){
      throw new CustomException("There is not this many product to purchase");
    }
    if(totalPrice > currentUser.getBudget()){
      throw new CustomException("you don't have enough to purchase");
    }
  }
}
