package br.uesb.cipec.loja_automatica.controller.docs;

import br.uesb.cipec.loja_automatica.DTO.PurchaseRequestDTO;
import br.uesb.cipec.loja_automatica.DTO.PurchaseResponseDTO;


public interface PurchaseControllerDocs {
  public PurchaseResponseDTO findByID(Long id);

  public PurchaseResponseDTO create(PurchaseRequestDTO purchaseRequestDTO);

}
