package com.domino.smerp.purchase.purchaseorder.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PurchaseOrderCreateRequest {
    @NotNull(message = "발주 ID는 필수 입력입니다.")
    private final Long roId;

    @NotNull(message = "거래처명은 필수 입력입니다.")
    private final String companyName;    // 거래처명

    @NotNull(message = "일자는 필수 입력입니다.")
    private final String documentNo;

    @NotNull(message = "창고명은 필수 입력입니다.")
    private String warehouseName;

    @Size(max = 100, message = "비고는 최대 100자까지 입력 가능합니다.")
    private final String remark;

    private final LocalDate newDocDate; // 전표번호 자동 생성 시 날짜 기준

}
