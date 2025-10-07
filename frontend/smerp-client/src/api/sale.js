import http from "./http";

export function fetchSales(page = 0, size = 20) {
  return http.get("/api/v1/sales-orders", {
    params: {
      page,
      size,
    },
  });
}

export function fetchSaleOrderDetail(soId) {
  return http.get(`/api/v1/sales-orders/${soId}`);
}

export function createSale(saleData) {
  return http.post("/api/v1/sales-orders", saleData);
}

export function deleteSale(documentNo) {
  return http.delete(`/api/v1/sales-orders/${documentNo}`);
}

export function getSaleSummary() {
  return http.get("/api/v1/sales-orders/summary");
}

export function updateSale(soId, saleData) {
  return http.patch(`/api/v1/sales-orders/${soId}`, saleData);
}