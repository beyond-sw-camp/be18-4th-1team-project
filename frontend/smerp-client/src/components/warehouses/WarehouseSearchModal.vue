<template>
  <div class="modal fade" id="warehouseSearchModal" tabindex="-1" aria-labelledby="warehouseSearchModalLabel" aria-hidden="true" ref="modalElement">
    <div class="modal-dialog modal-lg">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title" id="warehouseSearchModalLabel">창고 검색</h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <div class="modal-body">
          <div v-if="loading" class="text-center">
            <div class="spinner-border" role="status">
              <span class="visually-hidden">Loading...</span>
            </div>
          </div>
          <div v-else-if="error" class="alert alert-danger">
            {{ error }}
          </div>
          <div v-else>
            <div class="table-responsive">
              <table class="table table-hover">
                <thead>
                  <tr>
                    <th>창고명</th>
                    <th>선택</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-if="warehouses.length === 0">
                    <td colspan="4" class="text-center text-muted">창고 정보가 없습니다.</td>
                  </tr>
                  <tr v-for="warehouse in warehouses" :key="warehouse.warehouseName">
                    <td>{{ warehouse.name }}</td>
                    <td>
                      <button type="button" class="btn btn-primary btn-sm" @click="selectWarehouse(warehouse)" data-bs-dismiss="modal">
                        선택
                      </button>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, defineEmits, defineExpose } from 'vue';
import { fetchWarehouses } from '@/api/warehouse';
import { Modal } from 'bootstrap'; // Import Bootstrap's Modal

const emit = defineEmits(['select']);

const warehouses = ref([]);
const loading = ref(true);
const error = ref(null);
const modalElement = ref(null); // Ref for the modal DOM element
let bsModal = null; // To store the Bootstrap Modal instance

const fetchWarehouseList = async () => {
  loading.value = true;
  error.value = null;
  try {
    const response = await fetchWarehouses();
    warehouses.value = response.data
  } catch (e) {
    console.error('Error fetching warehouses:', e);
    error.value = '창고 정보를 불러오는 데 실패했습니다.';
  } finally {
    loading.value = false;
  }
};

function selectWarehouse(warehouse) {
  emit('select', warehouse);
}

onMounted(() => {
  fetchWarehouseList();
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
</style>