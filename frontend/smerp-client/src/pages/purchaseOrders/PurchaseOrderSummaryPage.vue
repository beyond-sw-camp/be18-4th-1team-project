<template>
  <div class="app d-flex">
    <Sidebar />
    <div class="flex-grow-1 d-flex flex-column">
      <Header
        :user-name="userStore.name || 'Guest'"
        :breadcrumbs="breadcrumbs"
        @logout="onLogout"
      />
      <main class="content container-fluid py-3">
        <div class="d-flex justify-content-between align-items-center mb-3">
          <h5 class="mb-0">구매 현황</h5>
          <div class="d-flex gap-2">
            <button class="btn btn-success btn-sm" @click="exportToExcel">
              <i class="bi bi-file-earmark-excel"></i> 엑셀로 내보내기
            </button>
          </div>
        </div>

        <PurchaseOrderSummaryTable ref="summaryTableRef" />
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from "vue-router";
import { useUserStore } from "@/stores/user";
import { logout as apiLogout } from "@/api/auth";
import Header from "@/components/layout/Header.vue";
import Sidebar from "@/components/layout/Sidebar.vue";
import PurchaseOrderSummaryTable from '@/components/purchaseOrders/PurchaseOrderSummaryTable.vue';

const userStore = useUserStore();
const router = useRouter();

const summaryTableRef = ref(null);

const breadcrumbs = ref([
  { label: "HOME", to: "/home" },
  { label: "구매 관리" },
  { label: "구매 주문 요약" },
]);

const exportToExcel = () => {
  const summaryData = summaryTableRef.value?.summaryData || [];

  if (summaryData.length === 0) {
    alert('엑셀로 내보낼 데이터가 없습니다.');
    return;
  }

  const headers = ["문서 번호", "거래처명", "품목명", "수량", "입고 단가", "공급 금액", "비고"];
  const rows = summaryData.map(item => [
    item.documentNo,
    item.companyName,
    item.itemName,
    item.qty,
    item.inboundUnitPrice,
    item.supplyAmount,
    item.remark
  ]);

  let csvContent = "data:text/csv;charset=utf-8,\uFEFF" 
    + headers.join(",") + "\n"
    + rows.map(e => e.join(",")).join("\n");

  const encodedUri = encodeURI(csvContent);
  const link = document.createElement("a");
  link.setAttribute("href", encodedUri);
  link.setAttribute("download", "구매_주문_요약.csv");
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
};

async function onLogout() {
  try {
    await apiLogout();
    userStore.clear();
    router.push("/login");
  } catch (error) {
    console.error("Logout failed:", error);
  }
}
</script>

<style scoped>
.app { min-height: 100vh; background: #f2f4f8; }
.content { background: #f2f4f8; min-width: 768px; }
.card {
  border: 0;
  border-radius: 12px;
}
.card-body {
  padding: 24px;
}
.btn-primary {
  background: var(--brand, #2563eb);
  border: none;
  border-radius: 8px;
  padding: 8px 16px;
  font-weight: 600;
}
.btn-primary:hover {
  background: var(--brand-d, #1e40af);
}
</style>