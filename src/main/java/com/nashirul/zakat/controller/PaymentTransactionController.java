package com.nashirul.zakat.controller;

import com.nashirul.zakat.dto.PaymentTransactionDto;
import com.nashirul.zakat.service.DokuService;
import com.nashirul.zakat.service.EmailService;
import com.nashirul.zakat.service.PaymentTransactionService;
import com.nashirul.zakat.service.XenditService;
import com.xendit.exception.XenditException;
import org.apache.commons.mail.EmailException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Controller
@RequestMapping("/payment")
public class PaymentTransactionController {

    @Autowired
    private final PaymentTransactionService paymentTransactionService;
    @Autowired
    private final DokuService dokuService;
    @Autowired
    private final EmailService emailService;

    public PaymentTransactionController(PaymentTransactionService paymentTransactionService, DokuService dokuService, XenditService xenditService, EmailService emailService) {
        this.paymentTransactionService = paymentTransactionService;
        this.dokuService = dokuService;
        this.emailService = emailService;
    }

    @PostMapping(path = "/add", consumes = "application/json")
    public ResponseEntity<Map<String, Object>> createPayments (@RequestBody PaymentTransactionDto paymentTransactionDto) throws XenditException {
        Map<String, Object> payment = paymentTransactionService.createPayment(paymentTransactionDto);

        if (payment != null) {
            try{
                paymentTransactionDto.setId((UUID) payment.get("paymentTransaction.id"));
                emailService.sendEmailPaymentUrl(paymentTransactionDto, payment.get("payment url").toString());
                return ResponseEntity.ok(payment);
            }catch (EmailException e){
                payment.put("message", "Error sending email");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(payment);
            }
        } else {
            Map<String, Object> message = new HashMap<>();
            message.put("message", "Error creating payment: Invalid payment details");
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(message);
        }
    }

    @GetMapping(path = "/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<PaymentTransactionDto>> getAllPayments(@RequestParam(required = false) String status,
                                                                      @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
                                                                      @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date end,
                                                                      @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)Pageable pageable){
        Page<PaymentTransactionDto> response = paymentTransactionService.getAllPaymentTransaction(status, start, end, pageable);

        return ResponseEntity.ok(response);
    }
    @GetMapping(path = "")
    public ResponseEntity<List<PaymentTransactionDto>> getFirst10Payments(){
        List<PaymentTransactionDto> response = paymentTransactionService.get10PaymentTransactionByStatus();

        return ResponseEntity.ok(response);
    }
//    @PostMapping(path = "/payment/doku", consumes = "application/json")
//    public ResponseEntity<String> generateSignature(@RequestBody PaymentTransactionDto paymentTransactionDto) throws IOException {
//
//        paymentTransactionDto.setId(UUID.randomUUID());
//        String result = dokuService.requestPayment(paymentTransactionDto);
//        return ResponseEntity.ok(result);
//    }

}
