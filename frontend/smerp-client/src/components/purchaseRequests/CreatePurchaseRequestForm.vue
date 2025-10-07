<template>
  <div class="card shadow-sm">
    <div class="card-body">
      <h5 class="card-title mb-4">새 구매 요청 등록</h5>

      <form @submit.prevent="submitForm">
        <div class="row g-3">
          <div class="col-md-6">
            <label for="empNo" class="form-label">담당자 사번</label>
            <div class="input-group">
              <input type="text" id="empNo" v-model="form.empNo" class="form-control" readonly required>
              <button type="button" class="btn btn-outline-secondary" @click="openUserSearchModal">담당자 선택</button>
            </div>
          </div>
          <div class="col-md-6">
            <label for="documentNo" class="form-label">요청날짜</label>
            <input type="date" id="documentNo" v-model="form.documentNo" class="form-control" required>
          </div>
          <div class="col-md-6">
            <label for="deliveryDate" class="form-label">납기희망일</label>
            <input type="date" id="deliveryDate" v-model="form.deliveryDate" class="form-control" required>
          </div>
          <div class="col-12">
            <label for="remark" class="form-label">비고</label>
            <textarea id="remark" v-model="form.remark" class="form-control" rows="3"></textarea>
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
              <th>총액</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(item, index) in form.items" :key="index">
              <td>{{ item.name }}</td>
              <td><input type="number" v-model="item.qty" class="form-control form-control-sm" min="1" required></td>
              <td><input type="number" v-model="item.inboundUnitPrice" class="form-control form-control-sm" step="0.01"
                  min="0" required></td>
              <td>{{ (item.qty * item.inboundUnitPrice).toFixed(2) }}</td>
              <td>
                <button type="button" class="btn btn-danger btn-sm" @click="removeItem(index)">삭제</button>
              </td>
            </tr>
            <tr v-if="form.items.length === 0">
              <td colspan="5" class="text-center text-muted">추가된 품목이 없습니다.</td>
            </tr>
          </tbody>
        </table>

        <div class="d-flex justify-content-end gap-2 mt-4">
          <button type="button" class="btn btn-outline-secondary" @click="cancel">취소</button>
          <button type="submit" class="btn btn-primary">저장</button>
        </div>
      </form>
    </div>
  </div>

  <UserSearchModal ref="userSearchModalRef" @select="handleUserSelect" />
  <ItemSearchModal ref="itemSearchModalRef" @select="handleItemSelect" />
</template>

<script setup>
import { ref } from 'vue';
import { createPurchaseRequest } from "@/api/purchaseRequest";
import UserSearchModal from "@/components/users/UserSearchModal.vue";
import ItemSearchModal from "@/components/items/ItemSearchModal.vue";

const form = ref({
  empNo: '',
  deliveryDate: '',
  remark: '',
  documentNo: '',
  items: [],
});

const emit = defineEmits(['cancel', 'save']);
const userSearchModalRef = ref(null);
const itemSearchModalRef = ref(null);

function openUserSearchModal() {
  userSearchModalRef.value.showModal();
}

function handleUserSelect(user) {
  form.value.empNo = user.empNo; // Assuming user object has an 'empNo' property
}

function openItemSearchModal() {
  itemSearchModalRef.value.showModal();
}

function handleItemSelect(item) {
  // Check if item already exists to prevent duplicates
  const existingItem = form.value.items.find(i => i.itemId === item.itemId);
  if (!existingItem) {
    form.value.items.push({
      itemId: item.itemId,
      name: item.name, // Store name for display
      qty: 1, // Default quantity
      inboundUnitPrice: 0, // Default price
    });
  } else {
    alert('이미 추가된 품목입니다.');
  }
}

function removeItem(index) {
  form.value.items.splice(index, 1);
}

async function submitForm() {
  try {
    const requestData = {
      empNo: form.value.empNo,
      deliveryDate: form.value.deliveryDate,
      remark: form.value.remark,
      documentNo: form.value.documentNo,
      items: form.value.items.map(item => ({
        itemId: item.itemId,
        qty: item.qty,
        inboundUnitPrice: item.inboundUnitPrice,
      })),
    };

    const response = await createPurchaseRequest(requestData);
    console.log('Purchase request created successfully:', response.data);
    emit('save');
  } catch (error) {
    console.error('Error creating purchase request:', error);
    alert('구매 요청 생성에 실패했습니다: ' + (error.response?.data?.message || error.message));
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

.form-control,
.form-select {
  border-radius: 8px;
  border: 1px solid #cbd5e1;
  padding: 10px 12px;
  font-size: 0.95rem;
}

.form-control:focus,
.form-select:focus {
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