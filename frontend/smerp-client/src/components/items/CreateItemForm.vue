<template>
  <div class="card shadow-sm">
    <div class="card-body">
      <h5 class="card-title mb-4">새 품목 추가</h5>

      <form @submit.prevent="submitForm">
        <div class="row g-3">
          <div class="col-md-4">
            <label for="name" class="form-label">품목명</label>
            <input type="text" id="name" v-model="form.name" class="form-control" required>
          </div>

          <div class="col-md-4">
            <label for="unit" class="form-label">단위</label>
            <input type="text" id="unit" v-model="form.unit" class="form-control" required>
          </div>

          <div class="col-md-4">
            <label for="itemStatusId" class="form-label">품목상태구분</label>
            <select id="itemStatusId" v-model="form.itemStatusId" class="form-select" required>
              <option v-for="status in itemStatusOptions" :key="status.id" :value="status.id">
                {{ status.name }}
              </option>
            </select>
          </div>

          <div class="col-md-4">
            <label for="rfid" class="form-label">RFID</label>
            <input type="text" id="rfid" v-model="form.rfid" class="form-control">
          </div>

          <div class="col-md-4">
            <label for="itemAct" class="form-label">품목활동</label>
            <select id="itemAct" v-model="form.itemAct" class="form-select" required>
              <option v-for="act in itemActOptions" :key="act.value" :value="act.value">
                {{ act.name }}
              </option>
            </select>
          </div>

          <div class="col-md-4">
            <label for="specification" class="form-label">규격</label>
            <input type="text" id="specification" v-model="form.specification" class="form-control">
          </div>

          <div class="col-md-4">
            <label for="inboundUnitPrice" class="form-label">입고 단가</label>
            <input type="number" id="inboundUnitPrice" v-model="form.inboundUnitPrice" class="form-control">
          </div>

          <div class="col-md-4">
            <label for="outboundUnitPrice" class="form-label">출고 단가</label>
            <input type="number" id="outboundUnitPrice" v-model="form.outboundUnitPrice" class="form-control">
          </div>

          <div class="col-md-4">
            <label for="groupName1" class="form-label">그룹1</label>
            <input type="text" id="groupName1" v-model="form.groupName1" class="form-control">
          </div>

          <div class="col-md-4">
            <label for="groupName2" class="form-label">그룹2</label>
            <input type="text" id="groupName2" v-model="form.groupName2" class="form-control">
          </div>

          <div class="col-md-4">
            <label for="groupName3" class="form-label">그룹3</label>
            <input type="text" id="groupName3" v-model="form.groupName3" class="form-control">
          </div>

          <div class="col-md-4">
            <label for="safetyStock" class="form-label">안전 재고</label>
            <input type="number" id="safetyStock" v-model="form.safetyStock" class="form-control">
          </div>

          <div class="col-md-4">
            <label for="safetyStockAct" class="form-label">안전 재고 활동</label>
            <select id="safetyStockAct" v-model="form.safetyStockAct" class="form-select" required>
              <option v-for="act in safetyStockActOptions" :key="act.value" :value="act.value">
                {{ act.name }}
              </option>
            </select>
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
import { createItem } from "@/api/item";

const form = ref({
  name: '',
  unit: '',
  itemStatusId: 1,
  rfid: '',
  itemAct: '사용중',
  specification: '',
  inboundUnitPrice: 0,
  outboundUnitPrice: 0,
  groupName1: '',
  groupName2: '',
  groupName3: '',
  safetyStock: 0,
  safetyStockAct: 'DISABLED',
});

const itemStatusOptions = [
  { id: 1, name: '원재료' },
  { id: 2, name: '부재료' },
  { id: 3, name: '반제품' },
  { id: 4, name: '제품' },
  { id: 5, name: '상품' },
  { id: 6, name: '무형상품' },
];

const itemActOptions = [
  { value: 'ACTIVE', name: '사용중' },
  { value: 'INACTIVE', name: '사용중지' },
];

const safetyStockActOptions = [
  { value: 'ENABLED', name: '사용중' },
  { value: 'DISABLED', name: '사용안함' },
];

const emit = defineEmits(['cancel', 'save']);

async function submitForm() {
  try {
    const itemData = {
      name: form.value.name,
      unit: form.value.unit,
      itemStatusId: form.value.itemStatusId,
      rfid: form.value.rfid || null,
      itemAct: form.value.itemAct,
      specification: form.value.specification || null,
      inboundUnitPrice: form.value.inboundUnitPrice || 0,
      outboundUnitPrice: form.value.outboundUnitPrice || 0,
      groupName1: form.value.groupName1 || null,
      groupName2: form.value.groupName2 || null,
      groupName3: form.value.groupName3 || null,
      safetyStock: form.value.safetyStock || 0,
      safetyStockAct: form.value.safetyStockAct || null,
    };

    const response = await createItem(itemData);
    console.log('Item created successfully:', response.data);
    emit('save');
  } catch (error) {
    console.error('Error creating item:', error);
    alert('품목 생성에 실패했습니다: ' + (error.response?.data?.message || error.message));
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

.form-control,
.form-select {
  border-radius: 8px;
  border: 1px solid #cbd5e1;
  padding: 10px 12px;
  font-size: 0.95rem;
}

.form-control:focus,
.form-select:focus {
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

.form-control,
.form-select {
  border-radius: 8px;
  border: 1px solid #cbd5e1;
  padding: 10px 12px;
  font-size: 0.95rem;
}

.form-control:focus,
.form-select:focus {
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
