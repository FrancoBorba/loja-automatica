package br.uesb.cipec.loja_automatica.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import br.uesb.cipec.loja_automatica.document.ProductDocument;

public interface ProductElasticSearchRepository extends ElasticsearchRepository<ProductDocument, Long> {

   Page<ProductDocument> findByNameContainingIgnoreCase(String name, Pageable pageable);

}

