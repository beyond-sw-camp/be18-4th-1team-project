<template>
  <div class="card shadow-sm mt-4">
    <div class="card-body">
      <table class="table table-hover">
        <thead class="table-light">
          <tr>
            <th scope="col">레벨</th>
            <th scope="col">품목명</th>
            <th scope="col">품목 상태</th>
            <th scope="col">수량</th>
            <th scope="col">단가</th>
            <th scope="col">총 비용</th>
          </tr>
        </thead>
        <tbody>
          <BomTreeNode :node="bomData" />
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import BomTreeNode from './BomTreeNode.vue';


const props = defineProps({
    bomData: Object
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
  padding: 20px;
}
.table {
  margin: 0;
  border-collapse: separate;
  border-spacing: 0;
}
.table thead th {
  font-size: 12px;
  letter-spacing: .02em;
  font-weight: 600;
  color: #6b7280;
  background: #f8f9fa;
  border-bottom: 1px solid #e9ecef !important;
  padding: 14px 16px;
  vertical-align: middle;
}

.table thead th:first-child {
  border-top-left-radius: 8px;
}
.table thead th:last-child {
  border-top-right-radius: 8px;
}
.table tbody td {
  border-top: 1px solid #eef1f6 !important;
  padding: 14px 16px;
  vertical-align: middle;
  color: #334155;
  font-size: 0.9rem;
}
.table-hover tbody tr:hover {
  background: #e9ecef;
}
.table tbody tr {
  height: 56px;
}
</style>