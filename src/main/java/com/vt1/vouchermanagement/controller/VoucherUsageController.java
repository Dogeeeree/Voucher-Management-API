package com.vt1.vouchermanagement.controller;

import com.vt1.vouchermanagement.dto.ApiResponse;
import com.vt1.vouchermanagement.dto.PageResponse;
import com.vt1.vouchermanagement.dto.UseVoucherRequest;
import com.vt1.vouchermanagement.dto.VoucherUsageResponse;
import com.vt1.vouchermanagement.service.VoucherUsageService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;

@Validated
@RestController
@RequestMapping("/voucher-usages")
public class VoucherUsageController {

    private final VoucherUsageService voucherUsageService;

    public VoucherUsageController(VoucherUsageService voucherUsageService) {
        this.voucherUsageService = voucherUsageService;
    }

    @GetMapping
    public ApiResponse<PageResponse<VoucherUsageResponse>> findAll(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size
    ) {
        return ApiResponse.success("Get voucher usages successfully", voucherUsageService.findAll(page, size));
    }

    @PostMapping
    public ApiResponse<VoucherUsageResponse> useVoucher(@Valid @RequestBody UseVoucherRequest request) {
        return ApiResponse.success("Use voucher successfully", voucherUsageService.useVoucher(request));
    }
}
