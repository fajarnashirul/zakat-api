package com.nashirul.zakat.controller;

import com.nashirul.zakat.dto.PaymentTransactionDto;
import com.nashirul.zakat.service.PaymentTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class PaymentTransactionController {

    @Autowired
    private final PaymentTransactionService paymentTransactionService;

    public PaymentTransactionController(PaymentTransactionService paymentTransactionService) {
        this.paymentTransactionService = paymentTransactionService;
    }

    @PostMapping(path = "/payment/add", consumes = "application/json")
    public ResponseEntity<Map<String, Object>> createPayments (@RequestBody PaymentTransactionDto paymentTransactionDto){
        PaymentTransactionDto payment = paymentTransactionService.createPayment(paymentTransactionDto);
        Map<String, Object> response = new HashMap<>();
        if (payment != null) {
            response.put("result", payment);
            response.put("message", "segera bayar woy");
        } else {
            response.put("result", null);
            response.put("message", "terjadi error");
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/payment")
    public ResponseEntity<List<PaymentTransactionDto>> getAllPayments(){
        List<PaymentTransactionDto> response = paymentTransactionService.getAllPaymentTransaction();

        return ResponseEntity.ok(response);
    }
}
