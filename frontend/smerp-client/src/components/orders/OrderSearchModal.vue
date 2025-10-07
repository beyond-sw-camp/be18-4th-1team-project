<template>
  <div class="modal fade" id="orderSearchModal" tabindex="-1" aria-labelledby="orderSearchModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title" id="orderSearchModalLabel">주문 검색</h5>
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
                    <th>주문 전표번호</th>
                    <th>주문 상태</th>
                    <th>거래처명</th>
                    <th>선택</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-if="orders.length === 0">
                    <td colspan="5" class="text-center text-muted">주문 정보가 없습니다.</td>
                  </tr>
                  <tr v-for="order in orders" :key="order.documentNo">
                    <td>{{ order.documentNo }}</td>
                    <td>{{ order.status }}</td>
                    <td>{{ order.companyName }}</td>
                    <td>
                      <button type="button" class="btn btn-primary btn-sm" @click="selectOrder(order)" data-bs-dismiss="modal">
                        선택
                      </button>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
            <Pagination
              :currentPage="currentPage"
              :totalPages="totalPages"
              @page-changed="handlePageChange"
            />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, defineEmits, watch } from 'vue';
import { fetchOrders } from '@/api/order';
import Pagination from '@/components/common/Pagination.vue';

const emit = defineEmits(['select']);

const orders = ref([]);
const loading = ref(true);
const error = ref(null);
const currentPage = ref(0);
const totalPages = ref(0);
const pageSize = ref(10);

const fetchOrderList = async (page = 0) => {
  loading.value = true;
  error.value = null;
  try {
    const response = await fetchOrders(page, pageSize.value);
    orders.value = response.data.content || [];
    totalPages.value = response.data.totalPages;
  } catch (e) {
    console.error('Error fetching orders:', e);
    error.value = '주문 정보를 불러오는 데 실패했습니다.';
  } finally {
    loading.value = false;
  }
};

function selectOrder(order) {
  emit('select', order);
}

function handlePageChange(page) {
  currentPage.value = page;
}

onMounted(() => {
  fetchOrderList(currentPage.value);
});

watch(currentPage, (newPage) => {
  fetchOrderList(newPage);
});
</script>

<style scoped>
</style>