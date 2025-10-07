<template>
  <div class="card shadow-sm">
    <div class="card-body">
      <div v-if="loading" class="text-center">
        <div class="spinner-border" role="status">
          <span class="visually-hidden">Loading...</span>
        </div>
      </div>

      <div v-else-if="error" class="alert alert-danger">
        {{ error }}
      </div>

      <table v-else-if="items.length > 0" class="table table-hover">
        <thead class="table-light">
          <tr>
            <th scope="col">품목</th>
            <th scope="col">RFID</th>
            <th scope="col">등록일</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in items" :key="item.itemId">
            <td>{{ item.name }}</td>
            <td>{{ item.rfid }}</td>
            <td>{{ item.createdAt }}</td>
            <td>
              <router-link :to="{ name: 'ItemDetail', params: { itemId: item.itemId }}" class="btn btn-sm btn-outline-primary">상세보기</router-link>
            </td>
          </tr>
        </tbody>
      </table>

      <div v-else class="text-center text-muted">
        표시할 품목이 없습니다.
      </div>

      <nav v-if="totalPages > 1">
        <ul class="pagination justify-content-center">
          <li class="page-item" :class="{ disabled: currentPage === 0 }">
            <a class="page-link" href="#" @click.prevent="goToPage(0)">처음</a>
          </li>
          <li class="page-item" :class="{ disabled: currentPage === 0 }">
            <a class="page-link" href="#" @click.prevent="goToPage(currentPage - 1)">이전</a>
          </li>
          <li class="page-item" v-for="page in pages" :key="page" :class="{ active: page === currentPage }">
            <a class="page-link" href="#" @click.prevent="goToPage(page)">{{ page + 1 }}</a>
          </li>
          <li class="page-item" :class="{ disabled: currentPage === totalPages - 1 }">
            <a class="page-link" href="#" @click.prevent="goToPage(currentPage + 1)">다음</a>
          </li>
          <li class="page-item" :class="{ disabled: currentPage === totalPages - 1 }">
            <a class="page-link" href="#" @click.prevent="goToPage(totalPages - 1)">끝</a>
          </li>
        </ul>
      </nav>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, defineExpose, watch } from "vue";
import { fetchItems } from "@/api/item";

const items = ref([]);
const loading = ref(true);
const error = ref(null);

const currentPage = ref(0);
const totalPages = ref(0);
const pageSize = ref(20);

const pages = ref([]);

async function load(page = currentPage.value, size = pageSize.value) {
  loading.value = true;
  error.value = null;
  try {
    const res = await fetchItems(page, size);
    items.value = res.data.content;
    currentPage.value = res.data.page;
    totalPages.value = res.data.totalPages;
    updatePagination();
  } catch (e) {
    console.error(e);
    error.value = "품목 정보를 불러오는 데 실패했습니다.";
  } finally {
    loading.value = false;
  }
}

function updatePagination() {
  pages.value = [];
  const startPage = Math.max(0, currentPage.value - 2);
  const endPage = Math.min(totalPages.value - 1, currentPage.value + 2);
  for (let i = startPage; i <= endPage; i++) {
    pages.value.push(i);
  }
}

function goToPage(page) {
  if (page >= 0 && page < totalPages.value) {
    currentPage.value = page;
    load(page);
  }
}

onMounted(() => {
  load();
});

defineExpose({ reload: load });
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
.table {
  margin: 0;
  border-collapse: separate;
  border-spacing: 0;
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

.table thead th:first-child {
  border-top-left-radius: 8px;
}
.table thead th:last-child {
  border-top-right-radius: 8px;
}
.table tbody td {
  border-top: 1px solid #eef1f6 !important;
  padding: 14px 16px;
  vertical-align: middle;
  color: #334155;
  font-size: 0.9rem;
}
.table-hover tbody tr:hover {
  background: #e9ecef;
}
.table tbody tr {
  height: 56px;
}
.spinner-border {
  width: 2rem;
  height: 2rem;
}
.alert {
  border: none;
  border-radius: 12px;
}
.pagination {
  margin-top: 20px;
}
.page-item .page-link {
  color: #2563eb;
  border-radius: 8px;
  margin: 0 4px;
}
.page-item.active .page-link {
  background-color: #2563eb;
  border-color: #2563eb;
  color: #fff;
}
.page-item.disabled .page-link {
  color: #6c757d;
}
</style>
