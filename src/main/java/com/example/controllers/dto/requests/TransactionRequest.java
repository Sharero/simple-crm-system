package com.example.controllers.dto.requests;

import com.example.controllers.dto.PaymentType;
import jakarta.validation.constraints.*;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionRequest {
    @NotNull
    private int sellerId;

    @NotNull
    @Positive
    private Double amount;

    @NotNull
    private PaymentType paymentType;
}
