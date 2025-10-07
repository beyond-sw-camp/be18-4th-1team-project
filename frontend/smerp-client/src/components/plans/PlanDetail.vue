<template>
  <div class="card shadow-sm">
    <div class="card-body">
      <h5 class="card-title mb-4">생산계획 상세 정보</h5>

      <div v-if="showSuccessMessage" class="alert alert-success" role="alert">
        수정이 완료됐습니다.
      </div>

      <div v-if="loading" class="text-center">
        <div class="spinner-border" role="status">
          <span class="visually-hidden">Loading...</span>
        </div>
      </div>

      <div v-else-if="error" class="alert alert-danger">
        {{ error }}
      </div>

      <div v-else-if="plan">
        <div v-if="!isEditing">
          <div class="row mb-3">
            <div class="col-md-4">
              <label class="form-label fw-bold">문서 번호</label>
              <p class="form-control-static">{{ plan.documentNo }}</p>
            </div>
            <div class="col-md-4">
              <label class="form-label fw-bold">제목</label>
              <p class="form-control-static">{{ plan.title }}</p>
            </div>
            <div class="col-md-4">
              <label class="form-label fw-bold">담당자</label>
              <p class="form-control-static">{{ plan.name }}</p>
            </div>
          </div>
          <div class="row mb-3">
            <div class="col-md-4">
              <label class="form-label fw-bold">상태</label>
              <p class="form-control-static">{{ plan.status }}</p>
            </div>
            <div class="col-md-4">
              <label class="form-label fw-bold">수량</label>
              <p class="form-control-static">{{ plan.qty }}</p>
            </div>
            <div class="col-md-4">
              <label class="form-label fw-bold">비고</label>
              <p class="form-control-static">{{ plan.remark }}</p>
            </div>
          </div>
        </div>

        <!-- Editing View -->
        <div v-else>
          <div class="row mb-3">
            <div class="col-md-4">
              <label class="form-label fw-bold">문서 번호</label>
              <p class="form-control-static">{{ editablePlan.documentNo }}</p>
            </div>
            <div class="col-md-4">
              <label class="form-label fw-bold">제목</label>
              <input id="title" v-model="editablePlan.title" type="text" class="form-control">
            </div>
            <div class="col-md-4">
              <label class="form-label fw-bold">담당자</label>
              <div class="input-group">
                <input type="text" id="name" v-model="editablePlan.name" class="form-control" readonly>
                <button type="button" class="btn btn-outline-secondary" @click="openUserSearchModal">담당자 선택</button>
              </div>
            </div>
          </div>
          <div class="row mb-3">
            <div class="col-md-4">
              <label class="form-label fw-bold">상태</label>
              <select id="status" v-model="editablePlan.status" class="form-select">
                <option value="PENDING">대기</option>
                <option value="COMPLETED">완료</option>
                <option value="APPROVED">승인</option>
                <option value="CANCELED">취소</option>
              </select>
            </div>
            <div class="col-md-4">
              <label class="form-label fw-bold">수량</label>
              <input id="qty" v-model="editablePlan.qty" type="number" class="form-control" step="0.01">
            </div>
            <div class="col-md-4">
              <label class="form-label fw-bold">비고</label>
              <textarea id="remark" v-model="editablePlan.remark" class="form-control" rows="3"></textarea>
            </div>
          </div>
        </div>

        <!-- Buttons -->
        <div class="d-flex justify-content-end gap-2 mt-4">
          <button type="button" class="btn btn-outline-secondary" @click="goToList">목록으로</button>
          <template v-if="!isEditing">
            <button type="button" class="btn btn-danger" @click="handleDeletePlan">삭제</button>
            <button type="button" class="btn btn-primary" @click="startEditing">수정</button>
          </template>
          <template v-else>
            <button type="button" class="btn btn-secondary" @click="cancelEditing">취소</button>
            <button type="button" class="btn btn-success" @click="savePlan">저장</button>
          </template>
        </div>
      </div>

      <div v-else class="text-center text-muted">
        생산계획 정보를 찾을 수 없습니다.
      </div>
    </div>
  </div>

  <UserSearchModal ref="userSearchModalRef" @select="handleUserSelect" />

  <!-- Delete Confirmation Modal -->
  <div class="modal fade" id="deleteConfirmModal" tabindex="-1" aria-labelledby="deleteConfirmModalLabel" aria-hidden="true" ref="deleteModal">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title" id="deleteConfirmModalLabel">생산계획 삭제 확인</h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <div class="modal-body">
          정말로 생산계획을 삭제하시겠습니까?
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
import { fetchPlanDetail, updateProductionPlan, deleteProductionPlan } from "@/api/plan";
import UserSearchModal from "@/components/users/UserSearchModal.vue";
import { Modal } from 'bootstrap'; // Import Bootstrap's Modal

const props = defineProps({
  planId: {
    type: String,
    required: true,
  },
});

const router = useRouter();
const plan = ref(null);
const loading = ref(true);
const error = ref(null);

const isEditing = ref(false);
const editablePlan = ref(null);
const showSuccessMessage = ref(false);
const userSearchModalRef = ref(null);
const deleteModal = ref(null); // Ref for the delete confirmation modal
let modalInstance = null; // To store the Bootstrap Modal instance

async function getPlanDetail(id) {
  loading.value = true;
  error.value = null;
  try {
    const response = await fetchPlanDetail(id);
    plan.value = response.data;
  } catch (e) {
    console.error('Error fetching plan details:', e);
    error.value = '생산계획 정보를 불러오는 데 실패했습니다.';
  } finally {
    loading.value = false;
  }
}

onMounted(() => {
  if (props.planId) {
    getPlanDetail(props.planId);
  }
  if (deleteModal.value) {
    modalInstance = new Modal(deleteModal.value);
  }
});

watch(() => props.planId, (newPlanId) => {
  if (newPlanId) {
    getPlanDetail(newPlanId);
  }
});

function startEditing() {
  editablePlan.value = JSON.parse(JSON.stringify(plan.value));
  isEditing.value = true;
}

function cancelEditing() {
  editablePlan.value = null;
  isEditing.value = false;
}

async function savePlan() {
  if (!editablePlan.value) return;

  const changedData = {};
  const originalPlan = plan.value;
  const editedPlan = editablePlan.value;

  const editableFields = ['title', 'name', 'status', 'remark', 'qty'];

  for (const key of editableFields) {
    if (originalPlan[key] !== editedPlan[key]) {
      changedData[key] = editedPlan[key];
    }
  }

  if (Object.keys(changedData).length === 0) {
    isEditing.value = false;
    editablePlan.value = null;
    return;
  }

  try {
    await updateProductionPlan(props.planId, changedData);
    await getPlanDetail(props.planId); // Refresh data
    isEditing.value = false;
    editablePlan.value = null;
    showSuccessMessage.value = true;
    setTimeout(() => {
      showSuccessMessage.value = false;
    }, 3000);
  } catch (err) {
    console.error('Failed to update production plan:', err);
    error.value = '생산계획 수정에 실패했습니다.';
  }
}

async function handleDeletePlan() {
  if (modalInstance) {
    modalInstance.show();
  }
}

async function confirmDelete() {
  try {
    await deleteProductionPlan(props.planId);
    modalInstance.hide();
    router.push('/plans'); // Redirect to the list page after deletion
  } catch (err) {
    console.error('Failed to delete production plan:', err);
    error.value = '생산계획 삭제에 실패했습니다.';
    modalInstance.hide();
  }
}

function goToList() {
  router.push('/plans');
}

function openUserSearchModal() {
  userSearchModalRef.value.showModal();
}

function handleUserSelect(user) {
  if (editablePlan.value) {
    editablePlan.value.name = user.name;
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
.btn-outline-secondary {
  border-radius: 8px;
  padding: 10px 20px;
  font-weight: 600;
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
</style>