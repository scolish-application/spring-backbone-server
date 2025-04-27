package com.github.simaodiazz.schola.backbone.server.router.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GuardianResponse {

    private Long id;
    private String name;
    private String relationship;
    private String nif;
    private String email;
    private String phone;
    private String occupation;
    private String workPhone;
    private boolean isPrimaryGuardian;
    private AddressResponse address;
    private Long userId;

}