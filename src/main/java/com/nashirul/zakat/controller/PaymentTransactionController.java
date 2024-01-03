package com.nashirul.zakat.controller;

import com.nashirul.zakat.dto.PaymentTransactionDto;
import com.nashirul.zakat.service.DokuService;
import com.nashirul.zakat.service.PaymentTransactionService;
import com.nashirul.zakat.service.XenditService;
import com.xendit.exception.XenditException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Controller
public class PaymentTransactionController {

    @Autowired
    private final PaymentTransactionService paymentTransactionService;
    @Autowired
    private final DokuService dokuService;
//    private final XenditService xenditService;

    public PaymentTransactionController(PaymentTransactionService paymentTransactionService, DokuService dokuService, XenditService xenditService) {
        this.paymentTransactionService = paymentTransactionService;
        this.dokuService = dokuService;
//        this.xenditService = xenditService;
    }

    @PostMapping(path = "/payment/add", consumes = "application/json")
    public ResponseEntity<Map<String, Object>> createPayments (@RequestBody PaymentTransactionDto paymentTransactionDto) throws XenditException {
        Map<String, Object> payment = paymentTransactionService.createPayment(paymentTransactionDto);
//        Map<String, Object> response = new HashMap<>();
        if (payment != null) {
            return ResponseEntity.ok(payment);
        } else {
            return (ResponseEntity<Map<String, Object>>) ResponseEntity.status(HttpStatus.BAD_GATEWAY);
        }
    }

    @GetMapping(path = "/payment")
    public ResponseEntity<Page<PaymentTransactionDto>> getAllPayments(@RequestParam(required = false) String status,
                                                                      @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
                                                                      @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date end,
                                                                      @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)Pageable pageable){
        Page<PaymentTransactionDto> response = paymentTransactionService.getAllPaymentTransaction(status, start, end, pageable);

        return ResponseEntity.ok(response);
    }
    @GetMapping(path = "/payment/customer")
    public ResponseEntity<List<PaymentTransactionDto>> getFirst10Payments(){
        List<PaymentTransactionDto> response = paymentTransactionService.get10PaymentTransactionByStatus();

        return ResponseEntity.ok(response);
    }
    @PostMapping(path = "/payment/doku", consumes = "application/json")
    public ResponseEntity<String> generateSignature(@RequestBody PaymentTransactionDto paymentTransactionDto) throws NoSuchAlgorithmException, InvalidKeyException, IOException {

        paymentTransactionDto.setId(UUID.randomUUID());
        String result = dokuService.requestPayment(paymentTransactionDto);
        return ResponseEntity.ok(result);
    }

//    @PostMapping(path = "/payment/xendit", consumes = "application/json")
//    public ResponseEntity<Map<String, Object>> createPayout(@RequestBody PaymentTransactionDto paymentTransactionDto) throws XenditException {
//
//        PaymentTransactionDto invoice = paymentTransactionService.createPayment(paymentTransactionDto);
//        String result = xenditService.createInvoice(paymentTransactionDto);
//        return ResponseEntity.ok(result);
//    }

}
