<template>
  <div class="table-responsive">
    <table class="table table-hover table-striped">
      <thead class="table-light">
        <tr>
          <th scope="col">로트명</th>
          <th scope="col">품목명</th>
          <th scope="col">수량</th>
          <th scope="col">상태</th>
          <th scope="col"></th>
        </tr>
      </thead>
      <tbody>
        <tr v-if="lots.length === 0">
          <td colspan="4" class="text-center">조회된 로트가 없습니다.</td>
        </tr>
        <tr v-for="lot in lots" :key="lot.lotId">
          <td>{{ lot.lotName }}</td>
          <td>{{ lot.itemName }}</td>
          <td>{{ lot.qty }}</td>
          <td>
            <span :class="getLotStatusClass(lot.status)">{{ lot.status }}</span>
          </td>
          <td>
            <router-link :to="{ name: 'LotDetail', params: { lotId: lot.lotId }}" class="btn btn-sm btn-outline-primary">상세보기</router-link>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
const props = defineProps({
  lots: {
    type: Array,
    required: true,
  },
});

function getLotStatusClass(status) {
  switch (status) {
    case '사용중':
      return 'badge bg-success';
    case '불량':
      return 'badge bg-danger';
    default:
      return 'badge bg-secondary';
  }
}
</script>

<style scoped>
.table-responsive {
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}
.badge {
  font-size: 0.9em;
  padding: 0.5em 0.75em;
}
</style>
