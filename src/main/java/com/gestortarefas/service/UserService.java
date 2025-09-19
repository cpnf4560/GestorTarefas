package com.gestortarefas.service;

import com.gestortarefas.model.User;
import com.gestortarefas.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Serviço para gestão de utilizadores e autenticação
 */
@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Autentica um utilizador
     */
    public Optional<User> authenticate(String identifier, String password) {
        Optional<User> userOpt = userRepository.findByUsernameOrEmail(identifier);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getActive() && passwordEncoder.matches(password, user.getPassword())) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    /**
     * Registra um novo utilizador
     */
    public User registerUser(String username, String email, String password, String fullName) {
        // Verificar se já existe utilizador com mesmo username ou email
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Nome de utilizador já existe");
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email já está registado");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setFullName(fullName);
        user.setCreatedAt(LocalDateTime.now());
        user.setActive(true);

        return userRepository.save(user);
    }

    /**
     * Busca utilizador por ID
     */
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Busca utilizador por username
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Busca utilizador por email
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Lista todos os utilizadores ativos
     */
    public List<User> findAllActiveUsers() {
        return userRepository.findByActiveTrue();
    }

    /**
     * Atualiza informações do utilizador
     */
    public User updateUser(User user) {
        if (user.getId() == null) {
            throw new IllegalArgumentException("ID do utilizador não pode ser nulo");
        }
        
        Optional<User> existingUser = userRepository.findById(user.getId());
        if (existingUser.isEmpty()) {
            throw new IllegalArgumentException("Utilizador não encontrado");
        }

        return userRepository.save(user);
    }

    /**
     * Altera a senha do utilizador
     */
    public boolean changePassword(Long userId, String currentPassword, String newPassword) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return false;
        }

        User user = userOpt.get();
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            return false;
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return true;
    }

    /**
     * Desativa um utilizador
     */
    public boolean deactivateUser(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return false;
        }

        User user = userOpt.get();
        user.setActive(false);
        userRepository.save(user);
        return true;
    }

    /**
     * Ativa um utilizador
     */
    public boolean activateUser(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return false;
        }

        User user = userOpt.get();
        user.setActive(true);
        userRepository.save(user);
        return true;
    }

    /**
     * Verifica se username está disponível
     */
    public boolean isUsernameAvailable(String username) {
        return !userRepository.existsByUsername(username);
    }

    /**
     * Verifica se email está disponível
     */
    public boolean isEmailAvailable(String email) {
        return !userRepository.existsByEmail(email);
    }

    /**
     * Conta o número total de utilizadores ativos
     */
    public long countActiveUsers() {
        return userRepository.countByActiveTrue();
    }

    /**
     * Busca utilizadores com tarefas
     */
    public List<User> findUsersWithTasks() {
        return userRepository.findUsersWithTasks();
    }

    /**
     * Busca utilizadores por nome completo
     */
    public List<User> searchUsersByName(String name) {
        return userRepository.findByFullNameContainingIgnoreCase(name);
    }

    /**
     * Elimina um utilizador (apenas se não tiver tarefas)
     */
    public boolean deleteUser(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return false;
        }

        User user = userOpt.get();
        if (!user.getTasks().isEmpty()) {
            throw new IllegalStateException("Não é possível eliminar utilizador com tarefas associadas");
        }

        userRepository.delete(user);
        return true;
    }
}