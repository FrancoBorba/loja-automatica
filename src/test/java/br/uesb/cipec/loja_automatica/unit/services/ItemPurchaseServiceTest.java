package br.uesb.cipec.loja_automatica.unit.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.uesb.cipec.loja_automatica.DTO.ItemPurchaseRequestDTO;
import br.uesb.cipec.loja_automatica.DTO.PurchaseResponseDTO;
import br.uesb.cipec.loja_automatica.component.AuthenticationFacade;
import br.uesb.cipec.loja_automatica.enums.StatusPurchase;
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
        assertEquals(new BigDecimal("300"), addedItem.getSubvalor());
        assertEquals(new BigDecimal("300"), purchase.getValue());


     
        verify(repository, times(1))
        .save(any(Purchase.class));

         
        

    }
    @Test
    void shouldUpdateQuantityWhenProductAlreadyInCart() {

        // Given ; Arrange


        List<ItemPurchase> itensInPurchase = new ArrayList<>();
       

        itensInPurchase.add(entityItem);
        purchase.setItens(itensInPurchase);

        when(facade.getCurrentUser()).thenReturn(currentUser);

        when(repository.findByUserIdAndStatus(currentUser.getId(), StatusPurchase.AGUARDANDO_PAGAMENTO))
        .thenReturn(Optional.of(purchase));


        when(repository.save(any(Purchase.class))).thenReturn(purchase);

        when(purchaseMapper.toResponseDTO(any(Purchase.class))).thenReturn(purchaseResponseDTO);

        // When ; Act
        purchaseResponseDTO = service.updateItemQuantity(1L, 4);

        // Then .; Assert
          ItemPurchase addedItem = purchase.getItens().get(0);
        assertNotNull(purchaseResponseDTO);
        assertEquals(4, addedItem.getQuantity());
         assertEquals(new BigDecimal("400"), addedItem.getSubvalor());
        assertEquals(new BigDecimal("400"), purchase.getValue());

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
    void shouldRemoveItemFromCartSuccessfully() {}

    @Test
    void shouldRecalculatePurchaseTotalWhenItemRemoved() {}

    @Test
    void shouldUpdateQuantityWhenNewQuantityIsPositive() {}

    @Test
    void shouldRemoveItemWhenNewQuantityIsZeroOrNegative() {}

    @Test
    void shouldRecalculatePurchaseTotalWhenUpdatingQuantity() {}

    @Test
    void shouldReturnExistingActiveCartForUser() {}

    @Test
    void shouldCreateNewCartWhenNoActiveCartExists() {}

    @Test
    void shouldReturnActiveCartForUser() {}

    // ---------------- UNHAPPY PATH ----------------

    @Test
    void shouldThrowExceptionWhenItemRequestIsNull() {}

    @Test
    void shouldThrowExceptionWhenQuantityIsZeroOrNegative() {}

    @Test
    void shouldThrowExceptionWhenItemNotFoundInCart() {}

    @Test
    void shouldThrowExceptionWhenUpdatingNonexistentItem() {}

    @Test
    void shouldThrowExceptionWhenNoActiveCartExists() {}
}
