package com.example.commercecartapi;

import com.example.commercecartapi.dto.ProductCreateRequest;
import com.example.commercecartapi.dto.ProductResponse;
import com.example.commercecartapi.enums.ProductStatus;
import com.example.commercecartapi.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
public class ProductIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private ProductRepository productRepository;

    @Test
    void product_EndToEnd_FlowTesting(){
        /*
        * We're posting a product. After that, we get it by using get. Finally, we're deleting it softly.
        * */

        // ----------------------------------------------------
        // STEP 1: POST MAPPING TESTING
        // ----------------------------------------------------

        ProductCreateRequest createRequest = new ProductCreateRequest(); // This is added, checked, and ,finally, deleted.
        createRequest.setName("Laptop");
        createRequest.setDescription("You can play video games");
        createRequest.setPrice(new BigDecimal("56000"));

        ResponseEntity<ProductResponse> postResponse = restTemplate.postForEntity(
                "/api/products", // url
                createRequest, // parameter
                ProductResponse.class // return type
        );

        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(postResponse.getBody()).isNotNull();

        Long generatedId = postResponse.getBody().getId();
        assertThat(generatedId).isNotNull();
        assertThat(postResponse.getBody().getStatus()).isEqualTo(ProductStatus.ACTIVE);
        assertThat(postResponse.getBody().getName()).isEqualTo(createRequest.getName());
        assertThat(postResponse.getBody().getDescription()).isEqualTo(createRequest.getDescription());
        assertThat(postResponse.getBody().getPrice()).isEqualTo(createRequest.getPrice());

        // ----------------------------------------------------
        // STEP 2: GET MAPPING TESTING
        // ----------------------------------------------------

        ResponseEntity<ProductResponse[]> getResponse = restTemplate.getForEntity(
                "/api/products",
                ProductResponse[].class
        ); // We return a list on get mapping so we used []

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).isNotEmpty(); // we use isNotEmpty because our returning element is a list
        assertThat(getResponse.getBody())
                .extracting(ProductResponse::getName)
                .contains("Laptop");

        // ----------------------------------------------------
        // STEP 3: SOFT DELETE MAPPING TESTING
        // ----------------------------------------------------

        ResponseEntity<ProductResponse> deleteResponse = restTemplate.exchange(
                "/api/products/{id}",
                HttpMethod.DELETE,
                HttpEntity.EMPTY,
                ProductResponse.class,
                generatedId // our id parameter
        ); // we can get requests such as delete and put by using method .exchange()

        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(deleteResponse.getBody()).isNotNull();
        assertThat(deleteResponse.getBody().getStatus()).isEqualTo(ProductStatus.DELETED);

    }

    @AfterEach
    void tearDown() {
        productRepository.deleteAll();
    }
}
