package com.github.simaodiazz.schola.backbone.server.router.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GuardianRequest {

    @NotBlank(message = "O nome é obrigatório")
    private String name;

    @NotBlank(message = "O grau de parentesco é obrigatório")
    private String relationship;

    @NotBlank(message = "O NIF é obrigatório")
    @Pattern(regexp = "^[0-9]{9}$", message = "O NIF deve conter 9 dígitos")
    private String nif;

    @Pattern(regexp = "^[0-9]{9}$", message = "O número de telefone deve conter 9 dígitos")
    private String phone;

    private String occupation;
    private String workPhone;
    private boolean isPrimaryGuardian;
    private AddressRequest address;
    private Long userId;

}