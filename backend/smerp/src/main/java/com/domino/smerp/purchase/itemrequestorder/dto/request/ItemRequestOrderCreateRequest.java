package com.domino.smerp.purchase.itemrequestorder.dto.request;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class ItemRequestOrderCreateRequest {
    private final Long itemId; // 품목 FK
    private final BigDecimal qty; // 수량
    private final BigDecimal specialPrice; // 요청 시 선택 입력
}
