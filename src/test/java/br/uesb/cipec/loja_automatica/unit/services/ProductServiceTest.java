package br.uesb.cipec.loja_automatica.unit.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.uesb.cipec.loja_automatica.DTO.ProductDTO;
import br.uesb.cipec.loja_automatica.mapper.ProductDocumentMapper;
import br.uesb.cipec.loja_automatica.mapper.ProductMapper;
import br.uesb.cipec.loja_automatica.model.Product;
import br.uesb.cipec.loja_automatica.repository.ProductRepository;
import br.uesb.cipec.loja_automatica.service.ProductService;
import br.uesb.cipec.loja_automatica.service.index.ProductIndexingService;


@ExtendWith(MockitoExtension.class) 
public class ProductServiceTest {

    // Test Data

    Product productEntity; // Entity
    ProductDTO productDTO;  // DTO
    BigDecimal expectedValue;

    // Mock
    @Mock  // Create a mock of repository 
    ProductRepository repository;
    @Mock
    ProductMapper mapper;
    @Mock
    ProductDocumentMapper documentMapper;
    @Mock
    ProductIndexingService indexingService;

    @InjectMocks // The class we are going to test
    ProductService mockService; 

   
    

    // Run before each test
    @BeforeEach
    void setUp(){
        // Mock setup 
      expectedValue = new BigDecimal("150.00");

      productEntity = new Product();
      productEntity.setId(1L);
      productEntity.setName("Teclado Teste");
      productEntity.setPrice(new BigDecimal("150.00"));
      productEntity.setAmount(10);

      productDTO = new ProductDTO();
      productDTO.setId(1L);
      productDTO.setName("Teclado Teste");
      productDTO.setPrice(new BigDecimal("150.00"));
      productDTO.setAmount(10);


    }
    
    // ------------------- Happy Path Tests ----------------

    @Test
    @DisplayName("Should return a ProductDTO when ID exists")
    void testFindById_WhenProductExists_ShouldReturnProductDTO(){
      // Arranje;Act;  Assert | Given; When ; Then

      // Arrange; Given
      when(repository.findById(1L))
      .thenReturn(Optional.of(productEntity));

      when(mapper.toDTO(productEntity)).thenReturn(productDTO);

      // Act; When
      ProductDTO result = mockService.findById(1L);

             

      // Assert; Then

      assertNotNull(result);
      assertEquals(1L, result.getId());
      assertEquals("Teclado Teste", result.getName());
      assertEquals(10, result.getAmount());
      assertEquals(expectedValue, result.getPrice());
    }

    @Test
    @DisplayName("Should return a paginated list of products")
    void testFindAll_ShouldReturnPagedListOfProducts(){
    }

    @Test
    @DisplayName("Should return an empty paginated list when no products exist")
    void testFindAll_WhenNoProductsExist_ShouldReturnEmptyPage() {
       
    }

    @Test
    @DisplayName("Should create a new product successfully with valid data")
    void testCreate_WithValidData_ShouldReturnNewProductDTO(){


        Product entityToSave = new Product();
        entityToSave.setName("Teclado Teste");
        entityToSave.setPrice(new BigDecimal("150.00"));
        entityToSave.setAmount(10);

        ProductDTO inputDTO = new ProductDTO();
        inputDTO.setName("Teclado Teste");
        inputDTO.setPrice(new BigDecimal("150.00"));
        inputDTO.setAmount(10);


   

        when(mapper.toEntity(productDTO)).thenReturn(entityToSave);

        when(repository.save(entityToSave)).thenReturn(productEntity);

        when(mapper.toDTO(productEntity)).thenReturn(productDTO);

      // Act ; When

      ProductDTO result = mockService.create(productDTO);

      // Assert; Then

      assertNotNull(result);
      assertEquals(1L, result.getId());
      assertEquals("Teclado Teste", result.getName());
      assertEquals(10, result.getAmount());
      assertEquals(expectedValue, result.getPrice());
    }

    @Test
    @DisplayName("Should update a product successfully when it exists")
    void testUpdate_WhenProductExists_ShouldReturnUpdatedProductDTO(){
    }

    @Test
    @DisplayName("Should delete a product successfully when it exists and is not in use")
    void testDelete_WhenProductExistsAndNotInUse_ShouldCompleteWithoutError(){
    }

    // ---------------- Exception Path Tests ------------

    @Test
    @DisplayName("Should throw ResourceNotFoundException when findById does not find an ID")
    void testFindById_WhenProductDoesNotExist_ShouldThrowResourceNotFoundException() {
    }
    
    // --- CREATE Method Exceptions ---

    @Test
    @DisplayName("Should throw RequiredObjectIsNullException when creating a null product")
    void testCreate_WithNullProduct_ShouldThrowRequiredObjectIsNullException() {
    }

    @Test
    @DisplayName("Should throw InvalidProductDataException when creating with a blank name")
    void testCreate_WithBlankName_ShouldThrowInvalidProductDataException() {
    }

    @Test
    @DisplayName("Should throw InvalidProductDataException when creating with a negative price")
    void testCreate_WithNegativePrice_ShouldThrowInvalidProductDataException() {
    }
    
    @Test
    @DisplayName("Should throw InvalidProductDataException when creating with a negative amount")
    void testCreate_WithNegativeAmount_ShouldThrowInvalidProductDataException() {
        // NOVO: Faltava o teste para a quantidade.
    }

    // --- UPDATE Method Exceptions ---
    
    @Test
    @DisplayName("Should throw ResourceNotFoundException when trying to update a non-existent product")
    void testUpdate_WhenProductDoesNotExist_ShouldThrowResourceNotFoundException() {
    }
    
    @Test
    @DisplayName("Should throw InvalidProductDataException when updating with a blank name")
    void testUpdate_WithBlankName_ShouldThrowInvalidProductDataException() {
        // NOVO: É importante testar as validações no update também.
    }

    // --- DELETE Method Exceptions ---

    @Test
    @DisplayName("Should throw ResourceNotFoundException when trying to delete a non-existent product")
    void testDelete_WhenProductDoesNotExist_ShouldThrowResourceNotFoundException() {
    }

    @Test
    @DisplayName("Should throw ProductInUseException when trying to delete a product that is in a purchase")
    void testDelete_WhenProductIsInUse_ShouldThrowProductInUseException() {
    }

   
}