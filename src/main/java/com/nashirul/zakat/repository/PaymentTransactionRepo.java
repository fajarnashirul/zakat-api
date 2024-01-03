package com.nashirul.zakat.repository;

import com.nashirul.zakat.entity.PaymentTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface PaymentTransactionRepo extends JpaRepository<PaymentTransaction, UUID> {
    List<PaymentTransaction> findFirst10ByStatusOrderByCreatedAtDesc(String status);
    Page<PaymentTransaction> findByStatusAndCreatedAtBetween(String status, Date start, Date end, Pageable pageable);
    Page<PaymentTransaction> findByStatus(String status, Pageable pageable);
    Page<PaymentTransaction> findByCreatedAtBetween(Date start, Date end, Pageable pageable);
    Page<PaymentTransaction> findAll(Pageable pageable);
}
