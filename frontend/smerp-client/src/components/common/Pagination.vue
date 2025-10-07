<template>
  <nav aria-label="Page navigation">
    <ul class="pagination justify-content-center">
      <li class="page-item" :class="{ disabled: currentPage === 0 }">
        <a class="page-link" href="#" @click.prevent="changePage(0)">처음</a>
      </li>
      <li class="page-item" :class="{ disabled: currentPage === 0 }">
        <a class="page-link" href="#" @click.prevent="changePage(currentPage - 1)">이전</a>
      </li>
      <li v-for="page in pageNumbers" :key="page" class="page-item" :class="{ active: currentPage === page - 1 }">
        <a class="page-link" href="#" @click.prevent="changePage(page - 1)">{{ page }}</a>
      </li>
      <li class="page-item" :class="{ disabled: currentPage === totalPages - 1 }">
        <a class="page-link" href="#" @click.prevent="changePage(currentPage + 1)">다음</a>
      </li>
      <li class="page-item" :class="{ disabled: currentPage === totalPages - 1 }">
        <a class="page-link" href="#" @click.prevent="changePage(totalPages - 1)">마지막</a>
      </li>
    </ul>
  </nav>
</template>

<script setup>
import { computed } from 'vue';

const props = defineProps({
  currentPage: {
    type: Number,
    required: true,
  },
  totalPages: {
    type: Number,
    required: true,
  },
  pageRange: {
    type: Number,
    default: 5,
  },
});

const emit = defineEmits(['page-changed']);

const pageNumbers = computed(() => {
  const start = Math.floor(props.currentPage / props.pageRange) * props.pageRange;
  const end = Math.min(start + props.pageRange, props.totalPages);
  const pages = [];
  for (let i = start; i < end; i++) {
    pages.push(i + 1);
  }
  return pages;
});

function changePage(page) {
  if (page >= 0 && page < props.totalPages) {
    emit('page-changed', page);
  }
}
</script>

<style scoped>
.pagination .page-item.active .page-link {
  background-color: #2563eb;
  border-color: #2563eb;
  color: #fff;
}
.pagination .page-link {
  color: #2563eb;
}
.pagination .page-item.disabled .page-link {
  color: #6c757d;
}
</style>
