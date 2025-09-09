package br.uesb.cipec.loja_automatica.unit.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.uesb.cipec.loja_automatica.DTO.ItemPurchaseRequestDTO;
import br.uesb.cipec.loja_automatica.DTO.PurchaseResponseDTO;
import br.uesb.cipec.loja_automatica.component.AuthenticationFacade;
import br.uesb.cipec.loja_automatica.enums.StatusPurchase;
import br.uesb.cipec.loja_automatica.exception.InvalidPurchaseQuantityException;
import br.uesb.cipec.loja_automatica.exception.RequiredObjectIsNullException;
import br.uesb.cipec.loja_automatica.exception.ResourceNotFoundException;
import br.uesb.cipec.loja_automatica.mapper.PurchaseMapper;
import br.uesb.cipec.loja_automatica.model.ItemPurchase;
import br.uesb.cipec.loja_automatica.model.Product;
import br.uesb.cipec.loja_automatica.model.Purchase;
import br.uesb.cipec.loja_automatica.model.User;
import br.uesb.cipec.loja_automatica.repository.ProductRepository;
import br.uesb.cipec.loja_automatica.repository.PurchaseRepository;
import br.uesb.cipec.loja_automatica.service.ItemPurchaseService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ItemPurchaseServiceTest {

    @Mock
    private PurchaseRepository repository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private PurchaseMapper purchaseMapper;
    @Mock
    private AuthenticationFacade facade;

    @InjectMocks
    ItemPurchaseService service;

    // Dados de apoio
    ItemPurchaseRequestDTO itemRequest;
    PurchaseResponseDTO purchaseResponseDTO;
    ItemPurchase entityItem;
    Purchase purchase;
    Product product;
    User currentUser;

    @BeforeEach
    void setUp() {
      
        currentUser = new User();
        currentUser.setId(1L);


        product = new Product();
        product.setId(10L);
        product.setName("Produto Teste");
        product.setPrice(new BigDecimal("100.00"));

        
        entityItem = new ItemPurchase();
        entityItem.setId(1L);
        entityItem.setProduct(product);
        entityItem.setQuantity(1);
        entityItem.setSubvalor(product.getPrice());

        
        purchase = new Purchase();
        purchase.setId(200L);
        purchase.setUser(currentUser);
        purchase.setItens(new ArrayList<>());
        purchase.setValue(BigDecimal.ZERO);

        // request DTO
        itemRequest = new ItemPurchaseRequestDTO();
        itemRequest.setProductID(product.getId());
        itemRequest.setQuantity(3);

        // response DTO fake
        purchaseResponseDTO = new PurchaseResponseDTO();
    }

    // ---------------- HAPPY PATH  ----------------

      @Test
    @DisplayName("Should create a new cart when adding an item and no active cart exists")
    void shouldCreateNewCartWhenNoActiveCartExists() {
      

     // Given ; Arrange
        Purchase newCart = new Purchase();
        newCart.setUser(currentUser);
        newCart.setItens(new ArrayList<>());

        when(facade.getCurrentUser()).thenReturn(currentUser);

       
        when(repository.findByUserIdAndStatus(anyLong(), any(StatusPurchase.class)))
            .thenReturn(Optional.empty());
        
      
        when(repository.save(any(Purchase.class))).thenReturn(newCart);

    
        when(productRepository.findById(itemRequest.getProductID())).thenReturn(Optional.of(product));
        
 
        when(purchaseMapper.toResponseDTO(any(Purchase.class))).thenReturn(purchaseResponseDTO);


        // When ; Act
      
        service.addItemToCart(itemRequest);


        // Thwn ; Assert

        verify(repository, times(2)).save(any(Purchase.class));
        assertEquals(1, newCart.getItens().size());
        assertEquals(itemRequest.getProductID(), newCart.getItens().get(0).getProduct().getId());
    }

    @Test
    void shouldAddNewItemToCartWhenProductNotInCart() {

       // Given ; Arrange

        when(facade.getCurrentUser()).thenReturn(currentUser);

        when(repository.findByUserIdAndStatus(currentUser.getId(), StatusPurchase.AGUARDANDO_PAGAMENTO))
        .thenReturn(Optional.of(purchase));

        when(productRepository.findById(itemRequest.getProductID())).
        thenReturn(Optional.of(product));

        when(repository.save(any(Purchase.class))).thenReturn(purchase);

        when(purchaseMapper.toResponseDTO(any(Purchase.class))).thenReturn(purchaseResponseDTO);


        // When ; Act

        PurchaseResponseDTO result = service.addItemToCart(itemRequest);

        // Then ; Assert
        
        ItemPurchase addedItem = purchase.getItens().get(0);

        assertEquals(3, addedItem.getQuantity()); 
        assertNotNull(result);
        assertEquals(1, purchase.getItens().size());
        assertEquals(3, purchase.getItens().get(0).getQuantity());
        assertEquals(result, purchaseResponseDTO); 
        assertEquals(new BigDecimal("300.00"), addedItem.getSubvalor());
        assertEquals(new BigDecimal("300.00"), purchase.getValue());


     
        verify(repository, times(1))
        .save(any(Purchase.class));

         
        

    }
        @Test
    @DisplayName("Should increment quantity when adding a product that is already in the cart")
    void shouldIncrementQuantityWhenAddingExistingProduct() {
        // --- ARRANGE ---

        entityItem.setQuantity(1);
        entityItem.setSubvalor(new BigDecimal("100.00"));  
        purchase.getItens().add(entityItem);
        purchase.setValue(new BigDecimal("100.00"));

        itemRequest.setProductID(entityItem.getProduct().getId());
        itemRequest.setQuantity(2);

 
        when(facade.getCurrentUser()).thenReturn(currentUser);

        when(repository.findByUserIdAndStatus(anyLong(), any()))
            .thenReturn(Optional.of(purchase));
        when(repository.save(any(Purchase.class))).thenReturn(purchase);

        // --- ACT ---
   
        service.addItemToCart(itemRequest);

        // --- ASSERT ---

        assertEquals(1, purchase.getItens().size());
  
        ItemPurchase updatedItem = purchase.getItens().get(0);
        assertEquals(3, updatedItem.getQuantity());
       
        assertEquals(new BigDecimal("300.00"), purchase.getValue()); 
    }

    @Test
    void shouldUpdateQuantityWhenProductAlreadyInCart() {

        // Given ; Arrange

      purchase.getItens().add(entityItem);

      when(facade.getCurrentUser()).thenReturn(currentUser);

      when(repository.findByUserIdAndStatus(currentUser.getId(), StatusPurchase.AGUARDANDO_PAGAMENTO))
        .thenReturn(Optional.of(purchase));


        when(repository.save(any(Purchase.class))).thenReturn(purchase);

        when(purchaseMapper.toResponseDTO(any(Purchase.class))).thenReturn(purchaseResponseDTO);

        // When ; Act
        purchaseResponseDTO = service.updateItemQuantity(1L, 4);

        // Then ; Assert
          ItemPurchase addedItem = purchase.getItens().get(0);
        assertNotNull(purchaseResponseDTO);
        assertEquals(4, addedItem.getQuantity());
         assertEquals(new BigDecimal("400.00"), addedItem.getSubvalor());
        assertEquals(new BigDecimal("400.00"), purchase.getValue());

         verify(repository, times(1))
        .save(any(Purchase.class));



    }
    

    @Test
    void shouldRecalculatePurchaseTotalWhenAddingItem() {
        Product secondProduct = new Product();
        secondProduct.setId(11L);
        secondProduct.setName("Produto Secundário");
        secondProduct.setPrice(new BigDecimal("50.00"));

        ItemPurchase secondItem = new ItemPurchase();
        secondItem.setId(101L);
        secondItem.setProduct(secondProduct);
        secondItem.setQuantity(2); 
        secondItem.setSubvalor(new BigDecimal("100.00")); 

        entityItem.setQuantity(1);
        entityItem.setSubvalor(new BigDecimal("100.00"));

        purchase.getItens().add(entityItem);   // Will be updated
        purchase.getItens().add(secondItem);  // will not be updated

        purchase.setValue(new BigDecimal("200.00"));

      when(facade.getCurrentUser()).thenReturn(currentUser);

      when(repository.findByUserIdAndStatus(currentUser.getId(), StatusPurchase.AGUARDANDO_PAGAMENTO))
        .thenReturn(Optional.of(purchase));

      when(repository.save(any(Purchase.class))).thenReturn(purchase);

      when(purchaseMapper.toResponseDTO(any(Purchase.class))).thenReturn(purchaseResponseDTO);

      purchaseResponseDTO = service.updateItemQuantity(1L, 3);

      assertNotNull(purchaseResponseDTO);

      ItemPurchase updatedItem = purchase.getItens().get(0);
      ItemPurchase unUpdatedItem = purchase.getItens().get(1);

      assertEquals(3, updatedItem.getQuantity());
      assertEquals(new BigDecimal("300.00"), updatedItem.getSubvalor());
      assertEquals(2, unUpdatedItem.getQuantity());
      assertEquals(new BigDecimal("100.00"), unUpdatedItem.getSubvalor());
      assertEquals(new BigDecimal("400.00") , purchase.getValue());


      verify(repository, times(1))
      .save(any(Purchase.class));

    }


    @Test
    void shouldRemoveItemFromCartSuccessfully() {

      // Given ; Arrange
      purchase.getItens().add(entityItem);


      when(facade.getCurrentUser()).thenReturn(currentUser);

        when(repository.findByUserIdAndStatus(anyLong(), any()))
                    .thenReturn(Optional.of(purchase));


      when(repository.save(any(Purchase.class))).thenReturn(purchase);

      when(purchaseMapper.toResponseDTO(any(Purchase.class))).thenReturn(purchaseResponseDTO);

      // When ; Act

      service.removeItemFromCart(1L);

      // Then ; Assert

      assertEquals(0, purchase.getItens().size());

      assertEquals(new BigDecimal("0"), purchase.getValue());

     verify(repository, times(1))
      .save(any(Purchase.class));


    }

    @Test
    void shouldRecalculatePurchaseTotalWhenItemRemoved() {

      // Given ; Arrange
      Product secondProduct = new Product();
        secondProduct.setId(11L);
        secondProduct.setName("Produto Secundário");
        secondProduct.setPrice(new BigDecimal("15.00"));

        ItemPurchase secondItem = new ItemPurchase();
        secondItem.setId(101L);
        secondItem.setProduct(secondProduct);
        secondItem.setQuantity(2); 
        secondItem.setSubvalor(new BigDecimal("30.00")); 

        entityItem.setQuantity(10);

        // Total value = 1030
        purchase.getItens().add(entityItem);
        purchase.getItens().add(secondItem);

      when(facade.getCurrentUser()).thenReturn(currentUser);

      when(repository.findByUserIdAndStatus(currentUser.getId(), StatusPurchase.AGUARDANDO_PAGAMENTO))
        .thenReturn(Optional.of(purchase));

      when(repository.save(any(Purchase.class))).thenReturn(purchase);

      when(purchaseMapper.toResponseDTO(any(Purchase.class))).thenReturn(purchaseResponseDTO);

      // When ; Act
      

      PurchaseResponseDTO result = service.removeItemFromCart(1L);

      // Then ; Assert
      ItemPurchase item = purchase.getItens().get(0);

      assertEquals(1L, purchase.getItens().size());
      assertEquals(new BigDecimal("30.00"), purchase.getValue());
      assertEquals(new BigDecimal("30.00"), item.getSubvalor());
      assertEquals(2, item.getQuantity());

      verify(repository , times(1)).save(purchase);

    }

 

    @Test
    void shouldRemoveItemWhenNewQuantityIsZero() {

      // Given ; Arrange
       Product secondProduct = new Product();
        secondProduct.setId(2L);
        secondProduct.setName("Produto Secundário");
        secondProduct.setPrice(new BigDecimal("15.00"));

        ItemPurchase secondItem = new ItemPurchase();
        secondItem.setId(2L);
        secondItem.setProduct(secondProduct);
        secondItem.setQuantity(2); 
        secondItem.setSubvalor(new BigDecimal("30.00")); 

        Product thirdProduct = new Product();
        thirdProduct.setId(3L);
        thirdProduct.setName("Produto Ternário");
        thirdProduct.setPrice(new BigDecimal("25.00"));

        ItemPurchase thirdItem = new ItemPurchase();
        thirdItem.setId(3L);
        thirdItem.setProduct(secondProduct);
        thirdItem.setQuantity(3); 
        thirdItem.setSubvalor(new BigDecimal("50.00"));
        

        entityItem.setQuantity(10);

        purchase.getItens().add(entityItem);
        purchase.getItens().add(secondItem);
        purchase.getItens().add(thirdItem);

      when(facade.getCurrentUser()).thenReturn(currentUser);

      when(repository.findByUserIdAndStatus(currentUser.getId(), StatusPurchase.AGUARDANDO_PAGAMENTO))
      .thenReturn(Optional.of(purchase));

      when(repository.save(any(Purchase.class))).thenReturn(purchase);

      when(purchaseMapper.toResponseDTO(purchase)).thenReturn(purchaseResponseDTO);

      // When ; Act 
      service.updateItemQuantity(1L, 0);
      

      // Then ; Assert

      ItemPurchase item1  = purchase.getItens().get(0); // Second
       ItemPurchase item2  = purchase.getItens().get(1); // Third

      assertEquals(2, purchase.getItens().size());
      assertEquals(new BigDecimal("80.00"), purchase.getValue());

      assertEquals(new BigDecimal("30.00"), item1.getSubvalor());
      assertEquals(2, item1.getQuantity());

      assertEquals(new BigDecimal("50.00"), item2.getSubvalor());
      assertEquals(3, item2.getQuantity());

      verify(repository , times(1)).save(purchase);

    }

     @Test
    void shouldRemoveItemWhenNewQuantityIsNegative() {

      // Given ; Arrange
        Product secondProduct = new Product();
        secondProduct.setId(2L);
        secondProduct.setName("Produto Secundário");
        secondProduct.setPrice(new BigDecimal("15.00"));

        ItemPurchase secondItem = new ItemPurchase();
        secondItem.setId(2L);
        secondItem.setProduct(secondProduct);
        secondItem.setQuantity(2); 
        secondItem.setSubvalor(new BigDecimal("30.00")); 

        Product thirdProduct = new Product();
        thirdProduct.setId(3L);
        thirdProduct.setName("Produto Ternário");
        thirdProduct.setPrice(new BigDecimal("25.00"));

        ItemPurchase thirdItem = new ItemPurchase();
        thirdItem.setId(3L);
        thirdItem.setProduct(secondProduct);
        thirdItem.setQuantity(3); 
        thirdItem.setSubvalor(new BigDecimal("50.00"));
        

        entityItem.setQuantity(10);
        entityItem.setSubvalor(new BigDecimal("1000.00"));

        purchase.getItens().add(entityItem);
        purchase.getItens().add(secondItem);
        purchase.getItens().add(thirdItem);

      when(facade.getCurrentUser()).thenReturn(currentUser);

      when(repository.findByUserIdAndStatus(currentUser.getId(), StatusPurchase.AGUARDANDO_PAGAMENTO))
      .thenReturn(Optional.of(purchase));

      when(repository.save(any(Purchase.class))).thenReturn(purchase);

      when(purchaseMapper.toResponseDTO(purchase)).thenReturn(purchaseResponseDTO);

      // When ; Act 
      service.updateItemQuantity(2L, -5);
      

      // Then ; Assert

      ItemPurchase firstItem  = purchase.getItens().get(0); // First
      ItemPurchase thirItemPurchase  = purchase.getItens().get(1); // Third

      assertEquals(2, purchase.getItens().size());
      assertEquals(new BigDecimal("1050.00"), purchase.getValue());
      assertEquals("Produto Teste", firstItem.getProduct().getName());

      assertEquals(new BigDecimal("1000.00"), firstItem.getSubvalor());
      assertEquals(10, firstItem.getQuantity());

      assertEquals(new BigDecimal("50.00"), thirItemPurchase.getSubvalor());
      assertEquals(3, thirItemPurchase.getQuantity());

      verify(repository , times(1)).save(purchase);

    }


    // ---------------- UNHAPPY PATH ----------------

    @Test
    void shouldThrowExceptionWhenAddingItemToCartAndItemRequestIsNull() {
      // Given ; Arrange

      Exception exception = assertThrows(RequiredObjectIsNullException.class, 
     () ->{
      service.addItemToCart(null);
     } );

     
         String expectedMessage = "The item to be added to the cart cannot be null.";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);


    }

    @Test
    void shouldThrowExceptionWhenQuantityIsZero() {

        itemRequest.setQuantity(0);

           Exception exception = assertThrows(InvalidPurchaseQuantityException.class, 
     () ->{
      service.addItemToCart(itemRequest);
     } );

     
         String expectedMessage = "The quantity of an item to be added must be greater than zero.";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);

    }

    @Test
    void shouldThrowExceptionWhenAddingItemToCartAndProductNonExistis() {

      purchase.setItens(new ArrayList<>());

      when(facade.getCurrentUser()).thenReturn(currentUser);

      when(repository.findByUserIdAndStatus(anyLong(), any())). 
      thenReturn(Optional.of(purchase));

      when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

      
      Exception exception = assertThrows(ResourceNotFoundException.class, 
      () ->{
        service.addItemToCart(itemRequest);
      });

        String expectedMessage = "Product not found";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);



    }

    @Test
    void shouldThrowExceptionWhenUpdatedAnItemNotFoundInCart() {

      
  
      when(facade.getCurrentUser()).thenReturn(currentUser);

      when(repository.findByUserIdAndStatus(anyLong(), any())). 
      thenReturn(Optional.of(purchase));

 

      
      Exception exception = assertThrows(ResourceNotFoundException.class, 
      () ->{
        service.updateItemQuantity(2L, 2);
      });

        String expectedMessage = "Item with ID " + 2 + " not found in the cart.";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);


    }

      @Test
    void shouldThrowExceptionWhenRemoveAnItemNotFoundInCart() {

      
      purchase.setItens(new ArrayList<>());
      purchase.getItens().add(entityItem);

      when(facade.getCurrentUser()).thenReturn(currentUser);

      when(repository.findByUserIdAndStatus(anyLong(), any())). 
      thenReturn(Optional.of(purchase));

      Exception exception = assertThrows(ResourceNotFoundException.class, 
      () ->{
        service.removeItemFromCart(2L);
      });

        String expectedMessage = "Item with ID " + 2 + " not found in the cart.";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);


    }



    @Test
    void shouldThrowExceptionWhenNoActiveCartExists() {

      // Given ; Arrange

      when(facade.getCurrentUser()).thenReturn(currentUser);

      when(repository.findByUserIdAndStatus(anyLong(), any()))
      .thenReturn(Optional.empty());

      Exception exception = assertThrows(ResourceNotFoundException.class, 
      () ->{
        // Any method with invoke  getActiveCartForCurrentUser()  could be called here
        service.removeItemFromCart(2L);
      });

        String expectedMessage = "No active cart found for the user.";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);

    }
}
