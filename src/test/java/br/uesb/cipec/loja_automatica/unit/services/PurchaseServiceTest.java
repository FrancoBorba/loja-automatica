package br.uesb.cipec.loja_automatica.unit.services;

import br.uesb.cipec.loja_automatica.DTO.PurchaseResponseDTO;
import br.uesb.cipec.loja_automatica.component.AuthenticationFacade;
import br.uesb.cipec.loja_automatica.enums.StatusPurchase;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
        // --- ARRANGE (Preparar o cenário) ---

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

   @Test
    @DisplayName("Should return a filtered list of purchases for the current user when a status is provided")
    void givenStatus_whenFindPurchasesByCurrentUser_thenReturnsFilteredPage() {

    }

    @Test
    @DisplayName("Should return an unfiltered list of all purchases for the current user when status is null")
    void givenNullStatus_whenFindPurchasesByCurrentUser_thenReturnsUnfilteredPage() {
       
    }

    @Test
    @DisplayName("Deve deletar uma compra com sucesso (delete)")
    void shouldDeletePurchaseSuccessfully() {

      // Given ; Arrange
      when(purchaseRepository.findById(anyLong())).thenReturn(Optional.of(activeCart));

      // When ; Act 

      purchaseService.delete(1L);

      verify(purchaseRepository , times(1)).delete(activeCart);
    }

    @Test
    @DisplayName("Deve retornar o carrinho ativo do usuário com sucesso (findActiveCartByUser)")
    void shouldFindActiveCartByUserSuccessfully() {

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
    @DisplayName("Deve lançar exceção ao buscar compra inexistente (findById)")
    void shouldThrowWhenPurchaseNotFoundById() {
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar deletar compra inexistente (delete)")
    void shouldThrowWhenDeletingNonexistentPurchase() {
    }

    @Test
    @DisplayName("Deve lançar exceção ao não encontrar carrinho ativo (checkout)")
    void shouldThrowWhenActiveCartNotFoundOnCheckout() {
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar fazer checkout com carrinho vazio (checkout)")
    void shouldThrowWhenCheckoutWithEmptyCart() {
    }

    @Test
    @DisplayName("Deve lançar exceção ao confirmar pagamento em estado inválido (confirmPayment)")
    void shouldThrowWhenConfirmPaymentWithInvalidStatus() {
    }
}
