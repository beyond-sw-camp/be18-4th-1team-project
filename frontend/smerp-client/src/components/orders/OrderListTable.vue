<template>
  <div class="card shadow-sm">
    <div class="card-body">
      <div class="table-responsive">
        <table class="table table-hover align-middle">
          <thead>
            <tr>
              <th scope="col">전표번호</th>
              <th scope="col">회사명</th>
              <th scope="col">상태</th>
              <th scope="col">납기일</th>
              <th scope="col">담당자</th>
              <th scope="col">첫번째 품목</th>
              <th scope="col">외 품목 수</th>
              <th scope="col">총 금액</th>
              <th scope="col">비고</th>
              <th scope="col"></th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="orders.length === 0">
              <td colspan="9" class="text-center text-muted">주문 정보가 없습니다.</td>
            </tr>
            <tr v-for="order in orders" :key="order.orderId">
              <td hidden>
                {{ order.orderId }}
              </td>
              <td>
                {{ order.documentNo }}
              </td>
              <td>{{ order.companyName }}</td>
              <td>
                <span :class="getOrderStatusClass(order.status)">{{ getOrderStatusDisplayName(order.status) }}</span>
              </td>
              <td>{{ order.deliveryDate }}</td>
              <td>{{ order.userName }}</td>
              <td>{{ order.firstItemName }}</td>
              <td>{{ order.otherItemCount }}</td>
              <td>{{ formatCurrency(order.totalAmount) }}</td>
              <td>{{ order.remark }}</td>
              <td><router-link :to="`/orders/${order.orderId}`" class="btn btn-sm btn-outline-primary">상세보기</router-link></td>
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
  orders: {
    type: Array,
    required: true,
  },
});

const router = useRouter();



function getOrderStatusDisplayName(status) {
  switch (status) {
    case 'COMPLETED': return '완료';
    case 'PENDING': return '대기중';
    case 'APPROVED': return '승인됨';
    case 'CANCELLED': return '취소됨';
    default: return status;
  }
}

function getOrderStatusClass(status) {
  switch (status) {
    case 'COMPLETED':
      return 'badge bg-success';
    case 'PENDING':
      return 'badge bg-warning';
    case 'APPROVED':
      return 'badge bg-info';
    case 'CANCELLED':
      return 'badge bg-danger';
    default:
      return 'badge bg-secondary';
  }
}

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