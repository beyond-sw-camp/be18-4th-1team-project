<!-- src/pages/boms/BomPage.vue -->
<template>
  <div class="app d-flex">
    <Sidebar @select="onSelect" />

    <div class="flex-grow-1 d-flex flex-column">
      <Header
          :notify-count="2"
          :user-name="userStore.name || 'Guest'"
          :breadcrumbs="breadcrumbs"
          @search="onSearch"
          @logout="onLogout"
      />

      <main class="content container-fluid py-3">
        <div class="d-flex justify-content-between align-items-center mb-3">
          <h5 class="mb-0">BOM 관리</h5>
          <div class="d-flex gap-2">
            <button class="btn btn-primary" @click="showCreateForm = !showCreateForm">
              {{ showCreateForm ? "목록 보기" : "BOM 신규 등록" }}
            </button>
            <button class="btn btn-primary" @click="reloadIfPossible">새로고침</button>
          </div>
        </div>
        <!--BOM 신규 등록-->
        <CreateBomForm
            v-if="showCreateForm"
            @submit-success="onBomCreated"
            @cancel="showCreateForm = false"
        />

        <!--BOM 목록-->
        <BomListTable ref="listRef" />
      </main>
    </div>
  </div>
</template>

<script setup>
import Header from "@/components/layout/Header.vue";
import Sidebar from "@/components/layout/Sidebar.vue";
import BomListTable from "@/components/boms/BomListTable.vue";
import CreateBomForm from "@/components/boms/CreateBomForm.vue";
import { useRouter } from "vue-router";
import { logout as apiLogout } from "@/api/auth";
import { useUserStore } from "@/stores/user";
import { ref } from "vue";

const router = useRouter();
const userStore = useUserStore();
const listRef = ref(null);
const showCreateForm = ref(false);


// 헤더
const breadcrumbs = [
  { label: "HOME", to: "/home" },
  { label: "생산관리", to: "/production" },
  { label: "BOM 조회" },
];

function onSelect({ section, item }) {
  if (item?.to) router.push(item.to);
}

function onSearch(q) { console.log("search:", q); }

async function onLogout() {
  try {
    await apiLogout();
    userStore.clear();
    router.push("/login");
  } catch (error) {
    console.error("Logout failed:", error);
  }
}

function reloadIfPossible() {
  listRef.value?.reload?.();
}

function onBomCreated() {
  showCreateForm.value = false; // 폼 숨기기
  reloadIfPossible(); // 목록 새로고침
}
</script>