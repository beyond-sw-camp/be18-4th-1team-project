import http from "./http";

// 발주 등록
export function createRequestOrder(orderData) {
  return http.post("/api/v1/request-orders", orderData);
}

// 발주 목록 조회
export function fetchRequestOrders(page = 0, size = 20, searchTerm = '') {
  return http.get("/api/v1/request-orders", { params: { page, size, searchTerm } });
}

// 발주 상세 조회
export function fetchRequestOrderDetail(roId) {
  return http.get(`/api/v1/request-orders/${roId}`);
}

// 발주 수정
export function updateRequestOrder(roId, orderData) {
  return http.patch(`/api/v1/request-orders/${roId}`, orderData);
}

// 발주 삭제
export function deleteRequestOrder(roId) {
  return http.delete(`/api/v1/request-orders/${roId}`);
}