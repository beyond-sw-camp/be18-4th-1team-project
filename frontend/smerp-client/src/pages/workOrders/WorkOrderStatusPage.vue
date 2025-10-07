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
        <div class="d-flex justify-content-between align-items-center mb-4">
          <h5 class="fw-bold mb-0">작업지시 현황</h5>
          <button class="btn btn-success" @click="exportToExcel">
            <i class="bi bi-file-earmark-excel"></i> 엑셀로 내보내기
          </button>
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
  list: defineAsyncComponent(() => import("@/components/workOrders/WorkOrderStatusTable.vue")),
};

const router = useRouter();
const route = useRoute();
const userStore = useUserStore();

const breadcrumbs = [
  { label: "HOME", to: "/home" },
  { label: "생산 관리", to: "/production" },
  { label: "작업 지시 현황" },
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

/** 동적 컴포넌트 스위칭 */
const activeComp = shallowRef(Views.list);
const activeRef = shallowRef(null);

function updateActiveComp() {
  const view = route.query.view || 'list';
  activeComp.value = Views[view] ?? Views.list;
}

watch(() => route.query.view, updateActiveComp, { immediate: true });

function switchView(key) {
  router.push({ query: { view: key } });
}

/** 현재 활성 컴포넌트에 reload 메서드가 있으면 호출 */
function reloadIfPossible() {
  activeRef.value?.load?.();
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

function exportToExcel() {
  const workOrders = activeRef.value?.workOrders;
  if (!workOrders || workOrders.length === 0) {
    alert("내보낼 데이터가 없습니다.");
    return;
  }

  const headers = ["문서 번호", "회사명", "담당자", "품목명", "상태", "계획 수량", "계획일"];
  const rows = workOrders.map(order => [
    order.documentNo,
    order.companyName || 'N/A',
    order.userName,
    order.itemName,
    order.status,
    order.planQty,
    order.planAt ? order.planAt.split('T')[0] : 'N/A',
  ]);

  let csvContent = "data:text/csv;charset=utf-8,\uFEFF" + headers.join(",") + "\n";
  rows.forEach(row => {
    csvContent += row.join(",") + "\n";
  });

  const encodedUri = encodeURI(csvContent);
  const link = document.createElement("a");
  link.setAttribute("href", encodedUri);
  link.setAttribute("download", "작업지시현황.csv");
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
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
