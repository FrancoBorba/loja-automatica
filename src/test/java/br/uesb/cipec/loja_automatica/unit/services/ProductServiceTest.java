package br.uesb.cipec.loja_automatica.unit.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.uesb.cipec.loja_automatica.model.Product;

public class ProductServiceTest {

    Product product;
    

    // Run before each test
    @BeforeEach
    void setUp(){
        // Mock setup (when, thenReturn) goes here
    }
    
    // ------------------- Happy Path Tests ----------------

    @Test
    @DisplayName("Should return a ProductDTO when ID exists")
    void testFindById_WhenProductExists_ShouldReturnProductDTO(){
    }

    @Test
    @DisplayName("Should return a paginated list of products")
    void testFindAll_ShouldReturnPagedListOfProducts(){
    }

    @Test
    @DisplayName("Should return an empty paginated list when no products exist")
    void testFindAll_WhenNoProductsExist_ShouldReturnEmptyPage() {
        // NOVO: Testa o cenário de borda com a lista vazia.
    }

    @Test
    @DisplayName("Should create a new product successfully with valid data")
    void testCreate_WithValidData_ShouldReturnNewProductDTO(){
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