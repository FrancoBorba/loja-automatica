package br.uesb.cipec.loja_automatica.unit.services;

import br.uesb.cipec.loja_automatica.DTO.PurchaseResponseDTO;
import br.uesb.cipec.loja_automatica.component.AuthenticationFacade;
import br.uesb.cipec.loja_automatica.enums.StatusPurchase;
import br.uesb.cipec.loja_automatica.exception.EmptyCartException;
import br.uesb.cipec.loja_automatica.exception.InvalidPurchaseStatusException;
import br.uesb.cipec.loja_automatica.exception.ResourceNotFoundException;
import br.uesb.cipec.loja_automatica.mapper.PurchaseMapper;
import br.uesb.cipec.loja_automatica.model.ItemPurchase;
import br.uesb.cipec.loja_automatica.model.Product;
import br.uesb.cipec.loja_automatica.model.Purchase;
import br.uesb.cipec.loja_automatica.model.User;
import br.uesb.cipec.loja_automatica.payment.StripeService;
import br.uesb.cipec.loja_automatica.repository.PurchaseRepository;
import br.uesb.cipec.loja_automatica.service.PurchaseService;
import br.uesb.cipec.loja_automatica.service.StockService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.stripe.exception.StripeException;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@ExtendWith(MockitoExtension.class)
class PurchaseServiceTest {

    @Mock
    private PurchaseRepository purchaseRepository;
    @Mock
    private PurchaseMapper purchaseMapper;
    @Mock
    private AuthenticationFacade authenticationFacade;
    @Mock
    private StockService stockService;
    @Mock
    private StripeService stripeService;

    @InjectMocks
    private PurchaseService purchaseService;

  // Datas
    private User currentUser;
    private Product product1;
    private Product product2;
    private Purchase activeCart;
    private ItemPurchase item1;
    private ItemPurchase item2;
    private PurchaseResponseDTO purchaseResponseDTO;

    @BeforeEach
    void setUp() {
      
        currentUser = new User();
        currentUser.setId(1L);

        product1 = new Product();
        product1.setId(10L);
        product1.setName("Produto Teste 1");
        product1.setPrice(new BigDecimal("100.00"));

        product2 = new Product();
        product2.setId(11L);
        product2.setName("Produto Teste 2");
        product2.setPrice(new BigDecimal("50.00"));

        item1 = new ItemPurchase();
        item1.setId(100L);
        item1.setProduct(product1);
        item1.setQuantity(1);
        item1.setSubvalor(new BigDecimal("100.00")); 

        item2 = new ItemPurchase();
        item2.setId(101L);
        item2.setProduct(product2);
        item2.setQuantity(3);
        item2.setSubvalor(new BigDecimal("150.00")); 

        activeCart = new Purchase();
        activeCart.setId(1L);
        activeCart.setUser(currentUser);
        activeCart.setStatus(StatusPurchase.AGUARDANDO_PAGAMENTO);
        activeCart.setItens(new ArrayList<>(List.of(item1, item2)));
        activeCart.setValue(new BigDecimal("250.00")); 
        
        purchaseResponseDTO = new PurchaseResponseDTO();
        purchaseResponseDTO.setId(activeCart.getId());
        purchaseResponseDTO.setStatus(activeCart.getStatus());
        purchaseResponseDTO.setValue(activeCart.getValue());
    }

    // --- HAPPY PATH TESTS ---

      @Test
    @DisplayName("Should return a paginated list of all purchases for an admin")
    void shouldFindAllPurchasesSuccessfully() {
        // --- ARRANGE ---

        List<Purchase> purchaseList = List.of(activeCart);

        Pageable pageable = PageRequest.of(0, 10);


        Page<Purchase> purchasePage = new PageImpl<>(purchaseList, pageable, 1L);

     
        when(purchaseRepository.findAll(any(Pageable.class))).thenReturn(purchasePage);

        PurchaseResponseDTO responseDTO = new PurchaseResponseDTO();
        responseDTO.setId(activeCart.getId());
        when(purchaseMapper.toResponseDTO(activeCart)).thenReturn(responseDTO);

        //  ACT 

        Page<PurchaseResponseDTO> resultPage = purchaseService.findAll(pageable);

        //  ASSERT 

        assertNotNull(resultPage);
        assertFalse(resultPage.getContent().isEmpty());
        assertEquals(1, resultPage.getContent().size());
        assertEquals(1L, resultPage.getContent().get(0).getId()); 

        assertEquals(1, resultPage.getTotalPages());
        assertEquals(1L, resultPage.getTotalElements());
        assertEquals(0, resultPage.getNumber());

        verify(purchaseRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("Must successfully search for purchase by ID (findById)")
    void shouldFindPurchaseByIdSuccessfully() {

      // Given ; Arrange
      when(purchaseRepository.findById(anyLong())).thenReturn(Optional.of(activeCart));

      when(purchaseMapper.toResponseDTO(any(Purchase.class))).thenReturn(purchaseResponseDTO);

      // When ; Act

        PurchaseResponseDTO result = purchaseService.findById(1L);

        // --- ASSERT ---

        assertNotNull(result);
        assertEquals(purchaseResponseDTO.getId(), result.getId());
        assertEquals(StatusPurchase.AGUARDANDO_PAGAMENTO , result.getStatus());
        assertEquals(new BigDecimal("250.00"), result.getValue());
        
        verify(purchaseRepository, times(1)).findById(1L);
        verify(purchaseMapper, times(1)).toResponseDTO(activeCart);
    }
    @ParameterizedTest
    @DisplayName("Should return a filtered list of purchases for the current user when a status is provided")
    @EnumSource(StatusPurchase.class)
    void givenStatus_whenFindPurchasesByCurrentUser_thenReturnsFilteredPage(StatusPurchase status) {

      // Given ; Arrange
      List<Purchase> purchaseList = List.of(activeCart);

      Pageable pageable = PageRequest.of(0, 10);

      Page<Purchase> purchasePage = new PageImpl<>(purchaseList, pageable, 1L);

      when(authenticationFacade.getCurrentUser()).thenReturn(currentUser);

     when(purchaseRepository.findByUserIdAndStatus(anyLong(), any(StatusPurchase.class), any(Pageable.class)))
            .thenReturn(purchasePage);

        PurchaseResponseDTO responseDTO = new PurchaseResponseDTO();
        responseDTO.setId(activeCart.getId());
        when(purchaseMapper.toResponseDTO(activeCart)).thenReturn(responseDTO);

      // When ; Act
      Page<PurchaseResponseDTO> resultPage = purchaseService
      .findPurchasesByCurrentUser(status, pageable);

      // Then ; Assert


        assertNotNull(resultPage);
        assertFalse(resultPage.getContent().isEmpty());
        assertEquals(1, resultPage.getContent().size());
        assertEquals(1L, resultPage.getContent().get(0).getId()); 

        assertEquals(1, resultPage.getTotalPages());
        assertEquals(1L, resultPage.getTotalElements());
        assertEquals(0, resultPage.getNumber());

      verify(purchaseRepository, times(1)).findByUserIdAndStatus(
          eq(currentUser.getId()), 
          eq(status), 
          any(Pageable.class)
      );
        
        verify(purchaseRepository, never()).findByUserId(anyLong(), any(Pageable.class));
    }

    @Test
    @DisplayName("Should return an unfiltered list of all purchases for the current user when status is null")
    void givenNullStatus_whenFindPurchasesByCurrentUser_thenReturnsUnfilteredPage() {
        // Given ; Arrange
      List<Purchase> purchaseList = List.of(activeCart);

      Pageable pageable = PageRequest.of(0, 10);

      Page<Purchase> purchasePage = new PageImpl<>(purchaseList, pageable, 1L);

      when(authenticationFacade.getCurrentUser()).thenReturn(currentUser);

     when(purchaseRepository.findByUserId(1L, pageable))
            .thenReturn(purchasePage);


        PurchaseResponseDTO responseDTO = new PurchaseResponseDTO();
        responseDTO.setId(activeCart.getId());
        when(purchaseMapper.toResponseDTO(activeCart)).thenReturn(responseDTO);

      // When ; Act
      
      Page<PurchaseResponseDTO> resultPage = purchaseService.findPurchasesByCurrentUser(null, pageable);

      // Then ; Assert


        assertNotNull(resultPage);
        assertFalse(resultPage.getContent().isEmpty());
        assertEquals(1, resultPage.getContent().size());
        assertEquals(1L, resultPage.getContent().get(0).getId()); 

        assertEquals(1, resultPage.getTotalPages());
        assertEquals(1L, resultPage.getTotalElements());
        assertEquals(0, resultPage.getNumber());
        
      verify(purchaseRepository, times(1)).findByUserId(anyLong() , any(Pageable.class));
  
        

    }

    @Test
    @DisplayName("Must delete a cart successfully (delete)")
    void shouldDeletePurchaseSuccessfully() {

      // Given ; Arrange
      when(purchaseRepository.findById(anyLong())).thenReturn(Optional.of(activeCart));

      // When ; Act 

      purchaseService.delete(1L);

      verify(purchaseRepository , times(1)).delete(activeCart);
    }

    @Test
    @DisplayName("Must return user's active cart successfully (findActiveCartByUser)")
    void shouldFindActiveCartByUserSuccessfully() {
      // Given ; Arrange

      when(authenticationFacade.getCurrentUser()).thenReturn(currentUser); 

      when(purchaseRepository.findByUserIdAndStatus(anyLong(), any(StatusPurchase.class)))
      .thenReturn(Optional.of(activeCart));

      when(purchaseMapper.toResponseDTO(activeCart)).thenReturn(purchaseResponseDTO);

      // When ; Act

      Optional<PurchaseResponseDTO> result = purchaseService.findActiveCartByUser();

      // Then ; Assert

      assertNotNull(result);
      assertEquals(1L, result.get().getId());
      assertEquals(StatusPurchase.AGUARDANDO_PAGAMENTO, result.get().getStatus());
      assertEquals(new BigDecimal("250.00"), result.get().getValue());

    }

    @Test
    @DisplayName("Must checkout successfully (checkout)")
    void shouldCheckoutSuccessfully() throws StripeException {

      when(authenticationFacade.getCurrentUser()).thenReturn(currentUser);

      when(purchaseRepository.findByUserIdAndStatus(anyLong(), any()))
      .thenReturn(Optional.of(activeCart));

      when(stripeService.creatCheckoutSesion(activeCart)).thenReturn("www.stripepage.com.br");

      String result =  purchaseService.checkout();

      assertEquals("www.stripepage.com.br" , result);

    }

    @Test
    @DisplayName("Must confirm successful payment (confirmPayment)")
    void shouldConfirmPaymentSuccessfully() {

      activeCart.setStatus(StatusPurchase.AGUARDANDO_PAGAMENTO);

      when(purchaseRepository.findById(anyLong())).thenReturn(Optional.of(activeCart));

      purchaseService.confirmPayment(1L);

      assertEquals(StatusPurchase.PAGO, activeCart.getStatus());

      verify(stockService, times(1)).debitStock(activeCart.getItens());

      verify(purchaseRepository , times(1)).save(activeCart);

    }

    // --- UNHAPPY PATH TESTS ---
 @Test
    @DisplayName("Should throw ResourceNotFoundException when finding a non-existent purchase by ID")
    void shouldThrowWhenPurchaseNotFoundById() {
        // ARRANGE
        // "Ensina" o repositório a não encontrar nada com este ID.
        when(purchaseRepository.findById(anyLong())).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(ResourceNotFoundException.class, () -> {
            purchaseService.findById(99L);
        });
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when trying to delete a non-existent purchase")
    void shouldThrowWhenDeletingNonexistentPurchase() {
        // ARRANGE
        when(purchaseRepository.findById(anyLong())).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(ResourceNotFoundException.class, () -> {
            purchaseService.delete(99L);
        });

        // Garante que o método 'delete' do repositório nunca foi chamado.
        verify(purchaseRepository, never()).delete(any(Purchase.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException on checkout when no active cart is found")
    void shouldThrowWhenActiveCartNotFoundOnCheckout() {
        // ARRANGE
     
        when(authenticationFacade.getCurrentUser()).thenReturn(currentUser);
        when(purchaseRepository.findByUserIdAndStatus(anyLong(), any()))
            .thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(ResourceNotFoundException.class, () -> {
            purchaseService.checkout();
        });
    }

    @Test
    @DisplayName("Should throw EmptyCartException when checking out with an empty cart")
    void shouldThrowWhenCheckoutWithEmptyCart() {
        // ARRANGE
       
        activeCart.getItens().clear(); 
        when(authenticationFacade.getCurrentUser()).thenReturn(currentUser);
        when(purchaseRepository.findByUserIdAndStatus(anyLong(), any()))
            .thenReturn(Optional.of(activeCart));

        // ACT & ASSERT
        assertThrows(EmptyCartException.class, () -> {
            purchaseService.checkout();
        });
    }

    @Test
    @DisplayName("Should throw InvalidPurchaseStatusException when confirming a payment that is not awaiting")
    void shouldThrowWhenConfirmPaymentWithInvalidStatus() {
        // ARRANGE
        activeCart.setStatus(StatusPurchase.PAGO);
        when(purchaseRepository.findById(anyLong())).thenReturn(Optional.of(activeCart));

        // ACT & ASSERT
        assertThrows(InvalidPurchaseStatusException.class, () -> {
            purchaseService.confirmPayment(activeCart.getId());
        });

        verify(stockService, never()).debitStock(any());
        verify(purchaseRepository, never()).save(any(Purchase.class));
    }

}
