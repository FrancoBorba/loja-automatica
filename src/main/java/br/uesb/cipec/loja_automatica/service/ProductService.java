package br.uesb.cipec.loja_automatica.service;

import br.uesb.cipec.loja_automatica.DTO.ProductDTO;
import br.uesb.cipec.loja_automatica.exception.InvalidProductDataException;
import br.uesb.cipec.loja_automatica.exception.ProductInUseException;
import br.uesb.cipec.loja_automatica.exception.RequiredObjectIsNullException;
import br.uesb.cipec.loja_automatica.exception.ResourceNotFoundException;
import br.uesb.cipec.loja_automatica.mapper.ProductDocumentMapper;
import br.uesb.cipec.loja_automatica.mapper.ProductMapper;
import br.uesb.cipec.loja_automatica.model.Product;
import br.uesb.cipec.loja_automatica.repository.ItemPurchaseRepository;
import br.uesb.cipec.loja_automatica.repository.ProductRepository;
import br.uesb.cipec.loja_automatica.service.index.ProductIndexingService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

@Service
public class ProductService {
    
    @Autowired 
    private ProductRepository repository;

    @Autowired
    private ProductMapper mapper;

    @Autowired
    private ItemPurchaseRepository itemPurchaseRepository; 

    @Autowired
    private ProductIndexingService indexingService; 
    
    @Autowired
    private ProductDocumentMapper documentMapper; 

    private final Logger logger = LoggerFactory.getLogger(ProductService.class.getName());

    @Transactional(readOnly = true)
    public ProductDTO findById(Long id) {
        logger.info("Finding one product with ID: {}", id); 
        Product entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return mapper.toDTO(entity);
    }

    @Transactional(readOnly = true)
    public Page<ProductDTO> findAll(Pageable pageable) {
        logger.info("Finding all products (paginated)");
        Page<Product> productPage = repository.findAll(pageable);
        return productPage.map(mapper::toDTO);
    }

    @Transactional
    public ProductDTO create(ProductDTO product) {
        if (product == null) {
            throw new RequiredObjectIsNullException("Product data cannot be null.");
        }
        if (product.getName() == null || product.getName().isBlank()) {
            throw new InvalidProductDataException("Product name cannot be null or empty.");
        }
        if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidProductDataException("Product price cannot be negative.");
        }
        if (product.getAmount() == null || product.getAmount() < 0) {
            throw new InvalidProductDataException("Product stock amount cannot be negative.");
        }
        
        logger.info("Creating one product");
        Product entity = mapper.toEntity(product);
        indexingService.save(documentMapper.toDocument(entity));
        Product savedEntity = repository.save(entity);
        return mapper.toDTO(savedEntity);
    }

    @Transactional
    public ProductDTO update(ProductDTO product) {
        if (product == null) {
            throw new RequiredObjectIsNullException("Product data cannot be null.");
        }
        
        logger.info("Updating product with ID: {}", product.getId());
        Product entity = repository.findById(product.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + product.getId()));

        // Aqui você pode adicionar as mesmas validações do `create` se necessário
        entity.setName(product.getName());
        entity.setAmount(product.getAmount());
        entity.setDescription(product.getDescription());
        entity.setPrice(product.getPrice());

        Product updatedEntity = repository.save(entity);
        indexingService.save(documentMapper.toDocument(updatedEntity));
        return mapper.toDTO(updatedEntity);
    }

    @Transactional
    public void delete(Long id) {
        logger.info("Deleting product with ID: {}", id);
        
        Product entity = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        
        if (itemPurchaseRepository.existsByProductId(id)) {
            throw new ProductInUseException("Cannot delete a product that is part of one or more existing purchases.");
        }
        
        repository.delete(entity);
    }
}