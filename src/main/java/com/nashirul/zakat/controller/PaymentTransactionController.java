package com.nashirul.zakat.controller;

import com.nashirul.zakat.dto.PaymentTransactionDto;
import com.nashirul.zakat.service.DokuService;
import com.nashirul.zakat.service.PaymentTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class PaymentTransactionController {

    @Autowired
    private final PaymentTransactionService paymentTransactionService;
    @Autowired
    private final DokuService dokuService;

    public PaymentTransactionController(PaymentTransactionService paymentTransactionService, DokuService dokuService) {
        this.paymentTransactionService = paymentTransactionService;
        this.dokuService = dokuService;
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
    @PostMapping(path = "/payment/doku", consumes = "application/json")
    public ResponseEntity<String> generateSignature(@RequestBody PaymentTransactionDto paymentTransactionDto) throws NoSuchAlgorithmException, InvalidKeyException {

        String result = dokuService.initiateDokuPayment(paymentTransactionDto);
        return ResponseEntity.ok(result);
    }

}
