package com.nashirul.zakat.service.implemetation;

import com.nashirul.zakat.entity.PaymentTransaction;
import com.nashirul.zakat.service.XenditService;
import com.xendit.Xendit;
import com.xendit.exception.XenditException;
import com.xendit.model.Invoice;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class XenditImpl implements XenditService {
    @Value("${xendit.api-key}")
    private String apiKey;
    @Value("${xendit.valid-token}")
    private String validToken;
    @Override
    public String createInvoice(PaymentTransaction paymentTransaction) throws XenditException {
        Xendit.Opt.setApiKey(apiKey);
        Map<String, Object> params = new HashMap<>();
        params.put("external_id", paymentTransaction.getId());
        params.put("amount", paymentTransaction.getAmount());
        params.put("payer_email", paymentTransaction.getEmail());
        params.put("description", "Invoice Demo #123");
        /* Without client */
        Invoice invoice = Invoice.create(params);
        return invoice.getInvoiceUrl();
    }

    @Override
    public Boolean isValidToken(String token) {
        return Objects.equals(token, validToken);
    }
}
