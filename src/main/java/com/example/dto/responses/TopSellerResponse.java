package com.example.dto.responses;

import com.example.entities.Seller;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TopSellerResponse {
  private Seller seller;

  private Double totalAmount;

  public TopSellerResponse(Seller seller, Double totalAmount) {
    this.seller = seller;
    this.totalAmount = totalAmount;
  }
}
