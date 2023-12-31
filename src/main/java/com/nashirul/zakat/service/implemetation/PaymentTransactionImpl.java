package com.nashirul.zakat.service.implemetation;

import com.nashirul.zakat.dto.PaymentTransactionDto;
import com.nashirul.zakat.entity.PaymentTransaction;
import com.nashirul.zakat.repository.PaymentTransactionRepo;
import com.nashirul.zakat.service.PaymentTransactionService;
import com.nashirul.zakat.service.XenditService;
import com.xendit.exception.XenditException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PaymentTransactionImpl implements PaymentTransactionService {
    private final PaymentTransactionRepo paymentTransactionRepo;
    private final XenditService xenditService;

    @Autowired
    public PaymentTransactionImpl(PaymentTransactionRepo paymentTransactionRepo, XenditService xenditService) {
        this.paymentTransactionRepo = paymentTransactionRepo;
        this.xenditService = xenditService;
    }

    public Map<String, Object> createPayment(PaymentTransactionDto paymentTransactionDto) throws XenditException {
        PaymentTransaction paymentTransaction = new PaymentTransaction();
        paymentTransaction.setName(paymentTransactionDto.getName());
        paymentTransaction.setEmail(paymentTransactionDto.getEmail());
        paymentTransaction.setNoWhatsApp(paymentTransactionDto.getNoWhatsApp());
        paymentTransaction.setAmount(paymentTransactionDto.getAmount());
        paymentTransaction.setType(paymentTransactionDto.getType());
        paymentTransaction.setStatus("PENDING");
        paymentTransaction.setCreatedAt(new Date());
        paymentTransactionRepo.save(paymentTransaction);

        try {
            //create payment url from with xendit
            String paymentUrl = xenditService.createInvoice(paymentTransaction);
            //map for payment and payment url
            Map<String, Object> payment = new HashMap<>();
            payment.put("paymentTransaction", mapToPaymentTransactionDto(paymentTransaction));
            payment.put("payment url", paymentUrl);
            return payment;
        }catch (XenditException error){
            paymentTransactionRepo.delete(paymentTransaction);
            return null;
        }
    }
    public List<PaymentTransactionDto> getAllPaymentTransaction(){

        List<com.nashirul.zakat.entity.PaymentTransaction> paymentTransactions = paymentTransactionRepo.findAll();
        return paymentTransactions.stream().map(this::mapToPaymentTransactionDto).collect(Collectors.toList());
    }

    private PaymentTransactionDto mapToPaymentTransactionDto(com.nashirul.zakat.entity.PaymentTransaction paymentTransaction){
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
