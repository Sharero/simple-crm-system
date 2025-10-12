package com.example.controllers;

import com.example.dto.responses.TopSellerResponse;
import com.example.entities.Seller;
import com.example.services.SellerService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/sellers")
@Validated
public class SellerController {
  private final SellerService sellerService;

  public SellerController(SellerService sellerService) {
    this.sellerService = sellerService;
  }

  // GET /sellers
  @GetMapping
  public ResponseEntity<List<Seller>> getAllSellers() {
    List<Seller> list = sellerService.getAllSellers();
    return ResponseEntity.ok(list);
  }

  // GET /sellers/{id}
  @GetMapping("/{id}")
  public ResponseEntity<Seller> getSellerById(@PathVariable @Min(1) int id) {
    return sellerService
        .getSellerById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  // POST /sellers
  @PostMapping
  public ResponseEntity<Seller> createSeller(@Valid @RequestBody Seller seller) {
    Seller createdSeller = sellerService.createSeller(seller);

    URI location =
        ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(createdSeller.getId())
            .toUri();
    return ResponseEntity.created(location).body(createdSeller);
  }

  // PUT /sellers/{id}
  @PutMapping("/{id}")
  public ResponseEntity<Seller> updateSeller(
      @PathVariable @Min(1) int id, @Valid @RequestBody Seller seller) {
    Seller updatedSeller = sellerService.updateSeller(id, seller);
    return ResponseEntity.ok(updatedSeller);
  }

  // DELETE /sellers/{id}
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteSeller(@PathVariable @Min(1) int id) {
    sellerService.deleteSeller(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/top-seller")
  public ResponseEntity<TopSellerResponse> findTopSellerByAmount(
      @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
          LocalDateTime start,
      @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
    TopSellerResponse seller = sellerService.findTopSellerByAmount(start, end);
    if (seller == null) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok(seller);
  }

  @GetMapping("/sellers-below")
  public ResponseEntity<List<TopSellerResponse>> getSellersBelow(
      @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
          LocalDateTime start,
      @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
      @RequestParam("limit") BigDecimal limit) {

    List<TopSellerResponse> list = sellerService.findSellersWithTotalLessThan(start, end, limit);
    if (list.isEmpty()) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok(list);
  }
}
