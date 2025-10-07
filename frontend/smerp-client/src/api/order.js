import http from "./http";

export function fetchOrders(page = 0, size = 20) {
  return http.get("/api/v1/orders", {
    params: {
      page,
      size,
    },
  });
}

export function fetchOrderDetail(documentNo) {
  return http.get(`/api/v1/orders/${documentNo}`);
}

export function getOrderSummary() {
  return http.get("/api/v1/orders/summary");
}

export function createOrder(orderData) {
  return http.post("/api/v1/orders", orderData);
}

export function deleteOrder(orderId) {
  return http.delete(`/api/v1/orders/${orderId}`);
}

export function fetchReturnSummary() {
  return http.get("/api/v1/orders/returns/summary");
}

export function createReturn(returnData) {
  return http.post("/api/v1/orders/returns", returnData);
}

export function updateOrder(orderId, orderData) {
  return http.patch(`/api/v1/orders/${orderId}`, orderData);
}
