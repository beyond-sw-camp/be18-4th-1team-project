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
        <PlanDetail :plan-id="planId" />
      </main>
    </div>
  </div>
</template>

<script setup>
import Header from "@/components/layout/Header.vue";
import Sidebar from "@/components/layout/Sidebar.vue";
import PlanDetail from "@/components/plans/PlanDetail.vue"; // This component will be created next
import { useUserStore } from "@/stores/user";
import { useRouter } from "vue-router";
import { logout as apiLogout } from "@/api/auth";
import { ref } from "vue";

const props = defineProps({
  planId: {
    type: String,
    required: true,
  },
});

const userStore = useUserStore();
const router = useRouter();

const breadcrumbs = ref([
  { label: "HOME", to: "/home" },
  { label: "생산 관리", to: "/production" },
  { label: "생산계획", to: "/plans" },
  { label: "상세 정보" },
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