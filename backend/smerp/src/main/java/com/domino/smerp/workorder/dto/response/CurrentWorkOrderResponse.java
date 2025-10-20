package com.domino.smerp.workorder.dto.response;

import com.domino.smerp.workorder.constants.Status;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CurrentWorkOrderResponse {

    private final Long id;

    // 일자 no
    @NotNull private final String documentNo;

    // 거래처명
    private final String companyName;

    // 생산 담당자
    private final String userName;

    // 계획된 작업일
    private final Instant planAt;

    // 상태
    @NotNull private final Status status;

    // 품목명
    private final String itemName;

    // 계획수량
    @NotNull private final BigDecimal planQty;
}
