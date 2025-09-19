package com.gestortarefas.service;

import com.gestortarefas.model.Team;
import com.gestortarefas.model.User;
import com.gestortarefas.repository.TeamRepository;
import com.gestortarefas.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Serviço para gestão de equipas
 */
@Service
@Transactional
public class TeamService {

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Cria uma nova equipa
     */
    public Team createTeam(String name, String description, User manager) {
        // Verificar se o utilizador pode criar equipas
        if (!manager.getRole().canManageTeams()) {
            throw new IllegalArgumentException("Utilizador não tem permissão para criar equipas");
        }

        // Verificar se já existe uma equipa com esse nome
        if (teamRepository.existsByNameIgnoreCase(name)) {
            throw new IllegalArgumentException("Já existe uma equipa com o nome: " + name);
        }

        Team team = new Team();
        team.setName(name);
        team.setDescription(description);
        team.setManager(manager);
        team.setActive(true);

        return teamRepository.save(team);
    }

    /**
     * Adiciona um membro à equipa
     */
    public Team addMemberToTeam(Long teamId, Long userId, User requester) {
        Team team = getTeamById(teamId);
        User userToAdd = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilizador não encontrado"));

        // Verificar permissões
        if (!canManageTeam(team, requester)) {
            throw new IllegalArgumentException("Sem permissão para gerir esta equipa");
        }

        team.addMember(userToAdd);
        return teamRepository.save(team);
    }

    /**
     * Remove um membro da equipa
     */
    public Team removeMemberFromTeam(Long teamId, Long userId, User requester) {
        Team team = getTeamById(teamId);
        User userToRemove = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilizador não encontrado"));

        // Verificar permissões
        if (!canManageTeam(team, requester)) {
            throw new IllegalArgumentException("Sem permissão para gerir esta equipa");
        }

        team.removeMember(userToRemove);
        return teamRepository.save(team);
    }

    /**
     * Atualiza uma equipa
     */
    public Team updateTeam(Long teamId, String name, String description, User requester) {
        Team team = getTeamById(teamId);

        // Verificar permissões
        if (!canManageTeam(team, requester)) {
            throw new IllegalArgumentException("Sem permissão para gerir esta equipa");
        }

        if (name != null && !name.trim().isEmpty()) {
            // Verificar se já existe outra equipa com esse nome
            if (teamRepository.existsByNameIgnoreCase(name) && 
                !team.getName().equalsIgnoreCase(name)) {
                throw new IllegalArgumentException("Já existe uma equipa com o nome: " + name);
            }
            team.setName(name);
        }

        if (description != null) {
            team.setDescription(description);
        }

        return teamRepository.save(team);
    }

    /**
     * Desativa uma equipa
     */
    public Team deactivateTeam(Long teamId, User requester) {
        Team team = getTeamById(teamId);

        // Apenas administradores podem desativar equipas
        if (!requester.getRole().isAdmin()) {
            throw new IllegalArgumentException("Apenas administradores podem desativar equipas");
        }

        team.setActive(false);
        return teamRepository.save(team);
    }

    /**
     * Ativa uma equipa
     */
    public Team activateTeam(Long teamId, User requester) {
        Team team = getTeamById(teamId);

        // Apenas administradores podem ativar equipas
        if (!requester.getRole().isAdmin()) {
            throw new IllegalArgumentException("Apenas administradores podem ativar equipas");
        }

        team.setActive(true);
        return teamRepository.save(team);
    }

    /**
     * Define um novo gerente para a equipa
     */
    public Team setTeamManager(Long teamId, Long managerId, User requester) {
        Team team = getTeamById(teamId);
        User newManager = userRepository.findById(managerId)
                .orElseThrow(() -> new IllegalArgumentException("Utilizador não encontrado"));

        // Apenas administradores podem alterar gerentes
        if (!requester.getRole().isAdmin()) {
            throw new IllegalArgumentException("Apenas administradores podem alterar gerentes de equipa");
        }

        // Verificar se o novo gerente tem o papel adequado
        if (!newManager.getRole().isManagerOrAbove()) {
            throw new IllegalArgumentException("O utilizador deve ter papel de Gerente ou Administrador");
        }

        team.setManager(newManager);
        return teamRepository.save(team);
    }

    /**
     * Busca equipa por ID
     */
    @Transactional(readOnly = true)
    public Team getTeamById(Long teamId) {
        return teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("Equipa não encontrada"));
    }

    /**
     * Lista todas as equipas ativas
     */
    @Transactional(readOnly = true)
    public List<Team> getAllActiveTeams() {
        return teamRepository.findByActiveTrue();
    }

    /**
     * Lista todas as equipas
     */
    @Transactional(readOnly = true)
    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }

    /**
     * Lista equipas geridas por um utilizador
     */
    @Transactional(readOnly = true)
    public List<Team> getTeamsManagedBy(User manager) {
        return teamRepository.findByManagerAndActiveTrue(manager);
    }

    /**
     * Lista equipas onde o utilizador é membro
     */
    @Transactional(readOnly = true)
    public List<Team> getTeamsForUser(User user) {
        return teamRepository.findTeamsByMember(user);
    }

    /**
     * Busca equipas por nome (pesquisa parcial)
     */
    @Transactional(readOnly = true)
    public List<Team> searchTeamsByName(String name) {
        return teamRepository.findByNameContainingIgnoreCase(name);
    }

    /**
     * Obtém estatísticas de uma equipa
     */
    @Transactional(readOnly = true)
    public TeamStats getTeamStats(Long teamId, User requester) {
        Team team = getTeamById(teamId);

        // Verificar se pode ver as estatísticas
        if (!canViewTeam(team, requester)) {
            throw new IllegalArgumentException("Sem permissão para ver esta equipa");
        }

        TeamStats stats = new TeamStats();
        stats.setTeamId(teamId);
        stats.setTeamName(team.getName());
        stats.setMemberCount(team.getMembers().size());
        stats.setTaskCount(team.getTasks().size());
        // Contar tarefas pendentes e concluídas usando a lógica da entidade
        long pendingTasks = team.getTasks().stream()
            .filter(task -> task.getStatus() != com.gestortarefas.model.Task.TaskStatus.CONCLUIDA)
            .count();
        long completedTasks = team.getCompletedTasksCount();
        
        stats.setPendingTaskCount((int) pendingTasks);
        stats.setCompletedTaskCount((int) completedTasks);

        return stats;
    }

    /**
     * Verifica se um utilizador pode gerir uma equipa
     */
    private boolean canManageTeam(Team team, User user) {
        return user.getRole().isAdmin() || team.getManager().equals(user);
    }

    /**
     * Verifica se um utilizador pode ver uma equipa
     */
    private boolean canViewTeam(Team team, User user) {
        return user.getRole().isAdmin() || 
               team.getManager().equals(user) || 
               team.getMembers().contains(user);
    }

    /**
     * Classe para estatísticas de equipa
     */
    public static class TeamStats {
        private Long teamId;
        private String teamName;
        private int memberCount;
        private int taskCount;
        private int pendingTaskCount;
        private int completedTaskCount;

        // Getters e Setters
        public Long getTeamId() { return teamId; }
        public void setTeamId(Long teamId) { this.teamId = teamId; }

        public String getTeamName() { return teamName; }
        public void setTeamName(String teamName) { this.teamName = teamName; }

        public int getMemberCount() { return memberCount; }
        public void setMemberCount(int memberCount) { this.memberCount = memberCount; }

        public int getTaskCount() { return taskCount; }
        public void setTaskCount(int taskCount) { this.taskCount = taskCount; }

        public int getPendingTaskCount() { return pendingTaskCount; }
        public void setPendingTaskCount(int pendingTaskCount) { this.pendingTaskCount = pendingTaskCount; }

        public int getCompletedTaskCount() { return completedTaskCount; }
        public void setCompletedTaskCount(int completedTaskCount) { this.completedTaskCount = completedTaskCount; }
    }
}