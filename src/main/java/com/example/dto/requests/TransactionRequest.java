package com.example.dto.requests;

import com.example.dto.PaymentType;
import jakarta.validation.constraints.*;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionRequest {
  @NotNull private int sellerId;

  @NotNull @Positive private Double amount;

  @NotNull private PaymentType paymentType;
}
