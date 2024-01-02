package com.nashirul.zakat.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "paymentTransaction")
public class PaymentTransaction {
    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;
    @Column(name = "name")
    private String name;
    @Column(name = "email")
    private String email;
    @Column(name = "noWhatsApp")
    private String noWhatsApp;
    @Column(name = "type")
    private String type;
    @Column(name = "amount")
    private Long amount;
    @Column(name = "createdAt")
    private Date createdAt;
    @Column(name = "status")
    private String status;
}
