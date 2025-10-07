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

      <table v-else-if="logs.length > 0" class="table table-hover">
        <thead class="table-light">
          <tr>
            <th scope="col">시간</th>
            <th scope="col">엔티티</th>
            <th scope="col">작업</th>
            <th scope="col">수행자</th>
            <th scope="col"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="log in logs" :key="log.logId">
            <td>{{ log.doAt }}</td>
            <td>{{ log.entity }}</td>
            <td>{{ log.action }}</td>
            <td>{{ log.actor }}</td>
            <td>
              <router-link :to="{ name: 'LogDetail', params: { logId: log.logId } }" class="btn btn-sm btn-outline-primary">상세보기</router-link>
            </td>
          </tr>
        </tbody>
      </table>

      <div v-else class="text-center text-muted">
        표시할 로그가 없습니다.
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, defineExpose } from "vue";
import { fetchLogs } from "@/api/log";

const logs = ref([]);
const loading = ref(true);
const error = ref(null);

async function load() {
  loading.value = true;
  error.value = null;
  try {
    const res = await fetchLogs();
    logs.value = res.data;
  } catch (e) {
    console.error(e);
    error.value = "로그 정보를 불러오는 데 실패했습니다.";
  } finally {
    loading.value = false;
  }
}

onMounted(load);

defineExpose({ reload: load });
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
