// src/api/bom.js
import http from "@/api/http";

/** ===============================
 *  1. 생성 (Create)
 *  =============================== */
// BOM 관계 생성
export function createBom(bomData) {
  return http.post("/api/v1/boms", bomData);
}

/** ===============================
 *  2. 조회 (Read)
 *  =============================== */
// BOM 목록 조회 (페이징, 첫화면)
export function fetchBoms(page = 0, size = 20, filters = {}) {
  return http.get("/api/v1/boms", {
    params: { page, size, ...filters }
  });
}

// BOM 상세 조회
export function fetchBomDetail(bomId, direction = "inbound") {
  return http.get(`/api/v1/boms/${bomId}`, {
    params: { direction }
  });
}

// 정전개, 역전개, 원재료리스트 조회 (선택한 품목 기준)
export const fetchBomAll = async (itemId) => {
  try {
    const response = await http.get(`/api/v1/boms/items/${itemId}/all`);
    // response.data에는 백엔드에서 반환하는 BomAllResponse 객체가 담겨 있습니다.
    return response.data;
  } catch (error) {
    console.error("fetchBomAll failed:", error);
    throw error;
  }
};

// BOM 소요량 산출
export const getBomRequirements = async (itemId) => {
  const response = await http.get(`/api/v1/boms/items/${itemId}/requirements`);
  return response.data;
}

/** ===============================
 *  3. 수정 (Update)
 *  =============================== */
// BOM 수량/비고 수정
export function updateBom(bomId, bomData) {
  return http.patch(`/api/v1/boms/${bomId}`, bomData);
}

// BOM 관계 수정
export function updateBomRelation(bomId, relationData) {
  return http.patch(`/api/v1/boms/${bomId}/relations`, relationData);
}

/** ===============================
 *  4. 삭제 (Delete)
 *  =============================== */
// BOM 삭제
export function deleteBom(bomId) {
  return http.delete(`/api/v1/boms/${bomId}`);
}

// BOM 강제 삭제
export function forceDeleteBom(bomId) {
  return http.delete(`/api/v1/boms/${bomId}/force`);
}

/** ===============================
 *  5. 캐시 (Cache)
 *  =============================== */
// BOM 전체 캐시 재생성
export function rebuildAllBomCache() {
  return http.post("/api/v1/boms/cache/rebuild");
}

// BOM 선택한 품목 캐시 재생성
export function refreshBomCache(itemId) {
  return http.post(`/api/v1/boms/cache/refresh/${itemId}`);
}

