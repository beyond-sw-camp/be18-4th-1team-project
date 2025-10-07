import http from "./http";

// 로그 목록 조회
export function fetchLogs() {
  return http.get("/api/v1/logs");
}

// 로그 상세 조회
export function fetchLogDetail(logId) {
  return http.get(`/api/v1/logs/${logId}`);
}
