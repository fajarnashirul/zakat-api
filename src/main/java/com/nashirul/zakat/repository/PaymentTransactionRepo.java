package com.nashirul.zakat.repository;

import com.nashirul.zakat.entity.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PaymentTransactionRepo extends JpaRepository<PaymentTransaction, UUID> {
    List<PaymentTransaction> findFirst10ByStatusOrderByCreatedAtDesc(String status);
}
