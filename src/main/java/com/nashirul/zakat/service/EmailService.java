package com.nashirul.zakat.service;

import com.nashirul.zakat.dto.PaymentTransactionDto;
import com.nashirul.zakat.entity.PaymentTransaction;
import org.apache.commons.mail.EmailException;

public interface EmailService {
    void sendEmailPaymentUrl (PaymentTransactionDto paymentTransactionDto, String link) throws EmailException;
    void sendEmailPaymentSuccess (PaymentTransaction paymentTransaction) throws EmailException;
}
