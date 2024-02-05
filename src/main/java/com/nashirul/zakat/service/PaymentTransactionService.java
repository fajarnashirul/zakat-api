package com.nashirul.zakat.service;

import com.nashirul.zakat.dto.PaymentTransactionDto;
import com.xendit.exception.XenditException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface PaymentTransactionService {
    Map<String, Object> createPayment(PaymentTransactionDto paymentTransactionDto) throws XenditException;
    Page<PaymentTransactionDto> getAllPaymentTransaction(String status, Date start, Date end, Pageable pageable);
    List<PaymentTransactionDto> get10PaymentTransactionByStatus();
    void updateStatus(String id, String Status);
    Map<String, Object>getTransactionMetric(Date start, Date end);
}
