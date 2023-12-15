package com.nashirul.zakat.service.implemetation;

import com.nashirul.zakat.dto.PaymentTransactionDto;
import com.nashirul.zakat.entity.PaymentTransaction;
import com.nashirul.zakat.repository.PaymentTransactionRepo;
import com.nashirul.zakat.service.PaymentTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PaymentTransactionImpl implements PaymentTransactionService {
    private final PaymentTransactionRepo paymentTransactionRepo;

    @Autowired
    public PaymentTransactionImpl(PaymentTransactionRepo paymentTransactionRepo) {
        this.paymentTransactionRepo = paymentTransactionRepo;
    }

    public PaymentTransactionDto createPayment(PaymentTransactionDto paymentTransactionDto){
        PaymentTransaction paymentTransaction = new PaymentTransaction();
//        paymentTransaction.setId(UUID.randomUUID());
        paymentTransaction.setName(paymentTransactionDto.getName());
        paymentTransaction.setEmail(paymentTransactionDto.getEmail());
        paymentTransaction.setNoWhatsApp(paymentTransactionDto.getNoWhatsApp());
        paymentTransaction.setAmount(paymentTransactionDto.getAmount());
        paymentTransaction.setType(paymentTransactionDto.getType());
        paymentTransaction.setStatus("PENDING");
        paymentTransaction.setCreatedAt(new Date());

        return mapToPaymentTransactionDto(paymentTransactionRepo.save(paymentTransaction));

    }
    public List<PaymentTransactionDto> getAllPaymentTransaction(){

        List<PaymentTransaction> paymentTransactions = paymentTransactionRepo.findAll();
        return paymentTransactions.stream().map(this::mapToPaymentTransactionDto).collect(Collectors.toList());
    }

    private PaymentTransactionDto mapToPaymentTransactionDto(PaymentTransaction paymentTransaction){
        PaymentTransactionDto paymentTransactionDto = new PaymentTransactionDto();
        paymentTransactionDto.setId(paymentTransaction.getId());
        paymentTransactionDto.setName(paymentTransaction.getName());
        paymentTransactionDto.setEmail(paymentTransaction.getEmail());
        paymentTransactionDto.setAmount(paymentTransaction.getAmount());
        paymentTransactionDto.setNoWhatsApp(paymentTransaction.getNoWhatsApp());
        paymentTransactionDto.setStatus(paymentTransaction.getStatus());
        paymentTransactionDto.setCreatedAt(paymentTransaction.getCreatedAt());
        paymentTransactionDto.setType(paymentTransaction.getType());

        return paymentTransactionDto;
    }
}
