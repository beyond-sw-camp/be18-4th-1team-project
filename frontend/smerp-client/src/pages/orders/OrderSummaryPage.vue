<template>
  <div class="app d-flex">
    <Sidebar @select="onSelect" />

    <!-- 우측 컨텐츠 -->
    <div class="flex-grow-1 d-flex flex-column">
      <Header :user-name="userStore.name || 'Guest'" :breadcrumbs="breadcrumbs" @logout="onLogout" />

      <main class="content container-fluid py-3">
        <div class="d-flex justify-content-between align-items-center mb-4">
          <h5 class="fw-bold mb-0">주문 현황</h5>
          <button class="btn btn-success" @click="exportToExcel">
            <i class="bi bi-file-earmark-excel"></i> 엑셀로 내보내기
          </button>
        </div>

        <div v-if="loading" class="text-center">
          <div class="spinner-border" role="status">
            <span class="visually-hidden">Loading...</span>
          </div>
        </div>

        <div v-else-if="error" class="alert alert-danger">
          {{ error }}
        </div>

        <div v-else>
          <div class="card shadow-sm">
            <div class="card-body">
              <div class="table-responsive">
                <table class="table table-hover align-middle">
                  <thead>
                    <tr>
                      <th scope="col">문서번호</th>
                      <th scope="col">회사명</th>
                      <th scope="col">품목명</th>
                      <th scope="col">수량</th>
                      <th scope="col">단가</th>
                      <th scope="col">공급가액</th>
                      <th scope="col">비고</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-if="summaryData.length === 0">
                      <td colspan="7" class="text-center text-muted">주문 현황 정보가 없습니다.</td>
                    </tr>
                    <tr v-for="(item, index) in summaryData" :key="index">
                      <td>{{ item.documentNo }}</td>
                      <td>{{ item.companyName }}</td>
                      <td>{{ item.itemName }}</td>
                      <td>{{ item.qty }}</td>
                      <td>{{ formatCurrency(item.specialPrice) }}</td>
                      <td>{{ formatCurrency(item.supplyAmount) }}</td>
                      <td>{{ item.remark }}</td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { getOrderSummary } from '@/api/order';
import Sidebar from '@/components/layout/Sidebar.vue';
import Header from '@/components/layout/Header.vue';
import { useUserStore } from "@/stores/user";
import { logout as apiLogout } from "@/api/auth";

const userStore = useUserStore();
const router = useRouter();

const summaryData = ref([]);
const loading = ref(true);
const error = ref(null);

const breadcrumbs = ref([
  { label: "HOME", to: "/home" },
  { label: "영업 관리", to: "#" },
  { label: "주문 현황", to: "/orders/summary" },
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

const fetchOrderSummary = async () => {
  loading.value = true;
  error.value = null;
  try {
    const response = await getOrderSummary();
    summaryData.value = response.data;
  } catch (e) {
    console.error('Error fetching order summary:', e);
    error.value = '주문 현황 정보를 불러오는 데 실패했습니다.';
  } finally {
    loading.value = false;
  }
};

function formatCurrency(amount) {
  if (amount === null || amount === undefined) return '';
  return new Intl.NumberFormat('ko-KR', { style: 'currency', currency: 'KRW' }).format(amount);
}

function exportToExcel() {
  const headers = ["문서번호", "회사명", "품목명", "수량", "단가", "공급가액", "비고"];
  const rows = summaryData.value.map(item => [
    item.documentNo,
    item.companyName,
    item.itemName,
    item.qty,
    item.specialPrice,
    item.supplyAmount,
    item.remark
  ]);

  let csvContent = "data:text/csv;charset=utf-8,\uFEFF" // Add BOM for Korean characters
    + headers.join(",") + "\n"
    + rows.map(e => e.join(",")).join("\n");

  const encodedUri = encodeURI(csvContent);
  const link = document.createElement("a");
  link.setAttribute("href", encodedUri);
  link.setAttribute("download", "주문_현황.csv");
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
}

onMounted(() => {
  fetchOrderSummary();
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