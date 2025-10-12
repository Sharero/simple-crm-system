package com.example.controllers;

import com.example.dto.requests.TransactionRequest;
import com.example.entities.Transaction;
import com.example.services.TransactionService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
  private final TransactionService transactionService;

  public TransactionController(TransactionService transactionService) {
    this.transactionService = transactionService;
  }

  // GET /transactions
  @GetMapping
  public List<Transaction> getAllTransactions() {
    return transactionService.getAllTransactions();
  }

  // GET /transactions/{id}
  @GetMapping("/{id}")
  public ResponseEntity<Transaction> getTransactionById(@PathVariable int id) {
    return transactionService
        .getTransactionById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  // POST /transactions
  @PostMapping
  public Transaction createTransaction(@RequestBody TransactionRequest transactionRequest) {
    return transactionService.createTransaction(transactionRequest);
  }

  // GET /transactions/sellers/{sellerId}
  @GetMapping("/sellers/{sellerId}")
  public List<Transaction> getAllTransactionsBySellerId(@PathVariable int sellerId) {
    return transactionService.getAllTransactionsBySellerId(sellerId);
  }
}
