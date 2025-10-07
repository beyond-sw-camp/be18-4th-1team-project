<template>
  <div class="card shadow-sm">
    <div class="card-body">
      <h5 class="card-title mb-4">새 거래처 추가</h5>

      <form @submit.prevent="submitForm">
        <div class="row g-3">
          <div class="col-md-4">
            <label for="companyName" class="form-label">회사명</label>
            <input type="text" id="companyName" v-model="form.companyName" class="form-control" required>
          </div>

          <div class="col-md-4">
            <label for="businessNumber" class="form-label">사업자번호</label>
            <input type="text" id="businessNumber" v-model="form.businessNumber" class="form-control" required>
          </div>

          <div class="col-md-4">
            <label for="ceoName" class="form-label">대표자명</label>
            <input type="text" id="ceoName" v-model="form.ceoName" class="form-control" required>
          </div>

          <div class="col-md-4">
            <label for="phone" class="form-label">대표 전화번호</label>
            <input type="tel" id="phone" v-model="form.phone" class="form-control" required>
          </div>

          <div class="col-md-4">
            <label for="ceoPhone" class="form-label">대표자 휴대폰</label>
            <input type="tel" id="ceoPhone" v-model="form.ceoPhone" class="form-control" required>
          </div>

          <div class="col-md-4">
            <label for="address" class="form-label">주소</label>
            <input type="text" id="address" v-model="form.address" class="form-control" required>
          </div>

          <div class="col-md-4">
            <label for="zipCode" class="form-label">우편번호</label>
            <input type="text" id="zipCode" v-model="form.zipCode" class="form-control" required>
          </div>

          <div class="col-md-4">
            <label for="status" class="form-label">상태</label>
            <select id="status" v-model="form.status" class="form-select" required>
              <option>SALE</option>
              <option>PURCHASE</option>
            </select>
          </div>
        </div>

        <hr class="my-4">

        <h6 class="card-subtitle mb-3 text-muted">담당자 정보</h6>

        <div class="row g-3">
          <div class="col-md-4">
            <label for="name1st" class="form-label">1차 담당자 이름</label>
            <input type="text" id="name1st" v-model="form.name1st" class="form-control" required>
          </div>

          <div class="col-md-4">
            <label for="phone1st" class="form-label">1차 담당자 휴대폰</label>
            <input type="tel" id="phone1st" v-model="form.phone1st" class="form-control" required>
          </div>

          <div class="col-md-4">
            <label for="job1st" class="form-label">1차 담당자 직급</label>
            <input type="text" id="job1st" v-model="form.job1st" class="form-control" required>
          </div>
        </div>

        <div class="row g-3 mt-3">
          <div class="col-md-4">
            <label for="name2nd" class="form-label">2차 담당자 이름</label>
            <input type="text" id="name2nd" v-model="form.name2nd" class="form-control">
          </div>

          <div class="col-md-4">
            <label for="phone2nd" class="form-label">2차 담당자 휴대폰</label>
            <input type="tel" id="phone2nd" v-model="form.phone2nd" class="form-control">
          </div>

          <div class="col-md-4">
            <label for="job2nd" class="form-label">2차 담당자 직급</label>
            <input type="text" id="job2nd" v-model="form.job2nd" class="form-control">
          </div>
        </div>

        <div class="row g-3 mt-3">
          <div class="col-md-4">
            <label for="name3rd" class="form-label">3차 담당자 이름</label>
            <input type="text" id="name3rd" v-model="form.name3rd" class="form-control">
          </div>

          <div class="col-md-4">
            <label for="phone3rd" class="form-label">3차 담당자 휴대폰</label>
            <input type="tel" id="phone3rd" v-model="form.phone3rd" class="form-control">
          </div>

          <div class="col-md-4">
            <label for="job3rd" class="form-label">3차 담당자 직급</label>
            <input type="text" id="job3rd" v-model="form.job3rd" class="form-control">
          </div>
        </div>

        <div class="d-flex justify-content-end gap-2 mt-4">
          <button type="button" class="btn btn-outline-secondary" @click="cancel">취소</button>
          <button type="submit" class="btn btn-primary">저장</button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import http from "@/api/http";

const form = ref({
  companyName: '',
  businessNumber: '',
  ceoName: '',
  phone: '',
  ceoPhone: '',
  address: '',
  zipCode: '',
  status: '',
  name1st: '',
  phone1st: '',
  job1st: '',
  name2nd: '',
  phone2nd: '',
  job2nd: '',
  name3rd: '',
  phone3rd: '',
  job3rd: '',
});

const emit = defineEmits(['cancel', 'save']);

async function submitForm() {
  try {
    const clientData = {
      companyName: form.value.companyName,
      businessNumber: form.value.businessNumber,
      ceoName: form.value.ceoName,
      phone: form.value.phone,
      ceoPhone: form.value.ceoPhone,
      address: form.value.address,
      zipCode: form.value.zipCode,
      status: form.value.status,
      name1st: form.value.name1st,
      phone1st: form.value.phone1st,
      job1st: form.value.job1st,
      name2nd: form.value.name2nd||null,
      phone2nd: form.value.phone2nd||null,
      job2nd: form.value.job2nd||null,
      name3rd: form.value.name3rd||null,
      phone3rd: form.value.phone3rd||null,
      job3rd: form.value.job3rd||null,
    };

    const response = await http.post("/api/v1/clients", clientData);
    console.log('Client created successfully:', response.data);
    emit('save');
  } catch (error) {
    console.error('Error creating client:', error);
    alert('거래처 생성에 실패했습니다: ' + (error.response?.data?.message || error.message));
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
