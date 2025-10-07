<template>
  <div class="app d-flex">
    <Sidebar @select="onSelect" />

    <!-- 우측 컨텐츠 -->
    <div class="flex-grow-1 d-flex flex-column">
      <Header :notify-count="2" :user-name="userStore.name || 'Guest'" :breadcrumbs="breadcrumbs" @search="onSearch" @logout="onLogout" />

      <main class="content container-fluid py-3">
        <div class="d-flex justify-content-between align-items-center mb-3">
          <h5 class="mb-0"> HOME
          </h5>
        </div>

        <!-- 메인 카드 -->
        <div class="card shadow-sm">
          <div class="card-body">
            <div class="row g-3 mb-4">
              <div class="col-6 col-md-4">
                <router-link to="/basic" class="ws-box">
                  기초관리
                </router-link>
              </div>
              <div class="col-6 col-md-4">
                <router-link to="/marketing" class="ws-box">
                  영업관리
                </router-link>
              </div>
              <div class="col-6 col-md-4">
                <router-link to="/purchase" class="ws-box">
                  구매관리
                </router-link>
              </div>
              <div class="col-6 col-md-4">
                <router-link to="/production" class="ws-box">
                  생산관리
                </router-link>
              </div>
              <div class="col-6 col-md-4">
                <router-link to="/reports" class="ws-box">
                통계 및 조회
                </router-link>
              </div>
            </div>

            <!-- Shortcuts -->
            <h6 class="fw-bold mb-3">바로가기</h6>
            <div class="row g-3">
              <div class="col-6 col-md-4">
                <router-link to="/boms" class="ws-box">BOM ↗</router-link>
                  
              </div>
              <div class="col-6 col-md-4"><router-link to="/boms/requirements" class="ws-box">BOM 소요량계산 ↗</router-link>
              </div>
              <div class="col-6 col-md-4"><router-link to="/work-orders" class="ws-box">작업지시 ↗</router-link>
              </div>
              <div class="col-6 col-md-4">
                <router-link to="/sales/summary" class="ws-box">판매현황 ↗</router-link>  
              </div>
              <div class="col-6 col-md-4">
                <router-link to="/plans" class="ws-box">생산계획 ↗</router-link>  
              </div>
              <div class="col-6 col-md-4">
                <router-link to="/production-results" class="ws-box">생산실적 ↗</router-link>  
              </div>
            </div>
          </div>
        </div>

      </main>
    </div>
  </div>
</template>

<script setup>
import Header from "@/components/layout/Header.vue";
import Sidebar from "@/components/layout/Sidebar.vue";
import { useRouter } from "vue-router";
import { logout as apiLogout } from "@/api/auth";
import { useUserStore } from "@/stores/user";

const userStore = useUserStore();
const router = useRouter();

const breadcrumbs = [
  { label: "HOME"}
];

function onSelect({ section, item }) {
  if (item?.to) router.push(item.to);

  breadcrumbs.splice(0, breadcrumbs.length,
    { label: "메뉴", to: "#" },
    { label: section.label, to: "#" },
    { label: item.label },
  );
}

function onSearch(q) { console.log("search:", q); }

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

.ws-box {
  height: 90px;
  background: #f8f9fa;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  font-weight: 500;
  font-size: 0.85rem;
  cursor: default;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
  text-decoration: none;
  color: inherit;   
}

.ws-box:hover {
  background: #e9ecef;
}

.shortcut {
  display: inline-block;
  font-size: 0.9rem;
  cursor: default;
}
</style>
