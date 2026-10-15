package com.example.commercecartapi;

import com.example.commercecartapi.dto.ProductCreateRequest;
import com.example.commercecartapi.dto.ProductResponse;
import com.example.commercecartapi.entity.Product;
import com.example.commercecartapi.enums.ProductStatus;
import com.example.commercecartapi.exception.ResourceNotFoundException;
import com.example.commercecartapi.mapper.ProductMapper;
import com.example.commercecartapi.repository.ProductRepository;
import com.example.commercecartapi.service.ProductService;
import org.aspectj.util.Reflection;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.util.ReflectionUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.InstanceOfAssertFactories.LIST;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;



    @Test
    void addNewProduct_ShouldAddNewProductAndReturnResponse(){
        ProductCreateRequest mockRequest = new ProductCreateRequest();
        mockRequest.setName("Laptop");
        mockRequest.setDescription("You can develop projects");
        mockRequest.setPrice(new BigDecimal("45990.85"));

        Product mockProduct = new Product(
                "Laptop",
                "You can develop projects",
                new BigDecimal("45990.85"));

        Product mockSavedProduct = new Product(
                "Laptop",
                "You can develop projects",
                new BigDecimal("45990.85"));
        mockSavedProduct.changeStatusToActive();
        ReflectionTestUtils.setField(mockSavedProduct, "id", 1L);

        ProductResponse mockResponse = new ProductResponse();
        mockResponse.setId(1L);
        mockResponse.setName("Laptop");
        mockResponse.setDescription("You can develop projects");
        mockResponse.setPrice(new BigDecimal("45990.85"));
        mockResponse.setStatus(ProductStatus.ACTIVE);

        when(productMapper.toEntity(mockRequest)).thenReturn(mockProduct);
        when(productRepository.save(mockProduct)).thenReturn(mockSavedProduct);
        when(productMapper.toResponse(mockSavedProduct)).thenReturn(mockResponse);

        ProductResponse result = productService.addNewProduct(mockRequest);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Laptop");
        assertThat(result.getDescription()).isEqualTo("You can develop projects");
        assertThat(result.getStatus()).isEqualTo(ProductStatus.ACTIVE);

        verify(productMapper, times(1)).toEntity(mockRequest);
        verify(productRepository, times(1)).save(mockProduct);
        verify(productMapper, times(1)).toResponse(mockSavedProduct);

    }

    @Test
    void getAllNonDeletedProducts_IfThereIsAtLeastOneProduct_ShouldReturnActiveAndOutOfStockProductsResponse(){
        List<Product> mockNonDeletedProducts = getProducts();


        ProductResponse mockResponse1 = new ProductResponse();
        mockResponse1.setId(1L);
        mockResponse1.setName("Laptop");
        mockResponse1.setDescription("You can develop projects");
        mockResponse1.setPrice(new BigDecimal("45990.85"));
        mockResponse1.setStatus(ProductStatus.ACTIVE);

        ProductResponse mockResponse2 = new ProductResponse();
        mockResponse2.setId(2L);
        mockResponse2.setName("Laptop 2");
        mockResponse2.setDescription("You can develop projects");
        mockResponse2.setPrice(new BigDecimal("45990.85"));
        mockResponse2.setStatus(ProductStatus.OUT_OF_STOCK);

        when(productRepository.findByStatusIsNot(ProductStatus.DELETED)).thenReturn(mockNonDeletedProducts); // if findNonDeletedProducts is called, return mockProducts
        when(productMapper.toResponse(mockNonDeletedProducts.get(0))).thenReturn(mockResponse1);
        when(productMapper.toResponse(mockNonDeletedProducts.get(1))).thenReturn(mockResponse2);

        List<ProductResponse> resultList = productService.getAllNonDeletedProducts();

        assertThat(resultList).isNotNull();
        assertThat(resultList).hasSize(2);
        assertThat(resultList).extracting(ProductResponse::getName).containsExactlyInAnyOrder("Laptop", "Laptop 2");

        verify(productRepository, times(1)).findByStatusIsNot(ProductStatus.DELETED);
        verify(productMapper, times(1)).toResponse(mockNonDeletedProducts.get(0));
        verify(productMapper, times(1)).toResponse(mockNonDeletedProducts.get(1));
    }

    @Test
    void getAllNonDeletedProducts_IfThereIsNoProduct_ShouldReturnAnEmptyList(){
        when(productRepository.findByStatusIsNot(ProductStatus.DELETED)).thenReturn(List.of());

        List<ProductResponse> resultList = productService.getAllNonDeletedProducts();

        assertThat(resultList).isNotNull();
        assertThat(resultList).isEmpty();

        verify(productRepository, times(1)).findByStatusIsNot(ProductStatus.DELETED);
        verify(productMapper, never()).toResponse(any(Product.class));
    }

    @Test
    void deleteProduct_WithValidId_ShouldChangeTheStatusOfProductToDeletedAndReturnResponse(){
        Long productId = 100L;
        Product mockExistingProduct = new Product(
                "Laptop",
                "You can develop projects",
                new BigDecimal("45990.85"));
        mockExistingProduct.changeStatusToActive();

        ReflectionTestUtils.setField(mockExistingProduct, "id", productId);

        Product mockSavedProduct = new Product(
                "Laptop",
                "You can develop projects",
                new BigDecimal("45990.85"));
        mockSavedProduct.changeStatusToDeleted();

        ReflectionTestUtils.setField(mockSavedProduct, "id", productId);

        ProductResponse mockResponse = new ProductResponse();
        mockResponse.setId(productId);
        mockResponse.setName("Laptop");
        mockResponse.setDescription("You can develop projects");
        mockResponse.setPrice(new BigDecimal("45990.85"));
        mockResponse.setStatus(ProductStatus.DELETED);

        when(productRepository.findById(productId)).thenReturn(Optional.of(mockExistingProduct));
        when(productRepository.save(mockExistingProduct)).thenReturn(mockSavedProduct);
        when(productMapper.toResponse(mockSavedProduct)).thenReturn(mockResponse);

        ProductResponse result = productService.deleteProduct(productId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(productId);
        assertThat(result.getStatus()).isEqualTo(ProductStatus.DELETED);

        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).save(mockExistingProduct);
        verify(productMapper, times(1)).toResponse(mockSavedProduct);
    }

    @Test
    void deleteProduct_WithInvalidId_ShouldThrowResourceNotFoundException(){
        Long productId = 999L;

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.deleteProduct(productId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Cannot found product with ID " + productId);

        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, never()).save(any(Product.class));
        verify(productMapper, never()).toResponse(any(Product.class));
    }

    private static @NonNull List<Product> getProducts() {
        Product mockProduct = new Product(
                "Laptop",
                "You can develop projects",
                new BigDecimal("45990.85"));

        mockProduct.changeStatusToActive();
        ReflectionTestUtils.setField(mockProduct, "id", 1L);

        Product mockProduct2 = new Product(
                "Laptop 2",
                "You can develop projects",
                new BigDecimal("45990.85"));

        mockProduct2.changeStatusToOutOfStock();
        ReflectionTestUtils.setField(mockProduct2, "id", 2L);

        return List.of(mockProduct, mockProduct2);
    }
}
