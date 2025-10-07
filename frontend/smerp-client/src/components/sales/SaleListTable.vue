<template>
  <div class="card shadow-sm">
    <div class="card-body">
      <div class="table-responsive">
        <table class="table table-hover align-middle">
          <thead>
            <tr>
              <th scope="col">판매 전표번호</th>
              <th scope="col">회사명</th>
              <th scope="col">담당자</th>
              <th scope="col">첫번째 품목</th>
              <th scope="col">외 품목 수</th>
              <th scope="col">창고명</th>
              <th scope="col">총 금액</th>
              <th scope="col">주문 전표번호</th>
              <th scope="col"></th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="sales.length === 0">
              <td colspan="9" class="text-center text-muted">판매 정보가 없습니다.</td>
            </tr>
            <tr v-for="sale in sales" :key="sale.documentNo">
              <td>
                {{ sale.documentNo }}
              </td>
              <td>{{ sale.companyName }}</td>
              <td>{{ sale.userName }}</td>
              <td>{{ sale.firstItemName }}</td>
              <td>{{ sale.otherItemCount }}</td>
              <td>{{ sale.warehouseName }}</td>
              <td>{{ formatCurrency(sale.totalAmount) }}</td>
              <td>{{ sale.orderDocumentNo }}</td>
              <td><router-link :to="`/sales/${sale.soId}`" class="btn btn-sm btn-outline-primary">상세보기</router-link></td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { defineProps } from 'vue';
import { useRouter } from 'vue-router';

const props = defineProps({
  sales: {
    type: Array,
    required: true,
  },
});

const router = useRouter();

console.log("Sales data in SaleListTable:", props.sales);

function formatCurrency(amount) {
  if (amount === null || amount === undefined) return '';
  return new Intl.NumberFormat('ko-KR', { style: 'currency', currency: 'KRW' }).format(amount);
}
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

.badge {
  font-size: 0.85em;
  padding: 0.5em 0.75em;
}
</style>