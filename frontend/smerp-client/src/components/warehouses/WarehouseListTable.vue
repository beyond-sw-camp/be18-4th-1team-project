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

      <table v-else-if="warehouses.length > 0" class="table table-hover">
        <thead class="table-light">
          <tr>
            <th scope="col">이름</th>
            <th scope="col">주소</th>
            <th scope="col">구분</th>
            <th scope="col">활성화</th>
            <th scope="col"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="warehouse in warehouses" :key="warehouse.id">
            <td>{{ warehouse.name }}</td>
            <td>{{ warehouse.address }}</td>
            <td>{{ warehouse.divisionType }}</td>
            <td>{{ warehouse.active ? '예' : '아니오' }}</td>
            <td>
              <router-link :to="{ name: 'WarehouseDetail', params: { warehouseId: warehouse.id } }" class="btn btn-sm btn-outline-primary">상세보기</router-link>
            </td>
          </tr>
        </tbody>
      </table>

      <div v-else class="text-center text-muted">
        표시할 창고가 없습니다.
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, defineExpose } from "vue";
import { fetchWarehouses } from "@/api/warehouse";

const warehouses = ref([]);
const loading = ref(true);
const error = ref(null);

async function load() {
  loading.value = true;
  error.value = null;
  try {
    const res = await fetchWarehouses();
    warehouses.value = res.data;
  } catch (e) {
    console.error(e);
    error.value = "창고 정보를 불러오는 데 실패했습니다.";
  } finally {
    loading.value = false;
  }
}

onMounted(load);

defineExpose({ reload: load });

function getWareHouseStatusClass(status) {
  switch (status) {
    case '예':
      return 'badge bg-success';
    case '불량':
      return 'badge bg-danger';
    default:
      return 'badge bg-secondary';
  }
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
