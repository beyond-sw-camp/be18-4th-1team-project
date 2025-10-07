<template>
  <div class="card shadow-sm">
    <div class="card-body">
      <h5 class="card-title mb-4">로그 상세 정보</h5>

      <div v-if="loading" class="text-center">
        <div class="spinner-border" role="status">
          <span class="visually-hidden">Loading...</span>
        </div>
      </div>

      <div v-else-if="error" class="alert alert-danger">
        {{ error }}
      </div>

      <div v-else-if="log">
        <div class="row mb-3">
          <div class="col-md-4">
            <label class="form-label fw-bold">시간</label>
            <p class="form-control-static">{{ log.doAt }}</p>
          </div>
          <div class="col-md-4">
            <label class="form-label fw-bold">엔티티</label>
            <p class="form-control-static">{{ log.entity }}</p>
          </div>
        </div>
        <div class="row mb-3">
          <div class="col-md-4">
            <label class="form-label fw-bold">작업</label>
            <p class="form-control-static">{{ log.action }}</p>
          </div>
          <div class="col-md-4">
            <label class="form-label fw-bold">수행자</label>
            <p class="form-control-static">{{ log.actor }}</p>
          </div>
        </div>

        <hr class="my-4">

        <div class="alert alert-info d-flex align-items-center" role="alert">
          <i class="bi bi-info-circle-fill me-2"></i>
          <div>
            변경사항은 하늘색으로 표시됩니다.
          </div>
        </div>
        <LogDataDiff :before-data="log.beforeData" :after-data="log.afterData" />

        <!-- Buttons -->
        <div class="d-flex justify-content-end gap-2 mt-4">
          <button type="button" class="btn btn-outline-secondary" @click="cancel">목록으로</button>
        </div>
      </div>

      <div v-else class="text-center text-muted">
        로그 정보를 찾을 수 없습니다.
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue';
import { useRouter } from 'vue-router';
import { fetchLogDetail } from "@/api/log";
import LogDataDiff from "@/components/logs/LogDataDiff.vue";

const props = defineProps({
  logId: {
    type: String,
    required: true,
  },
});

const router = useRouter();
const log = ref(null);
const loading = ref(true);
const error = ref(null);

async function loadLogDetail(id) {
  loading.value = true;
  error.value = null;
  try {
    const response = await fetchLogDetail(id);
    log.value = response.data;
  } catch (e) {
    console.error('Error fetching log details:', e);
    error.value = '로그 정보를 불러오는 데 실패했습니다.';
  } finally {
    loading.value = false;
  }
}



onMounted(() => {
  if (props.logId) {
    loadLogDetail(props.logId);
  }
});

watch(() => props.logId, (newLogId) => {
  if (newLogId) {
    loadLogDetail(newLogId);
  } else {
    log.value = null;
  }
});

function cancel() {
  router.push('/logs');
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
.form-label {
  font-weight: 600;
  font-size: 0.9rem;
  color: #334155;
}
.form-control-static {
  padding: 10px 12px;
  border: 1px solid transparent;
  border-radius: 8px;
  background-color: #f8f9fa;
  font-size: 0.95rem;
  color: #495057;
}
.btn-outline-secondary {
  border-radius: 8px;
  padding: 10px 20px;
  font-weight: 600;
}
.table-info {
  --bs-table-bg: #cce5ff;
  --bs-table-border-color: #b8daff;
}
</style>
