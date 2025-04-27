package com.github.simaodiazz.schola.backbone.server.router.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventusRequest {

    private Long id;

    @NotNull(message = "O nome do evento não pode ser nulo")
    @Size(min = 1, max = 255, message = "O nome do evento deve ter entre 1 e 255 caracteres")
    private String name;

    @Size(max = 2048, message = "A descrição do evento pode ter no máximo 2048 caracteres")
    private String description;

    @NotNull(message = "A data de início não pode ser nula")
    @FutureOrPresent(message = "A data de início deve ser no futuro ou no presente")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime start;

    @NotNull(message = "A data de término não pode ser nula")
    @FutureOrPresent(message = "A data de término deve ser no futuro ou no presente")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime end;

    @NotNull(message = "O ID do utilizador precisa ser específicado")
    private Long userId;

    private String userName;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime created;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updated;
}