<template>
  <div class="modal fade" id="clientSearchModal" tabindex="-1" aria-labelledby="clientSearchModalLabel" aria-hidden="true" ref="modalElement">
    <div class="modal-dialog modal-lg">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title" id="clientSearchModalLabel">거래처 검색</h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <div class="modal-body">
          <div class="mb-3">
            <input type="text" class="form-control" placeholder="거래처명 검색" v-model="searchTerm" @input="onSearch">
          </div>
          <table class="table table-hover">
            <thead>
              <tr>
                <th>회사명</th>
                <th>사업자번호</th>
                <th>대표자명</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              <tr v-if="clients.length === 0">
                <td colspan="4" class="text-center text-muted">검색 결과가 없습니다.</td>
              </tr>
              <tr v-for="client in clients" :key="client.businessNumber">
                <td>{{ client.companyName }}</td>
                <td>{{ client.businessNumber }}</td>
                <td>{{ client.ceoName }}</td>
                <td>
                  <button class="btn btn-sm btn-primary" @click="selectClient(client)" data-bs-dismiss="modal">선택</button>
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
import { fetchClients } from '@/api/client';
import Pagination from '@/components/common/Pagination.vue';
import { Modal } from 'bootstrap'; // Import Bootstrap's Modal

const clients = ref([]);
const searchTerm = ref('');
const currentPage = ref(0);
const totalPages = ref(0);
const pageSize = ref(10);
const modalElement = ref(null); // Ref for the modal DOM element
let bsModal = null; // To store the Bootstrap Modal instance

const emit = defineEmits(['select']);

async function loadClients() {
  try {
    const response = await fetchClients(currentPage.value, pageSize.value, searchTerm.value);
    clients.value = response.data.content;
    totalPages.value = response.data.totalPages;
  } catch (error) {
    console.error('Error fetching clients:', error);
  }
}


watch(searchTerm, () => {
  currentPage.value = 0;
  loadClients();
});

function selectClient(client) {
  emit('select', client);
}

function onSearch() {
}

function handlePageChange(page) {
  currentPage.value = page;
  loadClients();
}

onMounted(() => {
  loadClients();
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