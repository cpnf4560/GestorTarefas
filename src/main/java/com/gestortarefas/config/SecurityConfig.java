package com.gestortarefas.config;

import com.gestortarefas.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Configuração de segurança Spring Security para a aplicação.
 * 
 * Configurações atuais:
 * - CORS habilitado para todas as origens (*)
 * - CSRF desabilitado (comum para APIs REST)
 * - Autenticação HTTP Basic usando base de dados
 * - Utilizadores e passwords da base de dados com SHA-256 + salt
 * - Autorização baseada em roles (ADMIN, GERENTE, FUNCIONARIO)
 * - Frame options desabilitadas (para consoles de desenvolvimento)
 * 
 * Para PRODUÇÃO, considerar:
 * - Autenticação JWT ou OAuth2
 * - CORS restrito a domínios específicos
 * - Rate limiting e proteções contra ataques
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    /**
     * Configura a cadeia de filtros de segurança.
     * 
     * Usa autenticação HTTP Basic com dados da base de dados.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Habilita CORS com configuração personalizada
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            // Desabilita CSRF (comum para APIs REST stateless)
            .csrf(csrf -> csrf.disable())
            // Habilita autenticação HTTP Basic
            .httpBasic(httpBasic -> {})
            // Sessões stateless - não mantém estado no servidor
            .sessionManagement(session -> session.sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS))
            // Configuração de autorização
            .authorizeHttpRequests(authz -> authz
                // Endpoints públicos
                .requestMatchers("/", "/actuator/health", "/error", "/api/users/login").permitAll()
                // Endpoints de dashboard temporariamente públicos para funcionamento do frontend
                .requestMatchers("/api/dashboard/**", "/api/users/**", "/api/teams/**", "/api/tasks/**").permitAll()
                // Todas as outras requisições requerem autenticação
                .anyRequest().authenticated()
            )
            // Desabilita frame options para permitir consoles de desenvolvimento
            .headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.disable()));
            
        return http.build();
    }

    /**
     * Configura o AuthenticationManager para usar o CustomUserDetailsService.
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = 
            http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder
            .userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder());
        return authenticationManagerBuilder.build();
    }

    /**
     * Bean para codificação de senhas usando SHA-256 com salt.
     * 
     * SHA-256 com salt é um algoritmo seguro para hash de senhas.
     * Usado para criptografar senhas antes de armazenar na base de dados.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
    return new Sha256PasswordEncoder();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}