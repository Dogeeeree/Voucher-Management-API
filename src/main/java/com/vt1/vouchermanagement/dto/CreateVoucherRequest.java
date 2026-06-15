package com.vt1.vouchermanagement.dto;

import com.vt1.vouchermanagement.entity.VoucherStatus;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateVoucherRequest(
        @NotBlank(message = "Voucher code is required")
        @Size(max = 50, message = "Voucher code must be at most 50 characters")
        String code,

        @NotNull(message = "Discount percent is required")
        @Min(value = 1, message = "Discount percent must be from 1 to 100")
        @Max(value = 100, message = "Discount percent must be from 1 to 100")
        Integer discountPercent,

        @NotNull(message = "Quantity is required")
        @Min(value = 0, message = "Quantity must be greater than or equal to 0")
        Integer quantity,

        @NotNull(message = "Expired date is required")
        @Future(message = "Expired date must be greater than current date")
        LocalDate expiredDate,

        @NotNull(message = "Status is required")
        VoucherStatus status
) {
}
