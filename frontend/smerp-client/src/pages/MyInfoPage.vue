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
        <div class="card shadow-sm">
          <div class="card-body">
            <h5 class="card-title mb-4">내 정보</h5>

            <div v-if="loading" class="text-center">
              <div class="spinner-border" role="status">
                <span class="visually-hidden">Loading...</span>
              </div>
            </div>

            <div v-else-if="error" class="alert alert-danger">
              {{ error }}
            </div>

            <div v-else-if="user">
              <div class="row mb-3">
                <div class="col-md-4">
                  <label class="form-label fw-bold">이름</label>
                  <p class="form-control-static">{{ user.name }}</p>
                </div>
                <div class="col-md-4">
                  <label class="form-label fw-bold">사번</label>
                  <p class="form-control-static">{{ user.empNo }}</p>
                </div>
                <div class="col-md-4">
                  <label class="form-label fw-bold">로그인 ID</label>
                  <p class="form-control-static">{{ user.loginId }}</p>
                </div>
              </div>
              <div class="row mb-3">
                <div class="col-md-4">
                  <label class="form-label fw-bold">이메일</label>
                  <p class="form-control-static">{{ user.email }}</p>
                </div>
                <div class="col-md-4">
                  <label class="form-label fw-bold">연락처</label>
                  <p class="form-control-static" v-html="user.phone || '&nbsp;'"></p>
                </div>
                <div class="col-md-4">
                  <label class="form-label fw-bold">부서</label>
                  <p class="form-control-static" v-html="user.deptTitle || '&nbsp;'"></p>
                </div>
              </div>
              <div class="row mb-3">
                <div class="col-md-4">
                  <label class="form-label fw-bold">역할</label>
                  <p class="form-control-static">{{ user.role }}</p>
                </div>
                <div class="col-md-4">
                  <label class="form-label fw-bold">거래처명</label>
                  <p class="form-control-static" v-html="user.companyName || '&nbsp;'"></p>
                </div>
                <div class="col-md-4">
                  <label class="form-label fw-bold">주소</label>
                  <p class="form-control-static" v-html="user.address || '&nbsp;'"></p>
                </div>
              </div>
              <div class="row mb-3">
                <div class="col-md-4">
                  <label class="form-label fw-bold">주민등록번호</label>
                  <p class="form-control-static" v-html="user.ssn || '&nbsp;'"></p>
                </div>
                <div class="col-md-4">
                  <label class="form-label fw-bold">입사일</label>
                  <p class="form-control-static" v-html="user.hireDate || '&nbsp;'"></p>
                </div>
                <div class="col-md-4">
                  <label class="form-label fw-bold">퇴사일</label>
                  <p class="form-control-static" v-html="user.fireDate || '&nbsp;'"></p>
                </div>
              </div>
            </div>

            <div v-else class="text-center text-muted">
              내 정보를 찾을 수 없습니다.
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
import { useUserStore } from "@/stores/user";
import { useRouter } from "vue-router";
import { logout as apiLogout } from "@/api/auth";
import { ref, onMounted } from "vue";
import { fetchMyInfo } from "@/api/user";

const userStore = useUserStore();
const router = useRouter();

const user = ref(null);
const loading = ref(true);
const error = ref(null);

const breadcrumbs = ref([
  { label: "HOME", to: "/home" },
  { label: "내 정보" },
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

async function loadMyInfo() {
  loading.value = true;
  error.value = null;
  try {
    const response = await fetchMyInfo();
    user.value = response.data;
  } catch (e) {
    console.error('Error fetching my info:', e);
    error.value = '내 정보를 불러오는 데 실패했습니다.';
  } finally {
    loading.value = false;
  }
}

onMounted(() => {
  loadMyInfo();
});
</script>

<style scoped>
.app { min-height: 100vh; background: #f2f4f8; }
.content { background: #f2f4f8; min-width: 768px; }
.card {
  border: 0;
  border-radius: 12px;
  background: #fff;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}
.card-body {
  padding: 24px;
}
.form-label {
  font-weight: 600;
  font-size: 0.9rem;
  color: #334155;
}
.form-control-static {
  padding: 10px 12px;
  border: 1px solid transparent;
  border-radius: 8px;
  background-color: #f8f9fa;
  font-size: 0.95rem;
  color: #495057;
}
</style>
