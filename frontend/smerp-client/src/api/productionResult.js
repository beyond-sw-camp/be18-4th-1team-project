import http from "./http";

// 생산 실적 목록 조회
export function fetchProductionResults(page = 0, size = 20, searchTerm = '') {
  return http.get("/api/v1/production-results", { params: { page, size, searchTerm } });
}