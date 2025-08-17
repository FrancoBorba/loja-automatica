package br.uesb.cipec.loja_automatica.service.index;

import br.uesb.cipec.loja_automatica.document.ProductDocument;
import br.uesb.cipec.loja_automatica.mapper.ProductDocumentMapper;
import br.uesb.cipec.loja_automatica.repository.ProductElasticSearchRepository;
import br.uesb.cipec.loja_automatica.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductIndexingService {

    private final Logger logger = LoggerFactory.getLogger(ProductIndexingService.class);
    private final ProductRepository productRepository; // Repositório do PostgreSQL (JPA)
    private final ProductElasticSearchRepository elasticsearchRepository; // Repositório do Elasticsearch
    private final ProductDocumentMapper mapper;

    @Autowired
    public ProductIndexingService(
        ProductRepository productRepository, 
        ProductElasticSearchRepository elasticsearchRepository, 
        ProductDocumentMapper mapper
    ) {
        this.productRepository = productRepository;
        this.elasticsearchRepository = elasticsearchRepository;
        this.mapper = mapper;
    }

    // Este método lê todos os produtos do PostgreSQL e os salva no Elasticsearch
    public void indexAllProductsFromDatabase() {
        logger.info("Starting to index all products from database...");
        
        List<ProductDocument> productDocuments = productRepository.findAll().stream()
                .map(mapper::toDocument) // Converte cada Product para ProductDocument
                .toList();
        
        elasticsearchRepository.saveAll(productDocuments);
        
        logger.info("Successfully indexed {} products.", productDocuments.size());
    }

    public void save(final ProductDocument product){
        elasticsearchRepository.save(product);
    }

    public ProductDocument findById(Long id){
        return elasticsearchRepository.findById(id).orElse(null); // Retornar null ou Optional é mais seguro que .orElseThrow() aqui
    }
}