package com.example.repositories;

import com.example.entities.Seller;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Repository
public interface SellerRepository extends JpaRepository<Seller, Integer> {

  @Query(
      value =
          "SELECT s.id AS seller_id, SUM(t.amount) AS total "
              + "FROM transactions t "
              + "JOIN sellers s ON t.seller = s.id "
              + "WHERE t.transaction_date >= :start AND t.transaction_date <= :end "
              + "GROUP BY s.id "
              + "ORDER BY total DESC ",
      nativeQuery = true)
  List<Object[]> findTopSellerByAmount(
      @Param("start") Timestamp start, @Param("end") Timestamp end);

  @Query(
      value =
          "SELECT s.id AS seller_id, COALESCE(SUM(t.amount), 0) AS total "
              + "FROM sellers s "
              + "LEFT JOIN transactions t ON t.seller = s.id "
              + "  AND t.transaction_date >= :start AND t.transaction_date <= :end "
              + "GROUP BY s.id "
              + "HAVING COALESCE(SUM(t.amount), 0) < :limit "
              + "ORDER BY total ASC",
      nativeQuery = true)
  List<Object[]> findSellersWithTotalLessThan(
      @Param("start") Timestamp start,
      @Param("end") Timestamp end,
      @Param("limit") BigDecimal limit);
}
