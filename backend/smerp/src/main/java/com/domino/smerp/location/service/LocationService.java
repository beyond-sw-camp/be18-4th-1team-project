package com.domino.smerp.location.service;

import com.domino.smerp.location.dto.request.LocationIdListRequest;
import com.domino.smerp.location.dto.response.LocationIdListResponse;
import com.domino.smerp.location.dto.response.LocationListResponse;

public interface LocationService {

  //칸 생성(창고용)
  void createLocation(final Long warehouseId);

  //빈 칸만 반환(가능 위치 조회)
  LocationListResponse getUnFilledLocations(final Long warehouseId);

  //재고 생성, 수정하는 경우 받은 위치 id들에 해당하는 위치의 칸을 채움
  LocationIdListResponse fillLocations(final LocationIdListRequest locationIdListRequest);

  //재고를 삭제한 경우 받은 위치 id들에 해당하는 위치의 칸을 비움
  LocationIdListResponse unFillLocations(final LocationIdListRequest locationIdListRequest);

  //LocationResponse toLocationResponse(final Location location);

}
