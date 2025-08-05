package br.uesb.cipec.loja_automatica.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.uesb.cipec.loja_automatica.exception.InsufficientStockException;
import br.uesb.cipec.loja_automatica.model.ItemPurchase;
import br.uesb.cipec.loja_automatica.model.Product;
import br.uesb.cipec.loja_automatica.repository.ProductRepository;

@Service
public class StockService {

  @Autowired
  ProductRepository productRepository;

   @Transactional(rollbackFor = Exception.class)
  public Boolean debitStock(List<ItemPurchase> items){
    
    for(ItemPurchase item : items){
      Product product = productRepository.findByIdWithLock(item.getProduct().getId())
      .orElseThrow(() -> new RuntimeException("Product not found: " + item.getProduct().getId()));


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
