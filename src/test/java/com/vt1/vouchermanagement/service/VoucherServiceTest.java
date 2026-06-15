package com.vt1.vouchermanagement.service;

import com.vt1.vouchermanagement.dto.CreateVoucherRequest;
import com.vt1.vouchermanagement.dto.UpdateVoucherRequest;
import com.vt1.vouchermanagement.dto.VoucherResponse;
import com.vt1.vouchermanagement.entity.Voucher;
import com.vt1.vouchermanagement.entity.VoucherStatus;
import com.vt1.vouchermanagement.exception.ApiException;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VoucherServiceTest {

    @Mock
    private VoucherRepository voucherRepository;

    @Mock
    private VoucherUsageRepository voucherUsageRepository;

    @InjectMocks
    private VoucherService voucherService;

    @Test
    void createSuccess() {
        CreateVoucherRequest request = validCreateRequest();
        when(voucherRepository.existsByCodeIgnoreCase("SALE10")).thenReturn(false);
        when(voucherRepository.save(any(Voucher.class))).thenAnswer(invocation -> {
            Voucher voucher = invocation.getArgument(0);
            voucher.setId(1L);
            return voucher;
        });

        VoucherResponse response = voucherService.create(request);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.code()).isEqualTo("SALE10");
    }

    @Test
    void createRejectsDuplicateCode() {
        CreateVoucherRequest request = validCreateRequest();
        when(voucherRepository.existsByCodeIgnoreCase("SALE10")).thenReturn(true);

        assertThatThrownBy(() -> voucherService.create(request))
                .isInstanceOfSatisfying(ApiException.class, exception ->
                        assertThat(exception.getStatus()).isEqualTo(HttpStatus.CONFLICT));
    }

    @Test
    void createRejectsInvalidDiscount() {
        CreateVoucherRequest request = new CreateVoucherRequest("SALE10", 101, 10, LocalDate.now().plusDays(1), VoucherStatus.ACTIVE);

        assertThatThrownBy(() -> voucherService.create(request))
                .isInstanceOfSatisfying(ApiException.class, exception ->
                        assertThat(exception.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY));
    }

    @Test
    void createRejectsInvalidQuantity() {
        CreateVoucherRequest request = new CreateVoucherRequest("SALE10", 10, -1, LocalDate.now().plusDays(1), VoucherStatus.ACTIVE);

        assertThatThrownBy(() -> voucherService.create(request))
                .isInstanceOfSatisfying(ApiException.class, exception ->
                        assertThat(exception.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY));
    }

    @Test
    void createRejectsExpiredDateNotFuture() {
        CreateVoucherRequest request = new CreateVoucherRequest("SALE10", 10, 10, LocalDate.now(), VoucherStatus.ACTIVE);

        assertThatThrownBy(() -> voucherService.create(request))
                .isInstanceOfSatisfying(ApiException.class, exception ->
                        assertThat(exception.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY));
    }

    @Test
    void updateRejectsNotFound() {
        UpdateVoucherRequest request = new UpdateVoucherRequest("SALE10", 10, 10, LocalDate.now().plusDays(1), VoucherStatus.ACTIVE);
        when(voucherRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> voucherService.update(99L, request))
                .isInstanceOfSatisfying(ApiException.class, exception ->
                        assertThat(exception.getStatus()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void deleteRejectsNotFound() {
        when(voucherRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> voucherService.delete(99L))
                .isInstanceOfSatisfying(ApiException.class, exception ->
                        assertThat(exception.getStatus()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    private CreateVoucherRequest validCreateRequest() {
        return new CreateVoucherRequest("sale10", 10, 10, LocalDate.now().plusDays(1), VoucherStatus.ACTIVE);
    }
}
