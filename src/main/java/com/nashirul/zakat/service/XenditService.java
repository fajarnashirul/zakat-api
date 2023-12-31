package com.nashirul.zakat.service;

import com.nashirul.zakat.entity.PaymentTransaction;
import com.xendit.exception.XenditException;

public interface XenditService {
    String createInvoice(PaymentTransaction paymentTransactionDto) throws XenditException;
    Boolean isValidToken(String token);
}
