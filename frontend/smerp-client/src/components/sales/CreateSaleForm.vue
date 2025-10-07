<template>
  <div class="card shadow-sm">
    <div class="card-body">
      <h5 class="card-title mb-4">판매 생성</h5>
      <form @submit.prevent="submitSale">
        <div class="mb-3">
          <label for="orderDocumentNo" class="form-label">주문 전표번호</label>
          <div class="input-group">
            <input type="text" class="form-control" id="orderDocumentNo" v-model="sale.orderDocumentNo" readonly required>
            <button type="button" class="btn btn-outline-secondary" data-bs-toggle="modal" data-bs-target="#orderSearchModal">
              검색
            </button>
          </div>
        </div>
        <div class="mb-3">
          <label for="documentDate" class="form-label">문서 날짜</label>
          <input type="date" class="form-control" id="documentDate" v-model="sale.documentDate" required>
        </div>
        
        <div class="mb-3">
          <label for="warehouseName" class="form-label">창고명</label>
          <div class="input-group">
            <input type="text" class="form-control" id="warehouseName" v-model="sale.warehouseName" readonly required>
            <button type="button" class="btn btn-outline-secondary" data-bs-toggle="modal" data-bs-target="#warehouseSearchModal">
              검색
            </button>
          </div>
        </div>
        <div class="mb-3">
          <label for="remark" class="form-label">비고</label>
          <input type="text" class="form-control" id="remark" v-model="sale.remark">
        </div>
        <div class="d-flex justify-content-end">
          <button type="submit" class="btn btn-primary">판매 주문 생성</button>
        </div>
      </form>
      <div v-if="successMessage" class="alert alert-success mt-3">{{ successMessage }}</div>
      <div v-if="errorMessage" class="alert alert-danger mt-3">{{ errorMessage }}</div>
    </div>
  </div>

  <!-- Order Search Modal -->
  <OrderSearchModal @select="handleOrderSelect" />

  <!-- Warehouse Search Modal -->
  <WarehouseSearchModal @select="handleWarehouseSelect" />
</template>

<script setup>
import { ref } from 'vue';
import { createSale } from '@/api/sale';
import OrderSearchModal from '@/components/orders/OrderSearchModal.vue';
import WarehouseSearchModal from '@/components/warehouses/WarehouseSearchModal.vue';

const sale = ref({
  orderDocumentNo: '',
  documentDate: '',
  remark: '',
  warehouseName: ''
});

const successMessage = ref('');
const errorMessage = ref('');

function handleOrderSelect(order) {
  sale.value.orderDocumentNo = order.documentNo;
}

function handleWarehouseSelect(warehouse) {
  sale.value.warehouseName = warehouse.name;
}

async function submitSale() {
  successMessage.value = '';
  errorMessage.value = '';
  try {
    const saleData = {
      orderDocumentNo: sale.value.orderDocumentNo,
      documentDate: sale.value.documentDate,
      remark: sale.value.remark,
      warehouseName: sale.value.warehouseName,
    };

    const response = await createSale(saleData);
    successMessage.value = '판매 주문이 성공적으로 생성되었습니다.'
    sale.value = {
      orderDocumentNo: '',
      documentDate: '',
      remark: '',
      warehouseName: ''
    };
  } catch (error) {
    console.error('Error creating sale order:', error);
    errorMessage.value = '판매 주문 생성에 실패했습니다: ' + (error.response?.data?.message || error.message);
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