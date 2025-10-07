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
        <LogDetail :log-id="logId" />
      </main>
    </div>
  </div>
</template>

<script setup>
import Header from "@/components/layout/Header.vue";
import Sidebar from "@/components/layout/Sidebar.vue";
import LogDetail from "@/components/logs/LogDetail.vue";
import { useUserStore } from "@/stores/user";
import { useRouter } from "vue-router";
import { logout as apiLogout } from "@/api/auth";
import { ref } from "vue";

const props = defineProps({
  logId: {
    type: String,
    required: true,
  },
});

const userStore = useUserStore();
const router = useRouter();

const breadcrumbs = ref([
  { label: "HOME", to: "/home" },
  { label: "통계 및 로그", to: "/logs" },
  { label: "로그 상세" },
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
