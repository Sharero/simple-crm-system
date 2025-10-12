package com.example.repositories;

import com.example.entities.Seller;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;

@Repository
public interface SellerRepository extends JpaRepository<Seller, Integer> {

    @Query(value = ""
            + "SELECT s.* "
            + "FROM sellers s "
            + "JOIN transactions t ON t.seller = s.id "
            + "WHERE t.transaction_date >= :start AND t.transaction_date < :end "
            + "GROUP BY s.id "
            + "ORDER BY SUM(t.amount) DESC "
            + "LIMIT 1",
            nativeQuery = true)
    Seller findTopSellerByAmount(@Param("start") Timestamp start, @Param("end") Timestamp end);
}
