package br.uesb.cipec.loja_automatica.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.uesb.cipec.loja_automatica.DTO.PurchaseResponseDTO;
import br.uesb.cipec.loja_automatica.controller.docs.PurchaseControllerDocs;
import br.uesb.cipec.loja_automatica.service.PurchaseService;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/purchase")
@Tag(name = "Purchase" , description = "Endpoints for managing purchase")
public class ControllerPurchase implements PurchaseControllerDocs  {

  @Autowired
  PurchaseService service;

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
  
}
