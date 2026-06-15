package com.vt1.vouchermanagement.service;

import com.vt1.vouchermanagement.dto.CreateVoucherRequest;
import com.vt1.vouchermanagement.dto.PageResponse;
import com.vt1.vouchermanagement.dto.UpdateVoucherRequest;
import com.vt1.vouchermanagement.dto.VoucherResponse;
import com.vt1.vouchermanagement.entity.Voucher;
import com.vt1.vouchermanagement.exception.ApiException;
import com.vt1.vouchermanagement.repository.VoucherRepository;
import com.vt1.vouchermanagement.repository.VoucherUsageRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class VoucherService {

    private final VoucherRepository voucherRepository;
    private final VoucherUsageRepository voucherUsageRepository;

    public VoucherService(VoucherRepository voucherRepository, VoucherUsageRepository voucherUsageRepository) {
        this.voucherRepository = voucherRepository;
        this.voucherUsageRepository = voucherUsageRepository;
    }

    @Transactional(readOnly = true)
    public PageResponse<VoucherResponse> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return PageResponse.from(voucherRepository.findAll(pageable).map(VoucherResponse::from));
    }

    @Transactional(readOnly = true)
    public PageResponse<VoucherResponse> searchByCode(String code, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return PageResponse.from(
                voucherRepository.findByCodeContainingIgnoreCase(code.trim(), pageable).map(VoucherResponse::from)
        );
    }

    @Transactional
    public VoucherResponse create(CreateVoucherRequest request) {
        validateVoucherFields(request.discountPercent(), request.quantity(), request.expiredDate());

        String code = request.code().trim().toUpperCase();
        if (voucherRepository.existsByCodeIgnoreCase(code)) {
            throw new ApiException(HttpStatus.CONFLICT, "Voucher code already exists");
        }

        Voucher voucher = new Voucher();
        voucher.setCode(code);
        voucher.setDiscountPercent(request.discountPercent());
        voucher.setQuantity(request.quantity());
        voucher.setExpiredDate(request.expiredDate());
        voucher.setStatus(request.status());

        return VoucherResponse.from(voucherRepository.save(voucher));
    }

    @Transactional
    public VoucherResponse update(Long id, UpdateVoucherRequest request) {
        validateVoucherFields(request.discountPercent(), request.quantity(), request.expiredDate());

        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Voucher not found"));

        String code = request.code().trim().toUpperCase();
        if (voucherRepository.existsByCodeIgnoreCaseAndIdNot(code, id)) {
            throw new ApiException(HttpStatus.CONFLICT, "Voucher code already exists");
        }

        voucher.setCode(code);
        voucher.setDiscountPercent(request.discountPercent());
        voucher.setQuantity(request.quantity());
        voucher.setExpiredDate(request.expiredDate());
        voucher.setStatus(request.status());

        return VoucherResponse.from(voucherRepository.save(voucher));
    }

    @Transactional
    public void delete(Long id) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Voucher not found"));

        if (voucherUsageRepository.existsByVoucher_Id(id)) {
            throw new ApiException(HttpStatus.CONFLICT, "Cannot delete voucher with usage history");
        }

        voucherRepository.delete(voucher);
    }

    private void validateVoucherFields(Integer discountPercent, Integer quantity, LocalDate expiredDate) {
        if (discountPercent == null || discountPercent < 1 || discountPercent > 100) {
            throw new ApiException(HttpStatus.UNPROCESSABLE_ENTITY, "Discount percent must be from 1 to 100");
        }
        if (quantity == null || quantity < 0) {
            throw new ApiException(HttpStatus.UNPROCESSABLE_ENTITY, "Quantity must be greater than or equal to 0");
        }
        if (expiredDate == null || !expiredDate.isAfter(LocalDate.now())) {
            throw new ApiException(HttpStatus.UNPROCESSABLE_ENTITY, "Expired date must be greater than current date");
        }
    }
}
