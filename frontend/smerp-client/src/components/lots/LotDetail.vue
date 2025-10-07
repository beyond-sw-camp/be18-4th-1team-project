<template>
  <div class="card shadow-sm">
    <div class="card-body">
      <h5 class="card-title mb-4">로트 상세 정보</h5>

      <div v-if="showSuccessMessage" class="alert alert-success" role="alert">
        수정이 완료됐습니다.
      </div>

      <div v-if="loading" class="text-center">
        <div class="spinner-border" role="status">
          <span class="visually-hidden">Loading...</span>
        </div>
      </div>

      <div v-else-if="error" class="alert alert-danger">
        {{ error }}
      </div>

      <div v-else-if="lot">
        <div v-if="!isEditing">
          <div class="row mb-3">
            <div class="col-md-6">
              <label class="form-label fw-bold">로트명</label>
              <p class="form-control-static">{{ lot.lotName }}</p>
            </div>
            <div class="col-md-6">
              <label class="form-label fw-bold">품목명</label>
              <p class="form-control-static">{{ lot.itemName }}</p>
            </div>
          </div>
          <div class="row mb-3">
            <div class="col-md-4">
              <label class="form-label fw-bold">수량</label>
              <p class="form-control-static">{{ lot.lotQty }}</p>
            </div>
            <div class="col-md-4">
              <label class="form-label fw-bold">상태</label>
              <p class="form-control-static">
                <span :class="getLotStatusClass(lot.status)">{{ getLotStatusDisplayName(lot.status) }}</span>
              </p>
            </div>
            <div class="col-md-4">
              <label class="form-label fw-bold">생성일시</label>
              <p class="form-control-static">{{ new Date(lot.lotInstant).toLocaleString() }}</p>
            </div>
          </div>
        </div>

        <!-- Editing View -->
        <div v-else>
          <div class="row mb-3">
            <div class="col-md-6">
              <label class="form-label fw-bold">로트명</label>
              <p class="form-control-static">{{ editableLot.lotName }}</p>
            </div>
            <div class="col-md-6">
              <label class="form-label fw-bold">품목명</label>
              <p class="form-control-static">{{ editableLot.itemName }}</p>
            </div>
          </div>
          <div class="row mb-3">
            <div class="col-md-4">
              <label for="qty" class="form-label fw-bold">수량</label>
              <input id="qty" v-model.number="editableLot.qty" type="number" step="0.001" class="form-control">
            </div>
            <div class="col-md-4">
              <label for="status" class="form-label fw-bold">상태</label>
              <select id="status" v-model="editableLot.status" class="form-select">
                <option value="ACTIVE">사용중</option>
                <option value="DEFECTIVE">불량</option>
                <option value="SHIPPED">출고완</option>
                <option value="RETURNED">반품</option>
              </select>
            </div>
            <div class="col-md-4">
              <label class="form-label fw-bold">생성일시</label>
              <p class="form-control-static">{{ new Date(editableLot.lotInstant).toLocaleString() }}</p>
            </div>
          </div>
        </div>

        <!-- Buttons -->
        <div class="d-flex justify-content-end gap-2 mt-4">
          <button type="button" class="btn btn-outline-secondary" @click="goToList">목록으로</button>
          <button v-if="canDeleteLot" type="button" class="btn btn-danger" @click="handleDeleteLot">삭제</button>
          <template v-if="!isEditing">
            <button type="button" class="btn btn-primary" @click="startEditing">수정</button>
          </template>
          <template v-else>
            <button type="button" class="btn btn-secondary" @click="cancelEditing">취소</button>
            <button type="button" class="btn btn-success" @click="saveLot">저장</button>
          </template>
        </div>
      </div>

      <div v-else class="text-center text-muted">
        로트 정보를 찾을 수 없습니다.
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { fetchLotDetail, updateLot, deleteLot } from "@/api/lot";
import { useUserStore } from '@/stores/user';

const props = defineProps({
  lotId: {
    type: String,
    required: true,
  },
});

const router = useRouter();
const route = useRoute();
const lot = ref(null);
const loading = ref(true);
const error = ref(null);

const isEditing = ref(false);
const editableLot = ref(null);
const showSuccessMessage = ref(false);

const userStore = useUserStore();

const canDeleteLot = computed(() => {
  return userStore.role === '[ROLE_ADMIN]';
});

async function handleDeleteLot() {
  if (!confirm('정말로 이 로트를 삭제하시겠습니까?')) {
    return;
  }

  try {
    await deleteLot(props.lotId);
    alert('로트가 성공적으로 삭제되었습니다.');
    router.push('/lots');
  } catch (err) {
    console.error('로트 삭제 실패:', err);
    alert('로트 삭제에 실패했습니다.');
  }
}

async function loadLotDetail(id) {
  loading.value = true;
  error.value = null;
  try {
    const response = await fetchLotDetail(id);
    lot.value = response.data;
    if (route.query.view === 'edit') {
      startEditing();
    }
  } catch (e) {
    console.error('Error fetching lot details:', e);
    error.value = '로트 정보를 불러오는 데 실패했습니다.';
  } finally {
    loading.value = false;
  }
}

function startEditing() {
  editableLot.value = JSON.parse(JSON.stringify(lot.value));
  isEditing.value = true;
}

function cancelEditing() {
  editableLot.value = null;
  isEditing.value = false;
}

async function saveLot() {
  if (!editableLot.value) return;

  // Client-side validation for qty
  if (editableLot.value.qty <= 0) {
    alert('수량은 0보다 커야 합니다.');
    return;
  }

  const changedData = {};
  const originalLot = lot.value;
  const editedLot = editableLot.value;

  // Only qty and status can be updated
  if (originalLot.qty !== editedLot.qty) {
    changedData.qty = editedLot.qty;
  }
  if (originalLot.status !== editedLot.status) {
    changedData.status = editedLot.status;
  }

  if (Object.keys(changedData).length === 0) {
    isEditing.value = false;
    editableLot.value = null;
    return;
  }

  try {
    await updateLot(props.lotId, changedData);
    await loadLotDetail(props.lotId);

    isEditing.value = false;
    editableLot.value = null;
    showSuccessMessage.value = true;
    setTimeout(() => {
      showSuccessMessage.value = false;
    }, 3000);
  } catch (err) {
    console.error('Failed to update lot:', err);
    alert('로트 정보 수정에 실패했습니다.');
  }
}

function getLotStatusDisplayName(status) {
  switch (status) {
    case 'ACTIVE': return '사용중';
    case 'DEFECTIVE': return '불량';
    case 'SHIPPED': return '출고완';
    case 'RETURNED': return '반품';
    case 'PENDING': return '대기';
    default: return status;
  }
}

function getLotStatusClass(status) {
  switch (status) {
    case '사용중':
      return 'badge bg-success';
    case '불량':
      return 'badge bg-danger';
    case '대기':
      return 'badge bg-warning';
    default:
      return 'badge bg-secondary';
  }
}

function goToList() {
  router.push('/lots');
}

onMounted(() => {
  if (props.lotId) {
    loadLotDetail(props.lotId);
  }
});

watch(() => props.lotId, (newLotId) => {
  if (newLotId) {
    loadLotDetail(newLotId);
  } else {
    lot.value = null;
  }
});
</script>

<style scoped>
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
  border-radius: 8px;
  background-color: #f8f9fa;
  font-size: 0.95rem;
  color: #495057;
}

.badge {
  font-size: 0.9em;
  padding: 0.5em 0.75em;
}

.btn-outline-secondary {
  border-radius: 8px;
  padding: 10px 20px;
  font-weight: 600;
}
</style>