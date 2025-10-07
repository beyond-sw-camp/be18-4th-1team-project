<template>
  <div class="card shadow-sm">
    <div class="card-body">
      <LotListTable :lots="lots" />
      <div v-if="totalPages > 0" class="mt-4">
        <Pagination
          :current-page="currentPage"
          :total-pages="totalPages"
          @page-changed="handlePageChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import LotListTable from '@/components/lots/LotListTable.vue';
import Pagination from '@/components/common/Pagination.vue';
import { fetchLots } from '@/api/lot';

const lots = ref([]);
const currentPage = ref(0);
const totalPages = ref(0);
const loading = ref(false);
const error = ref(null);

async function loadLots(page) {
  loading.value = true;
  error.value = null;
  try {
    const response = await fetchLots(page, 10);
    lots.value = response.data.content;
    currentPage.value = response.data.page;
    totalPages.value = response.data.totalPages;
  } catch (err) {
    console.error('Error fetching lots:', err);
    error.value = '로트 정보를 불러오는 데 실패했습니다.';
  } finally {
    loading.value = false;
  }
}

function handlePageChange(page) {
  loadLots(page);
}

function reload() {
  loadLots(currentPage.value);
}

defineExpose({ reload });

onMounted(() => {
  loadLots(0);
});
</script>

<style scoped>
.card {
  border: 0;
  border-radius: 12px;
  background: #fff;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}
.card-body {
  padding: 20px;
}
</style>