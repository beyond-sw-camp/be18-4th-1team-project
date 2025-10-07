import http from "./http";

// 품목 목록 조회
export function fetchItems(page = 0, size = 20, searchTerm = '') {
  return http.get(`/api/v1/items?page=${page}&size=${size}&searchTerm=${searchTerm}`);
}

// 품목 상세 조회
export function fetchItemDetail(itemId) {
  return http.get(`/api/v1/items/${itemId}`);
}

// 품목 생성
export function createItem(itemData) {
  return http.post("/api/v1/items", itemData);
}

// 품목 정보 수정
export function updateItem(itemId, itemData) {
  return http.patch(`/api/v1/items/${itemId}`, itemData);
}

// 품목 삭제
export function deleteItem(itemId) {
  return http.delete(`/api/v1/items/${itemId}`);
}
