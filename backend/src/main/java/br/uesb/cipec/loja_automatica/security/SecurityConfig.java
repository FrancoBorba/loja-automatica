package br.uesb.cipec.loja_automatica.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Autowired
  private JwtAuthenticationFilter jwtAuthenticationFilter;

  @Bean
  @Order(1)
  public SecurityFilterChain webhookFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/api/webhooks/**") 
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll() 
            );
        return http.build();
    }
    
  @Bean
  @Order(2)
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            // ---- Public Endpoints ----

            .requestMatchers("/api/auth/**").permitAll() // Login e registro
            .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll() // Documentation
            .requestMatchers(HttpMethod.GET, "/api/products", "/api/products/**").permitAll() // View products is public

            
            // ---- Endpoints de ADMIN ----
            .requestMatchers(HttpMethod.POST, "/api/products").hasRole("ADMIN") // Create product

            .requestMatchers(HttpMethod.PUT, "/api/products/**").hasRole("ADMIN") //Update product

            .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasRole("ADMIN") // Delete product
           
            .requestMatchers("/api/users/me/**").authenticated() 

            .requestMatchers("/api/users/**").hasRole("ADMIN") 

            
            // ---- PURCHASE Endpoints ----
           .requestMatchers("/api/purchase/**").authenticated() 

           .requestMatchers("/payment-success").permitAll()
           .requestMatchers("/paymente-cancel").permitAll()

        
            
            // ---- Final rule ----
            .anyRequest().authenticated() // Any other request requires authentication

        ).addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
}


   @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173", "http://localhost:3000"));

        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception{
    
      return authenticationConfiguration.getAuthenticationManager();

    }

     @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

