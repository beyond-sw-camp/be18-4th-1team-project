<template>
    <div class="login-page d-flex">
        <div class="login-form d-flex flex-column justify-content-center p-5 flex-grow-1">
            <div class="mb-4 d-flex align-items-center justify-content-between">
                <div class="d-flex align-items-center gap-2">
                    <span class="badge rounded-2 fw-bold text-white" style="background-color:#6B9CFF;">S</span>
                    <span class="fw-semibold fs-5">SMERP</span>
                </div>

                <button class="btn btn-outline-primary btn-sm custom-btn" data-bs-toggle="modal"
                    data-bs-target="#contactModal">가입문의</button>
            </div>

            <h6 class="text-muted mb-1">환영합니다</h6>
            <h3 class="fw-bold mb-4">로그인</h3>

            <form @submit.prevent="onSubmit">
                <div class="mb-3">
                    <label class="form-label">아이디</label>
                    <input v-model="form.loginId" type="text" class="form-control" placeholder="아이디를 입력해주세요"
                        autocomplete="username" required />
                </div>

                <div class="mb-2">
                    <label class="form-label">비밀번호</label>
                    <div class="input-group">
                        <input :type="showPwd ? 'text' : 'password'" v-model.trim="form.password" class="form-control"
                            placeholder="********" autocomplete="current-password" required />
                        <button class="btn btn-outline-secondary" type="button" @click="showPwd = !showPwd">
                            <i class="bi" :class="showPwd ? 'bi-eye-slash' : 'bi-eye'"></i>
                        </button>
                    </div>
                </div>

                <div class="mb-4">
                    <a href="#" class="small text-primary text-decoration-none">비밀번호찾기</a>
                </div>

                <div class="d-grid mb-3">
                    <button type="submit" class="btn text-white" style="background-color:#6B9CFF;" :disabled="loading">
                        <span v-if="loading" class="spinner-border spinner-border-sm me-2"></span>
                        로그인
                    </button>
                </div>
            </form>

        </div>

        <div class="login-image d-none d-md-block flex-grow-1">
            <img src="@/assets/images/login-page-image.png" alt="작업자" />
        </div>

        <!-- 모달 -->
        <div class="modal fade" id="contactModal" tabindex="-1" aria-labelledby="contactModalLabel" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered">
                <div class="modal-content contact-modal">
                    <button type="button" class="btn-close ms-auto p-3" data-bs-dismiss="modal"
                        aria-label="Close"></button>

                    <div class="text-center px-4 pb-4 pt-2">
                        <!-- 상단 로고 라인 -->
                        <div class="d-flex align-items-center justify-content-center gap-2 mb-3">
                            <span class="badge rounded-2 fw-bold text-white" style="background-color:#6B9CFF;">S</span>
                            <span class="fw-semibold">SMERP</span>
                        </div>

                        <!-- 본문 -->
                        <i class="bi bi-info-circle fs-3 text-secondary d-block mb-2"></i>
                        <h5 id="contactModalLabel" class="fw-bold mb-3">가입문의</h5>

                        <div class="text-muted small mb-1">SMERP</div>
                        <div class="mb-1">02-111-1111</div>
                        <div>abc@smerp.com</div>

                        <!-- 확인 버튼 -->
                        <div class="d-grid mt-4">
                            <button type="button" class="btn btn-primary py-2" data-bs-dismiss="modal"
                                style="background-color:#6B9CFF; border-color:#6B9CFF;">
                                확인
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { reactive, ref } from "vue";
import { useRouter } from "vue-router";
import { login } from "@/api/auth";
import { useUserStore } from "@/stores/user";

const router = useRouter();
const userStore = useUserStore();

const form = reactive({
    loginId: "",
    password: "",
});

const loading = ref(false);
const error = ref(null);
const showPwd = ref(false);

async function onSubmit() {
    error.value = null;
    loading.value = true;
    try {
        await login({ loginId: form.loginId, password: form.password });
        await userStore.loadUser();
        const q = router.currentRoute.value.query;
        const redirect = typeof q.redirect === "string"
            ? decodeURIComponent(q.redirect)
            : "/home";
        router.replace(redirect);
    } catch (e) {
        error.value = e?.response?.data?.message || "아이디 또는 비밀번호를 확인해주세요.";
    } finally {
        loading.value = false;
    }
}
</script>

<style scoped>
.login-page {
    min-height: 100vh;
    display: flex;
    font-family: 'Noto Sans', sans-serif;
}

.login-form {
    width: 50%;
    max-width: none;
    background: #fff;
    padding: 3rem;
    display: flex;
    flex-direction: column;
    justify-content: center;
}

.login-image {
    width: 50%;
}

.login-image img {
    width: 100%;
    height: 100vh;
    object-fit: cover;
}

.custom-btn:hover {
    background-color: #6B9CFF;
    border-color: #6B9CFF;
    color: #fff;
}
</style>
