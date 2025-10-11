package com.example.entities;

import com.example.controllers.dto.PaymentType;
import lombok.*;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne()
    @JoinColumn(name = "seller", referencedColumnName = "id")
    private Seller seller;

    @Column(name = "amount")
    private Double amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type")
    private PaymentType paymentType;

    @Column(name = "transaction_date")
    private LocalDateTime transactionDate;

    public Transaction(Seller seller, Double amount, PaymentType paymentType) {
        this.seller = seller;
        this.amount = amount;
        this.paymentType = paymentType;
    }
}
