import http from "./http";

// 구매 등록
export function createPurchaseOrder(orderData) {
  return http.post("/api/v1/purchase-orders", orderData);
}

// 구매 목록 조회
export function fetchPurchaseOrders(page = 0, size = 20, searchTerm = '') {
  return http.get("/api/v1/purchase-orders", { params: { page, size, searchTerm } });
}

// 구매 상세 조회
export function fetchPurchaseOrderDetail(poId) {
  return http.get(`/api/v1/purchase-orders/${poId}`);
}

// 구매 수정
export function updatePurchaseOrder(poId, orderData) {
  return http.patch(`/api/v1/purchase-orders/${poId}`, orderData);
}

// 구매 주문 요약 조회
export function fetchPurchaseOrderSummary() {
  return http.get("/api/v1/purchase-orders/summary");
}

