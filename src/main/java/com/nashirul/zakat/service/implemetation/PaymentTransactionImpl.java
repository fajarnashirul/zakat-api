package com.nashirul.zakat.service.implemetation;

import com.nashirul.zakat.dto.PaymentTransactionDto;
import com.nashirul.zakat.entity.PaymentTransaction;
import com.nashirul.zakat.repository.PaymentTransactionRepo;
import com.nashirul.zakat.service.PaymentTransactionService;
import com.nashirul.zakat.service.XenditService;
import com.xendit.exception.XenditException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
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
            payment.put("paymentTransaction", mapToPaymentTransactionDtoAdmin(paymentTransaction));
            payment.put("payment url", paymentUrl);
            return payment;
        }catch (XenditException error){
            paymentTransactionRepo.delete(paymentTransaction);
            return null;
        }
    }
    public List<PaymentTransactionDto> getAllPaymentTransaction(){

        List<PaymentTransaction> paymentTransactions = paymentTransactionRepo.findAll();
        return paymentTransactions.stream().map(this::mapToPaymentTransactionDtoAdmin).collect(Collectors.toList());
    }

    public List<PaymentTransactionDto> get10PaymentTransactionByStatus(){
        String status = "paid";
        List<PaymentTransaction> paymentTransactions = paymentTransactionRepo.findFirst10ByStatusOrderByCreatedAtDesc(status);
        return paymentTransactions.stream().map(this::mapToPaymentTransactionCustomerDto).collect(Collectors.toList());
    }

    @Override
    public void updateStatus(String id, String status) {
        paymentTransactionRepo.findById(UUID.fromString(id))
                .ifPresent(paymentTransaction -> {
                    paymentTransaction.setStatus(status);
                    paymentTransactionRepo.save(paymentTransaction);
                });
    }

    private PaymentTransactionDto mapToPaymentTransactionCustomerDto(PaymentTransaction paymentTransaction){
        PaymentTransactionDto paymentTransactionDto = new PaymentTransactionDto();
        paymentTransactionDto.setId(paymentTransaction.getId());
        paymentTransactionDto.setName(paymentTransaction.getName());
        paymentTransactionDto.setAmount(paymentTransaction.getAmount());
        paymentTransactionDto.setCreatedAt(paymentTransaction.getCreatedAt());
        paymentTransactionDto.setType(paymentTransaction.getType());

        return paymentTransactionDto;
    }

    private PaymentTransactionDto mapToPaymentTransactionDtoAdmin(PaymentTransaction paymentTransaction){
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
