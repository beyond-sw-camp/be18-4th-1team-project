<template>
  <div class="modal fade" id="itemSearchModal" tabindex="-1" aria-labelledby="itemSearchModalLabel" aria-hidden="true" ref="modalElement">
    <div class="modal-dialog modal-lg">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title" id="itemSearchModalLabel">품목 검색</h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <div class="modal-body">
          <div class="mb-3">
            <input type="text" class="form-control" placeholder="품목명 검색" v-model="searchTerm" @input="onSearch">
          </div>
          <table class="table table-hover">
            <thead>
              <tr>
                <th>품목명</th>
                <th>RFID</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              <tr v-if="items.length === 0">
                <td colspan="3" class="text-center text-muted">검색 결과가 없습니다.</td>
              </tr>
              <tr v-for="item in items" :key="item.itemId">
                <td>{{ item.name }}</td>
                <td>{{ item.rfid }}</td>
                <td>
                  <button class="btn btn-sm btn-primary" @click="selectItem(item)">선택</button>
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
import { ref, onMounted, computed, watch, defineExpose } from 'vue';
import { fetchItems } from '@/api/item';
import Pagination from '@/components/common/Pagination.vue';
import { Modal } from 'bootstrap'; // Import Bootstrap's Modal

const items = ref([]);
const searchTerm = ref('');
const currentPage = ref(0);
const totalPages = ref(0);
const pageSize = ref(10);
const modalElement = ref(null); // Ref for the modal DOM element
let bsModal = null; // To store the Bootstrap Modal instance

const emit = defineEmits(['select', 'closed']);

async function loadItems() {
  try {
    const response = await fetchItems(currentPage.value, pageSize.value, searchTerm.value);
    items.value = response.data.content;
    totalPages.value = response.data.totalPages;
  } catch (error) {
    console.error('Error fetching items:', error);
  }
}

watch(searchTerm, () => {
  currentPage.value = 0;
  loadItems();
});

function selectItem(item) {
  emit('select', item);
  if (document.activeElement instanceof HTMLElement) {
    document.activeElement.blur();
  }
  bsModal?.hide();
}

function onSearch() {
}

function handlePageChange(page) {
  currentPage.value = page;
  loadItems();
}

onMounted(() => {
  loadItems();
  // Initialize Bootstrap Modal once the component is mounted
  if (modalElement.value) {
    bsModal = new Modal(modalElement.value);
    modalElement.value.addEventListener('hidden.bs.modal', () => {
      emit('closed');
    });
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