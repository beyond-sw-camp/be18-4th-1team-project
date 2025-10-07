<!-- src/components/Header.vue -->
<template>
    <header class="border-bottom bg-white">
        <nav class="navbar navbar-expand-lg py-2">
            <div class="container-fluid">
                <!-- 경로표시 -->
                <ol v-if="breadcrumbs?.length" class="breadcrumb mb-0 ms-3 d-none d-md-flex align-items-center">
                    <a class="navbar-brand d-flex align-items-center gap-2" href="#">
                        <span class="brand-badge fw-bold">S</span>
                    </a>
                    <li v-for="(bc, i) in breadcrumbs" :key="i" class="breadcrumb-item"
                        :class="{ active: i === breadcrumbs.length - 1 }" aria-current="page">
                        <a v-if="bc.to && i !== breadcrumbs.length - 1" :href="bc.to">
                            {{ bc.label }}
                        </a>
                        <span v-else>{{ bc.label }}</span>
                    </li>
                </ol>

                <!-- 검색 유저명 -->
                <div class="ms-auto d-flex align-items-center gap-3">
                    <form class="d-none d-lg-block" @submit.prevent="onSubmitSearch">
                        <div class="input-group">
                            <span class="input-group-text bg-body-secondary border-0">
                                <i class="bi bi-search"></i>
                            </span>
                            <input v-model="searchText" type="search" class="form-control bg-body-secondary border-0"
                                placeholder="검색" aria-label="검색" />
                        </div>
                    </form>

                    <div class="d-flex align-items-center gap-2">
                        <div class="d-flex align-items-center gap-2 cursor-pointer" @click="goToMyInfo">
                            <div class="avatar rounded-circle text-white d-flex align-items-center justify-content-center">
                                {{ userInitial }}
                            </div>
                            <span class="fw-semibold d-none d-sm-inline">{{ userName }}</span>
                        </div>
                        <button class="btn btn-outline-secondary btn-sm custom-btn" @click="emit('logout')">
                            로그아웃
                        </button>
                    </div>
                </div>
            </div>
        </nav>
    </header>
</template>

<script setup>
import { computed, ref } from "vue";
import { useRouter } from "vue-router";

const props = defineProps({
    breadcrumbs: { type: Array, default: null },
    userName: { type: String, default: "Domino" },
    notifyCount: { type: Number, default: 0 },
});

const emit = defineEmits(["search", "logout"]);

const searchText = ref("");
const userInitial = computed(() => (props.userName?.[0] ?? "?").toUpperCase());

const router = useRouter();

function onSubmitSearch() {
    emit("search", searchText.value);
}

function goToMyInfo() {
    router.push("/my-info");
}
</script>

<style scoped>
.brand-badge {
    background-color: #6B9CFF;
    color: #fff;
    border-radius: 0.5rem;
    padding: 0.25em 0.5em;
    font-size: 1rem;
}

.avatar {
    width: 32px;
    height: 32px;
    background: #6B9CFF;
    font-weight: 700;
    font-size: 0.9rem;
}

.breadcrumb {
    --bs-breadcrumb-divider: "›";
}

.custom-btn:hover {
    background-color: #6B9CFF;
    border-color: #6B9CFF;
    color: #fff;
}
</style>
