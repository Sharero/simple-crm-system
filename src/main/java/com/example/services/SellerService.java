package com.example.services;

import com.example.controllers.dto.requests.TopSellerRequest;
import com.example.controllers.dto.responses.TopSellerResponse;
import com.example.entities.Seller;
import com.example.repositories.SellerRepository;

import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

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

    public TopSellerResponse findTopSellerByAmount(TopSellerRequest topSellerRequest) {
        Timestamp startTs = toTimestamp(topSellerRequest.getStartPeriod());
        Timestamp endTs = toTimestamp(topSellerRequest.getEndPeriod());

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

    private Timestamp toTimestamp(java.time.LocalDateTime ldt) {
        ZonedDateTime zdt = ldt.atZone(ZONE);
        return Timestamp.from(zdt.toInstant());
    }
}
