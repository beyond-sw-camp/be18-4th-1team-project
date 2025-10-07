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
          <h5 class="mb-0">구매 요청 관리</h5>
          <div class="d-flex gap-2">
            <button class="btn btn-outline-secondary" @click="switchView('list')">목록</button>
            <button class="btn btn-outline-secondary" @click="switchView('create')">추가</button>
            <button class="btn btn-primary" @click="reloadIfPossible">새로고침</button>
            <button v-if="activeComp === Views.list" class="btn btn-success btn-sm" @click="exportToExcel">엑셀 내보내기</button>
          </div>
        </div>

        <KeepAlive>
          <component
            :is="activeComp"
            ref="activeRef"
            @cancel="switchToListView"
            @save="handleSave"
          />
        </KeepAlive>
      </main>
    </div>
  </div>
</template>

<script setup>
import Header from "@/components/layout/Header.vue";
import Sidebar from "@/components/layout/Sidebar.vue";
import { useRouter, useRoute } from "vue-router";
import { logout as apiLogout } from "@/api/auth";
import { useUserStore } from "@/stores/user";
import { defineAsyncComponent, shallowRef, watch } from "vue";

const Views = {
  list: defineAsyncComponent(() => import("@/components/purchaseRequests/PurchaseRequestListTable.vue")),
  create: defineAsyncComponent(() => import("@/components/purchaseRequests/CreatePurchaseRequestForm.vue")),
};

const router = useRouter();
const route = useRoute();
const userStore = useUserStore();

const activeRef = shallowRef(null);

const breadcrumbs = [
  { label: "HOME", to: "/home" },
  { label: "구매 관리", to: "/purchase" },
  { label: "구매 요청" },
];

function onSelect({ item }) {
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

function escapeCsvField(field) {
  if (field === null || field === undefined) {
    return '';
  }
  let stringField = String(field);
  if (stringField.includes(',') || stringField.includes('"') || stringField.includes('\n')) {
    return `"${stringField.replace(/"/g, '""')}"`;
  }
  return stringField;
}

const exportToExcel = () => {
  const purchaseRequests = activeRef.value?.purchaseRequests || [];

  if (purchaseRequests.length === 0) {
    alert("내보낼 데이터가 없습니다.");
    return;
  }

  const headers = ["문서 번호", "담당자 사번", "품목명", "총 수량", "납기일", "상태", "생성일"];
  const rows = purchaseRequests.map(request => [
    escapeCsvField(request.documentNo),
    escapeCsvField(request.empNo),
    escapeCsvField(request.itemName),
    escapeCsvField(request.totalQty),
    escapeCsvField(request.deliveryDate),
    escapeCsvField(request.status),
    escapeCsvField(request.createdAt),
  ]);

  let csvContent = "data:text/csv;charset=utf-8,\uFEFF" 
    + headers.join(",") + "\n"
    + rows.map(e => e.join(",")).join("\n");

  const encodedUri = encodeURI(csvContent);
  const link = document.createElement("a");
  link.setAttribute("href", encodedUri);
  link.setAttribute("download", "구매요청_목록.csv");
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
};

/** 동적 컴포넌트 스위칭 */
const activeComp = shallowRef(Views.list); // Default to list view

function updateActiveComp() {
  const view = route.query.view || 'list'; // Default to list view
  activeComp.value = Views[view] ?? Views.list;
}

watch(() => route.query.view, updateActiveComp, { immediate: true });

function switchView(key) {
  router.push({ query: { view: key } });
}

/** 현재 활성 컴포넌트에 reload 메서드가 있으면 호출 */
function reloadIfPossible() {
  if (activeComp.value === Views.list) {
    activeRef.value?.load?.();
  }
}

function switchToListView() {
  switchView('list');
}

function handleSave() {
  switchView('list');
  setTimeout(() => {
    reloadIfPossible();
  }, 100);
}
</script>

<style scoped>
.app { min-height: 100vh; background: #f2f4f8; }
.content { background: #f2f4f8; min-width: 768px; }

:root {
  --bg: #f5f7fb;
  --card-bg: #fff;
  --text-strong: #111827;
  --text-muted: #6b7280;
  --line: #eef1f6;
  --hover: #f7f9fc;
  --brand: #2563eb;
  --brand-d: #1e40af;
  --brand-l: #dbe7ff;
}
.btn.btn-primary {
  background: var(--brand); border: none; border-radius: 9999px;
  padding: 8px 16px; font-weight: 600;
  box-shadow: 0 4px 10px rgba(37,99,235,.25);
}
.btn.btn-primary:hover { background: var(--brand-d); }
.btn:focus { outline: 0; box-shadow: 0 0 0 4px var(--brand-l); }
</style>