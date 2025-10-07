<template>
  <div class="card shadow-sm">
    <div class="card-body">
      <h5 class="card-title mb-4">발주 상세 정보</h5>

      <div v-if="showSuccessMessage" class="alert alert-success" role="alert">
        수정이 완료됐습니다.
      </div>

      <div v-if="loading" class="text-center">
        <div class="spinner-border" role="status">
          <span class="visually-hidden">Loading...</span>
        </div>
      </div>

      <div v-else-if="requestOrder">
        <div v-if="!isEditing">
          <div class="row mb-3">
            <div class="col-md-4">
              <label class="form-label fw-bold">문서 번호</label>
              <p class="form-control-static">{{ requestOrder.documentNo }}</p>
            </div>
            <div class="col-md-4">
              <label class="form-label fw-bold">담당자 사번</label>
              <p class="form-control-static">{{ requestOrder.empNo }}</p>
            </div>
            <div class="col-md-4">
              <label class="form-label fw-bold">회사명</label>
              <p class="form-control-static">{{ requestOrder.companyName }}</p>
            </div>
          </div>
          <div class="row mb-3">
            <div class="col-md-4">
              <label class="form-label fw-bold">납기일</label>
              <p class="form-control-static">{{ requestOrder.deliveryDate }}</p>
            </div>
            <div class="col-md-4">
              <label class="form-label fw-bold">상태</label>
              <p class="form-control-static">{{ requestOrder.status }}</p>
            </div>
            <div class="col-md-4">
              <label class="form-label fw-bold">비고</label>
              <p class="form-control-static">{{ requestOrder.remark }}</p>
            </div>
          </div>
          <div class="row mb-3">
            <div class="col-md-4">
              <label class="form-label fw-bold">생성일</label>
              <p class="form-control-static">{{ requestOrder.createdAt }}</p>
            </div>
            <div class="col-md-4">
              <label class="form-label fw-bold">수정일</label>
              <p class="form-control-static">{{ requestOrder.updatedAt || 'N/A' }}</p>
            </div>
          </div>

          <hr class="my-4">

          <h6 class="card-subtitle mb-3 text-muted">품목 정보</h6>
          <table class="table table-bordered">
            <thead>
              <tr>
                <th>품목 ID</th>
                <th>품목명</th>
                <th>수량</th>
                <th>단가</th>
                <th>특별 단가</th>
                <th>총액</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(item, index) in requestOrder.items" :key="index">
                <td>{{ item.itemId }}</td>
                <td>{{ item.name }}</td>
                <td>{{ item.qty }}</td>
                <td>{{ item.inboundUnitPrice }}</td>
                <td>{{ item.specialPrice || 'N/A' }}</td>
                <td>{{ (item.qty * item.inboundUnitPrice).toFixed(2) }}</td>
              </tr>
              <tr v-if="requestOrder.items.length === 0">
                <td colspan="6" class="text-center text-muted">추가된 품목이 없습니다.</td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- Editing View -->
        <div v-else>
          <div class="row mb-3">
            <div class="col-md-4">
              <label class="form-label fw-bold">문서 번호</label>
              <input id="documentNo" v-model="editableRequestOrder.documentNo" type="text" class="form-control">
            </div>
            <div class="col-md-4">
              <label class="form-label fw-bold">담당자 사번</label>
              <div class="input-group">
                <input type="text" id="empNo" v-model="editableRequestOrder.empNo" class="form-control" readonly>
                <button type="button" class="btn btn-outline-secondary" @click="openUserSearchModal">담당자 선택</button>
              </div>
            </div>
            <div class="col-md-4">
              <label class="form-label fw-bold">회사명</label>
              <div class="input-group">
                <input type="text" id="companyName" v-model="editableRequestOrder.companyName" class="form-control" readonly>
                <button type="button" class="btn btn-outline-secondary" @click="openClientSearchModal">거래처 선택</button>
              </div>
            </div>
          </div>
          <div class="row mb-3">
            <div class="col-md-4">
              <label class="form-label fw-bold">납기일</label>
              <input id="deliveryDate" v-model="editableRequestOrder.deliveryDate" type="date" class="form-control">
            </div>
            <div class="col-md-4">
              <label class="form-label fw-bold">상태</label>
              <select id="status" v-model="editableRequestOrder.status" class="form-select">
                <option value="PENDING">대기</option>
                <option value="COMPLETED">완료</option>
                <option value="APPROVED">승인</option>
                <option value="CANCELED">취소</option>
              </select>
            </div>
            <div class="col-md-4">
              <label class="form-label fw-bold">비고</label>
              <textarea id="remark" v-model="editableRequestOrder.remark" class="form-control" rows="3"></textarea>
            </div>
          </div>

          <hr class="my-4">

          <h6 class="card-subtitle mb-3 text-muted">품목 정보</h6>
          <div class="mb-3">
            <button type="button" class="btn btn-outline-primary btn-sm" @click="openItemSearchModal">품목 추가</button>
          </div>
          <table class="table table-bordered">
            <thead>
              <tr>
                <th>품목명</th>
                <th>수량</th>
                <th>단가</th>
                <th>특별 단가</th>
                <th>총액</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(item, index) in editableRequestOrder.items" :key="index">
                <td>{{ item.name }}</td>
                <td><input type="number" v-model="item.qty" class="form-control form-control-sm" min="1"></td>
                <td><input type="number" v-model="item.inboundUnitPrice" class="form-control form-control-sm" step="0.01" min="0"></td>
                <td><input type="number" v-model="item.specialPrice" class="form-control form-control-sm" step="0.01" min="0"></td>
                <td>{{ (item.qty * item.inboundUnitPrice).toFixed(2) }}</td>
                <td>
                  <button type="button" class="btn btn-danger btn-sm" @click="removeItem(index)">삭제</button>
                </td>
              </tr>
              <tr v-if="editableRequestOrder.items.length === 0">
                <td colspan="6" class="text-center text-muted">추가된 품목이 없습니다.</td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- Buttons -->
        <div class="d-flex justify-content-end gap-2 mt-4">
          <button type="button" class="btn btn-outline-secondary" @click="goToList">목록으로</button>
          <template v-if="!isEditing">
            <button type="button" class="btn btn-danger" @click="handleDeleteRequestOrder">삭제</button>
            <button type="button" class="btn btn-primary" @click="startEditing">수정</button>
          </template>
          <template v-else>
            <button type="button" class="btn btn-secondary" @click="cancelEditing">취소</button>
            <button type="button" class="btn btn-success" @click="saveRequestOrder">저장</button>
          </template>
        </div>
      </div>

      <div v-else class="text-center text-muted">
        발주 정보를 찾을 수 없습니다.
      </div>
    </div>
  </div>

  <UserSearchModal ref="userSearchModalRef" @select="handleUserSelect" />
  <ClientSearchModal ref="clientSearchModalRef" @select="handleClientSelect" />
  <ItemSearchModal ref="itemSearchModalRef" @select="handleItemSelect" />

  <!-- Delete Confirmation Modal -->
  <div class="modal fade" id="deleteConfirmModal" tabindex="-1" aria-labelledby="deleteConfirmModalLabel" aria-hidden="true" ref="deleteModal">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title" id="deleteConfirmModalLabel">발주 삭제 확인</h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <div class="modal-body">
          정말로 발주를 삭제하시겠습니까?
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
import { fetchRequestOrderDetail, updateRequestOrder, deleteRequestOrder } from "@/api/requestOrder";
import UserSearchModal from "@/components/users/UserSearchModal.vue";
import ClientSearchModal from "@/components/clients/ClientSearchModal.vue";
import ItemSearchModal from "@/components/items/ItemSearchModal.vue";
import { Modal } from 'bootstrap'; // Import Bootstrap's Modal

const props = defineProps({
  roId: {
    type: String,
    required: true,
  },
});

const router = useRouter();
const requestOrder = ref(null);
const loading = ref(true);
const error = ref(null);

const isEditing = ref(false);
const editableRequestOrder = ref(null);
const showSuccessMessage = ref(false);
const userSearchModalRef = ref(null);
const clientSearchModalRef = ref(null);
const itemSearchModalRef = ref(null);
const deleteModal = ref(null); // Ref for the delete confirmation modal
let modalInstance = null; // To store the Bootstrap Modal instance

async function getRequestOrderDetail(id) {
  loading.value = true;
  error.value = null;
  try {
    const response = await fetchRequestOrderDetail(id);
    requestOrder.value = response.data;
  } catch (e) {
    console.error('Error fetching request order details:', e);
    error.value = '발주 정보를 불러오는 데 실패했습니다.';
  } finally {
    loading.value = false;
  }
}

onMounted(() => {
  if (props.roId) {
    getRequestOrderDetail(props.roId);
  }
  if (deleteModal.value) {
    modalInstance = new Modal(deleteModal.value);
  }
});

watch(() => props.roId, (newRoId) => {
  if (newRoId) {
    getRequestOrderDetail(newRoId);
  }
});

function startEditing() {
  editableRequestOrder.value = JSON.parse(JSON.stringify(requestOrder.value));
  isEditing.value = true;
}

function cancelEditing() {
  editableRequestOrder.value = null;
  isEditing.value = false;
}

async function saveRequestOrder() {
  if (!editableRequestOrder.value) return;

  const changedData = {};
  const originalRequestOrder = requestOrder.value;
  const editedRequestOrder = editableRequestOrder.value;

  const editableFields = ['documentNo', 'empNo', 'companyName', 'deliveryDate', 'remark', 'status'];

  for (const key of editableFields) {
    if (originalRequestOrder[key] !== editedRequestOrder[key]) {
      changedData[key] = editedRequestOrder[key];
    }
  }

  // deliveryDate가 null이 아니고, 변경되지 않았더라도 항상 포함
  if (editedRequestOrder.deliveryDate && !changedData.deliveryDate) {
    changedData.deliveryDate = editedRequestOrder.deliveryDate;
  }

  // Handle items separately
  // This is a simplified comparison. A more robust solution would compare each item's properties.
  if (JSON.stringify(originalRequestOrder.items) !== JSON.stringify(editedRequestOrder.items)) {
    changedData.items = editedRequestOrder.items.map(item => ({
      itemId: item.itemId,
      qty: item.qty,
      inboundUnitPrice: item.inboundUnitPrice,
      specialPrice: item.specialPrice, // Include specialPrice
    }));
  }

  if (Object.keys(changedData).length === 0) {
    isEditing.value = false;
    editableRequestOrder.value = null;
    return;
  }

  try {
    await updateRequestOrder(props.roId, changedData);
    await getRequestOrderDetail(props.roId); // Refresh data
    isEditing.value = false;
    editableRequestOrder.value = null;
    showSuccessMessage.value = true;
    setTimeout(() => {
      showSuccessMessage.value = false;
    }, 3000);
  } catch (err) {
    console.error('Failed to update request order:', err);
    error.value = '발주 수정에 실패했습니다.';
  }
}

async function handleDeleteRequestOrder() {
  if (modalInstance) {
    modalInstance.show();
  }
}

async function confirmDelete() {
  try {
    await deleteRequestOrder(props.roId);
    modalInstance.hide();
    router.push('/request-orders'); // Redirect to the list page after deletion
  } catch (err) {
    console.error('Failed to delete request order:', err);
    error.value = '발주 삭제에 실패했습니다.';
    modalInstance.hide();
  }
}

function goToList() {
  router.push('/request-orders');
}

function openUserSearchModal() {
  userSearchModalRef.value.showModal();
}

function handleUserSelect(user) {
  if (editableRequestOrder.value) {
    editableRequestOrder.value.empNo = user.empNo;
  }
}

function openClientSearchModal() {
  clientSearchModalRef.value.showModal();
}

function handleClientSelect(client) {
  if (editableRequestOrder.value) {
    editableRequestOrder.value.companyName = client.companyName;
  }
}

function openItemSearchModal() {
  itemSearchModalRef.value.showModal();
}

function handleItemSelect(item) {
  if (editableRequestOrder.value) {
    const existingItem = editableRequestOrder.value.items.find(i => i.itemId === item.itemId);
    if (!existingItem) {
      editableRequestOrder.value.items.push({
        itemId: item.itemId,
        name: item.name,
        qty: 1,
        inboundUnitPrice: 0,
        specialPrice: 0, // Default specialPrice
      });
    } else {
      alert('이미 추가된 품목입니다.');
    }
  }
}

function removeItem(index) {
  if (editableRequestOrder.value) {
    editableRequestOrder.value.items.splice(index, 1);
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