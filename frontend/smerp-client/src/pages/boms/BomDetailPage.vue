<!-- src/pages/boms/BomDetailPage.vue -->
<script setup>
import { ref, onMounted } from "vue";
import { useRoute } from "vue-router";
import { fetchBomDetail } from "@/api/bom";

const route = useRoute();
const bomId = Number(route.params.bomId);

const bomDetail = ref(null);
const loading = ref(false);
const error = ref(null);

async function load() {
  loading.value = true;
  error.value = null;
  try {
    const res = await fetchBomDetail(bomId, "inbound"); // 기본 정전개
    bomDetail.value = res.data;
  } catch (e) {
    error.value = "BOM 상세 정보를 불러오는 데 실패했습니다.";
  } finally {
    loading.value = false;
  }
}

onMounted(() => load());
</script>

<template>
  <div class="container mt-4">
    <h3>BOM 상세</h3>

    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border" role="status"><span class="visually-hidden">Loading...</span></div>
    </div>
    <div v-else-if="error" class="alert alert-danger">{{ error }}</div>

    <pre v-else>{{ bomDetail }}</pre>
  </div>
</template>