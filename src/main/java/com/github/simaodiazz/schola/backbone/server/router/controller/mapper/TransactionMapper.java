package com.github.simaodiazz.schola.backbone.server.router.controller.mapper;

import com.github.simaodiazz.schola.backbone.server.economy.data.model.Transaction;
import com.github.simaodiazz.schola.backbone.server.router.controller.dto.TransactionCreateRequest;
import com.github.simaodiazz.schola.backbone.server.router.controller.dto.TransactionRequest;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

    public TransactionRequest request(Transaction transaction) {
        if (transaction == null) {
            return null;
        }

        return new TransactionRequest(
                transaction.getId(),
                transaction.getLocation(),
                transaction.getCause(),
                transaction.getAmount(),
                transaction.getMovement()
        );
    }

    public Transaction createRequest(TransactionCreateRequest request) {
        if (request == null) {
            return null;
        }

        Transaction transaction = new Transaction();
        transaction.setLocation(request.getLocation());
        transaction.setCause(request.getCause());
        transaction.setAmount(request.getAmount());
        transaction.setMovement(request.getMovement());

        return transaction;
    }
}