<template>
  <div class="card shadow-sm">
    <div class="card-body">
      <h5 class="card-title mb-4">주문 상세 정보</h5>

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

      <div v-else-if="order">
        <!-- Display View -->
        <div v-if="!isEditing">
          <div class="row mb-3">
            <div class="col-md-4">
              <label class="form-label fw-bold">전표번호</label>
              <p class="form-control-static">{{ order.documentNo }}</p>
            </div>
            <div class="col-md-4">
              <label class="form-label fw-bold">회사명</label>
              <p class="form-control-static">{{ order.companyName }}</p>
            </div>
            <div class="col-md-4">
              <label class="form-label fw-bold">상태</label>
              <p class="form-control-static">
                <span :class="getOrderStatusClass(order.status)">{{ getOrderStatusDisplayName(order.status) }}</span>
              </p>
            </div>
          </div>
          <div class="row mb-3">
            <div class="col-md-4">
              <label class="form-label fw-bold">납기일</label>
              <p class="form-control-static">{{ order.deliveryDate }}</p>
            </div>
            <div class="col-md-4">
              <label class="form-label fw-bold">담당자</label>
              <p class="form-control-static">{{ order.userName }}</p>
            </div>
          </div>
          <div class="row mb-4">
            <div class="col-md-12">
              <label class="form-label fw-bold">비고</label>
              <p class="form-control-static">{{ order.remark }}</p>
            </div>
          </div>
        </div>

        <!-- Editing View -->
        <div v-else>
          <div class="row mb-3">
            <div class="col-md-4">
              <label for="documentDate" class="form-label fw-bold">문서일</label>
              <input id="documentDate" v-model="editableOrder.documentDate" type="date" class="form-control">
            </div>
            <div class="col-md-4">
              <label for="deliveryDate" class="form-label fw-bold">납기일</label>
              <input id="deliveryDate" v-model="editableOrder.deliveryDate" type="date" class="form-control">
            </div>
            <div class="col-md-4">
              <label for="status" class="form-label fw-bold">상태</label>
              <select id="status" v-model="editableOrder.status" class="form-select">
                <option value="COMPLETED">완료</option>
                <option value="PENDING">대기중</option>
                <option value="APPROVED">승인됨</option>
                <option value="CANCELLED">취소됨</option>
              </select>
            </div>
          </div>
          <div class="row mb-3">
            <div class="col-md-8">
                <label class="form-label fw-bold">담당자</label>
                <div class="input-group">
                    <input type="text" class="form-control" :value="`${editableOrder.userName} (${editableOrder.empNo})`" readonly>
                    <button class="btn btn-outline-secondary" type="button" data-bs-toggle="modal" data-bs-target="#userSearchModal">담당자 검색</button>
                </div>
            </div>
          </div>
          <div class="row mb-4">
            <div class="col-md-12">
              <label for="remark" class="form-label fw-bold">비고</label>
              <textarea id="remark" v-model="editableOrder.remark" class="form-control" rows="2"></textarea>
            </div>
          </div>

          <h6 class="fw-bold mt-4 mb-3">주문 품목</h6>
          <div class="table-responsive mb-3">
            <table class="table table-bordered align-middle">
              <thead>
                <tr>
                  <th>품목명</th>
                  <th style="width: 120px;">수량</th>
                  <th style="width: 150px;">특별 단가</th>
                  <th style="width: 80px;">삭제</th>
                </tr>
              </thead>
              <tbody>
                <tr v-if="editableOrder.items.length === 0">
                  <td colspan="4" class="text-center text-muted">품목을 추가해주세요.</td>
                </tr>
                <tr v-for="(item, index) in editableOrder.items" :key="index">
                  <td>{{ item.itemName }}</td>
                  <td><input type="number" class="form-control" v-model.number="item.qty"></td>
                  <td><input type="number" class="form-control" v-model.number="item.specialPrice"></td>
                  <td class="text-center">
                    <button class="btn btn-sm btn-outline-danger" @click="removeItem(index)">X</button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
          <button class="btn btn-primary" type="button" @click="openItemSearchModal">품목 추가</button>
        </div>

        <h6 class="fw-bold mb-3 mt-4">주문 품목 (읽기 전용)</h6>
        <div class="table-responsive mb-4">
          <table class="table table-hover align-middle">
            <thead>
              <tr>
                <th scope="col">품목명</th>
                <th scope="col">규격</th>
                <th scope="col">수량</th>
                <th scope="col">단위</th>
                <th scope="col">단가</th>
                <th scope="col">공급가액</th>
                <th scope="col">세액</th>
                <th scope="col">총액</th>
                <th scope="col">납기일</th>
                <th scope="col">비고</th>
              </tr>
            </thead>
            <tbody>
              <tr v-if="order.items.length === 0">
                <td colspan="10" class="text-center text-muted">주문 품목이 없습니다.</td>
              </tr>
              <tr v-for="item in order.items" :key="item.itemId">
                <td>{{ item.itemName }}</td>
                <td>{{ item.specification }}</td>
                <td>{{ item.qty }}</td>
                <td>{{ item.unit }}</td>
                <td>{{ formatCurrency(item.specialPrice) }}</td>
                <td>{{ formatCurrency(item.supplyAmount) }}</td>
                <td>{{ formatCurrency(item.tax) }}</td>
                <td>{{ formatCurrency(item.totalAmount) }}</td>
                <td>{{ item.deliveryDate }}</td>
                <td>{{ item.remark }}</td>
              </tr>
            </tbody>
          </table>
        </div>

        <div class="d-flex justify-content-end gap-2 mt-4">
          <button type="button" class="btn btn-outline-secondary" @click="goToList">목록으로</button>
          <button v-if="canDelete" type="button" class="btn btn-danger" @click="confirmDelete">삭제</button>
          <template v-if="!isEditing">
            <button type="button" class="btn btn-primary" @click="startEditing">수정</button>
          </template>
          <template v-else>
            <button type="button" class="btn btn-secondary" @click="cancelEditing">취소</button>
            <button type="button" class="btn btn-success" @click="saveOrder">저장</button>
          </template>
        </div>
      </div>

      <div v-else class="text-center text-muted">
        주문 정보를 찾을 수 없습니다.
      </div>
    </div>
  </div>

  <UserSearchModal @select="onUserSelected" />
  <ItemSearchModal ref="itemSearchModal" @select="onItemSelected" />
</template>

<script setup>
import { ref, onMounted, watch, computed } from 'vue';
import { useRouter } from 'vue-router';
import { fetchOrderDetail, deleteOrder, updateOrder } from "@/api/order";
import { useUserStore } from "@/stores/user";
import UserSearchModal from '@/components/users/UserSearchModal.vue';
import ItemSearchModal from '@/components/items/ItemSearchModal.vue';

const props = defineProps({
  orderId: {
    type: String,
    required: true,
  },
});

const router = useRouter();
const userStore = useUserStore();
const order = ref(null);
const loading = ref(true);
const error = ref(null);
const isEditing = ref(false);
const editableOrder = ref(null);
const showSuccessMessage = ref(false);
const itemSearchModal = ref(null);

function openItemSearchModal() {
  itemSearchModal.value.showModal();
}

const canDelete = computed(() => userStore.role === '[ROLE_ADMIN]' || '[ROLE_MANAGER]');

async function loadOrderDetail(orderId) {
  loading.value = true;
  error.value = null;
  try {
    const response = await fetchOrderDetail(orderId);
    order.value = response.data;
  } catch (e) {
    console.error('Error fetching order details:', e);
    error.value = '주문 상세 정보를 불러오는 데 실패했습니다.';
  } finally {
    loading.value = false;
  }
}

function startEditing() {
  editableOrder.value = JSON.parse(JSON.stringify(order.value));
  if (!editableOrder.value.items) {
    editableOrder.value.items = [];
  }
  isEditing.value = true;
}

function cancelEditing() {
  editableOrder.value = null;
  isEditing.value = false;
}

async function saveOrder() {
  if (!editableOrder.value) return;

  const orderDataToSave = {
    documentDate: editableOrder.value.documentDate,
    deliveryDate: editableOrder.value.deliveryDate,
    status: editableOrder.value.status,
    empNo: editableOrder.value.empNo,
    remark: editableOrder.value.remark,
    items: editableOrder.value.items.map(item => ({
      itemId: item.itemId,
      qty: item.qty,
      specialPrice: item.specialPrice || null,
    })),
  };

  try {
    await updateOrder(props.orderId, orderDataToSave);
    await loadOrderDetail(props.orderId);

    isEditing.value = false;
    editableOrder.value = null;
    showSuccessMessage.value = true;
    setTimeout(() => {
      showSuccessMessage.value = false;
    }, 3000);
  } catch (err) {
    console.error('Failed to update order:', err);
    alert('주문 정보 수정에 실패했습니다.');
  }
}

function onUserSelected(user) {
    if (editableOrder.value) {
        editableOrder.value.empNo = user.empNo;
        editableOrder.value.userName = user.name;
    }
}

function onItemSelected(item) {
    if (editableOrder.value) {
        const existingItem = editableOrder.value.items.find(i => i.itemId === item.itemId);
        if (existingItem) {
            existingItem.qty += 1;
        } else {
            editableOrder.value.items.push({
                itemId: item.itemId,
                itemName: item.name,
                qty: 1,
                specialPrice: 0,
            });
        }
    }
}

function removeItem(index) {
    if (editableOrder.value) {
        editableOrder.value.items.splice(index, 1);
    }
}


async function confirmDelete() {
  if (confirm('정말로 이 주문을 삭제하시겠습니까?')) {
    try {
      await deleteOrder(props.orderId);
      alert('주문이 성공적으로 삭제되었습니다.');
      router.push('/orders');
    } catch (e) {
      console.error('Error deleting order:', e);
      alert('주문 삭제에 실패했습니다: ' + (e.response?.data?.message || e.message));
    }
  }
}

function getOrderStatusDisplayName(status) {
  switch (status) {
    case 'COMPLETED': return '완료';
    case 'PENDING': return '대기중';
    case 'APPROVED': return '승인됨';
    case 'CANCELLED': return '취소됨';
    default: return status;
  }
}

function getOrderStatusClass(status) {
  switch (status) {
    case 'COMPLETED':
      return 'badge bg-success';
    case 'PENDING':
      return 'badge bg-warning';
    case 'APPROVED':
      return 'badge bg-info';
    case 'CANCELLED':
      return 'badge bg-danger';
    default:
      return 'badge bg-secondary';
  }
}

function formatCurrency(amount) {
  if (amount === null || amount === undefined) return '';
  return new Intl.NumberFormat('ko-KR', { style: 'currency', currency: 'KRW' }).format(amount);
}

function goToList() {
  router.push('/orders');
}

onMounted(() => {
  if (props.orderId) {
    loadOrderDetail(props.orderId);
  }
});

watch(() => props.orderId, (newOrderId) => {
  if (newOrderId) {
    loadOrderDetail(newOrderId);
  } else {
    order.value = null;
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
.table-responsive {
  margin-top: 1rem;
}
.table th,
.table td {
  white-space: nowrap;
  text-align: center;
}
.table thead th {
  background-color: #f8f9fa;
  font-weight: 600;
  color: #334155;
}
.badge {
  font-size: 0.85em;
  padding: 0.5em 0.75em;
}
</style>
