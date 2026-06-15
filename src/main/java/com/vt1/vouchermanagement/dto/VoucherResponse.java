package com.vt1.vouchermanagement.dto;

import com.vt1.vouchermanagement.entity.Voucher;
import com.vt1.vouchermanagement.entity.VoucherStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record VoucherResponse(
        Long id,
        String code,
        Integer discountPercent,
        Integer quantity,
        LocalDate expiredDate,
        VoucherStatus status,
        LocalDateTime createdAt
) {
    public static VoucherResponse from(Voucher voucher) {
        return new VoucherResponse(
                voucher.getId(),
                voucher.getCode(),
                voucher.getDiscountPercent(),
                voucher.getQuantity(),
                voucher.getExpiredDate(),
                voucher.getStatus(),
                voucher.getCreatedAt()
        );
    }
}
