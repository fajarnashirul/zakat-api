package com.nashirul.zakat.service;

import com.nashirul.zakat.dto.PaymentTransactionDto;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public interface DokuService {
      String initiateDokuPayment(PaymentTransactionDto paymentTransactionDto) throws NoSuchAlgorithmException, InvalidKeyException;
      String requestPayment(PaymentTransactionDto paymentTransactionDto) throws IOException;
//    String generateDigest(PaymentTransactionDto paymentTransactionDto) throws NoSuchAlgorithmException;
//    String generateSignature( String digest) throws InvalidKeyException, NoSuchAlgorithmException;
}
