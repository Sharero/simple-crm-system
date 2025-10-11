package com.example.controllers;

import com.example.entities.Seller;
import com.example.services.SellerService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sellers")
public class SellerController {
    private final SellerService sellerService;

    public SellerController(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    // GET /sellers
    @GetMapping
    public List<Seller> getAllSellers() {
        return sellerService.getAllSellers();
    }

    // GET /sellers/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Seller> getSellerById(@PathVariable int id) {
        return sellerService.getSellerById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /sellers
    @PostMapping
    public Seller createSeller(@RequestBody Seller seller) {
        return sellerService.createSeller(seller);
    }

    // PUT /sellers/{id}
    @PutMapping("/{id}")
    public Seller updateSeller(@PathVariable int id, @RequestBody Seller seller) {
        return sellerService.updateSeller(id, seller);
    }

    // DELETE /sellers/{id}
    @DeleteMapping("/{id}")
    public void deleteSeller(@PathVariable int id) {
        sellerService.deleteSeller(id);
    }
}
