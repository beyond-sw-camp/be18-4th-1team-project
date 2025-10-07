<template>
  <div class="card shadow-sm">
    <div class="card-body">
      <h5 class="card-title mb-4">새 사용자 추가</h5>

      <form @submit.prevent="submitForm">
        <div class="row g-3">
          <div class="col-md-4">
            <label for="name" class="form-label">이름</label>
            <input type="text" id="name" v-model="form.name" class="form-control" required>
          </div>

          <div class="col-md-4">
            <label for="email" class="form-label">이메일</label>
            <input type="email" id="email" v-model="form.email" class="form-control" required>
          </div>

          <div class="col-md-4">
            <label for="phone" class="form-label">연락처</label>
            <input type="tel" id="phone" v-model="form.phone" class="form-control">
          </div>

          <div class="col-md-4">
            <label for="dept" class="form-label">부서</label>
            <select id="dept" v-model="form.dept" class="form-select">
              <option disabled value="">부서를 선택하세요</option>
              <option>인사팀</option>
              <option>개발팀</option>
              <option>디자인팀</option>
              <option>영업팀</option>
            </select>
          </div>

          <div class="col-md-4">
            <label for="role" class="form-label">역할</label>
            <select id="role" v-model="form.role" class="form-select">
              <option disabled value="">역할을 선택하세요</option>
              <option>USER</option>
              <option>MANAGER</option>
              <option>ADMIN</option>
            </select>
          </div>

          <div class="col-md-4">
            <label for="address" class="form-label">주소</label>
            <input type="text" id="address" v-model="form.address" class="form-control">
          </div>

          <div class="col-md-4">
            <label for="ssn" class="form-label">주민등록번호</label>
            <input type="text" id="ssn" v-model="form.ssn" class="form-control">
          </div>

          <div class="col-md-4">
            <label for="loginId" class="form-label">로그인 ID</label>
            <input type="text" id="loginId" v-model="form.loginId" class="form-control" required>
          </div>

          <div class="col-md-4">
            <label for="password" class="form-label">비밀번호</label>
            <input type="password" id="password" v-model="form.password" class="form-control" required>
          </div>

          <div class="col-md-4">
            <label for="hireDate" class="form-label">입사일</label>
            <input type="date" id="hireDate" v-model="form.hireDate" class="form-control">
          </div>

          <div class="col-md-4">
            <label for="fireDate" class="form-label">퇴사일</label>
            <input type="date" id="fireDate" v-model="form.fireDate" class="form-control">
          </div>

          <div class="col-md-4">
            <label for="companyName" class="form-label">거래처명</label>
            <div class="input-group">
              <input type="text" id="companyName" v-model="form.companyName" class="form-control" readonly>
              <button class="btn btn-outline-secondary" type="button" @click="openClientModal">거래처 찾기</button>
            </div>
          </div>
        </div>

        <div class="d-flex justify-content-end gap-2 mt-4">
          <button type="button" class="btn btn-outline-secondary" @click="cancel">취소</button>
          <button type="submit" class="btn btn-primary">저장</button>
        </div>
      </form>
    </div>
  </div>
  <ClientSearchModal ref="clientModal" @select="onClientSelect" />
</template>

<script setup>
import { ref, onMounted } from 'vue';
import http from "@/api/http";
import ClientSearchModal from "@/components/clients/ClientSearchModal.vue";
import { Modal } from 'bootstrap';

const form = ref({
  name: '',
  email: '',
  phone: '',
  address: '',
  ssn: '',
  loginId: '',
  password: '',
  hireDate: '',
  resignDate: '',
  fireDate: '',
  dept: '',
  role: '',
  companyName: '',
  clientId: null,
});

const emit = defineEmits(['cancel', 'save']);

const clientModal = ref(null);
let modalInstance = null;

onMounted(() => {
  if (clientModal.value) {
    modalInstance = new Modal(clientModal.value.$el);
  }
});

function openClientModal() {
  if (modalInstance) {
    modalInstance.show();
  }
}

function onClientSelect(client) {
  form.value.companyName = client.companyName;
  form.value.clientId = client.businessNumber;
  if (modalInstance) {
    modalInstance.hide();
  }
}

async function submitForm() {
  try {
    const userData = {
      name: form.value.name,
      email: form.value.email,
      phone: form.value.phone,
      address: form.value.address,
      ssn: form.value.ssn,
      loginId: form.value.loginId,
      password: form.value.password,
      hireDate: form.value.hireDate,
      fireDate: form.value.fireDate || null,
      deptTitle: form.value.dept,
      role: form.value.role.toUpperCase(),
      companyName: form.value.companyName || null,
      clientId: form.value.clientId,
    };

    const response = await http.post("/api/v1/users", userData);
    console.log('User created successfully:', response.data);
    emit('save');
  } catch (error) {
    console.error('Error creating user:', error);
    alert('사용자 생성에 실패했습니다: ' + (error.response?.data?.message || error.message));
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
