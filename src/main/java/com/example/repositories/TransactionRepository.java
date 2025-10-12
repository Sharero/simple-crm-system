package com.example.repositories;

import com.example.entities.Transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
  List<Transaction> findBySellerId(int sellerId);
}
