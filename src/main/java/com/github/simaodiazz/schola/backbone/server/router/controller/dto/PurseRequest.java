package com.github.simaodiazz.schola.backbone.server.router.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PurseRequest {

    private Long id;
    private double purse;
    private Long userId;
    private List<TransactionRequest> transactions;

}