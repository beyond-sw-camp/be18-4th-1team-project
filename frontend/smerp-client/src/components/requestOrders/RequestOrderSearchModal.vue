<template>
  <div class="modal fade" id="requestOrderSearchModal" tabindex="-1" aria-labelledby="requestOrderSearchModalLabel" aria-hidden="true" ref="modalElement">
    <div class="modal-dialog modal-lg">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title" id="requestOrderSearchModalLabel">발주 검색</h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <div class="modal-body">
          <div class="mb-3">
            <input type="text" class="form-control" placeholder="문서 번호 또는 회사명 검색" v-model="searchTerm" @input="onSearch">
          </div>
          <table class="table table-hover">
            <thead>
              <tr>
                <th>발주 ID</th>
                <th>문서 번호</th>
                <th>회사명</th>
                <th>납기일</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              <tr v-if="requestOrders.length === 0">
                <td colspan="5" class="text-center text-muted">검색 결과가 없습니다.</td>
              </tr>
              <tr v-for="requestOrder in requestOrders" :key="requestOrder.roId">
                <td>{{ requestOrder.roId }}</td>
                <td>{{ requestOrder.documentNo }}</td>
                <td>{{ requestOrder.companyName }}</td>
                <td>{{ requestOrder.deliveryDate }}</td>
                <td>
                  <button class="btn btn-sm btn-primary" @click="selectRequestOrder(requestOrder)" data-bs-dismiss="modal">선택</button>
                </td>
              </tr>
            </tbody>
          </table>
          <Pagination
            :currentPage="currentPage"
            :totalPages="totalPages"
            @page-changed="handlePageChange"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, defineExpose } from 'vue';
import { fetchRequestOrders } from '@/api/requestOrder';
import Pagination from '@/components/common/Pagination.vue';
import { Modal } from 'bootstrap'; // Import Bootstrap's Modal

const requestOrders = ref([]);
const searchTerm = ref('');
const currentPage = ref(0);
const totalPages = ref(0);
const pageSize = ref(10);
const modalElement = ref(null); // Ref for the modal DOM element
let bsModal = null; // To store the Bootstrap Modal instance

const emit = defineEmits(['select']);

async function loadRequestOrders() {
  try {
    const response = await fetchRequestOrders(currentPage.value, pageSize.value, searchTerm.value);
    requestOrders.value = response.data.content;
    totalPages.value = response.data.totalPages;
  } catch (error) {
    console.error('Error fetching request orders:', error);
  }
}

watch(searchTerm, () => {
  currentPage.value = 0;
  loadRequestOrders();
});

function selectRequestOrder(requestOrder) {
  emit('select', requestOrder);
}

function onSearch() {
  // This function is called on input, loadRequestOrders will handle the actual search
}

function handlePageChange(page) {
  currentPage.value = page;
  loadRequestOrders();
}

onMounted(() => {
  loadRequestOrders();
  // Initialize Bootstrap Modal once the component is mounted
  if (modalElement.value) {
    bsModal = new Modal(modalElement.value);
  }
});

// Expose a method to show the modal
const showModal = () => {
  bsModal?.show();
};

defineExpose({ showModal });
</script>

<style scoped>
.modal-body {
  padding: 20px;
}
.form-control {
  border-radius: 8px;
  border: 1px solid #cbd5e1;
  padding: 10px 12px;
  font-size: 0.95rem;
}
.form-control:focus {
  border-color: var(--brand, #2563eb);
  box-shadow: 0 0 0 3px var(--brand-l, #dbe7ff);
  outline: none;
}
.table thead th {
  font-size: 12px;
  letter-spacing: .02em;
  font-weight: 600;
  color: #6b7280;
  background: #f8f9fa;
  border-bottom: 1px solid #e9ecef !important;
  padding: 14px 16px;
  vertical-align: middle;
}
.table tbody td {
  border-top: 1px solid #eef1f6 !important;
  padding: 14px 16px;
  vertical-align: middle;
  color: #334155;
  font-size: 0.9rem;
}
.btn-primary {
  background: var(--brand, #2563eb);
  border: none;
  border-radius: 8px;
  padding: 8px 16px;
  font-weight: 600;
}
.btn-primary:hover {
  background: var(--brand-d, #1e40af);
}
</style>