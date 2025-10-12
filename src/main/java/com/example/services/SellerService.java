package com.example.services;

import com.example.controllers.dto.requests.TopSellerRequest;
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

    public Seller findTopSellerByAmount(TopSellerRequest topSellerRequest) {
        Timestamp startTs = toTimestamp(topSellerRequest.getStartPeriod());
        Timestamp endTs = toTimestamp(topSellerRequest.getEndPeriod());

        Seller top = sellerRepository.findTopSellerByAmount(startTs, endTs);
        return top;
    }

    private Timestamp toTimestamp(java.time.LocalDateTime ldt) {
        ZonedDateTime zdt = ldt.atZone(ZONE);
        return Timestamp.from(zdt.toInstant());
    }
}
