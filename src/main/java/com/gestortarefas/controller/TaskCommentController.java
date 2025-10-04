package com.gestortarefas.controller;

import com.gestortarefas.model.TaskComment;
import com.gestortarefas.model.Task;
import com.gestortarefas.model.User;
import com.gestortarefas.repository.TaskCommentRepository;
import com.gestortarefas.repository.TaskRepository;
import com.gestortarefas.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

/**
 * Controlador REST para operações relacionadas aos comentários de tarefas
 */
@RestController
@RequestMapping("/api/tasks/{taskId}/comments")
@CrossOrigin(origins = "*")
public class TaskCommentController {

    @Autowired
    private TaskCommentRepository taskCommentRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Obter todos os comentários de uma tarefa
     */
    @GetMapping
    public ResponseEntity<?> getTaskComments(@PathVariable Long taskId,
                                            @RequestParam(defaultValue = "desc") String order) {
        try {
            Optional<Task> taskOpt = taskRepository.findById(taskId);
            if (!taskOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            List<TaskComment> comments;
            if ("asc".equalsIgnoreCase(order)) {
                comments = taskCommentRepository.findByTaskIdOrderByCreatedAtAsc(taskId);
            } else {
                comments = taskCommentRepository.findByTaskIdOrderByCreatedAtDesc(taskId);
            }

            // Criar lista de comentários com dados do utilizador explícitos
            List<Map<String, Object>> commentsWithUserData = comments.stream()
                .map(this::createCommentResponse)
                .toList();

            Map<String, Object> response = new HashMap<>();
            response.put("taskId", taskId);
            response.put("taskTitle", taskOpt.get().getTitle());
            response.put("total", comments.size());
            response.put("comments", commentsWithUserData);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Erro ao buscar comentários: " + e.getMessage()));
        }
    }

    /**
     * Adicionar um novo comentário a uma tarefa
     */
    @PostMapping
    public ResponseEntity<?> addTaskComment(@PathVariable Long taskId,
                                           @RequestBody CommentRequest request) {
        try {
            // Validar dados obrigatórios
            if (request.getCommentText() == null || request.getCommentText().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Texto do comentário é obrigatório"));
            }

            if (request.getUserId() == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "ID do utilizador é obrigatório"));
            }

            // Verificar se a tarefa existe
            Optional<Task> taskOpt = taskRepository.findById(taskId);
            if (!taskOpt.isPresent()) {
                return ResponseEntity.status(404)
                        .body(Map.of("error", "Tarefa não encontrada"));
            }

            // Verificar se o utilizador existe
            Optional<User> userOpt = userRepository.findById(request.getUserId());
            if (!userOpt.isPresent()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Utilizador não encontrado"));
            }

            Task task = taskOpt.get();
            User user = userOpt.get();

            // Verificar permissões - utilizador deve poder visualizar a tarefa
            if (!task.canBeViewedBy(user)) {
                return ResponseEntity.status(403)
                        .body(Map.of("error", "Não tem permissão para comentar nesta tarefa"));
            }

            // Criar o comentário
            TaskComment comment = new TaskComment(
                task, 
                user, 
                request.getCommentText().trim(),
                request.getIsSystemMessage() != null ? request.getIsSystemMessage() : false
            );

            // Guardar o comentário
            TaskComment savedComment = taskCommentRepository.save(comment);

            // Adicionar à lista de comentários da tarefa
            task.addComment(savedComment);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Comentário adicionado com sucesso");
            response.put("comment", savedComment);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Erro ao adicionar comentário: " + e.getMessage()));
        }
    }

    /**
     * Obter comentários recentes de uma tarefa (limitado)
     */
    @GetMapping("/recent")
    public ResponseEntity<?> getRecentComments(@PathVariable Long taskId,
                                              @RequestParam(defaultValue = "10") int limit) {
        try {
            Optional<Task> taskOpt = taskRepository.findById(taskId);
            if (!taskOpt.isPresent()) {
                return ResponseEntity.status(404)
                        .body(Map.of("error", "Tarefa não encontrada"));
            }

            List<TaskComment> recentComments = taskCommentRepository
                    .findRecentCommentsByTask(taskId, limit);

            Map<String, Object> response = new HashMap<>();
            response.put("taskId", taskId);
            response.put("limit", limit);
            response.put("total", recentComments.size());
            response.put("comments", recentComments);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Erro ao buscar comentários recentes: " + e.getMessage()));
        }
    }

    /**
     * Contar comentários de uma tarefa
     */
    @GetMapping("/count")
    public ResponseEntity<?> getCommentsCount(@PathVariable Long taskId) {
        try {
            Optional<Task> taskOpt = taskRepository.findById(taskId);
            if (!taskOpt.isPresent()) {
                return ResponseEntity.status(404)
                        .body(Map.of("error", "Tarefa não encontrada"));
            }

            long count = taskCommentRepository.countByTaskId(taskId);

            Map<String, Object> response = new HashMap<>();
            response.put("taskId", taskId);
            response.put("commentsCount", count);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Erro ao contar comentários: " + e.getMessage()));
        }
    }

    /**
     * Eliminar um comentário (apenas o autor ou admin/gerente)
     */
    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long taskId,
                                          @PathVariable Long commentId,
                                          @RequestParam Long userId) {
        try {
            // Verificar se o comentário existe
            Optional<TaskComment> commentOpt = taskCommentRepository.findById(commentId);
            if (!commentOpt.isPresent()) {
                return ResponseEntity.status(404)
                        .body(Map.of("error", "Comentário não encontrado"));
            }

            // Verificar se o utilizador existe
            Optional<User> userOpt = userRepository.findById(userId);
            if (!userOpt.isPresent()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Utilizador não encontrado"));
            }

            TaskComment comment = commentOpt.get();
            User user = userOpt.get();

            // Verificar permissões - apenas o autor, admin ou gerente da equipa
            boolean canDelete = comment.isFromUser(user) || 
                               user.isAdmin() || 
                               (comment.getTask().isAssignedToTeam() && 
                                comment.getTask().getAssignedTeam().isManager(user));

            if (!canDelete) {
                return ResponseEntity.status(403)
                        .body(Map.of("error", "Não tem permissão para eliminar este comentário"));
            }

            // Eliminar o comentário
            taskCommentRepository.delete(comment);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Comentário eliminado com sucesso"
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Erro ao eliminar comentário: " + e.getMessage()));
        }
    }

    // Classe para request de criação de comentário
    public static class CommentRequest {
        private Long userId;
        private String commentText;
        private Boolean isSystemMessage;

        public CommentRequest() {}

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public String getCommentText() {
            return commentText;
        }

        public void setCommentText(String commentText) {
            this.commentText = commentText;
        }

        public Boolean getIsSystemMessage() {
            return isSystemMessage;
        }

        public void setIsSystemMessage(Boolean isSystemMessage) {
            this.isSystemMessage = isSystemMessage;
        }
    }
    
    /**
     * Criar resposta do comentário com dados do utilizador explícitos
     */
    private Map<String, Object> createCommentResponse(TaskComment comment) {
        Map<String, Object> commentData = new HashMap<>();
        
        commentData.put("id", comment.getId());
        commentData.put("commentText", comment.getCommentText());
        commentData.put("createdAt", comment.getCreatedAt());
        commentData.put("updatedAt", comment.getUpdatedAt());
        commentData.put("isSystemMessage", comment.getIsSystemMessage());
        commentData.put("userId", comment.getUser().getId());
        
        // Dados do utilizador explícitos
        Map<String, Object> userData = new HashMap<>();
        userData.put("id", comment.getUser().getId());
        userData.put("username", comment.getUser().getUsername());
        userData.put("fullName", comment.getUser().getFullName());
        userData.put("email", comment.getUser().getEmail());
        
        commentData.put("user", userData);
        
        // Também incluir dados diretos para compatibilidade
        commentData.put("userName", comment.getUser().getFullName() != null ? 
            comment.getUser().getFullName() : comment.getUser().getUsername());
        commentData.put("userFullName", comment.getUser().getFullName());
        commentData.put("username", comment.getUser().getUsername());
        
        return commentData;
    }
}