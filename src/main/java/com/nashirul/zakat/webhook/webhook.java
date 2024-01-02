package com.nashirul.zakat.webhook;

import com.nashirul.zakat.service.PaymentTransactionService;
import com.nashirul.zakat.service.XenditService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@Controller
public class webhook {
    private final XenditService xenditService;
    private final PaymentTransactionService paymentTransactionService;

    public webhook(XenditService xenditService, PaymentTransactionService paymentTransactionService) {
        this.xenditService = xenditService;
        this.paymentTransactionService = paymentTransactionService;
    }

    @PostMapping("/xendit-webhook")
    public ResponseEntity<String> handleXenditWebhook(@RequestBody InvoiceModel invoiceModel,
                                              @RequestHeader("x-callback-token") String xCallbackToken) {

        // Validate the signature
        if (xenditService.isValidToken(xCallbackToken)) {
            // update status
            paymentTransactionService.updateStatus(invoiceModel.getExternal_id(), invoiceModel.getStatus());
            System.out.println("Received Xendit Webhook. param1: " + invoiceModel.getExternal_id()
                    + ", param2: " + invoiceModel.getStatus());

            return ResponseEntity.ok("Webhook Received Successfully");
        } else {
            // Invalid signature, possibly not from Xendit
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("invalid signature");
        }
    }
}
