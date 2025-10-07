<template>
  <div class="app d-flex">
    <Sidebar @select="onSelect" />

    <!-- 우측 컨텐츠 -->
    <div class="flex-grow-1 d-flex flex-column">
      <Header :user-name="userStore.name || 'Guest'" :breadcrumbs="breadcrumbs" @logout="onLogout" />

      <main class="content container-fluid py-3">
        <h5 class="fw-bold mb-4">주문 상세</h5>
        <OrderDetail :order-id="orderId" />
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import OrderDetail from '@/components/orders/OrderDetail.vue';
import Header from "@/components/layout/Header.vue";
import Sidebar from "@/components/layout/Sidebar.vue";
import { useUserStore } from "@/stores/user";
import { logout as apiLogout } from "@/api/auth";

const userStore = useUserStore();
const router = useRouter();
const route = useRoute();

const orderId = ref(route.params.orderId);

const breadcrumbs = ref([
  { label: "HOME", to: "/home" },
  { label: "영업 관리", to: "#" },
  { label: "주문", to: "/orders" },
  { label: "상세", to: "#" },
]);

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

watch(() => route.params.orderId, (newOrderId) => {
  orderId.value = newOrderId;
});

onMounted(() => {
  if (route.params.orderId) {
    orderId.value = route.params.orderId;
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