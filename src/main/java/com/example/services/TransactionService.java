package com.example.services;

import com.example.repositories.SellerRepository;
import com.example.repositories.TransactionRepository;
import com.example.entities.Transaction;
import com.example.entities.Seller;
import com.example.dto.requests.TransactionRequest;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {
  private final TransactionRepository transactionRepository;
  private final SellerRepository sellerRepository;

  public TransactionService(
      TransactionRepository transactionRepository, SellerRepository sellerRepository) {
    this.transactionRepository = transactionRepository;
    this.sellerRepository = sellerRepository;
  }

  public List<Transaction> getAllTransactions() {
    try {
      return transactionRepository.findAll();
    } catch (DataAccessException ex) {
      throw new RuntimeException(ex);
    }
  }

  public Optional<Transaction> getTransactionById(int id) {
    if (id <= 0) throw new IllegalArgumentException("id must be positive");

    try {
      return transactionRepository.findById(id);
    } catch (DataAccessException ex) {
      throw ex;
    }
  }

  public Transaction createTransaction(TransactionRequest transactionRequest) {
    if (transactionRequest == null) {
      throw new IllegalArgumentException("transactionRequest must not be null");
    }
    if (transactionRequest.getSellerId() == 0) {
      throw new IllegalArgumentException("sellerId is required");
    }
    if (transactionRequest.getAmount() == null) {
      throw new IllegalArgumentException("amount is required");
    }
    if (transactionRequest.getAmount() < 0) {
      throw new IllegalArgumentException("amount must be positive");
    }
    if (transactionRequest.getPaymentType() == null) {
      throw new IllegalArgumentException("paymentType is required");
    }

    Seller seller =
        sellerRepository
            .findById(transactionRequest.getSellerId())
            .orElseThrow(
                () ->
                    new EntityNotFoundException(
                        "Seller not found with id: " + transactionRequest.getSellerId()));

    Transaction transaction =
        new Transaction(
            seller, transactionRequest.getAmount(), transactionRequest.getPaymentType());
    transaction.setTransactionDate(LocalDateTime.now());

    try {
      return transactionRepository.save(transaction);
    } catch (DataAccessException ex) {
      throw ex;
    }
  }

  public List<Transaction> getAllTransactionsBySellerId(int sellerId) {
    if (sellerId <= 0) throw new IllegalArgumentException("sellerId must be positive");

    sellerRepository
        .findById(sellerId)
        .orElseThrow(() -> new EntityNotFoundException("Seller not found with id: " + sellerId));

    try {
      return transactionRepository.findBySellerId(sellerId);
    } catch (DataAccessException ex) {
      throw ex;
    }
  }
}
