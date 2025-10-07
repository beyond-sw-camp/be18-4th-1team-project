<!-- src/components/boms/BomAllTree.vue -->
<template>
  <ul class="bom-tree list-unstyled">
    <li>
      <div class="node d-flex align-items-center">
        <!-- 펼치기 버튼 -->
        <button
            v-if="hasChildren"
            class="btn btn-sm btn-link p-0 me-1"
            @click="isOpen = !isOpen"
        >
          <span v-if="isOpen">▼</span>
          <span v-else>▶</span>
        </button>
        <span v-else class="me-3 text-muted">•</span>

        <span class="fw-semibold">{{ node.itemName }}</span>
        <small class="text-muted ms-2">
          ({{ node.itemStatus }}) - 수량: {{ node.qty }}
        </small>
      </div>

      <!-- 자식 노드 -->
      <ul v-if="hasChildren && isOpen" class="ps-4 border-start ms-2">
        <li v-for="(child, idx) in node.children" :key="idx">
          <BomTree :node="child" :expand-all="expandAll" />
        </li>
      </ul>
    </li>
  </ul>
</template>

<script setup>
import BomTree from './BomTreeNode.vue';
import { ref, watch, computed } from "vue";

const props = defineProps({
  node: { type: Object, required: true },
  expandAll: { type: Boolean, default: false }
});

const isOpen = ref(false);
const hasChildren = computed(() => props.node.children && props.node.children.length > 0);

// expandAll 값이 바뀌면 모든 노드 열고/닫기
watch(
    () => props.expandAll,
    (val) => {
      isOpen.value = val;
    },
    { immediate: true }
);
</script>

<style scoped>
.bom-tree .node {
  padding: 4px 0;
  position: relative;
}
.bom-tree .border-start {
  border-color: #dee2e6 !important;
}
.bom-tree .fw-semibold {
  color: #2563eb;
}

</style>