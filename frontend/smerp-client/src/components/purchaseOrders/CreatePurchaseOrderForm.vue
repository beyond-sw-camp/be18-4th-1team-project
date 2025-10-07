<template>
  <div class="card shadow-sm">
    <div class="card-body">
      <h5 class="card-title mb-4">새 구매 등록</h5>

      <form @submit.prevent="submitForm">
        <div class="row g-3">
          <div class="col-md-6">
            <label for="roId" class="form-label">발주 ID</label>
            <div class="input-group">
              <input type="text" id="roId" v-model="form.roId" class="form-control" readonly required>
              <button type="button" class="btn btn-outline-secondary" @click="openRequestOrderSearchModal">발주 선택</button>
            </div>
          </div>

          <div class="col-md-6">
            <label for="companyName" class="form-label">거래처명</label>
            <div class="input-group">
              <input type="text" id="companyName" v-model="form.companyName" class="form-control" readonly required>
            </div>
          </div>

          <div class="col-md-6">
            <label for="documentNo" class="form-label">요청날짜</label>
            <input type="date" id="documentNo" v-model="form.documentNo" class="form-control" required>
          </div>

          <div class="col-md-6">
            <label for="warehouseName" class="form-label">창고명</label>
            <div class="input-group">
              <input type="text" id="warehouseName" v-model="form.warehouseName" class="form-control" readonly required>
              <button type="button" class="btn btn-outline-secondary" @click="openWarehouseSearchModal">창고 선택</button>
            </div>
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

  <RequestOrderSearchModal ref="requestOrderSearchModalRef" @select="handleRequestOrderSelect" />
  <ClientSearchModal ref="clientSearchModalRef" @select="handleClientSelect" />
  <WarehouseSearchModal ref="warehouseSearchModalRef" @select="handleWarehouseSelect" />
</template>

<script setup>
import { ref } from 'vue';
import { createPurchaseOrder } from "@/api/purchaseOrder";
import RequestOrderSearchModal from "@/components/requestOrders/RequestOrderSearchModal.vue";
import ClientSearchModal from "@/components/clients/ClientSearchModal.vue";
import WarehouseSearchModal from "@/components/warehouses/WarehouseSearchModal.vue";

const form = ref({
  roId: '',
  companyName: '',
  remark: '',
  documentNo: '',
  newDocDate: '',
  warehouseName: '',
});

const emit = defineEmits(['cancel', 'save']);
const requestOrderSearchModalRef = ref(null);
const clientSearchModalRef = ref(null);
const warehouseSearchModalRef = ref(null);

function openRequestOrderSearchModal() {
  requestOrderSearchModalRef.value.showModal();
}

function handleRequestOrderSelect(requestOrder) {
  form.value.roId = requestOrder.roId;
  form.value.companyName = requestOrder.companyName; // Pre-fill company name from request order
}

function openClientSearchModal() {
  clientSearchModalRef.value.showModal();
}

function handleClientSelect(client) {
  form.value.companyName = client.companyName;
}

function openWarehouseSearchModal() {
  warehouseSearchModalRef.value.showModal();
}

function handleWarehouseSelect(warehouse) {
  form.value.warehouseName = warehouse.name; // Assuming warehouse object has a 'name' property
}

async function submitForm() {
  try {
    const orderData = {
      roId: form.value.roId,
      companyName: form.value.companyName,
      remark: form.value.remark,
      documentNo: form.value.documentNo,
      newDocDate: form.value.newDocDate,
      warehouseName: form.value.warehouseName,
    };

    const response = await createPurchaseOrder(orderData);
    console.log('Purchase order created successfully:', response.data);
    emit('save');
  } catch (error) {
    console.error('Error creating purchase order:', error);
    alert('구매 등록에 실패했습니다: ' + (error.response?.data?.message || error.message));
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