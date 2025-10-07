<template>
  <div class="card shadow-sm">
    <div class="card-body">
      <h5 class="card-title mb-4">창고 상세 정보</h5>
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

      <div v-else-if="warehouse">
        <div v-if="!isEditing">
          <div class="row mb-3">
            <div class="col-md-4">
              <label class="form-label fw-bold">ID</label>
              <p class="form-control-static">{{ warehouse.id }}</p>
            </div>
            <div class="col-md-4">
              <label class="form-label fw-bold">이름</label>
              <p class="form-control-static">{{ warehouse.name }}</p>
            </div>
            <div class="col-md-4">
              <label class="form-label fw-bold">구분</label>
              <p class="form-control-static">{{ warehouse.divisionType }}</p>
            </div>
          </div>
          <div class="row mb-3">
            <div class="col-md-4">
              <label class="form-label fw-bold">활성화</label>
              <p class="form-control-static">{{ warehouse.active ? '예' : '아니오' }}</p>
            </div>
            <div class="col-md-4">
              <label class="form-label fw-bold">주소</label>
              <p class="form-control-static">{{ warehouse.address }}</p>
            </div>
            <div class="col-md-4">
              <label class="form-label fw-bold">우편번호</label>
              <p class="form-control-static">{{ warehouse.zipcode }}</p>
            </div>
          </div>
        </div>

        <!-- Editing View -->
        <div v-else>
          <div class="row mb-3">
            <div class="col-md-4">
              <label class="form-label fw-bold">ID</label>
              <p class="form-control-static">{{ editableWarehouse.id }}</p>
            </div>
            <div class="col-md-4">
              <label for="name" class="form-label fw-bold">이름</label>
              <input id="name" v-model="editableWarehouse.name" type="text" class="form-control">
            </div>
            <div class="col-md-4">
              <label for="divisionType" class="form-label fw-bold">구분</label>
              <input id="divisionType" v-model="editableWarehouse.divisionType" type="text" class="form-control">
            </div>
          </div>
          <div class="row mb-3">
            <div class="col-md-4">
              <label for="active" class="form-label fw-bold">활성화</label>
              <select id="active" v-model="editableWarehouse.active" class="form-select">
                <option :value="null">-- 선택 --</option>
                <option :value="true">예</option>
                <option :value="false">아니오</option>
              </select>
            </div>
            <div class="col-md-4">
              <label for="address" class="form-label fw-bold">주소</label>
              <input id="address" v-model="editableWarehouse.address" type="text" class="form-control">
            </div>
            <div class="col-md-4">
              <label for="zipcode" class="form-label fw-bold">우편번호</label>
              <input id="zipcode" v-model="editableWarehouse.zipcode" type="text" class="form-control">
            </div>
          </div>
        </div>

        <!-- Buttons -->
        <div class="d-flex justify-content-end gap-2 mt-4">
          <button type="button" class="btn btn-outline-secondary" @click="cancel">목록으로</button>
          <button v-if="canDelete" type="button" class="btn btn-danger" @click="handleDeleteWarehouse">삭제</button>
          <template v-if="!isEditing">
            <button type="button" class="btn btn-primary" @click="startEditing">수정</button>
          </template>
          <template v-else>
            <button type="button" class="btn btn-secondary" @click="cancelEditing">취소</button>
            <button type="button" class="btn btn-success" @click="saveWarehouse">저장</button>
          </template>
        </div>
      </div>

      <div v-else class="text-center text-muted">
        창고 정보를 찾을 수 없습니다.
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useUserStore } from '@/stores/user';
import { fetchWarehouseDetail, updateWarehouse, deleteWarehouse } from "@/api/warehouse";

const props = defineProps({
  warehouseId: {
    type: String,
    required: true,
  },
});

const userStore = useUserStore();
const router = useRouter();
const route = useRoute();
const warehouse = ref(null);
const loading = ref(true);
const error = ref(null);

const isEditing = ref(false);
const editableWarehouse = ref(null);
const showSuccessMessage = ref(false);

const canDelete = computed(() => {
  const role = userStore.role;
  return role === '[ROLE_ADMIN]' || role === '[ROLE_MANAGER]';
});

async function handleDeleteWarehouse() {
  if (!confirm('정말로 이 창고를 삭제하시겠습니까?')) {
    return;
  }

  try {
    await deleteWarehouse(props.warehouseId);
    alert('창고가 성공적으로 삭제되었습니다.');
    router.push('/warehouses');
  } catch (err) {
    console.error('창고 삭제 실패:', err);
    alert('창고 삭제에 실패했습니다.');
  }
}

async function loadWarehouseDetail(id) {
  loading.value = true;
  error.value = null;
  try {
    const response = await fetchWarehouseDetail(id);
    warehouse.value = response.data;
    // Check if the route has an 'edit' query parameter
    if (route.query.view === 'edit') {
      startEditing();
    }
  } catch (e) {
    console.error('Error fetching warehouse details:', e);
    error.value = '창고 정보를 불러오는 데 실패했습니다.';
  } finally {
    loading.value = false;
  }
}

onMounted(() => {
  if (props.warehouseId) {
    loadWarehouseDetail(props.warehouseId);
  }
});

watch(() => props.warehouseId, (newWarehouseId) => {
  if (newWarehouseId) {
    loadWarehouseDetail(newWarehouseId);
  } else {
    warehouse.value = null;
  }
});

function startEditing() {
  editableWarehouse.value = JSON.parse(JSON.stringify(warehouse.value));
  isEditing.value = true;
}

function cancelEditing() {
  editableWarehouse.value = null;
  isEditing.value = false;
}

async function saveWarehouse() {
  if (!editableWarehouse.value) return;

  const changedData = {};
  const originalWarehouse = warehouse.value;
  const editedWarehouse = editableWarehouse.value;

  const editableFields = [
    'name',
    'divisionType',
    'active',
    'address',
    'zipcode',
  ];

  for (const key of editableFields) {
    if (originalWarehouse[key] !== editedWarehouse[key]) {
      changedData[key] = editedWarehouse[key];
    }
  }

  if (Object.keys(changedData).length === 0) {
    isEditing.value = false;
    editableWarehouse.value = null;
    return;
  }

  try {
    await updateWarehouse(props.warehouseId, editableWarehouse.value);
    await loadWarehouseDetail(props.warehouseId);

    isEditing.value = false;
    editableWarehouse.value = null;
    showSuccessMessage.value = true;
    setTimeout(() => {
      showSuccessMessage.value = false;
    }, 3000);
  } catch (err) {
    console.error('Failed to update warehouse:', err);
    error.value = '창고 정보 수정에 실패했습니다.';
  }
}

function cancel() {
  router.push('/warehouses');
}
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
  border: 1px solid transparent;
  border-radius: 8px;
  background-color: #f8f9fa;
  font-size: 0.95rem;
  color: #495057;
}
.btn-outline-secondary {
  border-radius: 8px;
  padding: 10px 20px;
  font-weight: 600;
}
</style>

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
  border: 1px solid transparent;
  border-radius: 8px;
  background-color: #f8f9fa;
  font-size: 0.95rem;
  color: #495057;
}
.btn-outline-secondary {
  border-radius: 8px;
  padding: 10px 20px;
  font-weight: 600;
}
</style>
