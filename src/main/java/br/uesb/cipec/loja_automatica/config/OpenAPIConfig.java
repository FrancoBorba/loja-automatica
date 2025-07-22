package br.uesb.cipec.loja_automatica.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;



@Configuration
public class OpenAPIConfig {

  @Bean 
  OpenAPI customOpenApi(){
    return new OpenAPI()
    .info(new Info()
    .title("Automatic store for uesb")
    .version("V1")
    .description("This is an API developed in Peti Catia by Franco and Ana Clara, for an automatic product store produced at CIPEC for the UESB ecological fair.")
    .license(new License().name("Apache 2.0").url("https://www.apache.org/licenses/LICENSE-2.0")));
  } 
 
}
