package com.vt1.vouchermanagement.repository;

import com.vt1.vouchermanagement.entity.VoucherUsage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoucherUsageRepository extends JpaRepository<VoucherUsage, Long> {

    boolean existsByVoucher_Id(Long voucherId);
}
