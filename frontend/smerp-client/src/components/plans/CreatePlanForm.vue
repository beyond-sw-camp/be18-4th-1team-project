<template>
  <div class="card shadow-sm">
    <div class="card-body">
      <h5 class="card-title mb-4">새 생산계획 추가</h5>

      <form @submit.prevent="submitForm">
        <div class="row g-3">
          <div class="col-md-6">
            <label for="title" class="form-label">제목</label>
            <input type="text" id="title" v-model="form.title" class="form-control" required>
          </div>

          <div class="col-md-6">
            <label for="name" class="form-label">담당자</label>
            <div class="input-group">
              <input type="text" id="name" v-model="form.name" class="form-control" readonly required>
              <button type="button" class="btn btn-outline-secondary" @click="openUserSearchModal">담당자 선택</button>
            </div>
          </div>

          <div class="col-md-6">
            <label for="status" class="form-label">상태</label>
            <select id="status" v-model="form.status" class="form-select" required>
              <option value="PENDING">대기</option>
              <option value="COMPLETED">완료</option>
              <option value="APPROVED">승인</option>
              <option value="CANCELED">취소</option>
            </select>
          </div>

          <div class="col-md-6">
            <label for="qty" class="form-label">수량</label>
            <input type="number" id="qty" v-model="form.qty" class="form-control" step="0.01" required>
          </div>

          <div class="col-12">
            <label for="remark" class="form-label">비고</label>
            <textarea id="remark" v-model="form.remark" class="form-control" rows="3"></textarea>
          </div>
        </div>

        <div class="d-flex justify-content-end gap-2 mt-4">
          <button type="button" class="btn btn-outline-secondary" @click="cancel">취소</button>
          <button type="submit" class="btn btn-primary">저장</button>
        </div>
      </form>
    </div>
  </div>

  <UserSearchModal ref="userSearchModalRef" @select="handleUserSelect" /> <!-- Changed @select-user to @select -->
</template>

<script setup>
import { ref } from 'vue';
import { createProductionPlan } from "@/api/plan";
import UserSearchModal from "@/components/users/UserSearchModal.vue";

const form = ref({
  title: '',
  name: '',
  status: 'PENDING',
  remark: '',
  qty: 0,
});

const emit = defineEmits(['cancel', 'save']);
const userSearchModalRef = ref(null);

function openUserSearchModal() {
  userSearchModalRef.value.showModal();
}

function handleUserSelect(user) {
  form.value.name = user.name;
}

async function submitForm() {
  try {
    const planData = {
      title: form.value.title,
      name: form.value.name,
      status: form.value.status,
      remark: form.value.remark,
      qty: parseFloat(form.value.qty),
    };

    const response = await createProductionPlan(planData);
    console.log('Production plan created successfully:', response.data);
    emit('save');
  } catch (error) {
    console.error('Error creating production plan:', error);
    alert('생산계획 생성에 실패했습니다: ' + (error.response?.data?.message || error.message));
  }
}

function cancel() {
  emit('cancel');
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
.form-control, .form-select {
  border-radius: 8px;
  border: 1px solid #cbd5e1;
  padding: 10px 12px;
  font-size: 0.95rem;
}
.form-control:focus, .form-select:focus {
  border-color: var(--brand, #2563eb);
  box-shadow: 0 0 0 3px var(--brand-l, #dbe7ff);
  outline: none;
}
.btn-primary {
  background: var(--brand, #2563eb);
  border: none;
  border-radius: 8px;
  padding: 10px 20px;
  font-weight: 600;
}
.btn-primary:hover {
  background: var(--brand-d, #1e40af);
}
.btn-outline-secondary {
  border-radius: 8px;
  padding: 10px 20px;
  font-weight: 600;
}
</style>