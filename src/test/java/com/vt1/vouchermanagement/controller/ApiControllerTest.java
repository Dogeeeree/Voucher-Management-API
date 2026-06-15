package com.vt1.vouchermanagement.controller;

import com.vt1.vouchermanagement.dto.PageResponse;
import com.vt1.vouchermanagement.dto.UserResponse;
import com.vt1.vouchermanagement.dto.VoucherResponse;
import com.vt1.vouchermanagement.dto.VoucherUsageResponse;
import com.vt1.vouchermanagement.entity.VoucherStatus;
import com.vt1.vouchermanagement.service.UserService;
import com.vt1.vouchermanagement.service.VoucherService;
import com.vt1.vouchermanagement.service.VoucherUsageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({UserController.class, VoucherController.class, VoucherUsageController.class})
class ApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private VoucherService voucherService;

    @MockBean
    private VoucherUsageService voucherUsageService;

    @Test
    void createVoucherReturnsResponseEnvelope() throws Exception {
        VoucherResponse response = new VoucherResponse(
                1L,
                "SALE10",
                10,
                100,
                LocalDate.now().plusDays(1),
                VoucherStatus.ACTIVE,
                LocalDateTime.now()
        );
        when(voucherService.create(any())).thenReturn(response);

        mockMvc.perform(post("/vouchers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "code": "SALE10",
                                  "discountPercent": 10,
                                  "quantity": 100,
                                  "expiredDate": "%s",
                                  "status": "ACTIVE"
                                }
                                """.formatted(LocalDate.now().plusDays(1))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Create voucher successfully"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.code").value("SALE10"));
    }

    @Test
    void createVoucherValidationErrorReturnsFalseEnvelope() throws Exception {
        mockMvc.perform(post("/vouchers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "code": "SALE10",
                                  "discountPercent": 0,
                                  "quantity": 100,
                                  "expiredDate": "%s",
                                  "status": "ACTIVE"
                                }
                                """.formatted(LocalDate.now().plusDays(1))))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void createVoucherMissingBodyReturnsValidationErrorEnvelope() throws Exception {
        mockMvc.perform(post("/vouchers")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Request body is required or invalid"));
    }

    @Test
    void requiredListAndSearchRoutesReturnSuccessfully() throws Exception {
        when(voucherService.findAll(anyInt(), anyInt()))
                .thenReturn(new PageResponse<>(List.of(), 0, 10, 0, 0));
        when(voucherService.searchByCode(anyString(), anyInt(), anyInt()))
                .thenReturn(new PageResponse<>(List.of(), 0, 10, 0, 0));
        when(userService.findAll(anyInt(), anyInt()))
                .thenReturn(new PageResponse<>(List.of(), 0, 10, 0, 0));
        when(voucherUsageService.findAll(anyInt(), anyInt()))
                .thenReturn(new PageResponse<>(List.of(), 0, 10, 0, 0));

        mockMvc.perform(get("/vouchers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        mockMvc.perform(get("/vouchers/search").param("code", "SALE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        mockMvc.perform(get("/voucher-usages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void createUserAndUseVoucherRoutesReturnSuccessfully() throws Exception {
        when(userService.create(any())).thenReturn(new UserResponse(
                1L,
                "Nguyen Van A",
                "a@gmail.com",
                null,
                LocalDateTime.now()
        ));
        when(voucherUsageService.useVoucher(any())).thenReturn(new VoucherUsageResponse(
                1L,
                1L,
                "Nguyen Van A",
                1L,
                "SALE10",
                LocalDateTime.now()
        ));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "fullName": "Nguyen Van A",
                                  "email": "a@gmail.com",
                                  "phone": "0909"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true));

        mockMvc.perform(post("/voucher-usages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "userId": 1,
                                  "voucherId": 1
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
