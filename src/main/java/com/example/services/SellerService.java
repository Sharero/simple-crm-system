package com.example.services;

import com.example.controllers.dto.requests.TopSellerRequest;
import com.example.controllers.dto.responses.TopSellerResponse;
import com.example.entities.Seller;
import com.example.repositories.SellerRepository;

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
        return sellerRepository.findAll();
    }

    public Optional<Seller> getSellerById(int id) {
        return sellerRepository.findById(id);
    }

    public Seller createSeller(Seller seller) {
        seller.setRegistrationDate(LocalDateTime.now());
        return sellerRepository.save(seller);
    }

    public Seller updateSeller(int id, Seller seller) {
        seller.setId(id);
        return sellerRepository.save(seller);
    }

    public void deleteSeller(int id) {
        sellerRepository.deleteById(id);
    }

    public TopSellerResponse findTopSellerByAmount(LocalDateTime start,
                                                   LocalDateTime end) {
        Timestamp startTs = toTimestamp(start);
        Timestamp endTs = toTimestamp(end);

        List<Object[]> rows = sellerRepository.findTopSellerByAmount(startTs, endTs);

        Object[] row = rows.get(0);
        Object idObj = row[0];
        Object totalObj = row.length > 1 ? row[1] : null;

        Integer sellerId;
        if (idObj instanceof Number) {
            sellerId = ((Number) idObj).intValue();
        } else {
            sellerId = Integer.parseInt(idObj.toString());
        }

        Double total = null;
        if (totalObj != null) {
            if (totalObj instanceof Number) total = ((Number) totalObj).doubleValue();
            else total = Double.parseDouble(totalObj.toString());
        }

        Optional<Seller> sellerOpt = sellerRepository.findById(sellerId);

        Seller seller = sellerOpt.get();
        return new TopSellerResponse(seller, total);
    }

    public List<TopSellerResponse> findSellersWithTotalLessThan(LocalDateTime start,
                                                                LocalDateTime end,
                                                                BigDecimal limit) {
        Timestamp startTs = toTimestamp(start);
        Timestamp endTs = toTimestamp(end);

        List<Object[]> rows = sellerRepository.findSellersWithTotalLessThan(startTs, endTs, limit);

        List<Integer> ids = rows.stream()
                .map(r -> {
                    Object idObj = r[0];
                    if (idObj instanceof Number) return ((Number) idObj).intValue();
                    return Integer.parseInt(Objects.toString(idObj));
                })
                .collect(Collectors.toList());

        List<Seller> sellers = sellerRepository.findAllById(ids);
        Map<Integer, Seller> sellerMap = sellers.stream()
                .collect(Collectors.toMap(Seller::getId, s -> s));

        List<TopSellerResponse> result = new ArrayList<>(rows.size());
        for (Object[] row : rows) {
            Integer id = (row[0] instanceof Number) ? ((Number) row[0]).intValue() : Integer.parseInt(Objects.toString(row[0]));
            Object totalObj = row.length > 1 ? row[1] : null;

            Double total = null;
            if (totalObj != null) {
                if (totalObj instanceof Number) total = ((Number) totalObj).doubleValue();
                else if (totalObj instanceof BigDecimal) total = ((BigDecimal) totalObj).doubleValue();
                else total = Double.parseDouble(totalObj.toString());
            } else {
                total = 0.0;
            }

            Seller seller = sellerMap.get(id);
            if (seller != null) {
                result.add(new TopSellerResponse(seller, total));
            } else {
                Seller stub = new Seller();
                stub.setId(id);
                result.add(new TopSellerResponse(stub, total));
            }
        }

        return result;
    }

    private Timestamp toTimestamp(LocalDateTime ldt) {
        ZonedDateTime zdt = ldt.atZone(ZONE);
        return Timestamp.from(zdt.toInstant());
    }


}
