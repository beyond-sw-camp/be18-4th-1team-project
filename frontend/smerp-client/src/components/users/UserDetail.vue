<template>
  <div class="card shadow-sm">
    <div class="card-body">
      <h5 class="card-title mb-4">사용자 상세 정보</h5>
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

      <div v-else-if="user">
        <div v-if="!isEditing">
          <div class="row mb-3">
            <div class="col-md-4">
              <label class="form-label fw-bold">이름</label>
              <p class="form-control-static">{{ user.name }}</p>
            </div>
            <div class="col-md-4">
              <label class="form-label fw-bold">사번</label>
              <p class="form-control-static">{{ user.empNo }}</p>
            </div>
            <div class="col-md-4">
              <label class="form-label fw-bold">로그인 ID</label>
              <p class="form-control-static">{{ user.loginId }}</p>
            </div>
          </div>
          <div class="row mb-3">
            <div class="col-md-4">
              <label class="form-label fw-bold">이메일</label>
              <p class="form-control-static">{{ user.email }}</p>
            </div>
            <div class="col-md-4">
              <label class="form-label fw-bold">연락처</label>
              <p class="form-control-static" v-html="user.phone || '&nbsp;'"></p>
            </div>
            <div class="col-md-4">
              <label class="form-label fw-bold">부서</label>
              <p class="form-control-static" v-html="user.deptTitle || '&nbsp;'"></p>
            </div>
          </div>
          <div class="row mb-3">
            <div class="col-md-4">
              <label class="form-label fw-bold">역할</label>
              <p class="form-control-static">{{ user.role }}</p>
            </div>
            <div class="col-md-4">
              <label class="form-label fw-bold">거래처명</label>
              <p class="form-control-static" v-html="user.companyName || '&nbsp;'"></p>
            </div>
            <div class="col-md-4">
              <label class="form-label fw-bold">주소</label>
              <p class="form-control-static" v-html="user.address || '&nbsp;'"></p>
            </div>
          </div>
          <div class="row mb-3">
            <div class="col-md-4">
              <label class="form-label fw-bold">주민등록번호</label>
              <p class="form-control-static" v-html="user.ssn || '&nbsp;'"></p>
            </div>
            <div class="col-md-4">
              <label class="form-label fw-bold">입사일</label>
              <p class="form-control-static" v-html="user.hireDate || '&nbsp;'"></p>
            </div>
            <div class="col-md-4">
              <label class="form-label fw-bold">퇴사일</label>
              <p class="form-control-static" v-html="user.fireDate || '&nbsp;'"></p>
            </div>
          </div>
        </div>

        <!-- Editing View -->
        <div v-else>
          <div class="row mb-3">
            <div class="col-md-4">
              <label class="form-label fw-bold">이름</label>
              <p class="form-control-static">{{ editableUser.name }}</p>
            </div>
            <div class="col-md-4">
              <label class="form-label fw-bold">사번</label>
              <p class="form-control-static">{{ editableUser.empNo }}</p>
            </div>
            <div class="col-md-4">
              <label class="form-label fw-bold">로그인 ID</label>
              <p class="form-control-static">{{ editableUser.loginId }}</p>
            </div>
          </div>
          <div class="row mb-3">
            <div class="col-md-4">
              <label class="form-label fw-bold">이메일</label>
              <p class="form-control-static">{{ editableUser.email }}</p>
            </div>
            <div class="col-md-4">
              <label for="phone" class="form-label fw-bold">연락처</label>
              <input id="phone" v-model="editableUser.phone" type="text" class="form-control">
            </div>
            <div class="col-md-4">
              <label for="deptTitle" class="form-label fw-bold">부서</label>
              <input id="deptTitle" v-model="editableUser.deptTitle" type="text" class="form-control">
            </div>
          </div>
          <div class="row mb-3">
            <div class="col-md-4">
              <label class="form-label fw-bold">역할</label>
              <p class="form-control-static">{{ editableUser.role }}</p>
            </div>
            <div class="col-md-4">
              <label for="companyName" class="form-label">거래처명</label>
              <div class="input-group">
                <input type="text" id="companyName" v-model="editableUser.companyName" class="form-control" readonly>
                <button class="btn btn-outline-secondary" type="button" @click="openClientModal">거래처 찾기</button>
              </div>
            </div>
            <div class="col-md-4">
              <label for="address" class="form-label fw-bold">주소</label>
              <input id="address" v-model="editableUser.address" type="text" class="form-control">
            </div>
          </div>
          <div class="row mb-3">
            <div class="col-md-4">
              <label class="form-label fw-bold">주민등록번호</label>
              <p class="form-control-static">{{ editableUser.ssn }}</p>
            </div>
            <div class="col-md-4">
              <label class="form-label fw-bold">입사일</label>
              <p class="form-control-static">{{ editableUser.hireDate }}</p>
            </div>
            <div class="col-md-4">
              <label for="fireDate" class="form-label fw-bold">퇴사일</label>
              <input id="fireDate" v-model="editableUser.fireDate" type="date" class="form-control">
            </div>
          </div>
        </div>

        <!-- Buttons -->
        <div class="d-flex justify-content-end gap-2 mt-4">
          <button type="button" class="btn btn-outline-secondary" @click="cancel">목록으로</button>
          <template v-if="!isEditing">
            <button type="button" class="btn btn-primary" @click="startEditing">수정</button>
          </template>
          <template v-else>
            <button type="button" class="btn btn-secondary" @click="cancelEditing">취소</button>
            <button type="button" class="btn btn-success" @click="saveUser">저장</button>
          </template>
        </div>
      </div>

      <div v-else class="text-center text-muted">
        사용자 정보를 찾을 수 없습니다.
      </div>
    </div>
  </div>
  <ClientSearchModal ref="clientModal" @select="onClientSelect" />
</template>

<script setup>
import { ref, onMounted, watch } from 'vue';
import { useRouter } from 'vue-router';
import http from "@/api/http";
import { updateUser } from "@/api/user";
import ClientSearchModal from "@/components/clients/ClientSearchModal.vue";
import { Modal } from 'bootstrap';

const props = defineProps({
  userId: {
    type: String,
    required: true,
  },
});

const router = useRouter();
const user = ref(null);
const loading = ref(true);
const error = ref(null);

const isEditing = ref(false);
const editableUser = ref(null);
const showSuccessMessage = ref(false);

const clientModal = ref(null);
let modalInstance = null;

async function fetchUserDetail(id) {
  loading.value = true;
  error.value = null;
  try {
    const response = await http.get(`/api/v1/users/${id}`);
    user.value = response.data;
  } catch (e) {
    console.error('Error fetching user details:', e);
    error.value = '사용자 정보를 불러오는 데 실패했습니다.';
  } finally {
    loading.value = false;
  }
}

onMounted(() => {
  if (props.userId) {
    fetchUserDetail(props.userId);
  }
  if (clientModal.value) {
    modalInstance = new Modal(clientModal.value.$el);
  }
});

watch(() => props.userId, (newUserId) => {
  if (newUserId) {
    fetchUserDetail(newUserId);
  } else {
    user.value = null;
  }
});

function startEditing() {
  editableUser.value = JSON.parse(JSON.stringify(user.value));
  isEditing.value = true;
}

function cancelEditing() {
  editableUser.value = null;
  isEditing.value = false;
}

function openClientModal() {
  if (modalInstance) {
    modalInstance.show();
  }
}

function onClientSelect(client) {
  if (editableUser.value) {
    editableUser.value.companyName = client.companyName;
    editableUser.value.clientId = client.businessNumber;
  }
  if (modalInstance) {
    modalInstance.hide();
  }
}

async function saveUser() {
  if (!editableUser.value) return;

  const changedData = {};
  const originalUser = user.value;
  const editedUser = editableUser.value;

  const editableFields = ['address', 'phone', 'deptTitle', 'fireDate', 'companyName', 'clientId'];

  for (const key of editableFields) {
    if (originalUser[key] !== editedUser[key]) {
      changedData[key] = editedUser[key];
    }
  }

  if (Object.keys(changedData).length === 0) {
    isEditing.value = false;
    editableUser.value = null;
    return;
  }

  try {
    await updateUser(props.userId, changedData);
    await fetchUserDetail(props.userId);

    isEditing.value = false;
    editableUser.value = null;
    showSuccessMessage.value = true;
    setTimeout(() => {
      showSuccessMessage.value = false;
    }, 3000);
  } catch (err) {
    console.error('Failed to update user:', err);
    error.value = '사용자 정보 수정에 실패했습니다.';
  }
}

function cancel() {
  router.push('/users');
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
