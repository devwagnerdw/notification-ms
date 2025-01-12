package com.ead.notification.controllers;

import com.ead.notification.dtos.NotificationDto;
import com.ead.notification.models.NotificationModel;
import com.ead.notification.services.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserNotificationController {

    final NotificationService notificationService;

    public UserNotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PreAuthorize("hasAnyRole('STUDENT')")
    @GetMapping("/users/{userId}/notifications")
    @Operation(summary = "Buscar Notificações do Usuário", description = "Recupera todas as notificações de um usuário específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notificações encontradas com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<Page<NotificationModel>> getAllNotificationsByUser(@PathVariable(value="userId") UUID userId,
                                                                             @PageableDefault(page = 0, size = 10, sort = "notificationId", direction = Sort.Direction.ASC) Pageable pageable,
                                                                             Authentication Authentication){
        return ResponseEntity.status(HttpStatus.OK).body(notificationService.findAllNotificationsByUser(userId, pageable));
    }

    @PreAuthorize("hasAnyRole('STUDENT')")
    @PutMapping("/users/{userId}/notifications/{notificationId}")
    @Operation(summary = "Atualizar Notificação", description = "Atualiza o status de uma notificação de um usuário específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notificação atualizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Notificação ou usuário não encontrados"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<Object> updateNotification(@PathVariable(value="userId") UUID userId,
                                                     @PathVariable(value="notificationId") UUID notificationId,
                                                     @RequestBody @Valid NotificationDto notificationDto){
        Optional<NotificationModel> notificationModelOptional =
                notificationService.findByNotificationIdAndUserId(notificationId, userId);
        if(notificationModelOptional.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Notification not found!");
        }
        notificationModelOptional.get().setNotificationStatus(notificationDto.getNotificationStatus());
        notificationService.saveNotification(notificationModelOptional.get());
        return ResponseEntity.status(HttpStatus.OK).body(notificationModelOptional.get());
    }


}
