import { createRouter, createWebHistory } from "vue-router";
import { useUserStore } from "@/stores/user";
import MyInfoPage from "@/pages/MyInfoPage.vue";

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: "/", redirect: "/login" },
    { path: "/login", component: () => import("@/pages/Login.vue"), meta: { public: true } },
    { path: "/home", component: () => import("@/pages/Main.vue"), meta: { requiresAuth: true } },
    { path: "/basic", component: () => import("@/pages/workstation/Basic.vue") },
    { path: "/marketing", component: () => import("@/pages/workstation/Sales.vue") },
    { path: "/production", component: () => import("@/pages/workstation/Production.vue") },
    { path: "/plans", component: () => import("@/pages/plans/PlanPage.vue"), meta: { requiresAuth: true } },
    { path: "/plans/:planId", name: "PlanDetail", component: () => import("@/pages/plans/PlanDetailPage.vue"), props: true },
    { path: "/reports", component: () => import("@/pages/workstation/Reports.vue") },
    { path: "/purchase", component: () => import("@/pages/workstation/Purchase.vue") },
    { path: "/request-orders", component: () => import("@/pages/requestOrders/RequestOrderPage.vue"), meta: { requiresAuth: true } },
    { path: "/request-orders/:roId", name: "RequestOrderDetail", component: () => import("@/pages/requestOrders/RequestOrderDetailPage.vue"), props: true },
    { path: "/purchase-requests", component: () => import("@/pages/purchaseRequests/PurchaseRequestPage.vue"), meta: { requiresAuth: true } },
    { path: "/purchase-orders", component: () => import("@/pages/purchaseOrders/PurchaseOrderPage.vue"), meta: { requiresAuth: true } },
    { path: "/purchase-orders/:poId", name: "PurchaseOrderDetail", component: () => import("@/pages/purchaseOrders/PurchaseOrderDetailPage.vue"), props: true, meta: { requiresAuth: true } },
    { path: "/purchase-orders/summary", component: () => import("@/pages/purchaseOrders/PurchaseOrderSummaryPage.vue"), meta: { requiresAuth: true } },
    { path: "/work-orders", component: () => import("@/pages/workOrders/WorkOrderPage.vue"), meta: { requiresAuth: true } },
    { path: "/work-orders/status", component: () => import("@/pages/workOrders/WorkOrderStatusPage.vue"), meta: { requiresAuth: true } },
    { path: "/work-orders/:id", name: "WorkOrderDetail", component: () => import("@/pages/workOrders/WorkOrderDetailPage.vue"), props: true, meta: { requiresAuth: true } },
    { path: "/production-results", component: () => import("@/pages/productionResults/ProductionResultPage.vue"), meta: { requiresAuth: true } },
    { path: "/sales", component: () => import("@/pages/sales/SalePage.vue"), meta: { requiresAuth: true } },
    { path: "/sales/summary", component: () => import("@/pages/sales/SaleSummaryPage.vue"), meta: { requiresAuth: true } },
    { path: "/sales/:soId", name: "SaleOrderDetail", component: () => import("@/pages/sales/SaleOrderDetailPage.vue"), props: true },
    { path: "/users", component: () => import("@/pages/users/UserPage.vue") },
    { path: "/users/:userId", name: "UserDetail", component: () => import("@/pages/users/UserDetailPage.vue"), props: true },
    { path: "/clients", component: () => import("@/pages/clients/ClientPage.vue") },
    { path: "/clients/:clientId", name: "ClientDetail", component: () => import("@/pages/clients/ClientDetailPage.vue"), props: true },
    { path: "/logs", component: () => import("@/pages/logs/LogPage.vue") },
    { path: "/logs/:logId", name: "LogDetail", component: () => import("@/pages/logs/LogDetailPage.vue"), props: true },
    { path: "/items", component: () => import("@/pages/items/ItemPage.vue") },
    { path: "/items/:itemId", name: "ItemDetail", component: () => import("@/pages/items/ItemDetailPage.vue"), props: true },
    { path: "/boms", component: () => import("@/pages/boms/BomPage.vue"), meta: { requiresAuth: true } },
    { path: "/boms/:bomId", name: "BomDetail", component: () => import("@/pages/boms/BomDetailPage.vue"), props: true, meta: { requiresAuth: true } },
    { path: "/boms/requirements", component: () => import("@/pages/boms/BomRequirementPage.vue"), meta: { requiresAuth: true }},
    { path: "/warehouses", component: () => import("@/pages/warehouses/WarehousePage.vue") },
    { path: "/warehouses/:warehouseId", name: "WarehouseDetail", component: () => import("@/pages/warehouses/WarehouseDetailPage.vue"), props: true },
    { path: "/lots", component: () => import("@/pages/lots/LotPage.vue") },
    { path: "/lots/:lotId", name: "LotDetail", component: () => import("@/pages/lots/LotDetailPage.vue"), props: true },
    { path: "/orders", component: () => import("@/pages/orders/OrderPage.vue") },
    { path: "/orders/summary", component: () => import("@/pages/orders/OrderSummaryPage.vue"), meta: { requiresAuth: true } },
    { path: "/orders/returns/summary", component: () => import("@/pages/orders/ReturnSummaryPage.vue"), meta: { requiresAuth: true } },
    { path: "/orders/returns", component: () => import("@/pages/orders/ReturnPage.vue"), meta: { requiresAuth: true } },
    { path: "/orders/:orderId", name: "OrderDetail", component: () => import("@/pages/orders/OrderDetailPage.vue"), props: true },
    
    { path: "/my-info", component: MyInfoPage },
    { path: "/:pathMatch(.*)*", redirect: "/home" },
  ],
});

router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore();

  if (!to.meta.public && !userStore.loaded) {
    await userStore.loadUser();
  }

  const isAuthed = !!userStore.name;

  if (!isAuthed && !to.meta.public) {
    return next({ path: "/login", query: { redirect: to.fullPath } });
  }

  if (isAuthed && to.path === "/login") {

    return next("/home");
  }

  next();
});

export default router;