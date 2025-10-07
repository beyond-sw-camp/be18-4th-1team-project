<template>
  <div class="card shadow-sm">
    <div class="card-body">
      <h5 class="card-title mb-4">주문 생성</h5>
      <form @submit.prevent="submitOrder">
        <div class="mb-3">
          <label for="documentDate" class="form-label">문서 날짜</label>
          <input type="date" class="form-control" id="documentDate" v-model="order.documentDate" required>
        </div>
        <div class="mb-3">
          <label class="form-label">회사명</label>
          <div class="input-group">
            <input type="text" class="form-control" :value="order.companyName" readonly required>
            <button type="button" class="btn btn-outline-secondary" data-bs-toggle="modal" data-bs-target="#clientSearchModal">
              검색
            </button>
          </div>
          <input type="hidden" v-model="order.companyId"> <!-- Hidden field for company ID -->
        </div>
        <div class="mb-3">
          <label class="form-label">담당자</label>
          <div class="input-group">
            <input type="text" class="form-control" :value="order.empNo" readonly required> <!-- Display empNo -->
            <button type="button" class="btn btn-outline-secondary" data-bs-toggle="modal" data-bs-target="#userSearchModal">
              검색
            </button>
          </div>
          <input type="hidden" v-model="order.empNo"> <!-- Hidden field for employee ID -->
        </div>
        <div class="mb-3">
          <label for="deliveryDate" class="form-label">납기일</label>
          <input type="date" class="form-control" id="deliveryDate" v-model="order.deliveryDate" required>
        </div>
        <div class="mb-3">
          <label for="remark" class="form-label">비고</label>
          <textarea class="form-control" id="remark" v-model="order.remark"></textarea>
        </div>

        <h6 class="mt-4 mb-3">품목 정보</h6>
        <div v-for="(item, index) in order.items" :key="index" class="row mb-3 align-items-end">
          <div class="col-md-5">
            <label :for="'itemName-' + index" class="form-label">품목명</label>
            <div class="input-group">
              <input type="text" class="form-control" :id="'itemName-' + index" :value="item.itemName" readonly required>
              <button type="button" class="btn btn-outline-secondary" @click="openItemSearchModal(index)">
                검색
              </button>
            </div>
            <input type="hidden" v-model="item.itemId"> <!-- Hidden field for item ID -->
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
          <button type="submit" class="btn btn-primary">주문 생성</button>
        </div>
      </form>
      <div v-if="successMessage" class="alert alert-success mt-3">{{ successMessage }}</div>
      <div v-if="errorMessage" class="alert alert-danger mt-3">{{ errorMessage }}</div>
    </div>
  </div>

  <ClientSearchModal ref="clientModal" @select="handleClientSelect" />

  <UserSearchModal @select="handleUserSelect" />

  <ItemSearchModal ref="itemSearchModal" @select="handleItemSelect" />
</template>

<script setup>
import { ref } from 'vue';
import { createOrder } from '@/api/order';
import ClientSearchModal from '@/components/clients/ClientSearchModal.vue';
import UserSearchModal from '@/components/users/UserSearchModal.vue';
import ItemSearchModal from '@/components/items/ItemSearchModal.vue';

const order = ref({
  documentDate: '',
  companyName: '',
  companyId: null,
  empName: '',
  empNo: '',
  deliveryDate: '',
  remark: '',
  status: 'PENDING',
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
  order.value.items.push({ itemId: null, itemName: '', qty: null });
}

function removeItem(index) {
  order.value.items.splice(index, 1);
}

function handleClientSelect(client) {
  order.value.companyName = client.companyName;
}

function handleUserSelect(user) {
  order.value.empNo = user.empNo;
}

function handleItemSelect(item) {
  if (selectedItemIndex.value !== null) {
    order.value.items[selectedItemIndex.value].itemId = item.itemId;
    order.value.items[selectedItemIndex.value].itemName = item.name;
    selectedItemIndex.value = null;
  }
}

async function submitOrder() {
  successMessage.value = '';
  errorMessage.value = '';
  try {
    const orderData = {
      documentDate: order.value.documentDate,
      companyName: order.value.companyName,
      empNo: order.value.empNo,
      deliveryDate: order.value.deliveryDate,
      remark: order.value.remark,
      items: order.value.items.map(item => ({
        itemId: item.itemId,
        qty: item.qty
      }))
    };

    const response = await createOrder(orderData);
    successMessage.value = '주문이 성공적으로 생성되었습니다.'
    order.value = {
      documentDate: '',
      companyName: '',
      empNo: '',
      deliveryDate: '',
      remark: '',
      items: [{ itemId: null, itemName: '', qty: null }]
    };
  } catch (error) {
    console.error('Error creating order:', error);
    errorMessage.value = '주문 생성에 실패했습니다: ' + (error.response?.data?.message || error.message);
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
