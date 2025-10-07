<template>
  <div class="card shadow-sm mt-4">
    <div class="card-body">
      <div v-if="isLoading" class="text-center my-5">
        <div class="spinner-border" role="status">
          <span class="visually-hidden">Loading...</span>
        </div>
        <p class="mt-2">데이터를 불러오는 중입니다...</p>
      </div>

      <div v-else-if="error" class="alert alert-danger mt-4" role="alert">
        {{ error }}
      </div>

      <div v-else-if="summaryData && summaryData.length > 0">
        <table class="table table-hover">
          <thead>
            <tr>
              <th>문서 번호</th>
              <th>거래처명</th>
              <th>품목명</th>
              <th>수량</th>
              <th>입고 단가</th>
              <th>공급 금액</th>
              <th>비고</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(item, index) in summaryData" :key="index">
              <td>{{ item.documentNo }}</td>
              <td>{{ item.companyName }}</td>
              <td>{{ item.itemName }}</td>
              <td>{{ item.qty }}</td>
              <td>{{ formatCurrency(item.inboundUnitPrice) }}</td>
              <td>{{ formatCurrency(item.supplyAmount) }}</td>
              <td>{{ item.remark }}</td>
            </tr>
          </tbody>
        </table>
      </div>

      <div v-else class="text-center text-muted mt-4">
        <p>조회된 구매 주문 요약 데이터가 없습니다.</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, defineExpose } from 'vue';
import { fetchPurchaseOrderSummary } from '@/api/purchaseOrder';

const summaryData = ref([]);
const isLoading = ref(false);
const error = ref(null);

const formatCurrency = (value) => {
  if (value === null || value === undefined) return '0';
  return new Intl.NumberFormat('ko-KR', { style: 'currency', currency: 'KRW' }).format(value);
};

const loadSummaryData = async () => {
  isLoading.value = true;
  error.value = null;
  try {
    const response = await fetchPurchaseOrderSummary();
    if (response.data) {
      summaryData.value = response.data;
    } else {
      error.value = '구매 주문 요약 데이터를 찾을 수 없습니다.';
    }
  } catch (e) {
    console.error('구매 주문 요약 데이터 조회 실패:', e);
    error.value = '구매 주문 요약 데이터를 불러오는 데 실패했습니다. 서버 응답을 확인하세요.';
  } finally {
    isLoading.value = false;
  }
};

onMounted(() => {
  loadSummaryData();
});

defineExpose({ loadSummaryData, summaryData }); // summaryData 노출
</script>

<style scoped>
.card {
  border: 0;
  border-radius: 12px;
  background: #fff;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}
.card-body {
  padding: 20px;
}
.table {
  margin: 0;
  border-collapse: separate;
  border-spacing: 0;
}
.table thead th {
  font-size: 12px;
  letter-spacing: .02em;
  font-weight: 600;
  color: #6b7280;
  background: #f8f9fa;
  border-bottom: 1px solid #e9ecef !important;
  padding: 14px 16px;
  vertical-align: middle;
}

.table thead th:first-child {
  border-top-left-radius: 8px;
}
.table thead th:last-child {
  border-top-right-radius: 8px;
}
.table tbody td {
  border-top: 1px solid #eef1f6 !important;
  padding: 14px 16px;
  vertical-align: middle;
  color: #334155;
  font-size: 0.9rem;
}
.table-hover tbody tr:hover {
  background: #e9ecef;
}
.table tbody tr {
  height: 56px;
}
.spinner-border {
  width: 2rem;
  height: 2rem;
}
.alert {
  border: none;
  border-radius: 12px;
}
</style>