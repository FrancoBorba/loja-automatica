package br.uesb.cipec.loja_automatica.unit.services;


import br.uesb.cipec.loja_automatica.exception.InsufficientStockException;
import br.uesb.cipec.loja_automatica.exception.InvalidPurchaseQuantityException;
import br.uesb.cipec.loja_automatica.exception.RequiredObjectIsNullException;
import br.uesb.cipec.loja_automatica.exception.ResourceNotFoundException;
import br.uesb.cipec.loja_automatica.model.ItemPurchase;
import br.uesb.cipec.loja_automatica.model.Product;
import br.uesb.cipec.loja_automatica.repository.ProductRepository;
import br.uesb.cipec.loja_automatica.service.StockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StockServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private StockService stockService;

      // Data
    private Product product1;
    private Product product2;
    private ItemPurchase itemPurchase1;
    private ItemPurchase itemPurchase2;

    @BeforeEach
    void setUp() {
     
        product1 = new Product();
        product1.setId(1L);
        product1.setName("Produto Teste A");
        product1.setAmount(10); 

     
        itemPurchase1 = new ItemPurchase();
        itemPurchase1.setProduct(product1);


      
        product2 = new Product();
        product2.setId(2L);
        product2.setName("Produto Teste B");
        product2.setAmount(20); 

      
        itemPurchase2 = new ItemPurchase();
        itemPurchase2.setProduct(product2);
    }

    // --- HAPPY PATH TEST ---

    @Test
    @DisplayName("Given sufficient stock, when debitStock is called, then should decrement stock and return true")
    void givenSufficientStock_whenDebitStock_thenShouldDecrementStock() {

        // Given ; Arrange
        List<ItemPurchase> listOfItens = new ArrayList<>();

        listOfItens.add(itemPurchase1);
        listOfItens.add(itemPurchase2);
        
        itemPurchase1.setQuantity(5);
        itemPurchase2.setQuantity(5);

        when(productRepository.findByIdWithLock(1L)).thenReturn(Optional.of(product1));
        when(productRepository.findByIdWithLock(2L)).thenReturn(Optional.of(product2));

        when(productRepository.save(any(Product.class))).thenReturn(product1);

        // When ; Act

        Boolean result = stockService.debitStock(listOfItens);

        // Then ; Arrange


        assertTrue(result);
        assertEquals(5, product1.getAmount());
        assertEquals(15, product2.getAmount());

        verify(productRepository, times(2)).save(any(Product.class));
            
    }

    // --- UNHAPPY PATH / EXCEPTION TESTS ---

    @Test
    @DisplayName("Given a null list of items, when debitStock is called, then should throw RequiredObjectIsNullException")
    void givenNullItemList_whenDebitStock_thenThrowsException() {
      
         Exception exception = assertThrows(RequiredObjectIsNullException.class, 
      () ->{
        stockService.debitStock(null);
      });

        String expectedMessage = "The list of items to purchase cannot be null.";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);


    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when product in item list does not exist")
    void givenNonExistentProduct_whenDebitStock_thenThrowsException() {
        // ARRANGE
        // "Ensina" o repositório a NÃO encontrar o produto.
        when(productRepository.findByIdWithLock(anyLong())).thenReturn(Optional.empty());
        
        itemPurchase1.setQuantity(5);
        List<ItemPurchase> items = List.of(itemPurchase1);

        // ACT & ASSERT
        assertThrows(ResourceNotFoundException.class, () -> {
            stockService.debitStock(items);
        });
    }

    @Test
    @DisplayName("Should throw InvalidPurchaseQuantityException when item quantity is zero")
    void givenItemWithZeroQuantity_whenDebitStock_thenThrowsException() {
        // ARRANGE
     
        itemPurchase1.setQuantity(0);
        List<ItemPurchase> items = List.of(itemPurchase1);

 
        when(productRepository.findByIdWithLock(anyLong())).thenReturn(Optional.of(product1));

        // ACT & ASSERT
        assertThrows(InvalidPurchaseQuantityException.class, () -> {
            stockService.debitStock(items);
        });
    }

    @Test
    @DisplayName("Should throw InsufficientStockException when stock is not enough")
    void givenInsufficientStock_whenDebitStock_thenThrowsException() {
        // ARRANGE
  
        itemPurchase1.setQuantity(11);
        List<ItemPurchase> items = List.of(itemPurchase1);
        
        when(productRepository.findByIdWithLock(anyLong())).thenReturn(Optional.of(product1));

        // ACT & ASSERT
        Exception exception = assertThrows(InsufficientStockException.class, () -> {
            stockService.debitStock(items);
        });
        
        String expectedMessage = "Insufficient stock for the product: " + product1.getName();
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }
}