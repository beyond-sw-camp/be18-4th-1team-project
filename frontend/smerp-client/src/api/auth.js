import http from "./http";

// 로그인 (세션 생성)
export function login(payload) {
  return http.post("/api/v1/auth/login", payload);
}

// 현재 로그인 사용자 조회
export function fetchMe() {
  return http.get("/api/v1/auth/me");
}

// 로그아웃 (세션 만료)
export function logout() {
  return http.post("/api/v1/auth/logout");
}