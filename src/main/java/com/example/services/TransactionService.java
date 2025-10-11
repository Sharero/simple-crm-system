package com.example.services;

import com.example.repositories.SellerRepository;
import com.example.repositories.TransactionRepository;
import com.example.entities.Transaction;
import com.example.entities.Seller;
import com.example.controllers.dto.TransactionRequest;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final SellerRepository sellerRepository;

    public TransactionService(TransactionRepository transactionRepository, SellerRepository sellerRepository) {
        this.transactionRepository = transactionRepository;
        this.sellerRepository = sellerRepository;
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public Optional<Transaction> getTransactionById(int id) {
        return transactionRepository.findById(id);
    }

    public Transaction createTransaction(TransactionRequest transactionRequest) {
        Seller seller = sellerRepository.findById(transactionRequest.getSellerId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Seller not found with id: " + transactionRequest.getSellerId()
                ));

        Transaction transaction = new Transaction(seller, transactionRequest.getAmount(), transactionRequest.getPaymentType());

        transaction.setTransactionDate(LocalDateTime.now());

        return transactionRepository.save(transaction);
    }

    public List<Transaction> getAllTransactionsBySellerId(int sellerId) {
        return transactionRepository.findBySellerId(sellerId);
    }
}
