package com.nashirul.zakat.dto;

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
public class PaymentTransactionDto {
    private UUID id;
    private String name;
    private String email;
    private String noWhatsApp;
    private String type;
    private Long amount;
    private Date createdAt;
    private String Status;
}
