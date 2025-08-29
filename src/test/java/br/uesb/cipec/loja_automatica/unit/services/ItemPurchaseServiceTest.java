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
import br.uesb.cipec.loja_automatica.mapper.PurchaseMapper;
import br.uesb.cipec.loja_automatica.model.ItemPurchase;
import br.uesb.cipec.loja_automatica.model.Product;
import br.uesb.cipec.loja_automatica.model.Purchase;
import br.uesb.cipec.loja_automatica.model.User;
import br.uesb.cipec.loja_automatica.repository.ProductRepository;
import br.uesb.cipec.loja_automatica.repository.PurchaseRepository;
import br.uesb.cipec.loja_automatica.service.ItemPurchaseService;

import java.math.BigDecimal;
import java.util.ArrayList;

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
        product.setPrice(BigDecimal.valueOf(100));

        
        entityItem = new ItemPurchase();
        entityItem.setId(100L);
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
        itemRequest.setQuantity(1);

        // response DTO fake
        purchaseResponseDTO = new PurchaseResponseDTO();
    }

    // ---------------- HAPPY PATH  ----------------

    @Test
    void shouldAddNewItemToCartWhenProductNotInCart() {}

    @Test
    void shouldUpdateQuantityWhenProductAlreadyInCart() {}

    @Test
    void shouldRecalculatePurchaseTotalWhenAddingItem() {}

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
