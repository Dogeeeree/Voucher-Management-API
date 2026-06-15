package com.vt1.vouchermanagement.dto;

import jakarta.validation.constraints.NotNull;

public record UseVoucherRequest(
        @NotNull(message = "User id is required")
        Long userId,

        @NotNull(message = "Voucher id is required")
        Long voucherId
) {
}
