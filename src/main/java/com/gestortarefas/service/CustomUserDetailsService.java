package com.gestortarefas.service;

import com.gestortarefas.model.User;
import com.gestortarefas.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Serviço de autenticação personalizado que busca utilizadores na base de dados.
 * 
 * Este serviço é usado pelo Spring Security para autenticar utilizadores
 * consultando a base de dados em vez de usar autenticação HTTP básica fixa.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Carrega um utilizador pelo username e retorna os detalhes para autenticação.
     * 
     * @param username O username do utilizador
     * @return UserDetails com informações do utilizador
     * @throws UsernameNotFoundException se o utilizador não for encontrado
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Buscar o utilizador na base de dados
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Utilizador não encontrado: " + username));

        // Verificar se o utilizador está ativo
        if (!user.getActive()) {
            throw new UsernameNotFoundException("Utilizador inativo: " + username);
        }

        // Converter o papel do utilizador para GrantedAuthority
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));

        // Retornar UserDetails com os dados do utilizador
        return org.springframework.security.core.userdetails.User.builder()
            .username(user.getUsername())
            .password(user.getPassword()) // Já está em SHA-256 com salt
            .authorities(authorities)
            .accountExpired(false)
            .accountLocked(false)
            .credentialsExpired(false)
            .disabled(!user.getActive())
            .build();
    }
}