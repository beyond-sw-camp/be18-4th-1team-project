<template>
  <aside class="sidebar d-flex flex-column">
    <!-- 상단 로고 -->
    <router-link to="/home" class="navbar-brand d-flex align-items-center gap-2 px-3 py-3 no-underline">
      <span class="badge rounded-2 fw-bold text-white" style="background-color:#6B9CFF;">S</span>
      <span class="fw-semibold">SMERP</span>
    </router-link>

    <!-- 메뉴 헤더 -->
    <div class="navbar-brand d-flex align-items-center gap-2 px-3 py-3">
      <span class="fw-semibold">
        <i class="bi bi-list"></i> MENU
      </span>
    </div>

    <!-- 메뉴 섹션 -->
    <nav class="flex-grow-1 overflow-auto">
      <ul class="list-unstyled mb-0">
        <li v-for="(sec, idx) in menuItems" :key="idx" class="mb-1">
          <!-- 상위 탭 -->
          <button class="btn w-100 d-flex align-items-center justify-content-between px-3 py-2 sidebar-btn"
            :aria-expanded="isOpen(idx)" @click="toggle(idx)">
            <span class="d-flex align-items-center gap-2">
              <i :class="sec.icon || 'bi bi-folder2'"></i>
              {{ sec.label }}
            </span>
            <i class="bi" :class="isOpen(idx) ? 'bi-chevron-up' : 'bi-chevron-down'"></i>
          </button>

          <!-- 하부 목록 -->
          <transition name="slide">
            <ul v-show="isOpen(idx)" class="list-unstyled ps-4 py-1">
              <li v-for="(child, cidx) in sec.children" :key="cidx" class="py-1">

                <!-- 자식있음 -->
                <div v-if="child.children">
                  <button class="btn w-100 d-flex align-items-center justify-content-between px-2 py-1 sidebar-btn"
                    :aria-expanded="isOpen(idx + '-' + cidx)" @click="toggle(idx + '-' + cidx)">
                    <span class="d-flex align-items-center gap-2">
                      <i :class="child.icon || 'bi bi-dot'"></i>
                      {{ child.label }}
                    </span>
                    <i class="bi" :class="isOpen(idx + '-' + cidx) ? 'bi-chevron-up' : 'bi-chevron-down'"></i>
                  </button>

                  <transition name="slide">
                    <ul v-show="isOpen(idx + '-' + cidx)" class="list-unstyled ps-4 py-1">
                      <li v-for="(sub, sidx) in child.children" :key="sidx" class="py-1">
                        <router-link
                          :to="sub.to || '#'"
                          class="sidebar-link d-flex align-items-center gap-2 px-2 py-1 rounded-2"
                          @click="$emit('select', { section: sec, item: sub })"
                        >
                          <i :class="sub.icon || 'bi bi-dot'" style="font-size:1rem;"></i>
                          <span>{{ sub.label }}</span>
                        </router-link>
                      </li>
                    </ul>
                  </transition>
                </div>

                <!-- 자식 없는 그냥 메뉴 -->
                <router-link
                  v-else
                  :to="child.to || '#'"
                  class="sidebar-link d-flex align-items-center gap-2 px-2 py-1 rounded-2"
                  @click="$emit('select', { section: sec, item: child })"
                >
                  <i :class="child.icon || 'bi bi-dot'" style="font-size:1rem;"></i>
                  <span>{{ child.label }}</span>
                </router-link>

              </li>
            </ul>
          </transition>
        </li>
      </ul>
    </nav>

    <!-- 하단 -->
    <div class="mt-auto px-3 py-3">
      <router-link to="#" class="sidebar-link d-flex align-items-center gap-2 px-2 py-2 rounded-2">
        <i class="bi bi-gear"></i><span>설정</span>
      </router-link>
    </div>
  </aside>
</template>

<script setup>
import { reactive } from "vue";

defineEmits(["select"]);

const menuItems = [
  {
    label: "기초 관리",
    icon: "bi bi-house-door",
    children: [
      {
        label: "사용자",
        children: [
          { label: "사용자 등록", to: "/users?view=create" },
          { label: "사용자 조회", to: "/users?view=list" },
        ],
      },
      {
        label: "거래처",
        children: [
          { label: "거래처 등록", to: "/clients?view=create" },
          { label: "거래처 조회", to: "/clients?view=list" },
        ],
      },
      {
        label: "창고",
        children: [
          { label: "창고 등록", to: "/warehouses?view=create" },
          { label: "창고 조회", to: "/warehouses?view=list" },
        ],
      },
      {
        label: "품목",
        children: [
          { label: "품목 등록", to: "/items?view=create" },
          { label: "품목 조회", to: "/items?view=list" },
        ],
      },
    ],
  },
  {
    label: "영업 관리",
    icon: "bi bi-graph-up",
    children: [
      {
        label: "주문",
        children: [
          { label: "주문 등록", to: "/orders?view=create" },
          { label: "주문 조회", to: "/orders" },
          { label: "주문 현황", to: "/orders/summary" },
        ],
      },
      {
        label: "판매",
        children: [
          { label: "판매 등록", to: "/sales?view=create" },
          { label: "판매 조회", to: "/sales" },
          { label: "판매 현황", to: "/sales/summary" },
        ],
      },
      {
        label: "반품",
        children: [
          { label: "반품 등록", to: "/orders/returns?view=create" },
          { label: "반품 현황", to: "/orders/returns/summary" },
        ],
      },
    ],
  },
  {
    label: "구매 관리",
    icon: "bi bi-bag-check",
    children: [
      {
        label: "구매",
        children: [
          
          { label: "구매 등록", to: "/purchase-orders?view=create" },
          { label: "구매 조회", to: "/purchase-orders?view=list" },
          { label: "구매 현황", to: "/purchase-orders/summary" },
        ],
      },
      {
        label: "발주",
        children: [
          { label: "발주 등록", to: "/request-orders?view=create" },
          { label: "발주 조회", to: "/request-orders?view=list" },
        ],
      },
      {
        label: "구매요청",
        children: [
          { label: "구매요청 등록", to: "/purchase-requests?view=create" },
          { label: "구매요청 조회", to: "/purchase-requests?view=list" },
        ],
      },
    ],
  },
  {
    label: "생산 관리",
    icon: "bi bi-cpu",
    children: [
      {
        label: "생산계획",
        children: [
          { label: "생산계획 등록", to: "/plans?view=create" },
          { label: "생산계획 조회", to: "/plans?view=list" },
        ],
      },
      {
        label: "작업 지시",
        children: [
          { label: "작업 지시 등록", to: "/work-orders?view=create" },
          { label: "작업 지시 조회", to: "/work-orders" },
          { label: "작업 지시 현황", to: "/work-orders/status" },
        ],
      },
      {
        label: "생산 실적 조회",
        to: "/production-results",
      },
      { label: "LOT 조회", to: "/lots" },
      { label: "BOM 조회", to: "/boms" },
      { label: "소요량계산", to: "/boms/requirements" },
    ],
  },
  {
    label: "통계 및 로그",
    icon: "bi bi-printer",
    children: [
      { label: "판매 현황", to: "/sales/summary" },
      { label: "구매 현황", to: "/purchase-orders/summary" },
      { label: "생산 현황", to: "/production-results" },
      { label: "주문 현황", to: "/orders/summary" },
      { label: "로그 목록", to: "/logs" },
    ],
  },
];

const open = reactive(new Set());

const isOpen = (key) => open.has(key);
const toggle = (key) => (isOpen(key) ? open.delete(key) : open.add(key));
</script>

<style scoped>
.sidebar {
  width: 260px;
  min-height: 100vh;
  background: #1e67c7;
  color: #fff;
  flex-shrink: 0;
}

.sidebar-btn,
.sidebar-link {
  background: transparent;
  color: #fff;
  text-align: left;
  text-decoration: none;
  border: none;
}

.sidebar-btn:hover,
.sidebar-link:hover {
  background: rgba(255, 255, 255, 0.08);
  color: #fff;
}

.slide-enter-active,
.slide-leave-active {
  transition: all .18s ease;
}

.slide-enter-from,
.slide-leave-to {
  opacity: 0;
  transform: translateY(-4px);
}

.no-underline {
  text-decoration: none;
  color: inherit;
}
</style>