package br.uesb.cipec.loja_automatica.controller.docs;

import java.util.List;

import br.uesb.cipec.loja_automatica.DTO.PurchaseRequestDTO;
import br.uesb.cipec.loja_automatica.DTO.PurchaseResponseDTO;


public interface PurchaseControllerDocs {
  public PurchaseResponseDTO findByID(Long id);

  public PurchaseResponseDTO create(PurchaseRequestDTO purchaseRequestDTO);

  public PurchaseResponseDTO update(Long id ,PurchaseRequestDTO purchaseRequestDTO);

  public void delete(Long id);

  public List<PurchaseResponseDTO> findAll();

}
