<template>
  <div class="card shadow-sm">
    <div class="card-body">
      <h5 class="card-title mb-4">작업 지시 등록</h5>

      <div v-if="showSuccessMessage" class="alert alert-success" role="alert">
        작업 지시가 성공적으로 등록되었습니다.
      </div>
      <div v-if="error" class="alert alert-danger" role="alert">
        {{ error }}
      </div>

      <form @submit.prevent="submitForm">
        <div class="row mb-3">
          <div class="col-md-6">
            <label for="itemName" class="form-label fw-bold">품목명</label>
            <div class="input-group">
              <input type="text" id="itemName" v-model="workOrder.itemName" class="form-control" readonly required>
              <button type="button" class="btn btn-outline-secondary" @click="openItemSearchModal">품목 선택</button>
            </div>
          </div>
          <div class="col-md-6">
            <label for="productionPlanId" class="form-label fw-bold">생산 계획 ID</label>
            <div class="input-group">
              <input type="number" id="productionPlanId" v-model="workOrder.productionPlanId" class="form-control" readonly required>
              <button type="button" class="btn btn-outline-secondary" @click="openProductionPlanSearchModal">계획 선택</button>
            </div>
          </div>
        </div>

        <div class="row mb-3">
          <div class="col-md-6">
            <label for="planQty" class="form-label fw-bold">계획 수량</label>
            <input type="number" id="planQty" v-model="workOrder.planQty" class="form-control" required>
          </div>
          <div class="col-md-6">
            <label for="planAt" class="form-label fw-bold">계획일</label>
            <input type="datetime-local" id="planAt" v-model="workOrder.planAt" class="form-control" required>
          </div>
        </div>

        <div class="row mb-3">
          <div class="col-md-6">
            <label for="factoryName" class="form-label fw-bold">공장명</label>
            <div class="input-group">
              <input type="text" id="factoryName" v-model="workOrder.factoryName" class="form-control" readonly required>
              <button type="button" class="btn btn-outline-secondary" @click="openWarehouseSearchModal">공장 선택</button>
            </div>
          </div>
          <div class="col-md-6">
            <label for="userName" class="form-label fw-bold">담당자</label>
            <div class="input-group">
              <input type="text" id="userName" v-model="workOrder.userName" class="form-control" readonly required>
              <button type="button" class="btn btn-outline-secondary" @click="openUserSearchModal">담당자 선택</button>
            </div>
          </div>
        </div>

        <div class="mb-3">
          <label for="remark" class="form-label fw-bold">비고</label>
          <textarea id="remark" v-model="workOrder.remark" class="form-control" rows="3"></textarea>
        </div>

        <div class="d-flex justify-content-end gap-2 mt-4">
          <button type="button" class="btn btn-outline-secondary" @click="cancel">취소</button>
          <button type="submit" class="btn btn-primary">등록</button>
        </div>
      </form>
    </div>
  </div>

  <UserSearchModal ref="userSearchModalRef" @select="handleUserSelect" />
  <WarehouseSearchModal ref="warehouseSearchModalRef" @select="handleWarehouseSelect" />
  <ItemSearchModal ref="itemSearchModalRef" @select="handleItemSelect" />
  <ProductionPlanSearchModal ref="productionPlanSearchModalRef" @select="handleProductionPlanSelect" />
</template>

<script setup>
import { ref } from 'vue';
import { createWorkOrder } from '@/api/workOrder';
import UserSearchModal from "@/components/users/UserSearchModal.vue";
import WarehouseSearchModal from "@/components/warehouses/WarehouseSearchModal.vue";
import ItemSearchModal from "@/components/items/ItemSearchModal.vue";
import ProductionPlanSearchModal from "@/components/plans/ProductionPlanSearchModal.vue";

const emit = defineEmits(['cancel', 'save']);

const workOrder = ref({
  itemName: '',
  productionPlanId: null,
  planQty: null,
  planAt: '',
  factoryName: '',
  userName: '',
  remark: '',
});

const showSuccessMessage = ref(false);
const error = ref(null);

const userSearchModalRef = ref(null);
const warehouseSearchModalRef = ref(null);
const itemSearchModalRef = ref(null);
const productionPlanSearchModalRef = ref(null);

async function submitForm() {
  error.value = null;
  try {
    const formattedWorkOrder = { ...workOrder.value };
    if (formattedWorkOrder.planAt) {
      // Append seconds and 'Z' for UTC to match java.time.Instant format
      formattedWorkOrder.planAt = formattedWorkOrder.planAt + ':00Z';
    }
    await createWorkOrder(formattedWorkOrder);
    showSuccessMessage.value = true;
    setTimeout(() => {
      showSuccessMessage.value = false;
      emit('save');
    }, 3000);
  } catch (err) {
    console.error('Failed to create work order:', err);
    error.value = '작업 지시 등록에 실패했습니다.';
  }
}

function cancel() {
  emit('cancel');
}

function openUserSearchModal() {
  userSearchModalRef.value.showModal();
}

function handleUserSelect(user) {
  if (user) {
    workOrder.value.userName = user.name; // Assuming user object has userName
  }
}

function openWarehouseSearchModal() {
  warehouseSearchModalRef.value.showModal();
}

function handleWarehouseSelect(warehouse) {
  if (warehouse) {
    workOrder.value.factoryName = warehouse.name; // Assuming warehouse object has name
  }
}

function openItemSearchModal() {
  itemSearchModalRef.value.showModal();
}

function handleItemSelect(item) {
  if (item) {
    workOrder.value.itemName = item.name;
  }
}

function openProductionPlanSearchModal() {
  productionPlanSearchModalRef.value.showModal();
}

function handleProductionPlanSelect(plan) {
  if (plan) {
    workOrder.value.productionPlanId = plan.id; // Use plan.id as per sample data
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
  padding: 24px;
}
.form-label {
  font-weight: 600;
  font-size: 0.9rem;
  color: #334155;
}
.btn-outline-secondary, .btn-primary {
  border-radius: 8px;
  padding: 10px 20px;
  font-weight: 600;
}
.btn-primary {
  background: var(--brand, #2563eb);
  border: none;
}
.btn-primary:hover {
  background: var(--brand-d, #1e40af);
}
</style>
