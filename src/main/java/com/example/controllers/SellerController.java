package com.example.controllers;

import com.example.controllers.dto.requests.TopSellerRequest;
import com.example.controllers.dto.responses.TopSellerResponse;
import com.example.entities.Seller;
import com.example.services.SellerService;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    @GetMapping("/top-seller")
    public ResponseEntity<TopSellerResponse> findTopSellerByAmount(@RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
                                                                   @RequestParam("end")   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        TopSellerResponse seller = sellerService.findTopSellerByAmount(start, end);
        if (seller == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(seller);
    }

    @GetMapping("/sellers-below")
    public ResponseEntity<List<TopSellerResponse>> getSellersBelow(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam("end")   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @RequestParam("limit") BigDecimal limit) {

        List<TopSellerResponse> list = sellerService.findSellersWithTotalLessThan(start, end, limit);
        if (list.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(list);
    }
}
