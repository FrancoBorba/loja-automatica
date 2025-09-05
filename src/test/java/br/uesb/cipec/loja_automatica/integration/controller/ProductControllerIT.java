package br.uesb.cipec.loja_automatica.integration.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.checkerframework.checker.units.qual.m;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.uesb.cipec.loja_automatica.DTO.ProductDTO;
import br.uesb.cipec.loja_automatica.enums.UserRole;
import br.uesb.cipec.loja_automatica.integration.AbstractIntegrationTest;
import br.uesb.cipec.loja_automatica.model.Product;
import br.uesb.cipec.loja_automatica.model.User;
import br.uesb.cipec.loja_automatica.repository.ProductRepository;
import br.uesb.cipec.loja_automatica.repository.UserRepository;
import br.uesb.cipec.loja_automatica.security.JwtUtil;
import br.uesb.cipec.loja_automatica.service.index.ProductIndexingService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc // Enables MockMVC
public class ProductControllerIT extends AbstractIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc; 

    @Autowired
    private ProductRepository productRepository; // The real repository

    @Autowired
    private ObjectMapper objectMapper; // Convert objects in json

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

        @Autowired
    private ProductIndexingService productIndexingService;

    private Product product;

    @BeforeEach
    void setUp(){

        productRepository.deleteAll(); // Clean the repository

        
        product = new Product();
        product.setName("Teclado Mecânico Teste");
        product.setDescription("Descrição do teclado");
        product.setPrice(new BigDecimal("250.50"));
        product.setAmount(10);
    }

   @Test
   @DisplayName("Given an existing product, when calling findById, then should return 200 OK and the product data")
   public void givenExistingProduct_whenFindById_thenReturnsOkAndProductData() throws Exception {
        // --- ARRANGE ---
    
        Product savedProduct = productRepository.save(product);
        Long productId = savedProduct.getId();

       // ACT : ARRANGE
        
       // Check if the status is correct
       mockMvc.perform(get("/api/products/{id}" , productId)).andExpect(status().isOk())

       // Check if the json fields matchers
       .andExpect(jsonPath("$.id").value(productId))
        .andExpect(jsonPath("$.name").value("Teclado Mecânico Teste"))
        .andExpect(jsonPath("$.price").value(250.50));
}

    @Test
    @DisplayName("Given a non-existent product ID, when calling findById, then should return 404 Not Found")
    void givenNonExistentProduct_whenFindById_thenReturnsNotFound() throws Exception {

        mockMvc.perform(get("/api/products/{id}" , 100L)).andExpect(status().isNotFound());
    }

    // --- Testes para GET /api/products (paginado) ---
    
   @Test
    @DisplayName("When calling findAll with pagination parameters, then should return a paginated result")
    void whenFindAll_thenReturnsOkAndPaginatedResult() throws Exception {
        // --- ARRANGE ---
   
        List<Product> productList = new ArrayList<>();
      
        for (int i = 1; i <= 6; i++) {
            Product p = new Product();
            p.setName("Produto " + i);
            p.setDescription("Descrição do Produto " + i);
            p.setPrice(new BigDecimal(10.00 * i));
            p.setAmount(10);
            productList.add(p);
        }
         productRepository.saveAll(productList);

        productIndexingService.indexAllProductsFromDatabase();

        // (Elasticsearch é "near real-time")
        Thread.sleep(1000); 

        // --- ACT & ASSERT ---
        mockMvc.perform(get("/api/products")
                      
                        .param("page", "0")
                        .param("size", "5")
                        .param("sort", "name,asc")) 
                .andExpect(status().isOk())
            
                .andExpect(jsonPath("$.totalElements").value(6))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.number").value(0))
            
                .andExpect(jsonPath("$.content.length()").value(5))
                .andExpect(jsonPath("$.content[0].name").value("Produto 1"))
                .andExpect(jsonPath("$.content[4].name").value("Produto 5"));
    }

    
    // --- Testes para POST /api/products (Segurança) ---

  @Test
    @DisplayName("Given valid product data, when calling create as ADMIN, then should return 201 Created")
    void givenValidData_whenCreateAsAdmin_thenReturnsCreated() throws Exception {
        // --- ARRANGE ---
        
        //  Get a valid JWT token for an ADMIN user                        
        String adminToken = getJwtToken("admin@test.com", UserRole.ADMIN);

        // Prepares the DTO that will be sent in the request body
        ProductDTO dto = new ProductDTO();
        dto.setName("Teclado Mecânico Teste");
        dto.setDescription("Descrição do teclado");
        dto.setPrice(new BigDecimal("250.50"));
        dto.setAmount(10);

        // --- ACT & ASSERT ---
        mockMvc.perform(post("/api/products")
                  
                    .header("Authorization", "Bearer " + adminToken)
                
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto)))

                .andExpect(status().isCreated()) 
                .andExpect(jsonPath("$.id").isNumber()) 
                .andExpect(jsonPath("$.name").value("Teclado Mecânico Teste"))
                .andExpect(jsonPath("$.price").value(250.50));
    }

    @Test
    @DisplayName("Given valid product data, when calling create as USER, then should return 403 Forbidden")
    void givenValidData_whenCreateAsUser_thenReturnsForbidden() throws  Exception {
           // --- ARRANGE ---
        
        //  Get a valid JWT token for an ADMIN user                        
        String adminToken = getJwtToken("user@test.com", UserRole.USER);

        // Prepares the DTO that will be sent in the request body
        ProductDTO dto = new ProductDTO();
        dto.setName("Teclado Mecânico Teste");
        dto.setDescription("Descrição do teclado");
        dto.setPrice(new BigDecimal("250.50"));
        dto.setAmount(10);

        // --- ACT & ASSERT ---
        mockMvc.perform(post("/api/products")
                  
                    .header("Authorization", "Bearer " + adminToken)
                
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto)))

                .andExpect(status().isForbidden());
          
    }
    
    @Test
    @DisplayName("Given invalid product data, when calling create as ADMIN, then should return 400 Bad Request")
    void givenInvalidData_whenCreateAsAdmin_thenReturnsBadRequest() throws  Exception {
          // --- ARRANGE ---
        
        //  Get a valid JWT token for an ADMIN user                        
        String adminToken = getJwtToken("admin@test.com", UserRole.ADMIN);

        // Prepares the DTO that will be sent in the request body
        ProductDTO dto = new ProductDTO();
        dto.setName("Teclado Mecânico Teste");
        dto.setPrice(null);
        dto.setAmount(10);

        // --- ACT & ASSERT ---
        mockMvc.perform(post("/api/products")
                  
                    .header("Authorization", "Bearer " + adminToken)
                
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto)))

                .andExpect(status().isBadRequest());
    }


private String getJwtToken(String email, UserRole role) {
    User user = new User();
    user.setEmail(email);
    user.setPassword(passwordEncoder.encode("test-password"));
    user.setRole(role);
    user.setActive(true);
    user.setName("Test User");
    user.setDateOfBirth(LocalDate.now().minusYears(20));
    
    userRepository.save(user);

    return jwtUtil.generateToken(email);
}
}
