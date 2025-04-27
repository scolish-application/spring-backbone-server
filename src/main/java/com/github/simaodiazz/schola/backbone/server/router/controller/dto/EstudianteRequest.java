package com.github.simaodiazz.schola.backbone.server.router.controller.dto;

import com.github.simaodiazz.schola.backbone.server.entity.data.model.Gender;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstudianteRequest {
    @NotBlank(message = "O nome é obrigatório")
    private String name;

    @NotBlank(message = "O NIF é obrigatório")
    @Pattern(regexp = "^[0-9]{9}$", message = "O NIF deve conter 9 dígitos")
    private String nif;

    @Past(message = "A data de nascimento deve estar no passado")
    private LocalDate dateOfBirth;

    @Pattern(regexp = "^[0-9]{9}$", message = "O número de telefone deve conter 9 dígitos")
    private String phone;

    private Gender gender;

    @Valid
    private AddressRequest address;

    @Valid
    private List<GuardianRequest> guardians = new ArrayList<>();

    @NotBlank(message = "A turma é obrigatória")
    private String schoolClass;

    private String emergencyContactName;

    @Pattern(regexp = "^[0-9]{9}$", message = "O número de emergência deve conter 9 dígitos")
    private String emergencyContactPhone;

    private boolean specialNeeds;

    private String medicalInformation;

    private Long userId;
}
