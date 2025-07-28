package br.uesb.cipec.loja_automatica.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import br.uesb.cipec.loja_automatica.DTO.PurchaseRequestDTO;
import br.uesb.cipec.loja_automatica.DTO.PurchaseResponseDTO;
import br.uesb.cipec.loja_automatica.controller.docs.PurchaseControllerDocs;
import br.uesb.cipec.loja_automatica.service.PurchaseService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/purchase")
@Tag(name = "Purchase" , description = "Endpoints for managing purchase")
public class ControllerPurchase implements PurchaseControllerDocs  {

  @Autowired
  PurchaseService service;

    @Override
    @GetMapping(  
        produces = { 
      MediaType.APPLICATION_JSON_VALUE,
      MediaType.APPLICATION_XML_VALUE ,
      MediaType.APPLICATION_YAML_VALUE}
      )
    public List<PurchaseResponseDTO> findAll(){
      return service.findAll();
    }

  @Override
  @GetMapping(
    value = "/{id}",
    produces = {
      MediaType.APPLICATION_JSON_VALUE,
      MediaType.APPLICATION_XML_VALUE,
      MediaType.APPLICATION_YAML_VALUE
    }
  )
  public PurchaseResponseDTO findByID(@PathVariable("id") Long id) {
    return service.findById(id);
  }

  @Override
  @PostMapping(
    consumes = {
      MediaType.APPLICATION_JSON_VALUE,
      MediaType.APPLICATION_XML_VALUE,
      MediaType.APPLICATION_YAML_VALUE
    },
    produces = {
      MediaType.APPLICATION_JSON_VALUE,
      MediaType.APPLICATION_XML_VALUE,
      MediaType.APPLICATION_YAML_VALUE
    }
  )
  public PurchaseResponseDTO create(@RequestBody @Valid PurchaseRequestDTO purchaseRequestDTO) {
      return service.createPurchase(purchaseRequestDTO);
  }
  
  @Override
  @PutMapping(
    value = "/{id}",
    consumes = {
      MediaType.APPLICATION_JSON_VALUE,
      MediaType.APPLICATION_XML_VALUE,
      MediaType.APPLICATION_YAML_VALUE
    },
    produces = {
      MediaType.APPLICATION_JSON_VALUE,
      MediaType.APPLICATION_XML_VALUE,
      MediaType.APPLICATION_YAML_VALUE
    }
  )
  public PurchaseResponseDTO update(@PathVariable("id") Long id , @RequestBody @Valid PurchaseRequestDTO purchaseRequestDTO){
    return service.updatePurchase(id, purchaseRequestDTO);
  }


  @Override
  @DeleteMapping(
    value = "/{id}"
  )
  public void delete(@PathVariable ("id") Long id){
    service.delete(id);

  }
}
