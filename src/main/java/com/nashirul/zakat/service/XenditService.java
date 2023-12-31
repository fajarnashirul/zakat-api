package com.nashirul.zakat.service;

import com.nashirul.zakat.dto.PaymentTransactionDto;
import com.xendit.exception.XenditException;

public interface XenditService {
    String createInvoice(PaymentTransactionDto paymentTransactionDto) throws XenditException;
    Boolean isValidToken(String token);
}
