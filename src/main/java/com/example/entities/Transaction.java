package com.example.entities;

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

    @Column(name = "payment_type")
    private String paymentType;

    @Column(name = "transaction_date")
    private LocalDateTime transactionDate;

    public Transaction(Seller seller, Double amount, String paymentType) {
        this.seller = seller;
        this.amount = amount;
        this.paymentType = paymentType;
    }
}
