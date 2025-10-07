package com.domino.smerp.bom.dto.response;

import com.domino.smerp.bom.entity.Bom;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class BomDetailResponse {

  private Long bomId;
  private Long parentItemId;
  private String parentItemName;
  private Long childItemId;
  private String childItemName;
  private String remark;

  public static BomDetailResponse fromEntity(final Bom bom) {
    return BomDetailResponse.builder()
        .bomId(bom.getBomId())
        .parentItemId(bom.getParentItem().getItemId())
        .parentItemName(bom.getParentItem().getName())
        .childItemId(bom.getChildItem().getItemId())
        .childItemName(bom.getChildItem().getName())
        .remark(bom.getRemark())
        .build();
  }
}
