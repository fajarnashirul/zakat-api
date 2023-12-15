package com.nashirul.zakat.service;

import com.nashirul.zakat.dto.PaymentTransactionDto;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public interface DokuService {
      String initiateDokuPayment(PaymentTransactionDto paymentTransactionDto) throws NoSuchAlgorithmException, InvalidKeyException;
//    String generateDigest(PaymentTransactionDto paymentTransactionDto) throws NoSuchAlgorithmException;
//    String generateSignature( String digest) throws InvalidKeyException, NoSuchAlgorithmException;
}
