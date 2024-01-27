package com.nashirul.zakat.service.implemetation;

import com.nashirul.zakat.dto.PaymentTransactionDto;
import com.nashirul.zakat.entity.PaymentTransaction;
import com.nashirul.zakat.repository.PaymentTransactionRepo;
import com.nashirul.zakat.service.EmailService;
import com.nashirul.zakat.service.PaymentTransactionService;
import com.nashirul.zakat.service.XenditService;
import com.xendit.exception.XenditException;
import org.apache.commons.mail.EmailException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PaymentTransactionImpl implements PaymentTransactionService {
    private final PaymentTransactionRepo paymentTransactionRepo;
    private final XenditService xenditService;
    private final EmailService emailService;

    @Autowired
    public PaymentTransactionImpl(PaymentTransactionRepo paymentTransactionRepo, XenditService xenditService, EmailService emailService) {
        this.paymentTransactionRepo = paymentTransactionRepo;
        this.xenditService = xenditService;
        this.emailService = emailService;
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
    public Page<PaymentTransactionDto> getAllPaymentTransaction(String status, Date start, Date end, Pageable pageable){

        if (status != null) {
            if (start != null && end != null) {
                return findByStatusAndCreatedAt(status, start, end, pageable);
            }else {
                return findByStatus(status, pageable);
            }
        } else if (start != null && end != null) {
            return findByCreatedAt( start, end, pageable);
        } else {
            return findAll(pageable);
        }
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
                    //send email notification for success payment
                    try {
                        emailService.sendEmailPaymentSuccess(paymentTransaction);
                    } catch (EmailException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    private Page<PaymentTransactionDto> findByStatusAndCreatedAt(String status, Date start, Date end, Pageable pageable) {
        Page<PaymentTransaction> paymentTransactions = paymentTransactionRepo.findByStatusAndCreatedAtBetween(status, start, end, pageable);
        if (paymentTransactions.isEmpty()){
            throw new NoSuchElementException("No payments transaction found");
        }
        return paymentTransactions.map(this::mapToPaymentTransactionDtoAdmin);
    }
    private Page<PaymentTransactionDto> findByStatus(String status, Pageable pageable) {
        Page<PaymentTransaction> paymentTransactions = paymentTransactionRepo.findByStatus(status, pageable);
        if (paymentTransactions.isEmpty()){
            throw new NoSuchElementException("No payments transaction found");
        }
        return paymentTransactions.map(this::mapToPaymentTransactionDtoAdmin);
    }

    private Page<PaymentTransactionDto> findByCreatedAt(Date start, Date end, Pageable pageable) {
        Page<PaymentTransaction> paymentTransactions = paymentTransactionRepo.findByCreatedAtBetween(start, end, pageable);
        if (paymentTransactions.isEmpty()){
            throw new NoSuchElementException("No payments transaction found");
        }
        return paymentTransactions.map(this::mapToPaymentTransactionDtoAdmin);
    }

    private Page<PaymentTransactionDto> findAll(Pageable pageable) {
        Page<PaymentTransaction> paymentTransactions = paymentTransactionRepo.findAll(pageable);
        if (paymentTransactions.isEmpty()){
            throw new NoSuchElementException("No payments transaction found");
        }
        return paymentTransactions.map(this::mapToPaymentTransactionDtoAdmin);
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
