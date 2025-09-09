package br.uesb.cipec.loja_automatica.service.search;

import br.uesb.cipec.loja_automatica.DTO.ProductDTO;
import br.uesb.cipec.loja_automatica.document.ProductDocument;
import br.uesb.cipec.loja_automatica.mapper.ProductDocumentMapper;
import br.uesb.cipec.loja_automatica.repository.ProductElasticSearchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProductSearchService {

    @Autowired
    private ProductElasticSearchRepository elasticsearchRepository;

    @Autowired
    private ProductDocumentMapper documentMapper; // O mapper que já criamos

    public Page<ProductDTO> searchByName(String name, Pageable pageable) {
        Page<ProductDocument> productPage;

        if (name == null || name.isBlank()) {
            
            productPage = elasticsearchRepository.findAll(pageable);
        } else {
            
            productPage = elasticsearchRepository.findByNameContainingIgnoreCase(name, pageable);
        }
        
     
        return productPage.map(documentMapper::toDTO); 
    }
}