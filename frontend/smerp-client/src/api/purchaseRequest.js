import http from "./http";

// 구매 요청 등록
export function createPurchaseRequest(requestData) {
  return http.post("/api/v1/request-purchase-orders", requestData);
}

// 구매 요청 목록 조회
export function fetchPurchaseRequests(page = 0, size = 20, searchTerm = '') {
  return http.get("/api/v1/request-purchase-orders", { params: { page, size, searchTerm } });
}
