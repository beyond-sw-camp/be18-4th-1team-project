'''<template>
  <div class="card shadow-sm">
    <div class="card-body">
      <h5 class="card-title mb-4">구매 상세 정보</h5>

      <div v-if="showSuccessMessage" class="alert alert-success" role="alert">
        수정이 완료됐습니다.
      </div>

      <div v-if="loading" class="text-center">
        <div class="spinner-border" role="status">
          <span class="visually-hidden">Loading...</span>
        </div>
      </div>

      <div v-else-if="purchaseOrder">
        <div>
          <div class="row mb-3">
            <div class="col-md-4">
              <label class="form-label fw-bold">문서 번호</label>
              <p class="form-control-static">{{ purchaseOrder.documentNo }}</p>
            </div>
            <div class="col-md-4">
              <label class="form-label fw-bold">담당자 사번</label>
              <p class="form-control-static">{{ purchaseOrder.empNo }}</p>
            </div>
            <div class="col-md-4">
              <label class="form-label fw-bold">회사명</label>
              <p class="form-control-static">{{ purchaseOrder.companyName }}</p>
            </div>
          </div>
          <div class="row mb-3">
            <div class="col-md-4">
              <label class="form-label fw-bold">창고명</label>
              <p v-if="!isEditing" class="form-control-static">{{ purchaseOrder.warehouseName }}</p>
              <div v-else class="input-group">
                <input type="text" id="warehouseName" v-model="editablePurchaseOrder.warehouseName" class="form-control" readonly>
                <button type="button" class="btn btn-outline-secondary" @click="openWarehouseSearchModal">창고 선택</button>
              </div>
            </div>
            <div class="col-md-8">
              <label class="form-label fw-bold">비고</label>
              <p v-if="!isEditing" class="form-control-static">{{ purchaseOrder.remark }}</p>
              <textarea v-else id="remark" v-model="editablePurchaseOrder.remark" class="form-control" rows="3"></textarea>
            </div>
          </div>
          <div class="row mb-3">
            <div class="col-md-4">
              <label class="form-label fw-bold">생성일</label>
              <p class="form-control-static">{{ purchaseOrder.createdAt }}</p>
            </div>
            <div class="col-md-4">
              <label class="form-label fw-bold">수정일</label>
              <p class="form-control-static">{{ purchaseOrder.updatedAt || 'N/A' }}</p>
            </div>
            <div class="col-md-4" v-if="isEditing">
              <label class="form-label fw-bold">납기일</label>
              <input id="newDocDate" v-model="editablePurchaseOrder.newDocDate" type="date" class="form-control">
            </div>
          </div>

          <hr class="my-4">

          <h6 class="card-subtitle mb-3 text-muted">품목 정보</h6>
          <table class="table table-bordered">
            <thead>
              <tr>
                <th>품목명</th>
                <th>수량</th>
                <th>입고 단가</th>
                <th>공급가액</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(item, index) in (isEditing ? editablePurchaseOrder.items : purchaseOrder.items)" :key="index">
                <td>{{ item.itemName }}</td>
                <td>{{ item.qty }}</td>
                <td>{{ item.inboundUnitPrice }}</td>
                <td>{{ item.supplyAmount }}</td>
              </tr>
              <tr v-if="purchaseOrder.items.length === 0">
                <td colspan="4" class="text-center text-muted">추가된 품목이 없습니다.</td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- Buttons -->
        <div class="d-flex justify-content-end gap-2 mt-4">
          <button type="button" class="btn btn-outline-secondary" @click="goToList">목록으로</button>
          <template v-if="!isEditing">
            <button type="button" class="btn btn-primary" @click="startEditing">수정</button>
          </template>
          <template v-else>
            <button type="button" class="btn btn-secondary" @click="cancelEditing">취소</button>
            <button type="button" class="btn btn-success" @click="savePurchaseOrder">저장</button>
          </template>
        </div>
      </div>

      <div v-else class="text-center text-muted">
        구매 발주 정보를 찾을 수 없습니다.
      </div>
    </div>
  </div>
  <WarehouseSearchModal ref="warehouseSearchModalRef" @select="handleWarehouseSelect" />
</template>

<script setup>
import { ref, onMounted, watch } from 'vue';
import { useRouter } from 'vue-router';
import { fetchPurchaseOrderDetail, updatePurchaseOrder } from "@/api/purchaseOrder";
import WarehouseSearchModal from "@/components/warehouses/WarehouseSearchModal.vue";

const props = defineProps({
  poId: {
    type: String,
    required: true,
  },
});

const router = useRouter();
const purchaseOrder = ref(null);
const loading = ref(true);
const error = ref(null);

const isEditing = ref(false);
const editablePurchaseOrder = ref(null);
const showSuccessMessage = ref(false);
const warehouseSearchModalRef = ref(null);

async function getPurchaseOrderDetail(id) {
  loading.value = true;
  error.value = null;
  try {
    const response = await fetchPurchaseOrderDetail(id);
    purchaseOrder.value = response.data;
  } catch (e) {
    console.error('Error fetching purchase order details:', e);
    error.value = '구매 발주 정보를 불러오는 데 실패했습니다.';
  } finally {
    loading.value = false;
  }
}

onMounted(() => {
  if (props.poId) {
    getPurchaseOrderDetail(props.poId);
  }
});

watch(() => props.poId, (newPoId) => {
  if (newPoId) {
    getPurchaseOrderDetail(newPoId);
  }
});

function startEditing() {
  editablePurchaseOrder.value = JSON.parse(JSON.stringify(purchaseOrder.value));
  // Initialize newDocDate if it's null or undefined
  if (!editablePurchaseOrder.value.newDocDate) {
    editablePurchaseOrder.value.newDocDate = new Date().toISOString().slice(0, 10);
  }
  isEditing.value = true;
}

function cancelEditing() {
  editablePurchaseOrder.value = null;
  isEditing.value = false;
}

async function savePurchaseOrder() {
  if (!editablePurchaseOrder.value) return;

  const changedData = {
    remark: editablePurchaseOrder.value.remark,
    newDocDate: editablePurchaseOrder.value.newDocDate,
    warehouseName: editablePurchaseOrder.value.warehouseName,
  };

  try {
    await updatePurchaseOrder(props.poId, changedData);
    await getPurchaseOrderDetail(props.poId); // Refresh data
    isEditing.value = false;
    editablePurchaseOrder.value = null;
    showSuccessMessage.value = true;
    setTimeout(() => {
      showSuccessMessage.value = false;
    }, 3000);
  } catch (err) {
    console.error('Failed to update purchase order:', err);
    error.value = '구매 발주 수정에 실패했습니다.';
  }
}

function goToList() {
  router.push('/purchase-orders');
}

function openWarehouseSearchModal() {
  warehouseSearchModalRef.value.showModal();
}

function handleWarehouseSelect(warehouse) {
  if (editablePurchaseOrder.value) {
    editablePurchaseOrder.value.warehouseName = warehouse.name;
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
.form-control-static {
  padding: 10px 12px;
  border: 1px solid transparent;
  border-radius: 8px;
  background-color: #f8f9fa;
  font-size: 0.95rem;
  color: #495057;
}
.btn-outline-secondary, .btn-primary, .btn-secondary, .btn-success {
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

''
