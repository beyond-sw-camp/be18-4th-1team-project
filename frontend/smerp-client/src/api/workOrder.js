import http from "./http";

// 작업 지시 등록
export function createWorkOrder(workOrderData) {
  return http.post("/api/v1/work-orders", workOrderData);
}

// 작업 지시 목록 조회
export function fetchWorkOrders(page = 0, size = 20, searchTerm = '') {
  return http.get("/api/v1/work-orders", { params: { page, size, searchTerm } });
}

// 작업 지시 상세 조회
export function fetchWorkOrderDetail(id) {
  return http.get(`/api/v1/work-orders/${id}`);
}

// 작업 지시 수정
export function updateWorkOrder(id, workOrderData) {
  return http.patch(`/api/v1/work-orders/${id}`, workOrderData);
}

// 작업 지시 삭제
export function deleteWorkOrder(id) {
  return http.delete(`/api/v1/work-orders/${id}`);
}