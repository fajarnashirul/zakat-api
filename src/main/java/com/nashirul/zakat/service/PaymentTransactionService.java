package com.nashirul.zakat.service;

import com.nashirul.zakat.dto.PaymentTransactionDto;
import com.xendit.exception.XenditException;

import java.util.List;
import java.util.Map;

public interface PaymentTransactionService {
    Map<String, Object> createPayment (PaymentTransactionDto paymentTransactionDto) throws XenditException;
    List<PaymentTransactionDto> getAllPaymentTransaction();

//    void updateStatus(String id, String Status);
}
