package com.nashirul.zakat.repository;

import com.nashirul.zakat.entity.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentTransactionRepo extends JpaRepository<PaymentTransaction, String> {
}
