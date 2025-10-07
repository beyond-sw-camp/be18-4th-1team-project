<template>
  <div class="card shadow-sm">
    <div class="card-body">
      <h5 class="card-title mb-4">작업 지시 상세 정보</h5>

      <div v-if="showSuccessMessage" class="alert alert-success" role="alert">
        수정이 완료됐습니다.
      </div>

      <div v-if="loading" class="text-center">
        <div class="spinner-border" role="status">
          <span class="visually-hidden">Loading...</span>
        </div>
      </div>

      <div v-else-if="workOrder">
        <div>
          <div class="row mb-3">
            <div class="col-md-4">
              <label class="form-label fw-bold">문서 번호</label>
              <p class="form-control-static">{{ workOrder.documentNo }}</p>
            </div>
            <div class="col-md-4">
              <label class="form-label fw-bold">회사명</label>
              <p class="form-control-static">{{ workOrder.companyName || 'N/A' }}</p>
            </div>
            <div class="col-md-4">
              <label class="form-label fw-bold">담당자</label>
              <p class="form-control-static">{{ workOrder.userName }}</p>
            </div>
          </div>
          <div class="row mb-3">
            <div class="col-md-4">
              <label class="form-label fw-bold">품목명</label>
              <p class="form-control-static">{{ workOrder.itemName }}</p>
            </div>
            <div class="col-md-4">
              <label class="form-label fw-bold">상태</label>
              <p v-if="!isEditing" class="form-control-static">{{ workOrder.status }}</p>
              <select v-else id="status" v-model="editableWorkOrder.status" class="form-select">
                <option value="PENDING">대기</option>
                <option value="APPROVED">승인</option>
                <option value="COMPLETED">완료</option>
                <option value="RETURNED">반려</option>
              </select>
            </div>
            <div class="col-md-4">
              <label class="form-label fw-bold">계획 수량</label>
              <p v-if="!isEditing" class="form-control-static">{{ workOrder.planQty }}</p>
              <input v-else type="number" id="planQty" v-model="editableWorkOrder.planQty" class="form-control">
            </div>
          </div>
          <div class="row mb-3">
            <div class="col-md-4">
              <label class="form-label fw-bold">생산 수량</label>
              <p v-if="!isEditing" class="form-control-static">{{ workOrder.producedQty }}</p>
              <input v-else type="number" id="planQty" v-model="editableWorkOrder.producedQty" class="form-control">
            </div>
            <div class="col-md-4">
              <label class="form-label fw-bold">계획일</label>
              <p v-if="!isEditing" class="form-control-static">{{ workOrder.planAt ? workOrder.planAt.split('T')[0] : 'N/A' }}</p>
              <input v-else type="datetime-local" id="planAt" v-model="editableWorkOrder.planAt" class="form-control">
            </div>
            <div class="col-md-4">
              <label class="form-label fw-bold">생산일</label>
              <p class="form-control-static">{{ workOrder.producedAt ? workOrder.producedAt.split('T')[0] : 'N/A' }}</p>
            </div>
          </div>
          <div class="row mb-3">
            <div class="col-md-12">
              <label class="form-label fw-bold">비고</label>
              <p v-if="!isEditing" class="form-control-static">{{ workOrder.remark || 'N/A' }}</p>
              <textarea v-else id="remark" v-model="editableWorkOrder.remark" class="form-control" rows="3"></textarea>
            </div>
          </div>

          <hr class="my-4">

          <!-- Buttons -->
          <div class="d-flex justify-content-end gap-2 mt-4">
            <button type="button" class="btn btn-outline-secondary" @click="goToList">목록으로</button>
            <template v-if="!isEditing">
              <button type="button" class="btn btn-danger" @click="handleDeleteWorkOrder">삭제</button>
              <button type="button" class="btn btn-primary" @click="startEditing">수정</button>
            </template>
            <template v-else>
              <button type="button" class="btn btn-secondary" @click="cancelEditing">취소</button>
              <button type="button" class="btn btn-success" @click="saveWorkOrder">저장</button>
            </template>
          </div>
        </div>
      </div>

      <div v-else class="text-center text-muted">
        작업 지시 정보를 찾을 수 없습니다.
      </div>
    </div>
  </div>

  <!-- Delete Confirmation Modal -->
  <div class="modal fade" id="deleteConfirmModal" tabindex="-1" aria-labelledby="deleteConfirmModalLabel" aria-hidden="true" ref="deleteModal">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title" id="deleteConfirmModalLabel">작업 지시 삭제 확인</h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <div class="modal-body">
          정말로 작업 지시를 삭제하시겠습니까?
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">취소</button>
          <button type="button" class="btn btn-danger" @click="confirmDelete">삭제</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue';
import { useRouter } from 'vue-router';
import { fetchWorkOrderDetail, updateWorkOrder, deleteWorkOrder } from "@/api/workOrder";
import { Modal } from 'bootstrap'; // Import Bootstrap's Modal

const props = defineProps({
  id: {
    type: String,
    required: true,
  },
});

const router = useRouter();
const workOrder = ref(null);
const loading = ref(true);
const error = ref(null);

const isEditing = ref(false);
const editableWorkOrder = ref(null);
const showSuccessMessage = ref(false);
const deleteModal = ref(null); // Ref for the delete confirmation modal
let modalInstance = null; // To store the Bootstrap Modal instance

async function getWorkOrderDetail(id) {
  loading.value = true;
  error.value = null;
  try {
    const response = await fetchWorkOrderDetail(id);
    workOrder.value = response.data;
  } catch (e) {
    console.error('Error fetching work order details:', e);
    error.value = '작업 지시 정보를 불러오는 데 실패했습니다.';
  } finally {
    loading.value = false;
  }
}

onMounted(() => {
  if (props.id) {
    getWorkOrderDetail(props.id);
  }
  if (deleteModal.value) {
    modalInstance = new Modal(deleteModal.value);
  }
});

watch(() => props.id, (newId) => {
  if (newId) {
    getWorkOrderDetail(newId);
  }
});

function startEditing() {
  editableWorkOrder.value = JSON.parse(JSON.stringify(workOrder.value));
  if (editableWorkOrder.value.planAt) {
    editableWorkOrder.value.planAt = editableWorkOrder.value.planAt.substring(0, 16);
  }
  isEditing.value = true;
}

function cancelEditing() {
  editableWorkOrder.value = null;
  isEditing.value = false;
}

async function saveWorkOrder() {
  if (!editableWorkOrder.value) return;

  const changedData = {
    status: editableWorkOrder.value.status,
    planQty: editableWorkOrder.value.planQty,
    producedQty: editableWorkOrder.value.producedQty,
    remark: editableWorkOrder.value.remark,
  };

  if (editableWorkOrder.value.planAt) {
    changedData.planAt = editableWorkOrder.value.planAt + ':00Z';
  }

  try {
    await updateWorkOrder(props.id, changedData);
    await getWorkOrderDetail(props.id); // Refresh data
    isEditing.value = false;
    editableWorkOrder.value = null;
    showSuccessMessage.value = true;
    setTimeout(() => {
      showSuccessMessage.value = false;
    }, 3000);
  } catch (err) {
    console.error('Failed to update work order:', err);
    error.value = '작업 지시 수정에 실패했습니다.';
  }
}

function goToList() {
  router.push('/work-orders');
}

async function handleDeleteWorkOrder() {
  if (modalInstance) {
    modalInstance.show();
  }
}

async function confirmDelete() {
  try {
    await deleteWorkOrder(props.id);
    modalInstance.hide();
    router.push('/work-orders'); // Redirect to the list page after deletion
  } catch (err) {
    console.error('Failed to delete work order:', err);
    error.value = '작업 지시 삭제에 실패했습니다.';
    modalInstance.hide();
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
