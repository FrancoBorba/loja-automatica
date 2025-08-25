package br.uesb.cipec.loja_automatica.unit.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import br.uesb.cipec.loja_automatica.DTO.ProductDTO;
import br.uesb.cipec.loja_automatica.document.ProductDocument;
import br.uesb.cipec.loja_automatica.exception.ResourceNotFoundException;
import br.uesb.cipec.loja_automatica.mapper.ProductDocumentMapper;
import br.uesb.cipec.loja_automatica.mapper.ProductMapper;
import br.uesb.cipec.loja_automatica.model.Product;
import br.uesb.cipec.loja_automatica.repository.ItemPurchaseRepository;
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
    @Mock
    ItemPurchaseRepository itemPurchaseRepository;

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
      // Arrange
      List<Product> products = new ArrayList();
      for(int i = 0 ; i < 10 ; i++){
        products.add(productEntity);
      }
     Pageable pageableWithProducts = PageRequest.of(0, 10);

    Page<Product> productPage = new PageImpl<>(products, pageableWithProducts, 10L);

     when(mapper.toDTO(productEntity)).thenReturn(productDTO);
     

     when(repository.findAll(any(Pageable.class))).thenReturn(productPage);

    //  ACT 
    Page<ProductDTO> resultPage = mockService.findAll(pageableWithProducts);

      //  ASSERT
    assertNotNull(resultPage);
    assertEquals(10, resultPage.getContent().size());
    assertEquals("Teclado Teste", resultPage.getContent().get(0).getName());
    assertEquals(1, resultPage.getTotalPages());
    assertEquals(10L, resultPage.getTotalElements());
}

    @Test
    @DisplayName("Should return an empty paginated list when no products exist")
    void testFindAll_WhenNoProductsExist_ShouldReturnEmptyPage() {
      // Arrange ; Given

      Pageable pageableWithZeroProduct = PageRequest.of(0, 10);

      when(repository.findAll(any(Pageable.class))).thenReturn(Page.empty());

      // Act
      Page<ProductDTO> result = mockService.findAll(pageableWithZeroProduct);

      // Assert
      assertNotNull(result);
      assertTrue(result.isEmpty());
      assertEquals(0, result.getTotalElements());

       
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

        ProductDocument document = new ProductDocument();


        when(mapper.toEntity(inputDTO)).thenReturn(entityToSave);

        when(repository.save(entityToSave)).thenReturn(productEntity);

        when(mapper.toDTO(productEntity)).thenReturn(productDTO);

         when(documentMapper.toDocument(any(Product.class))).thenReturn(document);

      // Act ; When

      ProductDTO result = mockService.create(inputDTO);

      // Assert; Then

      assertNotNull(result);
      assertEquals(1L, result.getId());
      assertEquals("Teclado Teste", result.getName());
      assertEquals(10, result.getAmount());
      assertEquals(expectedValue, result.getPrice());

    verify(repository, times(1)).save(any(Product.class));
    verify(indexingService, times(1)).save(any(ProductDocument.class));
    }

    @Test
    @DisplayName("Should update a product successfully when it exists")
    void testUpdate_WhenProductExists_ShouldReturnUpdatedProductDTO(){

    // Arrange ; given
    when(repository.findById(1L)).thenReturn(Optional.of(productEntity));

    
    ProductDTO updatedDataDTO = new ProductDTO();
    updatedDataDTO.setId(1L);
    updatedDataDTO.setName("Teclado Teste Atualizado");
    updatedDataDTO.setPrice(new BigDecimal("175.00"));
    updatedDataDTO.setAmount(5);

    Product updatedEntity = new Product();
    updatedEntity.setId(1L);
    updatedEntity.setName("Teclado Teste Atualizado");
    updatedEntity.setPrice(new BigDecimal("175.00"));
    updatedEntity.setAmount(5);

    ProductDocument document = new ProductDocument();

    when(repository.save(any(Product.class))).thenReturn(updatedEntity);

    when(mapper.toDTO(any(Product.class))).thenReturn(updatedDataDTO);

    when(documentMapper.toDocument(any(Product.class))).thenReturn(document);

    // Act ; When
    ProductDTO result = mockService.update(updatedDataDTO);

    // Assert ; then
    assertNotNull(result);
    assertEquals(1L, result.getId());
    assertEquals("Teclado Teste Atualizado", result.getName());
    assertEquals(new BigDecimal("175.00"), result.getPrice());

    verify(repository, times(1)).save(any(Product.class));
    verify(indexingService, times(1)).save(any(ProductDocument.class));

    }

    @Test
    @DisplayName("Should delete a product successfully when it exists and is not in use")
    void testDelete_WhenProductExistsAndNotInUse_ShouldCompleteWithoutError(){

      // Arrange
      when(repository.findById(1L)).thenReturn(Optional.of(productEntity));

      when(itemPurchaseRepository.existsByProductId(1L)).thenReturn(false);

      // Act
       mockService.delete(1L);

       // Assert
       verify(repository, times(1)).delete(productEntity);
       verify(itemPurchaseRepository, times(1)).existsByProductId(1L);


    }

    // ---------------- Exception Path Tests ------------

    @Test
    @DisplayName("Should throw ResourceNotFoundException when findById does not find an ID")
    void testFindById_WhenProductDoesNotExist_ShouldThrowResourceNotFoundException() {
      // Arrange; Given
      when(repository.findById(2L)).thenReturn(Optional.empty());

      // Act|Assert ; Then|When
      Exception exception = assertThrows(ResourceNotFoundException.class  ,
      ()-> {
        mockService.findById(2L);
      });

      String expectedMessage = "Product not found with id: " + 2;
      String actualMessage = exception.getMessage();

      assertTrue(expectedMessage.contains(actualMessage));

  
    }
    
    // --- CREATE Method Exceptions ---

    @Test
     @Disabled
    @DisplayName("Should throw RequiredObjectIsNullException when creating a null product")
    void testCreate_WithNullProduct_ShouldThrowRequiredObjectIsNullException() {

  
    }

    @Test
     @Disabled
    @DisplayName("Should throw InvalidProductDataException when creating with a blank name")
    void testCreate_WithBlankName_ShouldThrowInvalidProductDataException() {
    }

    @Test
     @Disabled
    @DisplayName("Should throw InvalidProductDataException when creating with a negative price")
    void testCreate_WithNegativePrice_ShouldThrowInvalidProductDataException() {
    }
    
    @Test
     @Disabled
    @DisplayName("Should throw InvalidProductDataException when creating with a negative amount")
    void testCreate_WithNegativeAmount_ShouldThrowInvalidProductDataException() {
        // NOVO: Faltava o teste para a quantidade.
    }

    // --- UPDATE Method Exceptions ---
    
    @Test
     @Disabled
    @DisplayName("Should throw ResourceNotFoundException when trying to update a non-existent product")
    void testUpdate_WhenProductDoesNotExist_ShouldThrowResourceNotFoundException() {
    }
    
    @Test
     @Disabled
    @DisplayName("Should throw InvalidProductDataException when updating with a blank name")
    void testUpdate_WithBlankName_ShouldThrowInvalidProductDataException() {
        // NOVO: É importante testar as validações no update também.
    }

    // --- DELETE Method Exceptions ---

    @Test
     @Disabled
    @DisplayName("Should throw ResourceNotFoundException when trying to delete a non-existent product")
    void testDelete_WhenProductDoesNotExist_ShouldThrowResourceNotFoundException() {
    }

    @Test
     @Disabled
    @DisplayName("Should throw ProductInUseException when trying to delete a product that is in a purchase")
    void testDelete_WhenProductIsInUse_ShouldThrowProductInUseException() {
    }

   
}