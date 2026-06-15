package com.vt1.vouchermanagement.controller;

import com.vt1.vouchermanagement.dto.ApiResponse;
import com.vt1.vouchermanagement.dto.CreateVoucherRequest;
import com.vt1.vouchermanagement.dto.PageResponse;
import com.vt1.vouchermanagement.dto.UpdateVoucherRequest;
import com.vt1.vouchermanagement.dto.VoucherResponse;
import com.vt1.vouchermanagement.service.VoucherService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/vouchers")
public class VoucherController {

    private final VoucherService voucherService;

    public VoucherController(VoucherService voucherService) {
        this.voucherService = voucherService;
    }

    @GetMapping
    public ApiResponse<PageResponse<VoucherResponse>> findAll(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size
    ) {
        return ApiResponse.success("Get vouchers successfully", voucherService.findAll(page, size));
    }

    @GetMapping("/search")
    public ApiResponse<PageResponse<VoucherResponse>> search(
            @RequestParam @NotBlank(message = "Voucher code is required") String code,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size
    ) {
        return ApiResponse.success("Search vouchers successfully", voucherService.searchByCode(code, page, size));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<VoucherResponse>> create(@Valid @RequestBody CreateVoucherRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Create voucher successfully", voucherService.create(request)));
    }

    @PutMapping("/{id}")
    public ApiResponse<VoucherResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateVoucherRequest request
    ) {
        return ApiResponse.success("Update voucher successfully", voucherService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        voucherService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
