package com.example.services;

import com.example.entities.Seller;
import com.example.repositories.SellerRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SellerService {
    private final SellerRepository sellerRepository;

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
}
