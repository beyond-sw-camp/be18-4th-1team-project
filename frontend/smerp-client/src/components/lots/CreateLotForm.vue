<template>
  <div class="card shadow-sm">
    <div class="card-body">
      <h5 class="card-title mb-4">신규 로트 생성</h5>
      <form @submit.prevent="handleSubmit">
        <input type="hidden" v-model="form.itemId">
        <div class="mb-3">
          <label for="itemName" class="form-label">품목명</label>
          <div class="input-group">
            <input type="text" class="form-control" id="itemName" v-model="form.itemName" readonly required>
            <button class="btn btn-outline-secondary" type="button" @click="openItemSearchModal">품목 검색</button>
          </div>
        </div>
        <div class="mb-3">
          <label for="qty" class="form-label">수량</label>
          <input type="number" class="form-control" id="qty" v-model.number="form.qty" required>
        </div>
        <div class="mb-3">
          <label for="status" class="form-label">상태</label>
          <select class="form-select" id="status" v-model="form.status">
            <option value="ACTIVE">사용중</option>
            <option value="DEFECTIVE">불량</option>
            <option value="SHIPPED">출고완</option>
            <option value="RETURNED">반품</option>
          </select>
        </div>
        <div class="d-flex justify-content-end gap-2 mt-4">
          <button type="button" class="btn btn-outline-secondary" @click="cancel">취소</button>
          <button type="submit" class="btn btn-primary">저장</button>
        </div>
      </form>
    </div>
  </div>

  <ItemSearchModal ref="itemSearchModal" @select="handleItemSelect" />
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { createLot } from '@/api/lot';
import ItemSearchModal from '@/components/items/ItemSearchModal.vue';
import { Modal } from 'bootstrap';

const emit = defineEmits(['cancel', 'save']);

const form = ref({
  itemId: null,
  itemName: '',
  qty: 0,
  status: 'NORMAL',
});

const itemSearchModal = ref(null);
let modalInstance = null;

onMounted(() => {
  const modalEl = itemSearchModal.value?.$el;
  if (modalEl) {
    modalInstance = new Modal(modalEl);
  }
});

function openItemSearchModal() {
  if (modalInstance) {
    modalInstance.show();
  }
}

function handleItemSelect(item) {
  form.value.itemId = item.itemId;
  form.value.itemName = item.name;
  if (modalInstance) {
    modalInstance.hide();
  }
}

async function handleSubmit() {
  try {
    const payload = {
      itemId: form.value.itemId,
      lotInstant: new Date().toISOString(),
      qty: form.value.qty,
      status: form.value.status,
    };
    await createLot(payload);
    emit('save');
  } catch (error) {
    console.error('Error creating lot:', error);
    alert('로트 생성에 실패했습니다.');
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
}
</style>
