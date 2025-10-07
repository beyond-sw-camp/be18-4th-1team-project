import http from "./http";

// 생산계획 목록 조회
export function fetchProductionPlans() {
  return http.get("/api/v1/plans");
}

// 생산계획 상세 조회
export function fetchPlanDetail(planId) {
  return http.get(`/api/v1/plans/${planId}`);
}

// 생산계획 생성
export function createProductionPlan(planData) {
  return http.post("/api/v1/plans", planData);
}

// 생산계획 수정
export function updateProductionPlan(planId, planData) {
  return http.patch(`/api/v1/plans/${planId}`, planData);
}

// 생산계획 삭제
export function deleteProductionPlan(planId) {
  return http.delete(`/api/v1/plans/${planId}`);
}