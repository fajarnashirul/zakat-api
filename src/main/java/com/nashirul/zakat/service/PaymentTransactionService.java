package com.nashirul.zakat.service;

import com.nashirul.zakat.dto.PaymentTransactionDto;
import com.nashirul.zakat.entity.PaymentTransaction;

import java.util.List;

public interface PaymentTransactionService {
    PaymentTransactionDto createPayment (PaymentTransactionDto paymentTransactionDto);
    List<PaymentTransactionDto> getAllPaymentTransaction();
}
