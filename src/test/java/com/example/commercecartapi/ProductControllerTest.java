package com.example.commercecartapi;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.mockito.Mockito.*;

import com.example.commercecartapi.exception.ResourceNotFoundException;
import com.example.commercecartapi.controller.ProductController;
import com.example.commercecartapi.dto.ProductCreateRequest;
import com.example.commercecartapi.dto.ProductResponse;
import com.example.commercecartapi.enums.ProductStatus;
import com.example.commercecartapi.service.ProductService;

import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;

@WebMvcTest(ProductController.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;

    @Test
    void addNewProduct_WithValidRequest_ShouldReturn201CreatedAndInformationOfProduct() throws Exception {
        ProductCreateRequest request = new ProductCreateRequest();
        request.setName("Laptop");
        request.setDescription("You can play games");
        request.setPrice(new BigDecimal("49999.50"));

        ProductResponse productResponse = new ProductResponse();
        productResponse.setId(1L);
        productResponse.setName("Laptop");
        productResponse.setDescription("You can play games");
        productResponse.setPrice(new BigDecimal("49999.50"));
        productResponse.setStatus(ProductStatus.ACTIVE);

        when(productService.addNewProduct(any(ProductCreateRequest.class))).thenReturn(productResponse);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))

                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.description").value("You can play games"))
                .andExpect(jsonPath("$.price").value(new BigDecimal("49999.5")))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        verify(productService, times(1)).addNewProduct(any(ProductCreateRequest.class));
    }

    @Test
    void addNewProduct_WithInvalidRequest_ShouldReturnBadRequest() throws Exception {
        ProductCreateRequest invalidRequest = new ProductCreateRequest();
        invalidRequest.setName("");
        invalidRequest.setDescription("");
        invalidRequest.setPrice(new BigDecimal("-3"));


        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))

                .andExpect(status().isBadRequest());

        // .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Validation failed"));
    }

    @Test
    void getAllNonDeletedProducts_IfThereIsAtLeastOneProduct_ShouldReturn200OkAndInformationOfProducts() throws Exception {

        ProductResponse productResponse = new ProductResponse();
        productResponse.setId(1L);
        productResponse.setName("Laptop");
        productResponse.setDescription("You can play games");
        productResponse.setPrice(new BigDecimal("49999.50"));
        productResponse.setStatus(ProductStatus.ACTIVE);

        ProductResponse productResponse2 = new ProductResponse();
        productResponse2.setId(2L);
        productResponse2.setName("Laptop 2");
        productResponse2.setDescription("You can watch videos");
        productResponse2.setPrice(new BigDecimal("48999.5"));
        productResponse2.setStatus(ProductStatus.OUT_OF_STOCK);

        List<ProductResponse> productResponses = List.of(productResponse, productResponse2);

        when(productService.getAllNonDeletedProducts()).thenReturn(productResponses);

        mockMvc.perform(get("/api/products")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[0].name").value("Laptop"))
                .andExpect(jsonPath("$[1].name").value("Laptop 2"))
                .andExpect(jsonPath("$[0].description").value("You can play games"))
                .andExpect(jsonPath("$[1].description").value("You can watch videos"))
                .andExpect(jsonPath("$[0].price").value(new BigDecimal("49999.5")))
                .andExpect(jsonPath("$[1].price").value(new BigDecimal("48999.5")))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"))
                .andExpect(jsonPath("$[1].status").value("OUT_OF_STOCK"));

        verify(productService, times(1)).getAllNonDeletedProducts();
    }

    @Test
    void getAllNonDeletedProducts_IfThereIsNoProduct_ShouldReturn200OkAndEmptyList() throws Exception {
        when(productService.getAllNonDeletedProducts()).thenReturn(List.of());

        mockMvc.perform(get("/api/products")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(productService, times(1)).getAllNonDeletedProducts();
    }

    @Test
    void deleteProduct_WithValidRequest_ShouldReturn200OkAndInformationOfProduct() throws Exception {
        Long productId = 1L;

        ProductResponse productResponse = new ProductResponse();
        productResponse.setId(productId);
        productResponse.setName("Laptop");
        productResponse.setDescription("You can play games");
        productResponse.setPrice(new BigDecimal("49999.50"));
        productResponse.setStatus(ProductStatus.DELETED);

        when(productService.deleteProduct(productId)).thenReturn(productResponse);

        mockMvc.perform(delete("/api/products/{id}", productId)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productId))
                .andExpect(jsonPath("$.status").value("DELETED"));

        verify(productService, times(1)).deleteProduct(productId);
    }

    @Test
    void deleteProduct_WithInvalidRequest_ShouldReturn404NotFound() throws Exception {
        Long invalidProductId = 999L;

        when(productService.deleteProduct(invalidProductId))
                .thenThrow((new ResourceNotFoundException("Cannot found product with ID " + invalidProductId)));

        mockMvc.perform(delete("/api/products/{id}", invalidProductId)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isNotFound());

        verify(productService, times(1)).deleteProduct(invalidProductId);
    }
}
