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

      <table v-else-if="workOrders.length > 0" class="table table-hover">
        <thead class="table-light">
          <tr>
            <th scope="col">문서 번호</th>
            <th scope="col">회사명</th>
            <th scope="col">담당자</th>
            <th scope="col">품목명</th>
            <th scope="col">상태</th>
            <th scope="col">계획 수량</th>
            <th scope="col">계획일</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="workOrder in workOrders" :key="workOrder.id">
            <td>{{ workOrder.documentNo }}</td>
            <td>{{ workOrder.companyName || 'N/A' }}</td>
            <td>{{ workOrder.userName }}</td>
            <td>{{ workOrder.itemName }}</td>
            <td>{{ workOrder.status }}</td>
            <td>{{ workOrder.planQty }}</td>
            <td>{{ workOrder.planAt ? workOrder.planAt.split('T')[0] : 'N/A' }}</td>
          </tr>
        </tbody>
      </table>

      <div v-else class="text-center text-muted">
        표시할 작업 지시 현황이 없습니다.
      </div>

      <div v-if="totalPages > 1" class="mt-4">
        <Pagination
          :current-page="currentPage"
          :total-pages="totalPages"
          @page-changed="handlePageChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, defineExpose } from "vue";
import { fetchWorkOrders } from "@/api/workOrder";
import Pagination from "@/components/common/Pagination.vue";

const workOrders = ref([]);
const loading = ref(true);
const error = ref(null);
const currentPage = ref(0);
const totalPages = ref(0);

async function load(page = 0) {
  loading.value = true;
  error.value = null;
  try {
    const res = await fetchWorkOrders(page, 10); // Assuming page size of 10
    workOrders.value = res.data.workOrderResponses;
    currentPage.value = res.data.page;
    totalPages.value = res.data.totalPages;
  } catch (e) {
    console.error(e);
    error.value = "작업 지시 현황 정보를 불러오는 데 실패했습니다.";
  } finally {
    loading.value = false;
  }
}



function handlePageChange(page) {
  load(page);
}

onMounted(() => load());

defineExpose({ load, workOrders }); // Expose load and workOrders for parent component
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
