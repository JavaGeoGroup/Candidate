package ge.project.demo.controller;

import ge.project.demo.dto.PagingResponseDto;
import ge.project.demo.dto.PurchaseProductDto;
import ge.project.demo.dto.product.ProductDto;
import ge.project.demo.service.ProductService;
import lombok.extern.log4j.Log4j2;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpServerErrorException;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@RestController
@RequestMapping("/product")
public class ProductController {
  private ProductService productService;

  @Autowired
  public ProductController(ProductService productService) {
    this.productService = productService;
  }

  @PutMapping("/create-update")
  public ResponseEntity<String> createUpdateProduct(HttpServletRequest request, @RequestBody ProductDto productDto) {
    ResponseEntity<String> responseEntity;
    try {
      KeycloakAuthenticationToken principal = (KeycloakAuthenticationToken) request.getUserPrincipal();
      String userId = principal.getAccount().getPrincipal().getName();
      responseEntity = productService.createUpdateProduct(productDto, userId);
    } catch (HttpServerErrorException e) {
      log.error(e.getMessage(), e);
      responseEntity = new ResponseEntity<>("Error while createUpdateProduct:" + e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      responseEntity = new ResponseEntity<>("Error while createUpdateProduct:" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return responseEntity;
  }

  @GetMapping("/user/products")
  public ResponseEntity<List<ProductDto>> getUserProducts(HttpServletRequest request) {
    ResponseEntity<List<ProductDto>> responseEntity;
    try {
      KeycloakAuthenticationToken principal = (KeycloakAuthenticationToken) request.getUserPrincipal();
      String userId = principal.getAccount().getPrincipal().getName();
      responseEntity = productService.getUserProducts(userId);
    } catch (HttpServerErrorException e) {
      log.error(e.getMessage(), e);
      responseEntity = new ResponseEntity<>(new ArrayList<>(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      responseEntity = new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return responseEntity;
  }

  @GetMapping("/list")
  public ResponseEntity<PagingResponseDto> getUserProducts(HttpServletRequest request, @RequestParam int page, @RequestParam int limit) {
    ResponseEntity<PagingResponseDto> responseEntity;
    try {
      responseEntity = productService.getProductList(page, limit);
    } catch (HttpServerErrorException e) {
      log.error(e.getMessage(), e);
      responseEntity = new ResponseEntity<>(new PagingResponseDto(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      responseEntity = new ResponseEntity<>(new PagingResponseDto(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return responseEntity;
  }

  @PostMapping("/purchase")
  public ResponseEntity<ProductDto> purchaseProduct(HttpServletRequest request, @RequestBody PurchaseProductDto purchaseProduct) {
    ResponseEntity<ProductDto> responseEntity;
    try {
      KeycloakAuthenticationToken principal = (KeycloakAuthenticationToken) request.getUserPrincipal();
      String userId = principal.getAccount().getPrincipal().getName();
      responseEntity = productService.purchaseProduct(purchaseProduct, userId);
    } catch (HttpServerErrorException e) {
      log.error(e.getMessage(), e);
      responseEntity = new ResponseEntity<>(new ProductDto(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      responseEntity = new ResponseEntity<>(new ProductDto(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return responseEntity;
  }
}
