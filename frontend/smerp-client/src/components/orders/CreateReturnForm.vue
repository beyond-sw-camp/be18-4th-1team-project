<template>
  <div class="card shadow-sm">
    <div class="card-body">
      <h5 class="card-title mb-4">반품 등록</h5>
      <form @submit.prevent="submitReturn">
        <div class="mb-3">
          <label for="documentNo" class="form-label">전표번호</label>
          <div class="input-group">
            <input type="text" class="form-control" id="documentNo" v-model="returnForm.documentNo" readonly required>
            <button type="button" class="btn btn-outline-secondary" data-bs-toggle="modal" data-bs-target="#orderSearchModal">
              검색
            </button>
          </div>
        </div>
        <div class="mb-3">
          <label for="remark" class="form-label">비고</label>
          <input type="text" class="form-control" id="remark" v-model="returnForm.remark">
        </div>
        <div class="mb-3">
          <label for="empNo" class="form-label">사원 번호</label>
          <div class="input-group">
            <input type="text" class="form-control" id="empNo" :value="returnForm.empNo" readonly required>
            <button type="button" class="btn btn-outline-secondary" data-bs-toggle="modal" data-bs-target="#userSearchModal">
              검색
            </button>
          </div>
        </div>

        <h6 class="mt-4 mb-3">반품 품목</h6>
        <div v-for="(item, index) in returnForm.items" :key="index" class="row mb-3 align-items-end">
          <div class="col-md-5">
            <label :for="'itemName-' + index" class="form-label">품목명</label>
            <div class="input-group">
              <input type="text" class="form-control" :id="'itemName-' + index" :value="item.itemName" readonly required>
              <button type="button" class="btn btn-outline-secondary" @click="openItemSearchModal(index)">
                검색
              </button>
            </div>
            <input type="hidden" v-model="item.itemId">
          </div>
          <div class="col-md-5">
            <label :for="'qty-' + index" class="form-label">수량</label>
            <input type="number" class="form-control" :id="'qty-' + index" v-model="item.qty" required>
          </div>
          <div class="col-md-2">
            <button type="button" class="btn btn-danger w-100" @click="removeItem(index)">삭제</button>
          </div>
        </div>
        <button type="button" class="btn btn-secondary mb-4" @click="addItem">품목 추가</button>

        <div class="d-flex justify-content-end">
          <button type="submit" class="btn btn-primary">반품 등록</button>
        </div>
      </form>
      <div v-if="successMessage" class="alert alert-success mt-3">{{ successMessage }}</div>
      <div v-if="errorMessage" class="alert alert-danger mt-3">{{ errorMessage }}</div>
    </div>
  </div>

  <ItemSearchModal ref="itemSearchModal" @select="handleItemSelect" />

  <OrderSearchModal @select="handleOrderSelect" />

  <UserSearchModal @select="handleUserSelect" />
</template>

<script setup>
import { ref } from 'vue';
import { createReturn } from '@/api/order';
import ItemSearchModal from '@/components/items/ItemSearchModal.vue';
import OrderSearchModal from '@/components/orders/OrderSearchModal.vue';
import UserSearchModal from '@/components/users/UserSearchModal.vue';

const returnForm = ref({
  documentNo: '',
  remark: '',
  empNo: '',
  items: [
    { itemId: null, itemName: '', qty: null }
  ]
});

const successMessage = ref('');
const errorMessage = ref('');
const selectedItemIndex = ref(null);
const itemSearchModal = ref(null);

function openItemSearchModal(index) {
  selectedItemIndex.value = index;
  itemSearchModal.value.showModal();
}

function addItem() {
  returnForm.value.items.push({ itemId: null, itemName: '', qty: null });
}

function removeItem(index) {
  returnForm.value.items.splice(index, 1);
}

function handleItemSelect(item) {
  if (selectedItemIndex.value !== null) {
    returnForm.value.items[selectedItemIndex.value].itemId = item.itemId;
    returnForm.value.items[selectedItemIndex.value].itemName = item.name;
    selectedItemIndex.value = null;
  }
}

function handleOrderSelect(order) {
  returnForm.value.documentNo = order.documentNo;
}

function handleUserSelect(user) {
  returnForm.value.empNo = user.empNo;
}

async function submitReturn() {
  successMessage.value = '';
  errorMessage.value = '';
  try {
    const returnData = {
      documentNo: returnForm.value.documentNo,
      remark: returnForm.value.remark,
      empNo: returnForm.value.empNo,
      items: returnForm.value.items.map(item => ({
        itemId: item.itemId,
        qty: item.qty
      }))
    };

    const response = await createReturn(returnData);
    successMessage.value = '반품이 성공적으로 등록되었습니다.'
    returnForm.value = {
      documentNo: '',
      remark: '',
      empNo: '',
      items: [{ itemId: null, itemName: '', qty: null }]
    };
  } catch (error) {
    console.error('Error creating return:', error);
    errorMessage.value = '반품 등록에 실패했습니다: ' + (error.response?.data?.message || error.message);
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
  color: #334155;
}
</style>
