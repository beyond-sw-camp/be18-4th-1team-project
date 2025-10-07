<template>
  <div class="app d-flex">
    <Sidebar @select="onSelect" />

    <!-- 우측 컨텐츠 -->
    <div class="flex-grow-1 d-flex flex-column">
      <Header :user-name="userStore.name || 'Guest'" :breadcrumbs="breadcrumbs" @logout="onLogout" />

      <main class="content container-fluid py-3">
        <div class="d-flex justify-content-between align-items-center mb-4">
          <h5 class="fw-bold mb-0">반품 관리</h5>
          <button v-if="currentView !== 'create'" class="btn btn-primary" @click="router.push({ query: { view: 'create' } })">
            반품 등록
          </button>
          <button v-else class="btn btn-secondary" @click="router.push('/orders/returns/summary')">
            반품 목록
          </button>
        </div>

        <div v-if="currentView === 'create'">
          <CreateReturnForm />
        </div>
        <div v-else>
          <p class="text-center text-muted">반품 목록이 여기에 표시됩니다.</p>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import Sidebar from '@/components/layout/Sidebar.vue';
import Header from '@/components/layout/Header.vue';
import CreateReturnForm from '@/components/orders/CreateReturnForm.vue';
import { useUserStore } from "@/stores/user";
import { logout as apiLogout } from "@/api/auth";

const userStore = useUserStore();
const router = useRouter();
const route = useRoute();

const breadcrumbs = ref([]);

const currentView = computed(() => route.query.view || 'list');

watch(currentView, (newView) => {
  if (newView === 'create') {
    breadcrumbs.value = [
      { label: "HOME", to: "/home" },
      { label: "영업 관리", to: "#" },
      { label: "반품", to: "/orders/returns" },
      { label: "반품 등록" },
    ];
  } else {
    breadcrumbs.value = [
      { label: "HOME", to: "/home" },
      { label: "영업 관리", to: "#" },
      { label: "반품", to: "/orders/returns" },
    ];
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