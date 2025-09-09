package br.uesb.cipec.loja_automatica.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/*
 * What are beans?
 *  A bean is a Java object that is part of the Spring application 
 * context and is controlled by the framework. 
 * This means that Spring takes care of creating, configuring , and
 * managing the lifecycle of these objects, instead of you having to do it manually with new
 */

@Configuration // This annotation says that the class contains bean definitions
public class WebConfig implements WebMvcConfigurer{
     public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {

    // Via HEADER PARAM
            configurer.favorParameter(false)
            .ignoreAcceptHeader(false)
            .useRegisteredExtensionsOnly(false) 
            .defaultContentType(MediaType.APPLICATION_JSON) 
            .mediaType("json", MediaType.APPLICATION_JSON)
            .mediaType("xml", MediaType.APPLICATION_XML)
            .mediaType("yaml", MediaType.APPLICATION_YAML);
}
    
}
