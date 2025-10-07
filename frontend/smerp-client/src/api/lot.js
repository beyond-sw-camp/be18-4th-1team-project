import http from "./http";

// 로트 목록 조회 (페이지네이션)
export function fetchLots(page = 0, size = 20) {
  return http.get("/api/v1/lots", {
    params: {
      page,
      size,
    },
  });
}

// 로트 생성
export function createLot(lotData) {
  return http.post("/api/v1/lots", lotData);
}

// 로트 상세 조회
export function fetchLotDetail(lotId) {
  return http.get(`/api/v1/lots/${lotId}`);
}

// 로트 정보 수정
export function updateLot(lotId, lotData) {
  return http.patch(`/api/v1/lots/${lotId}`, lotData);
}

// 로트 삭제
export function deleteLot(lotId) {
  return http.delete(`/api/v1/lots/${lotId}`);
}
