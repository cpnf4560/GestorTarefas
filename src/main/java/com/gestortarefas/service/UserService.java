package com.gestortarefas.service;

import com.gestortarefas.model.User;
import com.gestortarefas.model.Team;
import com.gestortarefas.model.UserRole;
import com.gestortarefas.repository.UserRepository;
import com.gestortarefas.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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
    private TeamRepository teamRepository;

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
     * Lista todos os utilizadores (ativos e inativos)
     */
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Atualiza informações do utilizador
     */
    public User updateUser(User user) {
        if (user.getId() == null) {
            throw new IllegalArgumentException("ID do utilizador não pode ser nulo");
        }
        
        Optional<User> existingUserOpt = userRepository.findById(user.getId());
        if (existingUserOpt.isEmpty()) {
            throw new IllegalArgumentException("Utilizador não encontrado");
        }
        
        User existingUser = existingUserOpt.get();
        
        // Atualizar apenas os campos que não são nulos na requisição
        if (user.getFullName() != null) {
            existingUser.setFullName(user.getFullName());
        }
        if (user.getEmail() != null) {
            existingUser.setEmail(user.getEmail());
        }
        if (user.getActive() != null) {
            existingUser.setActive(user.getActive());
        }
        if (user.getRole() != null) {
            existingUser.setRole(user.getRole());
        }

        return userRepository.save(existingUser);
    }

    /**
     * Atualiza utilizador a partir de Map (suporta teamId)
     */
    public User updateUserFromMap(Long userId, Map<String, Object> userData) {
        if (userId == null) {
            throw new IllegalArgumentException("ID do utilizador não pode ser nulo");
        }
        
        Optional<User> existingUserOpt = userRepository.findById(userId);
        if (existingUserOpt.isEmpty()) {
            throw new IllegalArgumentException("Utilizador não encontrado");
        }
        
        User existingUser = existingUserOpt.get();
        
        // Atualizar campos básicos
        if (userData.containsKey("fullName") && userData.get("fullName") != null) {
            existingUser.setFullName((String) userData.get("fullName"));
        }
        if (userData.containsKey("email") && userData.get("email") != null) {
            existingUser.setEmail((String) userData.get("email"));
        }
        if (userData.containsKey("active") && userData.get("active") != null) {
            existingUser.setActive((Boolean) userData.get("active"));
        }
        if (userData.containsKey("role") && userData.get("role") != null) {
            String roleStr = (String) userData.get("role");
            existingUser.setRole(UserRole.valueOf(roleStr));
        }
        
        // Processar teamId
        if (userData.containsKey("teamId") && userData.get("teamId") != null) {
            Long teamId = null;
            Object teamIdObj = userData.get("teamId");
            if (teamIdObj instanceof Number) {
                teamId = ((Number) teamIdObj).longValue();
            } else if (teamIdObj instanceof String) {
                teamId = Long.parseLong((String) teamIdObj);
            }
            
            if (teamId != null) {
                // Remover utilizador de todas as equipas atuais
                existingUser.getTeams().clear();
                
                // Adicionar à nova equipa
                Optional<Team> teamOpt = teamRepository.findById(teamId);
                if (teamOpt.isPresent()) {
                    Team team = teamOpt.get();
                    existingUser.addToTeam(team);
                }
            }
        } else {
            // Se não há teamId ou é null, remover de todas as equipas
            existingUser.getTeams().clear();
        }

        return userRepository.save(existingUser);
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