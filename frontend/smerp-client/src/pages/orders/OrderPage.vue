<template>
  <div class="app d-flex">
    <Sidebar @select="onSelect" />

    <!-- 우측 컨텐츠 -->
    <div class="flex-grow-1 d-flex flex-column">
      <Header :user-name="userStore.name || 'Guest'" :breadcrumbs="breadcrumbs" @logout="onLogout" />

      <main class="content container-fluid py-3">
        <div class="d-flex justify-content-between align-items-center mb-4">
          <h5 class="fw-bold mb-0">주문 관리</h5>
          <button v-if="currentView !== 'create'" class="btn btn-primary" @click="router.push({ query: { view: 'create' } })">
            주문 생성
          </button>
          <button v-else class="btn btn-secondary" @click="router.push({ query: { view: 'list' } })">
            주문 목록
          </button>
        </div>

        <div v-if="loading" class="text-center">
          <div class="spinner-border" role="status">
            <span class="visually-hidden">Loading...</span>
          </div>
        </div>

        <div v-else-if="error" class="alert alert-danger">
          {{ error }}
        </div>

        <div v-else>
          <CreateOrderForm v-if="currentView === 'create'" />
          <div v-else>
            <OrderListTable :orders="orders" />
            <Pagination
              :currentPage="currentPage"
              :totalPages="totalPages"
              @page-changed="handlePageChange"
            />
          </div>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { fetchOrders } from '@/api/order';
import OrderListTable from '@/components/orders/OrderListTable.vue';
import CreateOrderForm from '@/components/orders/CreateOrderForm.vue';
import Pagination from '@/components/common/Pagination.vue';
import Header from "@/components/layout/Header.vue";
import Sidebar from "@/components/layout/Sidebar.vue";
import { useUserStore } from "@/stores/user";
import { logout as apiLogout } from "@/api/auth";

const userStore = useUserStore();
const router = useRouter();
const route = useRoute();

const orders = ref([]);
const loading = ref(true);
const error = ref(null);
const currentPage = ref(0);
const totalPages = ref(0);
const pageSize = ref(20);

const breadcrumbs = ref([]);

const currentView = computed(() => route.query.view || 'list');

watch(currentView, (newView) => {
  if (newView === 'create') {
    breadcrumbs.value = [
      { label: "HOME", to: "/home" },
      { label: "영업 관리", to: "#" },
      { label: "주문", to: "/orders" },
      { label: "주문 생성" },
    ];
    loading.value = false;
  } else {
    breadcrumbs.value = [
      { label: "HOME", to: "/home" },
      { label: "영업 관리", to: "#" },
      { label: "주문", to: "/orders" },
    ];
    loadOrders();
  }
}, { immediate: true });

function onSelect({ section, item }) {
  if (item?.to) router.push(item.to);

  breadcrumbs.value = [
    { label: "HOME", to: "/home" },
    { label: section.label, to: "#" },
    { label: item.label, to: item.to },
  ];
}

async function onLogout() {
  try { await apiLogout(); } catch { }
  userStore.clear();
  router.replace("/login");
}

async function loadOrders() {
  loading.value = true;
  error.value = null;
  try {
    const response = await fetchOrders(currentPage.value, pageSize.value);
    orders.value = response.data.content;
    totalPages.value = response.data.totalPages;
  } catch (e) {
    console.error('Error fetching orders:', e);
    error.value = '주문 목록을 불러오는 데 실패했습니다.';
  } finally {
    loading.value = false;
  }
}

function handlePageChange(page) {
  currentPage.value = page;
}

onMounted(() => {
});

watch(currentPage, () => {
  if (currentView.value === 'list') {
    loadOrders();
  }
});
</script>

<style scoped>
.app {
  min-height: 100vh;
  background: #f2f4f8;
}
.content {
  background: #f2f4f8;
}
</style>