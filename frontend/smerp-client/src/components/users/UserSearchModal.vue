<template>
  <div class="modal fade" id="userSearchModal" tabindex="-1" aria-labelledby="userSearchModalLabel" aria-hidden="true" ref="modalElement">
    <div class="modal-dialog modal-lg">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title" id="userSearchModalLabel">사용자 검색</h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <div class="modal-body">
          <div class="mb-3">
            <input type="text" class="form-control" placeholder="사용자명 또는 사원 번호 검색" v-model="searchTerm" @input="onSearch">
          </div>
          <table class="table table-hover">
            <thead>
              <tr>
                <th>사용자명</th>
                <th>사원 번호</th>
                <th>이메일</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              <tr v-if="users.length === 0">
                <td colspan="4" class="text-center text-muted">검색 결과가 없습니다.</td>
              </tr>
              <tr v-for="user in users" :key="user.userId">
                <td>{{ user.name }}</td>
                <td>{{ user.empNo }}</td>
                <td>{{ user.email }}</td>
                <td>
                  <button class="btn btn-sm btn-primary" @click="selectUser(user)" data-bs-dismiss="modal">선택</button>
                </td>
              </tr>
            </tbody>
          </table>
          <Pagination
            :currentPage="currentPage"
            :totalPages="totalPages"
            @page-changed="handlePageChange"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch, defineExpose } from 'vue';
import { fetchUsers } from '@/api/user';
import Pagination from '@/components/common/Pagination.vue';
import { Modal } from 'bootstrap';

const users = ref([]);
const searchTerm = ref('');
const currentPage = ref(0);
const totalPages = ref(0);
const pageSize = ref(10);
const modalElement = ref(null);
let bsModal = null;

const emit = defineEmits(['select']);

async function loadUsers() {
  try {
    const response = await fetchUsers(currentPage.value, pageSize.value, searchTerm.value);
    users.value = response.data.content;
    totalPages.value = response.data.totalPages;
  } catch (error) {
    console.error('Error fetching users:', error);
  }
}

watch(searchTerm, () => {
  currentPage.value = 0;
  loadUsers();
});

function selectUser(user) {
  emit('select', user);
}

function onSearch() {
}

function handlePageChange(page) {
  currentPage.value = page;
  loadUsers();
}

onMounted(() => {
  loadUsers();
  if (modalElement.value) {
    bsModal = new Modal(modalElement.value);
  }
});

const showModal = () => {
  bsModal?.show();
};

defineExpose({ showModal });
</script>