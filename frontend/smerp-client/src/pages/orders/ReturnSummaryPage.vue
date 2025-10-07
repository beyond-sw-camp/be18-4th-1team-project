<template>
  <div class="app d-flex">
    <Sidebar @select="onSelect" />

    <!-- 우측 컨텐츠 -->
    <div class="flex-grow-1 d-flex flex-column">
      <Header :user-name="userStore.name || 'Guest'" :breadcrumbs="breadcrumbs" @logout="onLogout" />

      <main class="content container-fluid py-3">
        <div class="d-flex justify-content-between align-items-center mb-4">
          <h5 class="fw-bold mb-0">반품 현황</h5>
          
          <button class="btn btn-success" @click="exportToExcel">
            <i class="bi bi-file-earmark-excel"></i> 엑셀로 내보내기
          </button>
        </div>

        <ReturnSummaryTable
          :returnSummaryData="returnSummaryData"
          :loading="loading"
          :error="error"
          :formatCurrency="formatCurrency"
        />
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { fetchReturnSummary } from '@/api/order';
import Sidebar from '@/components/layout/Sidebar.vue';
import Header from '@/components/layout/Header.vue';
import ReturnSummaryTable from '@/components/orders/ReturnSummaryTable.vue';
import { useUserStore } from "@/stores/user";
import { logout as apiLogout } from "@/api/auth";

const userStore = useUserStore();
const router = useRouter();

const returnSummaryData = ref([]);
const loading = ref(true);
const error = ref(null);

const breadcrumbs = ref([
  { label: "HOME", to: "/home" },
  { label: "영업 관리", to: "#" },
  { label: "반품 현황", to: "/orders/returns/summary" },
]);

function onSelect({ section, item }) {
  if (item?.to) router.push(item.to);

  breadcrumbs.value = [
    { label: "HOME", to: "/home" },
    { label: section.label, to: "#" },
    { label: item.label, to: item.to },
  ];
}

async function onLogout() {
  try { await apiLogout(); } catch { }
  userStore.clear();
  router.replace("/login");
}

const loadReturnSummary = async () => {
  loading.value = true;
  error.value = null;
  try {
    const response = await fetchReturnSummary();
    returnSummaryData.value = response.data;
  } catch (e) {
    console.error('Error fetching return summary:', e);
    error.value = '반품 현황 정보를 불러오는 데 실패했습니다.';
  } finally {
    loading.value = false;
  }
};

function formatCurrency(amount) {
  if (amount === null || amount === undefined) return '';
  return new Intl.NumberFormat('ko-KR', { style: 'currency', currency: 'KRW' }).format(amount);
}

function exportToExcel() {
  const headers = ["반품 전표번호", "회사명", "품목명", "수량", "단가", "환불 금액", "비고"];
  const rows = returnSummaryData.value.map(item => [
    item.documentNo,
    item.companyName,
    item.itemName,
    item.qty,
    item.specialPrice,
    item.refundAmount,
    item.remark
  ]);

  let csvContent = "data:text/csv;charset=utf-8,\uFEFF"
    + headers.join(",") + "\n"
    + rows.map(e => e.join(",")).join("\n");

  const encodedUri = encodeURI(csvContent);
  const link = document.createElement("a");
  link.setAttribute("href", encodedUri);
  link.setAttribute("download", "반품_현황.csv");
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
}

onMounted(() => {
  loadReturnSummary();
});
</script>

<style scoped>
.app {
  min-height: 100vh;
  background: #f2f4f8;
}
.content {
  background: #f2f4f8;
}
.card {
  border: 0;
  border-radius: 12px;
  background: #fff;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}
.card-body {
  padding: 24px;
}
.table-responsive {
  margin-top: 1rem;
}
.table th,
.table td {
  white-space: nowrap;
  text-align: center;
}
.table thead th {
  background-color: #f8f9fa;
  font-weight: 600;
  color: #334155;
}
</style>