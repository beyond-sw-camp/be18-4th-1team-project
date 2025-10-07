<template>
  <div class="app d-flex">
    <Sidebar />
    <div class="flex-grow-1 d-flex flex-column">
      <Header
        :user-name="userStore.name || 'Guest'"
        :breadcrumbs="breadcrumbs"
        @logout="onLogout"
      />
      <main class="content container-fluid py-3">
        <WarehouseDetail :warehouse-id="warehouseId" />
      </main>
    </div>
  </div>
</template>

<script setup>
import Header from "@/components/layout/Header.vue";
import Sidebar from "@/components/layout/Sidebar.vue";
import WarehouseDetail from "@/components/warehouses/WarehouseDetail.vue";
import { useUserStore } from "@/stores/user";
import { useRouter } from "vue-router";
import { logout as apiLogout } from "@/api/auth";
import { ref } from "vue";

const props = defineProps({
  warehouseId: {
    type: String,
    required: true,
  },
});

const userStore = useUserStore();
const router = useRouter();

const breadcrumbs = ref([
  { label: "HOME", to: "/home" },
  { label: "기초관리", to: "/basic" },
  { label: "창고", to: "/warehouses" },
  { label: "창고 상세" },
]);

async function onLogout() {
  try {
    await apiLogout();
    userStore.clear();
    router.push("/login");
  } catch (error) {
    console.error("Logout failed:", error);
  }
}
</script>

<style scoped>
.app { min-height: 100vh; background: #f2f4f8; }
.content { background: #f2f4f8; min-width: 768px; }
</style>
