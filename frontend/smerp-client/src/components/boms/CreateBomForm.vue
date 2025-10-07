<template>
  <div class="card shadow-sm mb-4">
    <div class="card-body">
      <h5 class="card-title">BOM 신규 등록</h5>
      <form @submit.prevent="submitForm">
        <div class="mb-3">
          <label for="parentItem" class="form-label">부모 품목 ID <span class="text-danger">*</span></label>
          <input type="number" class="form-control" id="parentItem" v-model="formData.parentItemId" required>
        </div>
        <div class="mb-3">
          <label for="childItem" class="form-label">자식 품목 ID <span class="text-danger">*</span></label>
          <input type="number" class="form-control" id="childItem" v-model="formData.childItemId" required>
        </div>
        <div class="mb-3">
          <label for="qty" class="form-label">수량 <span class="text-danger">*</span></label>
          <input type="number" class="form-control" id="qty" v-model="formData.qty" required min="0.001" step="0.001">
        </div>
        <div class="mb-3">
          <label for="remark" class="form-label">비고</label>
          <input type="text" class="form-control" id="remark" v-model="formData.remark">
        </div>

        <div v-if="error" class="alert alert-danger mt-3">{{ error }}</div>

        <div class="d-flex justify-content-end gap-2">
          <button type="button" class="btn btn-secondary" @click="emit('cancel')">취소</button>
          <button type="submit" class="btn btn-primary" :disabled="loading">
            <span v-if="loading" class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
            등록
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { createBom } from "@/api/bom";

const emit = defineEmits(['submit-success', 'cancel']);

const formData = reactive({
  parentItemId: null,
  childItemId: null,
  qty: null,
  remark: ''
});

const loading = ref(false);
const error = ref(null);

async function submitForm() {
  // 간단한 클라이언트 측 유효성 검사
  if (!formData.parentItemId || !formData.childItemId || !formData.qty) {
    error.value = "필수 항목을 모두 입력해주세요.";
    return;
  }
  if (formData.qty <= 0) {
    error.value = "수량은 0보다 커야 합니다.";
    return;
  }

  loading.value = true;
  error.value = null;

  try {
    await createBom(formData);
    alert("BOM이 성공적으로 등록되었습니다.");
    emit('submit-success');
  } catch (e) {
    console.error("BOM 등록 실패:", e);
    error.value = "BOM 등록에 실패했습니다. 유효한 품목 ID를 확인해주세요.";
  } finally {
    loading.value = false;
  }
}
</script>

<style scoped>
/* 필요한 스타일 추가 */
</style>