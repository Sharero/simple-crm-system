package com.example.services;

import com.example.dto.responses.TopSellerResponse;
import com.example.entities.Seller;
import com.example.repositories.SellerRepository;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SellerService {
  private final SellerRepository sellerRepository;

  private static final ZoneId ZONE = ZoneId.of("Asia/Novosibirsk");

  public SellerService(SellerRepository sellerRepository) {
    this.sellerRepository = sellerRepository;
  }

  public List<Seller> getAllSellers() {
    try {
      return sellerRepository.findAll();
    } catch (DataAccessException ex) {
      throw ex;
    }
  }

  public Optional<Seller> getSellerById(int id) {
    if (id <= 0) throw new IllegalArgumentException("id must be positive");

    try {
      return sellerRepository.findById(id);
    } catch (DataAccessException ex) {
      throw ex;
    }
  }

  public Seller createSeller(Seller seller) {
    if (seller == null) {
      throw new IllegalArgumentException("seller must not be null");
    }
    if (seller.getName() == null) {
      throw new IllegalArgumentException("name is required");
    }
    if (seller.getContactInfo() == null) {
      throw new IllegalArgumentException("contact info is required");
    }

    seller.setRegistrationDate(LocalDateTime.now());

    try {
      return sellerRepository.save(seller);
    } catch (DataAccessException ex) {
      throw ex;
    }
  }

  public Seller updateSeller(int id, Seller seller) {
    if (id <= 0) {
      throw new IllegalArgumentException("id must be positive");
    }
    if (seller == null) {
      throw new IllegalArgumentException("seller must not be null");
    }

    Seller newSeller =
        sellerRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Seller not found with id: " + id));

    newSeller.setName(seller.getName());
    newSeller.setContactInfo(seller.getContactInfo());

    try {
      return sellerRepository.save(newSeller);
    } catch (DataAccessException ex) {
      throw ex;
    }
  }

  public void deleteSeller(int id) {
    if (id <= 0) throw new IllegalArgumentException("id must be positive");

    try {
      sellerRepository.deleteById(id);
    } catch (EmptyResultDataAccessException ex) {
      throw new EntityNotFoundException("Seller not found while deleting with id: " + id);
    } catch (DataAccessException ex) {
      throw ex;
    }
  }

  public TopSellerResponse findTopSellerByAmount(LocalDateTime start, LocalDateTime end) {
    if (start == null || end == null) {
      throw new IllegalArgumentException("start and end dates are required");
    }
    if (end.isBefore(start)) {
      throw new IllegalArgumentException("end must be after start");
    }

    Timestamp startTs = toTimestamp(start);
    Timestamp endTs = toTimestamp(end);

    List<Object[]> rows;
    try {
      rows = sellerRepository.findTopSellerByAmount(startTs, endTs);
    } catch (DataAccessException ex) {
      throw ex;
    }

    if (rows == null || rows.isEmpty()) {
      throw new EntityNotFoundException("No transactions found in given period");
    }

    Object[] row = rows.get(0);

    Object idObj = row[0];
    Object totalObj = row[1];

    Integer sellerId = ((Number) idObj).intValue();
    Double total = ((Number) totalObj).doubleValue();

    Seller seller =
        sellerRepository
            .findById(sellerId)
            .orElseThrow(
                () -> new EntityNotFoundException("Seller not found with id: " + sellerId));

    return new TopSellerResponse(seller, total);
  }

  public List<TopSellerResponse> findSellersWithTotalLessThan(
      LocalDateTime start, LocalDateTime end, BigDecimal limit) {
    if (end.isBefore(start)) {
      throw new IllegalArgumentException("end must be after start");
    }

    Timestamp startTs = toTimestamp(start);
    Timestamp endTs = toTimestamp(end);

    List<Object[]> rows;
    try {
      rows = sellerRepository.findSellersWithTotalLessThan(startTs, endTs, limit);
    } catch (DataAccessException ex) {
      throw ex;
    }

    List<Integer> sellerIds =
        rows.stream()
            .map(
                r ->
                    (r[0] instanceof Number)
                        ? ((Number) r[0]).intValue()
                        : Integer.parseInt(r[0].toString()))
            .toList();

    Map<Integer, Seller> sellerMap =
        sellerRepository.findAllById(sellerIds).stream()
            .collect(Collectors.toMap(Seller::getId, s -> s));

    List<TopSellerResponse> result = new ArrayList<>(rows.size());
    for (Object[] row : rows) {
      Object idObj = row[0];
      Object totalObj = row[1];

      Integer sellerId = ((Number) idObj).intValue();
      Double total = ((Number) totalObj).doubleValue();

      Seller seller = sellerMap.get(sellerId);

      if (seller != null) {
        result.add(new TopSellerResponse(seller, total));
      } else {
        Seller stub = new Seller();
        stub.setId(sellerId);
        result.add(new TopSellerResponse(stub, total));
      }
    }

    return result;
  }

  private Timestamp toTimestamp(LocalDateTime ldt) {
    if (ldt == null) throw new IllegalArgumentException("LocalDateTime must not be null");

    ZonedDateTime zdt = ldt.atZone(ZONE);
    return Timestamp.from(zdt.toInstant());
  }
}
