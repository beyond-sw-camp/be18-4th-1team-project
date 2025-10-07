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

      <table v-else-if="plans.length > 0" class="table table-hover">
        <thead class="table-light">
          <tr>
            <th scope="col">문서 번호</th>
            <th scope="col">제목</th>
            <th scope="col">담당자</th>
            <th scope="col">상태</th>
            <th scope="col">수량</th>
            <th scope="col">비고</th>
            <th scope="col">상세보기</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="plan in plans" :key="plan.id">
            <td>{{ plan.documentNo }}</td>
            <td>{{ plan.title }}</td>
            <td>{{ plan.name }}</td>
            <td>{{ plan.status }}</td>
            <td>{{ plan.qty }}</td>
            <td>{{ plan.remark }}</td>
            <td>
              <button class="btn btn-sm btn-outline-primary" @click="goToDetail(plan.id)">상세보기</button>
            </td>
          </tr>
        </tbody>
      </table>

      <div v-else class="text-center text-muted">
        표시할 생산 계획이 없습니다.
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { fetchProductionPlans } from "@/api/plan";
import { useRouter } from "vue-router";

const plans = ref([]);
const loading = ref(true);
const error = ref(null);
const router = useRouter();

async function load() {
  loading.value = true;
  error.value = null;
  try {
    const res = await fetchProductionPlans();
    plans.value = res.data.productionPlans;
  } catch (e) {
    console.error(e);
    error.value = "생산 계획 정보를 불러오는 데 실패했습니다.";
  } finally {
    loading.value = false;
  }
}

function goToDetail(planId) {
  router.push({ name: 'PlanDetail', params: { planId } });
}

onMounted(() => load());
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