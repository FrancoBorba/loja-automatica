package br.uesb.cipec.loja_automatica.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import br.uesb.cipec.loja_automatica.service.AuthorizhationService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
  private AuthorizhationService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String token = this.recoverToken(request);

        if (token != null) {
            try {
                String email = jwtUtil.extractEmail(token); // Valida e extrai o e-mail
                UserDetails userDetails = userDetailsService.loadUserByUsername(email); // Carrega o usuário

                // Cria o objeto de autenticação para o Spring Security
                var authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                
                // Define o usuário como autenticado para esta requisição
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (Exception e) {
                // Se o token for inválido, o contexto de segurança continua limpo (não autenticado)
                SecurityContextHolder.clearContext();
            }
        }
        
        // Continua a cadeia de filtros
        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        return authHeader.substring(7); // Remove o "Bearer "
    }
}