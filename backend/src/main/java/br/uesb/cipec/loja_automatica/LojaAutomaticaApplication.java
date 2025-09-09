package br.uesb.cipec.loja_automatica;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import br.uesb.cipec.loja_automatica.service.index.ProductIndexingService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@SpringBootApplication
// --- ANOTAÇÕES PARA CONFIGURAR A SEGURANÇA NO SWAGGER ---
@OpenAPIDefinition(
    info = @Info(title = "Loja Automática API", version = "v1", description = "API para o projeto da Loja Automática da UESB"),
    security = {@SecurityRequirement(name = "bearerAuth")}
)
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT"
)
public class LojaAutomaticaApplication {
	public static void main(String[] args) {
		SpringApplication.run(LojaAutomaticaApplication.class, args);
	}

    
     @Bean
    public CommandLineRunner initialIndexer(ProductIndexingService indexingService) {
        return args -> {
            System.out.println("--- Executando a indexação inicial de produtos ---");
            indexingService.indexAllProductsFromDatabase();
            System.out.println("--- Indexação inicial concluída ---");
        };
    }
}
