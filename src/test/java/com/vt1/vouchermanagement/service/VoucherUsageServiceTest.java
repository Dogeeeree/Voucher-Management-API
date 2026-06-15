package com.vt1.vouchermanagement.service;

import com.vt1.vouchermanagement.dto.UseVoucherRequest;
import com.vt1.vouchermanagement.entity.User;
import com.vt1.vouchermanagement.entity.Voucher;
import com.vt1.vouchermanagement.entity.VoucherStatus;
import com.vt1.vouchermanagement.entity.VoucherUsage;
import com.vt1.vouchermanagement.exception.ApiException;
import com.vt1.vouchermanagement.repository.UserRepository;
import com.vt1.vouchermanagement.repository.VoucherRepository;
import com.vt1.vouchermanagement.repository.VoucherUsageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VoucherUsageServiceTest {

    @Mock
    private VoucherUsageRepository voucherUsageRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private VoucherRepository voucherRepository;

    @InjectMocks
    private VoucherUsageService voucherUsageService;

    @Test
    void useVoucherSuccess() {
        User user = user();
        Voucher voucher = voucher(VoucherStatus.ACTIVE, 2, LocalDate.now().plusDays(1));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(voucherRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(voucher));
        when(voucherUsageRepository.save(any(VoucherUsage.class))).thenAnswer(invocation -> invocation.getArgument(0));

        voucherUsageService.useVoucher(new UseVoucherRequest(1L, 1L));

        assertThat(voucher.getQuantity()).isEqualTo(1);
        verify(voucherUsageRepository).save(any(VoucherUsage.class));
    }

    @Test
    void useVoucherRejectsExpiredVoucher() {
        assertVoucherRejected(voucher(VoucherStatus.ACTIVE, 2, LocalDate.now()), "Voucher expired");
    }

    @Test
    void useVoucherRejectsInactiveVoucher() {
        assertVoucherRejected(voucher(VoucherStatus.INACTIVE, 2, LocalDate.now().plusDays(1)), "Voucher inactive");
    }

    @Test
    void useVoucherRejectsZeroQuantity() {
        assertVoucherRejected(voucher(VoucherStatus.ACTIVE, 0, LocalDate.now().plusDays(1)), "Voucher quantity is zero");
    }

    @Test
    void useVoucherRejectsMissingUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> voucherUsageService.useVoucher(new UseVoucherRequest(1L, 1L)))
                .isInstanceOfSatisfying(ApiException.class, exception ->
                        assertThat(exception.getStatus()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void useVoucherRejectsMissingVoucher() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user()));
        when(voucherRepository.findByIdForUpdate(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> voucherUsageService.useVoucher(new UseVoucherRequest(1L, 1L)))
                .isInstanceOfSatisfying(ApiException.class, exception ->
                        assertThat(exception.getStatus()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    private void assertVoucherRejected(Voucher voucher, String message) {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user()));
        when(voucherRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(voucher));

        assertThatThrownBy(() -> voucherUsageService.useVoucher(new UseVoucherRequest(1L, 1L)))
                .isInstanceOfSatisfying(ApiException.class, exception -> {
                    assertThat(exception.getStatus()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(exception.getMessage()).isEqualTo(message);
                });
    }

    private User user() {
        User user = new User();
        user.setId(1L);
        user.setFullName("Nguyen Van A");
        user.setEmail("a@gmail.com");
        return user;
    }

    private Voucher voucher(VoucherStatus status, int quantity, LocalDate expiredDate) {
        Voucher voucher = new Voucher();
        voucher.setId(1L);
        voucher.setCode("SALE10");
        voucher.setStatus(status);
        voucher.setQuantity(quantity);
        voucher.setDiscountPercent(10);
        voucher.setExpiredDate(expiredDate);
        return voucher;
    }
}
