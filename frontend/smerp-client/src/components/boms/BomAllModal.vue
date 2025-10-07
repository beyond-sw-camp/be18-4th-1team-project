<!-- src/components/boms/BomAllModal.vue -->
<template>
  <div class="modal fade" id="bomAllModal" tabindex="-1" aria-labelledby="bomAllModalLabel" aria-hidden="true" ref="modalElement">
    <div class="modal-dialog modal-xl modal-dialog-scrollable">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title" id="bomAllModalLabel">BOM 보기</h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="닫기"></button>
        </div>

        <div class="modal-body">
          <!-- Tabs -->
          <ul class="nav nav-tabs mb-3">
            <li class="nav-item">
              <a class="nav-link fw-semibold"
                 :class="{ active: selectedTab === 'inbound' }"
                 href="#" @click.prevent="selectedTab = 'inbound'">
                🔽 정전개
              </a>
            </li>
            <li class="nav-item">
              <a class="nav-link fw-semibold"
                 :class="{ active: selectedTab === 'outbound' }"
                 href="#" @click.prevent="selectedTab = 'outbound'">
                🔼 역전개
              </a>
            </li>
            <li class="nav-item">
              <a class="nav-link fw-semibold"
                 :class="{ active: selectedTab === 'rawMaterials' }"
                 href="#" @click.prevent="selectedTab = 'rawMaterials'">
                🧩 원재료 리스트
              </a>
            </li>
          </ul>

          <!--  전체 펼치기/접기 버튼 (정전개일 때만 표시) -->
          <div v-if="selectedTab === 'inbound'" class="d-flex justify-content-end mb-2">
            <button class="btn btn-sm btn-outline-secondary me-2" @click="expandAll = true">전체 펼치기</button>
            <button class="btn btn-sm btn-outline-secondary" @click="expandAll = false">전체 접기</button>
          </div>

          <!-- 로딩/에러 -->
          <div v-if="loading" class="text-center py-5">
            <div class="spinner-border text-primary" role="status"></div>
            <p class="mt-2 text-muted">데이터를 불러오는 중...</p>
          </div>

          <div v-else-if="error" class="alert alert-danger">{{ error }}</div>

          <!-- 캐시 준비 중 -->
          <div v-else-if="!bomData || Object.keys(bomData).length === 0" class="text-center text-muted py-5">
            ⏳ 캐시 로딩 중입니다. 잠시 후 다시 시도해주세요.
          </div>

          <!-- 탭별 내용 -->
          <div v-else>
            <div v-if="selectedTab === 'inbound' && bomData?.inbound">
              <table class="table table-sm table-striped table-hover align-middle">
                <thead class="table-light">
                <tr>
                  <th>품목명</th>
                  <th class="text-end">소요량</th>
                  <th class="text-end">단가</th>
                  <th class="text-end">총 원가</th>
                  <th class="text-center">계층</th>
                </tr>
                </thead>
                <tbody>
                <tr v-for="(node, idx) in flattenBomTree(bomData.inbound)" :key="idx">
                  <td :style="{ 'padding-left': `${node.depth * 20 + 8}px` }">{{ node.itemName }}</td>
                  <td class="text-end">{{ node.qty }}</td>
                  <td class="text-end">{{ formatCurrency(node.unitCost) }}</td>
                  <td class="text-end">{{ formatCurrency(node.totalCost) }}</td>
                  <td class="text-center">{{ node.depth }}</td>
                </tr>
                </tbody>
              </table>
            </div>
            <!-- 역전개 -->
            <div v-else-if="selectedTab === 'outbound' && bomData?.outbound">
              <table class="table table-sm table-striped table-hover align-middle">
                <thead class="table-light">
                <tr>
                  <th>품목명</th>
                  <th class="text-end">소요량</th>
                  <th class="text-end">단가</th>
                  <th class="text-end">총 원가</th>
                  <th class="text-center">계층</th>
                </tr>
                </thead>
                <tbody>
                <tr v-for="(node, idx) in flattenBomTree(bomData.outbound)" :key="idx">
                  <td>{{ node.itemName }}</td>
                  <td class="text-end">{{ node.qty }}</td>
                  <td class="text-end">{{ formatCurrency(node.unitCost) }}</td>
                  <td class="text-end">{{ formatCurrency(node.totalCost) }}</td>
                  <td class="text-center">{{ node.depth }}</td>
                </tr>
                </tbody>
              </table>
            </div>

            <!-- 원재료 리스트 -->
            <div v-else-if="selectedTab === 'rawMaterials' && bomData?.rawMaterials">
              <table class="table table-sm table-striped table-hover align-middle">
                <thead class="table-light">
                <tr>
                  <th>품목명</th>
                  <th class="text-end">소요량</th>
                  <th class="text-end">단가</th>
                  <th class="text-end">총 원가</th>
                  <th class="text-center">품목구분</th>
                </tr>
                </thead>
                <tbody>
                <tr v-for="(mat, idx) in bomData.rawMaterials" :key="idx">
<!--                  <td>{{ mat.itemId }}</td>-->
                  <td>{{ mat.itemName }}</td>
                  <td class="text-end">{{ mat.qty }}</td>
                  <td class="text-end">{{ formatCurrency(mat.unitCost) }}</td>
                  <td class="text-end">{{ formatCurrency(mat.totalCost) }}</td>
                  <td class="text-center">{{ mat.itemStatus }}</td>
                </tr>
                </tbody>
              </table>
            </div>

            <div v-else class="text-center text-muted py-5">데이터가 없습니다.</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { Modal } from "bootstrap";
import { fetchBomAll } from "@/api/bom";
import BomTree from "@/components/boms/BomAllTree.vue";

const props = defineProps({
  itemId: { type: Number, required: true },
});

const expandAll = ref(false);
const bomData = ref(null);
const loading = ref(false);
const error = ref(null);
const selectedTab = ref("inbound");

const modalElement = ref(null);
let bsModal = null;


async function loadData() {
  loading.value = true;
  error.value = null;
  try {
    const res = await fetchBomAll(props.itemId); // 이미 BomAllResponse 객체를 반환
    // 수정된 부분: res.data가 아닌 res를 할당
    bomData.value = res ?? {};
  } catch (e) {
    console.error(e);
    error.value = "BOM 데이터를 불러오지 못했습니다.";
  } finally {
    loading.value = false;
  }
}

// BomAllModal.vue 스크립트 부분
function flattenBomTree(nodeOrNodes, list = []) {
  // 인자가 배열이면 각 요소를 순회하여 재귀 호출
  if (Array.isArray(nodeOrNodes)) {
    nodeOrNodes.forEach(item => flattenBomTree(item, list));
  }
  // 인자가 단일 객체이면 기존 로직 실행
  else if (nodeOrNodes) {
    list.push(nodeOrNodes);
    if (nodeOrNodes.children) {
      nodeOrNodes.children.forEach(child => flattenBomTree(child, list));
    }
  }
  return list;
}



function formatCurrency(amount) {
  if (amount == null) return "-";
  return new Intl.NumberFormat("ko-KR", { style: "currency", currency: "KRW" }).format(amount);
}

onMounted(() => {
  if (modalElement.value) {
    bsModal = new Modal(modalElement.value);
  }
});

const showModal = async () => {
  selectedTab.value = "inbound"; // 기본은 정전개
  await loadData();
  bsModal?.show();
};
defineExpose({ showModal });
</script>
