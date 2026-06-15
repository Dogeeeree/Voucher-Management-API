package com.vt1.vouchermanagement.dto;

import com.vt1.vouchermanagement.entity.VoucherUsage;

import java.time.LocalDateTime;

public record VoucherUsageResponse(
        Long id,
        Long userId,
        String userFullName,
        Long voucherId,
        String voucherCode,
        LocalDateTime usedAt
) {
    public static VoucherUsageResponse from(VoucherUsage usage) {
        return new VoucherUsageResponse(
                usage.getId(),
                usage.getUser().getId(),
                usage.getUser().getFullName(),
                usage.getVoucher().getId(),
                usage.getVoucher().getCode(),
                usage.getUsedAt()
        );
    }
}
