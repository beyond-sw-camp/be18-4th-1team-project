<template>
  <div class="modal fade" id="productionPlanSearchModal" tabindex="-1" aria-labelledby="productionPlanSearchModalLabel"
    aria-hidden="true" ref="modalElement">
    <div class="modal-dialog modal-lg">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title" id="productionPlanSearchModalLabel">생산 계획 검색</h5>
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
                    <th>계획 ID</th>
                    <th>문서 번호</th>
                    <th>선택</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="plan in productionPlanList" :key="plan.id"> <td>{{ plan.id }}</td> <td>{{ plan.title }}</td> <td> <button type="button" class="btn btn-primary btn-sm" @click="selectProductionPlan(plan)" data-bs-dismiss="modal"> 선택 </button> </td> </tr>
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
import { fetchProductionPlans } from '@/api/plan';
import { Modal } from 'bootstrap'; // Import Bootstrap's Modal

const emit = defineEmits(['select']);

const productionPlanList = ref([]);
const loading = ref(true);
const error = ref(null);
const modalElement = ref(null); // Ref for the modal DOM element
let bsModal = null; // To store the Bootstrap Modal instance

const fetchProductionPlanList = async () => {
  loading.value = true;
  error.value = null;
  try {
    const response = await fetchProductionPlans();
    console.log('API Response for Production Plans:', response.data);
    productionPlanList.value = response.data.productionPlans || [];
  } catch (e) {
    console.error('Error fetching production plans:', e);
    error.value = '생산 계획 정보를 불러오는 데 실패했습니다.';
  } finally {
    loading.value = false;
  }
};

function selectProductionPlan(plan) {
  emit('select', plan);
}

onMounted(() => {
  fetchProductionPlanList();
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

<style scoped></style>
