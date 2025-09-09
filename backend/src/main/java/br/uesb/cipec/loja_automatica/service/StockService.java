package br.uesb.cipec.loja_automatica.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.uesb.cipec.loja_automatica.exception.InsufficientStockException;
import br.uesb.cipec.loja_automatica.exception.InvalidPurchaseQuantityException;
import br.uesb.cipec.loja_automatica.exception.RequiredObjectIsNullException;
import br.uesb.cipec.loja_automatica.exception.ResourceNotFoundException;
import br.uesb.cipec.loja_automatica.model.ItemPurchase;
import br.uesb.cipec.loja_automatica.model.Product;
import br.uesb.cipec.loja_automatica.repository.ProductRepository;

@Service
public class StockService {

  @Autowired
  ProductRepository productRepository;

  @Transactional(rollbackFor = Exception.class)
  public Boolean debitStock(List<ItemPurchase> items){
    if (items == null) {
      throw new RequiredObjectIsNullException("The list of items to purchase cannot be null.");
    }
    
    for(ItemPurchase item : items){
      Product product = productRepository.findByIdWithLock(item.getProduct().getId())
      .orElseThrow(() -> new  ResourceNotFoundException("Product not found: " + item.getProduct().getId()));

      if (item.getQuantity() <= 0) {
        throw new InvalidPurchaseQuantityException(
            "Purchase quantity for product with id " + item.getProduct().getId() + " must be greater than zero."
        );
      }


      int quantityInStock = product.getAmount();
      if(quantityInStock >= item.getQuantity()){
        product.setAmount(quantityInStock - item.getQuantity());
        productRepository.save(product);
      } else{
         throw new InsufficientStockException("Insufficient stock for the product: " + product.getName());
      }

    }
    return true;
  }
}
