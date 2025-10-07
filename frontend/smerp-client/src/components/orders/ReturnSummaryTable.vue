<template>
  <div class="card shadow-sm">
    <div class="card-body">

      <div v-if="loading" class="text-center">
        <div class="spinner-border" role="status">
          <span class="visually-hidden">Loading...</span>
        </div>
      </div>

      <div v-else-if="error" class="alert alert-danger">
        {{ error }}
      </div>

      <div v-else>
        <div class="table-responsive">
          <table class="table table-hover align-middle">
            <thead>
              <tr>
                <th scope="col">반품 전표번호</th>
                <th scope="col">회사명</th>
                <th scope="col">품목명</th>
                <th scope="col">수량</th>
                <th scope="col">단가</th>
                <th scope="col">환불 금액</th>
                <th scope="col">비고</th>
              </tr>
            </thead>
            <tbody>
              <tr v-if="returnSummaryData.length === 0">
                <td colspan="7" class="text-center text-muted">반품 현황 정보가 없습니다.</td>
              </tr>
              <tr v-for="(item, index) in returnSummaryData" :key="index">
                <td>{{ item.documentNo }}</td>
                <td>{{ item.companyName }}</td>
                <td>{{ item.itemName }}</td>
                <td>{{ item.qty }}</td>
                <td>{{ formatCurrency(item.specialPrice) }}</td>
                <td>{{ formatCurrency(item.refundAmount) }}</td>
                <td>{{ item.remark }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { defineProps } from 'vue';

const props = defineProps({
  returnSummaryData: {
    type: Array,
    required: true,
  },
  loading: {
    type: Boolean,
    required: true,
  },
  error: {
    type: [String, null],
    required: true,
  },
  formatCurrency: {
    type: Function,
    required: true,
  },
});
</script>

<style scoped>
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