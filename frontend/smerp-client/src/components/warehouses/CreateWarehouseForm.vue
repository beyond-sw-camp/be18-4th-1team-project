<template>
  <div class="card shadow-sm">
    <div class="card-body">
      <h5 class="card-title mb-4">새 창고 추가</h5>

      <form @submit.prevent="submitForm">
        <div class="row g-3">
          <div class="col-md-4">
            <label for="name" class="form-label">이름</label>
            <input type="text" id="name" v-model="form.name" class="form-control" required>
          </div>

          <div class="col-md-4">
            <label for="divisionType" class="form-label">구분</label>
            <input type="text" id="divisionType" v-model="form.divisionType" class="form-control" required>
          </div>

          <div class="col-md-4">
            <label for="active" class="form-label">활성화</label>
            <div class="form-check form-switch">
              <input class="form-check-input" type="checkbox" id="active" v-model="form.active">
              <label class="form-check-label" for="active">{{ form.active ? '예' : '아니오' }}</label>
            </div>
          </div>

          <div class="col-md-4">
            <label for="address" class="form-label">주소</label>
            <input type="text" id="address" v-model="form.address" class="form-control" required>
          </div>

          <div class="col-md-4">
            <label for="zipcode" class="form-label">우편번호</label>
            <input type="text" id="zipcode" v-model="form.zipcode" class="form-control" required>
          </div>
        </div>

        <div class="d-flex justify-content-end gap-2 mt-4">
          <button type="button" class="btn btn-outline-secondary" @click="cancel">취소</button>
          <button type="submit" class="btn btn-primary">저장</button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import http from "@/api/http";
import { createWarehouse } from "@/api/warehouse";

const form = ref({
  name: '',
  divisionType: '',
  active: true,
  address: '',
  zipcode: '',
});

const emit = defineEmits(['cancel', 'save']);

async function submitForm() {
  try {
    const warehouseData = {
      name: form.value.name,
      divisionType: form.value.divisionType,
      active: form.value.active,
      address: form.value.address,
      zipcode: form.value.zipcode,
    };

    const response = await createWarehouse(warehouseData);
    console.log('Warehouse created successfully:', response.data);
    emit('save');
  } catch (error) {
    console.error('Error creating warehouse:', error);
    alert('창고 생성에 실패했습니다: ' + (error.response?.data?.message || error.message));
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
