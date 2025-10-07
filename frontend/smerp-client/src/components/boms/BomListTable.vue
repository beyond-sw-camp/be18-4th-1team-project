<template>
  <div class="card shadow-sm">
    <div class="card-body">
      <h5 class="card-title mb-4">BOM 조회</h5>

      <div class="d-flex justify-content-end mb-3">
        <button class="btn btn-sm btn-outline-secondary" @click="showAll = !showAll">
          {{ showAll ? "전체 보기" : "BOM 등록된 것만 보기" }}
        </button>
      </div>

      <div v-if="loading" class="text-center">
        <div class="spinner-border" role="status">
          <span class="visually-hidden">Loading...</span>
        </div>
      </div>

      <div v-else-if="error" class="alert alert-danger">
        {{ error }}
      </div>

      <table v-else-if="filteredBoms.length > 0" class="table table-hover">
        <thead class="table-light">
        <tr>
          <th>품목명</th>
          <th>규격</th>
          <th>품목 구분</th>
          <th>원재료 개수</th>
          <th>BOM 등록 여부</th>
          <th>조회</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="bom in filteredBoms" :key="bom.itemId">
          <td>{{ bom.itemName }}</td>
          <td>{{ bom.specification }}</td>
          <td>{{ getItemStatusDescription(bom.itemStatus) }}</td>
          <td>{{ bom.materialCount }}</td>
          <td>{{ bom.hasBom ? "등록됨" : "미등록" }}</td>
          <td>
            <button
                v-if="bom.hasBom"
                class="btn btn-sm btn-outline-primary"
                @click="openModal(bom.itemId)"
            >
              조회
            </button>
          </td>
        </tr>
        </tbody>
      </table>

      <div v-else class="text-muted text-center">
        표시할 BOM이 없습니다.
      </div>

      <Pagination
          v-if="totalPages > 1"
          :current-page="currentPage"
          :total-pages="totalPages"
          @page-changed="goToPage"
      />

      <BomAllModal ref="bomAllModal" :item-id="selectedItemId" />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { fetchBoms } from "@/api/bom";
import Pagination from "@/components/common/Pagination.vue";
import BomAllModal from "@/components/boms/BomAllModal.vue";

const boms = ref([]);
const loading = ref(true);
const error = ref(null);
const currentPage = ref(0);
const totalPages = ref(0);

const showAll = ref(false);

const selectedItemId = ref(null);
const bomAllModal = ref(null);

const filteredBoms = computed(() =>
    showAll.value ? boms.value : boms.value.filter((b) => b.hasBom)
);

async function load(page = 0) {
  loading.value = true;
  error.value = null;
  try {
    const res = await fetchBoms(page, 20); // API 호출
    boms.value = res.data.content; // List<BomListResponse>
    currentPage.value = res.data.page;
    totalPages.value = res.data.totalPages;
  } catch (e) {
    console.error(e);
    error.value = "BOM 정보를 불러오는 데 실패했습니다.";
  } finally {
    loading.value = false;
  }
}

// 품목구분 영어 한글로 매핑
function getItemStatusDescription(status) {
  const statusMap = {
    RAW_MATERIAL: "원재료",
    AUXILIARY_MATERIAL: "부재료",
    SEMI_FINISHED_PRODUCT: "반제품",
    FINISHED_PRODUCT: "제품",
    MERCHANDISE: "상품",
    INTANGIBLE_GOODS: "무형상품"
  };
  return statusMap[status] || status; // 매핑된 값이 없으면 원래 값 반환
}

function goToPage(page) {
  if (page >= 0 && page < totalPages.value) {
    load(page);
  }
}

function openModal(itemId) {
  selectedItemId.value = itemId;
  bomAllModal.value.showModal();
}

function reload() {
  load(currentPage.value);
}

defineExpose({ reload });

onMounted(() => load());
</script>