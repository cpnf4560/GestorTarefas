package com.gestortarefas.service;

import com.gestortarefas.model.User;
import com.gestortarefas.model.UserProfile;
import com.gestortarefas.repository.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

/**
 * Serviço para gestão de perfis de utilizador
 */
@Service
@Transactional
public class UserProfileService {

    private static final String UPLOAD_DIR = "uploads/profiles/";
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final String[] ALLOWED_EXTENSIONS = {".jpg", ".jpeg", ".png", ".gif"};

    @Autowired
    private UserProfileRepository userProfileRepository;

    /**
     * Cria um perfil para um utilizador
     */
    public UserProfile createProfile(User user) {
        // Verificar se já existe um perfil para este utilizador
        if (userProfileRepository.findByUser(user).isPresent()) {
            throw new IllegalArgumentException("Utilizador já possui um perfil");
        }

        UserProfile profile = new UserProfile();
        profile.setUser(user);
        // Não existe displayName na entidade UserProfile
        
        return userProfileRepository.save(profile);
    }

    /**
     * Atualiza informações do perfil
     */
    public UserProfile updateProfile(Long userId, String jobTitle, String bio, 
                                    String phoneNumber, String department, String location, 
                                    User requester) {
        
        UserProfile profile = getProfileByUserId(userId);
        
        // Verificar permissões - só o próprio utilizador ou admin pode alterar
        if (!canEditProfile(profile, requester)) {
            throw new IllegalArgumentException("Sem permissão para alterar este perfil");
        }

        if (jobTitle != null && !jobTitle.trim().isEmpty()) {
            profile.setJobTitle(jobTitle.trim());
        }

        if (bio != null) {
            profile.setBio(bio.trim());
        }

        if (phoneNumber != null) {
            profile.setPhoneNumber(phoneNumber.trim());
        }

        if (department != null) {
            profile.setDepartment(department.trim());
        }

        if (location != null) {
            profile.setLocation(location.trim());
        }

        return userProfileRepository.save(profile);
    }

    /**
     * Faz upload da foto de perfil
     */
    public UserProfile updateProfilePicture(Long userId, MultipartFile file, User requester) {
        UserProfile profile = getProfileByUserId(userId);

        // Verificar permissões
        if (!canEditProfile(profile, requester)) {
            throw new IllegalArgumentException("Sem permissão para alterar esta foto de perfil");
        }

        // Validar arquivo
        validateProfilePicture(file);

        try {
            // Criar diretório se não existir
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Gerar nome único para o arquivo
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || !originalFilename.contains(".")) {
                throw new IllegalArgumentException("Nome de arquivo inválido");
            }
            String extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
            String filename = "profile_" + userId + "_" + UUID.randomUUID().toString() + extension;
            
            Path filePath = uploadPath.resolve(filename);

            // Remover foto anterior se existir
            if (profile.getProfilePicturePath() != null) {
                try {
                    Path oldFile = Paths.get(profile.getProfilePicturePath());
                    Files.deleteIfExists(oldFile);
                } catch (IOException e) {
                    // Log do erro, mas continua com o upload da nova foto
                    System.err.println("Erro ao remover foto anterior: " + e.getMessage());
                }
            }

            // Salvar novo arquivo
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            profile.setProfilePicturePath(filePath.toString());

            return userProfileRepository.save(profile);

        } catch (IOException e) {
            throw new RuntimeException("Erro ao fazer upload da foto de perfil", e);
        }
    }

    /**
     * Remove a foto de perfil
     */
    public UserProfile removeProfilePicture(Long userId, User requester) {
        UserProfile profile = getProfileByUserId(userId);

        // Verificar permissões
        if (!canEditProfile(profile, requester)) {
            throw new IllegalArgumentException("Sem permissão para remover esta foto de perfil");
        }

        if (profile.getProfilePicturePath() != null) {
            try {
                Path filePath = Paths.get(profile.getProfilePicturePath());
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                System.err.println("Erro ao remover arquivo de foto: " + e.getMessage());
            }
            
            profile.setProfilePicturePath(null);
            return userProfileRepository.save(profile);
        }

        return profile;
    }

    /**
     * Atualiza preferências do utilizador
     */
    public UserProfile updatePreferences(Long userId, String theme, String language, 
                                        Boolean emailNotifications, User requester) {
        
        UserProfile profile = getProfileByUserId(userId);

        // Verificar permissões
        if (!canEditProfile(profile, requester)) {
            throw new IllegalArgumentException("Sem permissão para alterar estas preferências");
        }

        if (theme != null) {
            profile.setThemePreference(theme);
        }

        if (language != null) {
            profile.setLanguagePreference(language);
        }

        if (emailNotifications != null) {
            profile.setEmailNotifications(emailNotifications);
        }

        return userProfileRepository.save(profile);
    }

    /**
     * Obtém perfil por ID do utilizador
     */
    @Transactional(readOnly = true)
    public UserProfile getProfileByUserId(Long userId) {
        return userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Perfil não encontrado"));
    }

    /**
     * Obtém perfil por utilizador
     */
    @Transactional(readOnly = true)
    public UserProfile getProfileByUser(User user) {
        return userProfileRepository.findByUser(user)
                .orElse(null); // Retorna null se não encontrar, permitindo criação posterior
    }

    /**
     * Lista todos os perfis
     */
    @Transactional(readOnly = true)
    public List<UserProfile> getAllProfiles() {
        return userProfileRepository.findAll();
    }

    /**
     * Busca perfis por departamento
     */
    @Transactional(readOnly = true)
    public List<UserProfile> getProfilesByDepartment(String department) {
        return userProfileRepository.findByDepartmentIgnoreCase(department);
    }

    /**
     * Busca perfis com fotos
     */
    @Transactional(readOnly = true)
    public List<UserProfile> getProfilesWithPictures() {
        return userProfileRepository.findByProfilePicturePathIsNotNull();
    }

    /**
     * Busca perfil por cargo (job title)
     */
    @Transactional(readOnly = true)
    public List<UserProfile> searchProfilesByJobTitle(String jobTitle) {
        return userProfileRepository.findByJobTitleContainingIgnoreCase(jobTitle);
    }

    /**
     * Valida arquivo de foto de perfil
     */
    private void validateProfilePicture(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Arquivo de imagem é obrigatório");
        }

        // Verificar tamanho
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("Arquivo muito grande. Máximo permitido: 5MB");
        }

        // Verificar extensão
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new IllegalArgumentException("Nome do arquivo inválido");
        }

        String extension = originalFilename.substring(originalFilename.lastIndexOf('.')).toLowerCase();
        boolean validExtension = false;
        for (String allowedExt : ALLOWED_EXTENSIONS) {
            if (allowedExt.equals(extension)) {
                validExtension = true;
                break;
            }
        }

        if (!validExtension) {
            throw new IllegalArgumentException("Tipo de arquivo não suportado. Use: JPG, PNG, GIF");
        }

        // Verificar tipo de conteúdo
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Arquivo deve ser uma imagem");
        }
    }

    /**
     * Verifica se um utilizador pode editar um perfil
     */
    private boolean canEditProfile(UserProfile profile, User requester) {
        return requester.getRole().isAdmin() || profile.getUser().equals(requester);
    }
}