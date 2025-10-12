package com.example.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "sellers")
@Getter
@Setter
@NoArgsConstructor
public class Seller {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(name = "name")
  private String name;

  @Column(name = "contact_info")
  private String contactInfo;

  @Column(name = "registration_date")
  private LocalDateTime registrationDate;

  @JsonIgnore
  @OneToMany(mappedBy = "seller")
  private List<Transaction> transactions;

  public Seller(String name, String contactInfo) {
    this.name = name;
    this.contactInfo = contactInfo;
  }
}
