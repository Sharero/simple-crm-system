package com.example.controllers.dto.responses;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TopSellerResponse {
    private int id;

    private String name;

    private Double totalAmount;

    public TopSellerResponse(int id, String name, Double totalAmount) {
        this.id = id;
        this.name = name;
        this.totalAmount = totalAmount;
    }
}
