package com.github.simaodiazz.schola.backbone.server.router.controller.dto;

import com.github.simaodiazz.schola.backbone.server.entity.data.model.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstudianteResponse {

    private Long id;
    private String name;
    private String nif;
    private LocalDate dateOfBirth;
    private String email;
    private String phone;
    private Gender gender;
    private AddressResponse address;
    private List<GuardianResponse> guardians = new ArrayList<>();
    private long classroomId;
    private boolean special;
    private String medicalInformation;
    private Long userId;

}