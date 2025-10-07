import http from "./http";

// 창고 목록 조회
export function fetchWarehouses() {
  return http.get("/api/v1/warehouses");
}

// 창고 상세 조회
export function fetchWarehouseDetail(warehouseId) {
  return http.get(`/api/v1/warehouses/${warehouseId}`);
}

// 창고 생성
export function createWarehouse(warehouseData) {
  return http.post("/api/v1/warehouses", warehouseData);
}

// 창고 정보 수정
export function updateWarehouse(warehouseId, warehouseData) {
  return http.patch(`/api/v1/warehouses/${warehouseId}`, warehouseData);
}

// 창고 삭제
export function deleteWarehouse(warehouseId) {
  return http.delete(`/api/v1/warehouses/${warehouseId}`);
}
