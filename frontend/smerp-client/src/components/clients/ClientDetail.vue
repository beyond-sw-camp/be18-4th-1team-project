<template>
  <div class="card shadow-sm">
    <div class="card-body">
      <h5 class="card-title mb-4">거래처 상세 정보</h5>
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

      <div v-else-if="client">
        <div v-if="!isEditing">
          <div class="row mb-3">
            <div class="col-md-4">
              <label class="form-label fw-bold">회사명</label>
              <p class="form-control-static">{{ client.companyName }}</p>
            </div>
            <div class="col-md-4">
              <label class="form-label fw-bold">사업자번호</label>
              <p class="form-control-static">{{ client.businessNumber }}</p>
            </div>
            <div class="col-md-4">
              <label class="form-label fw-bold">대표자명</label>
              <p class="form-control-static">{{ client.ceoName }}</p>
            </div>
          </div>
          <div class="row mb-3">
            <div class="col-md-4">
              <label class="form-label fw-bold">대표자 연락처</label>
              <p class="form-control-static" v-html="client.ceoPhone || '&nbsp;'"></p>
            </div>
            <div class="col-md-4">
              <label class="form-label fw-bold">회사 연락처</label>
              <p class="form-control-static" v-html="client.phone || '&nbsp;'"></p>
            </div>
            <div class="col-md-4">
              <label class="form-label fw-bold">주소</label>
              <p class="form-control-static" v-html="client.address || '&nbsp;'"></p>
            </div>
          </div>
          <div class="row mb-3">
            <div class="col-md-4">
              <label class="form-label fw-bold">우편번호</label>
              <p class="form-control-static" v-html="client.zipCode || '&nbsp;'"></p>
            </div>
            <div class="col-md-4">
              <label class="form-label fw-bold">상태</label>
              <p class="form-control-static" v-html="client.status || '&nbsp;'"></p>
            </div>
          </div>

          <hr class="my-4">

          <h6 class="card-subtitle mb-3 text-muted">담당자 정보</h6>

          <div class="row mb-3">
            <div class="col-md-4">
              <label class="form-label fw-bold">담당자 1</label>
              <p class="form-control-static" v-html="client.name1st || '&nbsp;'"></p>
            </div>
            <div class="col-md-4">
              <label class="form-label fw-bold">연락처 1</label>
              <p class="form-control-static" v-html="client.phone1st || '&nbsp;'"></p>
            </div>
            <div class="col-md-4">
              <label class="form-label fw-bold">직급 1</label>
              <p class="form-control-static" v-html="client.job1st || '&nbsp;'"></p>
            </div>
          </div>

          <div class="row mb-3">
            <div class="col-md-4">
              <label class="form-label fw-bold">담당자 2</label>
              <p class="form-control-static" v-html="client.name2nd || '&nbsp;'"></p>
            </div>
            <div class="col-md-4">
              <label class="form-label fw-bold">연락처 2</label>
              <p class="form-control-static" v-html="client.phone2nd || '&nbsp;'"></p>
            </div>
            <div class="col-md-4">
              <label class="form-label fw-bold">직급 2</label>
              <p class="form-control-static" v-html="client.job2nd || '&nbsp;'"></p>
            </div>
          </div>

          <div class="row mb-3">
            <div class="col-md-4">
              <label class="form-label fw-bold">담당자 3</label>
              <p class="form-control-static" v-html="client.name3rd || '&nbsp;'"></p>
            </div>
            <div class="col-md-4">
              <label class="form-label fw-bold">연락처 3</label>
              <p class="form-control-static" v-html="client.phone3rd || '&nbsp;'"></p>
            </div>
            <div class="col-md-4">
              <label class="form-label fw-bold">직급 3</label>
              <p class="form-control-static" v-html="client.job3rd || '&nbsp;'"></p>
            </div>
          </div>
        </div>

        <!-- Editing View -->
        <div v-else>
          <div class="row mb-3">
            <div class="col-md-4">
              <label class="form-label fw-bold">회사명</label>
              <input id="companyName" v-model="editableClient.companyName" type="text" class="form-control">
            </div>
            <div class="col-md-4">
              <label class="form-label fw-bold">사업자번호</label>
              <p class="form-control-static">{{ editableClient.businessNumber }}</p>
            </div>
            <div class="col-md-4">
              <label class="form-label fw-bold">대표자명</label>
              <input id="ceoName" v-model="editableClient.ceoName" type="text" class="form-control">
            </div>
          </div>
          <div class="row mb-3">
            <div class="col-md-4">
              <label class="form-label fw-bold">대표자 연락처</label>
              <input id="ceoPhone" v-model="editableClient.ceoPhone" type="text" class="form-control">
            </div>
            <div class="col-md-4">
              <label class="form-label fw-bold">회사 연락처</label>
              <input id="phone" v-model="editableClient.phone" type="text" class="form-control">
            </div>
            <div class="col-md-4">
              <label class="form-label fw-bold">주소</label>
              <input id="address" v-model="editableClient.address" type="text" class="form-control">
            </div>
          </div>
          <div class="row mb-3">
            <div class="col-md-4">
              <label class="form-label fw-bold">우편번호</label>
              <input id="zipCode" v-model="editableClient.zipCode" type="text" class="form-control">
            </div>
            <div class="col-md-4">
              <label class="form-label fw-bold">상태</label>
              <input id="status" v-model="editableClient.status" type="text" class="form-control">
            </div>
          </div>

          <hr class="my-4">

          <h6 class="card-subtitle mb-3 text-muted">담당자 정보</h6>

          <div class="row mb-3">
            <div class="col-md-4">
              <label class="form-label fw-bold">담당자 1</label>
              <input id="name1st" v-model="editableClient.name1st" type="text" class="form-control">
            </div>
            <div class="col-md-4">
              <label class="form-label fw-bold">연락처 1</label>
              <input id="phone1st" v-model="editableClient.phone1st" type="text" class="form-control">
            </div>
            <div class="col-md-4">
              <label class="form-label fw-bold">직급 1</label>
              <input id="job1st" v-model="editableClient.job1st" type="text" class="form-control">
            </div>
          </div>

          <div class="row mb-3">
            <div class="col-md-4">
              <label class="form-label fw-bold">담당자 2</label>
              <input id="name2nd" v-model="editableClient.name2nd" type="text" class="form-control">
            </div>
            <div class="col-md-4">
              <label class="form-label fw-bold">연락처 2</label>
              <input id="phone2nd" v-model="editableClient.phone2nd" type="text" class="form-control">
            </div>
            <div class="col-md-4">
              <label class="form-label fw-bold">직급 2</label>
              <input id="job2nd" v-model="editableClient.job2nd" type="text" class="form-control">
            </div>
          </div>

          <div class="row mb-3">
            <div class="col-md-4">
              <label class="form-label fw-bold">담당자 3</label>
              <input id="name3rd" v-model="editableClient.name3rd" type="text" class="form-control">
            </div>
            <div class="col-md-4">
              <label class="form-label fw-bold">연락처 3</label>
              <input id="phone3rd" v-model="editableClient.phone3rd" type="text" class="form-control">
            </div>
            <div class="col-md-4">
              <label class="form-label fw-bold">직급 3</label>
              <input id="job3rd" v-model="editableClient.job3rd" type="text" class="form-control">
            </div>
          </div>
        </div>

        <!-- Buttons -->
        <div class="d-flex justify-content-end gap-2 mt-4">
          <button type="button" class="btn btn-outline-secondary" @click="cancel">목록으로</button>
          <template v-if="!isEditing">
            <button v-if="isAdmin" type="button" class="btn btn-danger" @click="handleDeleteClient">삭제</button>
            <button type="button" class="btn btn-primary" @click="startEditing">수정</button>
          </template>
          <template v-else>
            <button type="button" class="btn btn-secondary" @click="cancelEditing">취소</button>
            <button type="button" class="btn btn-success" @click="saveClient">저장</button>
          </template>
        </div>
      </div>

      <div v-else class="text-center text-muted">
        거래처 정보를 찾을 수 없습니다.
      </div>
    </div>

    <!-- Delete Confirmation Modal -->
    <div class="modal fade" id="deleteConfirmModal" tabindex="-1" aria-labelledby="deleteConfirmModalLabel" aria-hidden="true" ref="deleteModal">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title" id="deleteConfirmModalLabel">거래처 삭제 확인</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body">
            정말로 거래처를 삭제하시겠습니까?
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">취소</button>
            <button type="button" class="btn btn-danger" @click="confirmDelete">삭제</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, computed } from 'vue';
import { useRouter } from 'vue-router';
import http from "@/api/http";
import { updateClient, deleteClient } from "@/api/client";
import { useUserStore } from '@/stores/user';
import { Modal } from 'bootstrap';

const userStore = useUserStore();
const isAdmin = computed(() => userStore.role === '[ROLE_ADMIN]'||'[ROLE_MANAGER]');
const deleteModal = ref(null);
let modalInstance = null;

const props = defineProps({
  clientId: {
    type: String,
    required: true,
  },
});

const router = useRouter();
const client = ref(null);
const loading = ref(true);
const error = ref(null);

const isEditing = ref(false);
const editableClient = ref(null);
const showSuccessMessage = ref(false);

async function fetchClientDetail(id) {
  loading.value = true;
  error.value = null;
  try {
    const response = await http.get(`/api/v1/clients/${id}`);
    client.value = response.data;
  } catch (e) {
    console.error('Error fetching client details:', e);
    error.value = '거래처 정보를 불러오는 데 실패했습니다.';
  } finally {
    loading.value = false;
  }
}

onMounted(() => {
  if (props.clientId) {
    fetchClientDetail(props.clientId);
  }
  if (deleteModal.value) {
    modalInstance = new Modal(deleteModal.value);
  }
});

watch(() => props.clientId, (newClientId) => {
  if (newClientId) {
    fetchClientDetail(newClientId);
  } else {
    client.value = null;
  }
});

function startEditing() {
  editableClient.value = JSON.parse(JSON.stringify(client.value));
  isEditing.value = true;
}

function cancelEditing() {
  editableClient.value = null;
  isEditing.value = false;
}

async function saveClient() {
  if (!editableClient.value) return;

  const changedData = {};
  const originalClient = client.value;
  const editedClient = editableClient.value;

  const editableFields = ['companyName', 'ceoName', 'phone', 'address', 'zipCode', 'ceoPhone', 'name1st', 'phone1st', 'job1st', 'name2nd', 'phone2nd', 'job2nd', 'name3rd', 'phone3rd', 'job3rd', 'status'];

  for (const key of editableFields) {
    if (originalClient[key] !== editedClient[key]) {
      changedData[key] = editedClient[key];
    }
  }

  if (Object.keys(changedData).length === 0) {
    isEditing.value = false;
    editableClient.value = null;
    return;
  }

  try {
    await updateClient(props.clientId, changedData);
    await fetchClientDetail(props.clientId);

    isEditing.value = false;
    editableClient.value = null;
    showSuccessMessage.value = true;
    setTimeout(() => {
      showSuccessMessage.value = false;
    }, 3000);
  } catch (err) {
    console.error('Failed to update client:', err);
    error.value = '거래처 정보 수정에 실패했습니다.';
  }
}

async function handleDeleteClient() {
  if (modalInstance) {
    modalInstance.show();
  }
}

async function confirmDelete() {
  try {
    await deleteClient(props.clientId);
    modalInstance.hide();
    router.push('/clients');
  } catch (err) {
    console.error('Failed to delete client:', err);
    error.value = '거래처 삭제에 실패했습니다.';
    modalInstance.hide();
  }
}

function cancel() {
  router.push('/clients');
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