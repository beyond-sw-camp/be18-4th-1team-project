import http from "./http";

// 거래처 목록 조회
export function fetchClients(page = 0, size = 20, searchTerm = '') {
  return http.get("/api/v1/clients", { params: { page, size, searchTerm } });
}

// 거래처 상세 조회
export function fetchClientDetail(clientId) {
  return http.get(`/api/v1/clients/${clientId}`);
}

// 거래처 정보 수정
export function updateClient(clientId, clientData) {
  return http.patch(`/api/v1/clients/${clientId}`, clientData);
}

// 거래처 삭제
export function deleteClient(clientId) {
  return http.delete(`/api/v1/clients/${clientId}`);
}
