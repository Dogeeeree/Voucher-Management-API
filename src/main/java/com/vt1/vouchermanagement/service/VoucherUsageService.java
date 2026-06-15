package com.vt1.vouchermanagement.service;

import com.vt1.vouchermanagement.dto.PageResponse;
import com.vt1.vouchermanagement.dto.UseVoucherRequest;
import com.vt1.vouchermanagement.dto.VoucherUsageResponse;
import com.vt1.vouchermanagement.entity.User;
import com.vt1.vouchermanagement.entity.Voucher;
import com.vt1.vouchermanagement.entity.VoucherStatus;
import com.vt1.vouchermanagement.entity.VoucherUsage;
import com.vt1.vouchermanagement.exception.ApiException;
import com.vt1.vouchermanagement.repository.UserRepository;
import com.vt1.vouchermanagement.repository.VoucherRepository;
import com.vt1.vouchermanagement.repository.VoucherUsageRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class VoucherUsageService {

    private final VoucherUsageRepository voucherUsageRepository;
    private final UserRepository userRepository;
    private final VoucherRepository voucherRepository;

    public VoucherUsageService(
            VoucherUsageRepository voucherUsageRepository,
            UserRepository userRepository,
            VoucherRepository voucherRepository
    ) {
        this.voucherUsageRepository = voucherUsageRepository;
        this.userRepository = userRepository;
        this.voucherRepository = voucherRepository;
    }

    @Transactional(readOnly = true)
    public PageResponse<VoucherUsageResponse> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return PageResponse.from(voucherUsageRepository.findAll(pageable).map(VoucherUsageResponse::from));
    }

    @Transactional
    public VoucherUsageResponse useVoucher(UseVoucherRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));

        Voucher voucher = voucherRepository.findByIdForUpdate(request.voucherId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Voucher not found"));

        validateUsable(voucher);

        voucher.setQuantity(voucher.getQuantity() - 1);

        VoucherUsage usage = new VoucherUsage();
        usage.setUser(user);
        usage.setVoucher(voucher);

        return VoucherUsageResponse.from(voucherUsageRepository.save(usage));
    }

    private void validateUsable(Voucher voucher) {
        if (voucher.getExpiredDate().isBefore(LocalDate.now()) || voucher.getExpiredDate().isEqual(LocalDate.now())) {
            throw new ApiException(HttpStatus.CONFLICT, "Voucher expired");
        }
        if (voucher.getStatus() == VoucherStatus.INACTIVE) {
            throw new ApiException(HttpStatus.CONFLICT, "Voucher inactive");
        }
        if (voucher.getQuantity() <= 0) {
            throw new ApiException(HttpStatus.CONFLICT, "Voucher quantity is zero");
        }
    }
}
