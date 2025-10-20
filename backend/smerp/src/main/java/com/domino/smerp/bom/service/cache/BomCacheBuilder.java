package com.domino.smerp.bom.service.cache;

import com.domino.smerp.bom.entity.Bom;
import com.domino.smerp.bom.entity.BomCostCache;
import com.domino.smerp.bom.repository.BomRepository;
import com.domino.smerp.item.Item;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BomCacheBuilder {

    private final BomRepository bomRepository;

    public List<BomCostCache> build(final Item rootItem) {
        final List<BomCostCache> caches = new ArrayList<>();
        dfsBuild(rootItem, rootItem, BigDecimal.ONE, 0, caches);
        return caches;
    }

    private BigDecimal dfsBuild(
            final Item root, // 루트 품목
            final Item current, // 순회할 때 만나는 아이템
            final BigDecimal accQty,
            final int depth,
            final List<BomCostCache> caches) {
        final List<Bom> children = bomRepository.findByParentItem_ItemId(current.getItemId());

        log.info(
                "DFS build: root={}, current={}, depth={}, childrenCount={}",
                root.getName(),
                current.getName(),
                depth,
                children.size());

        // 리프 노드
        if (children.isEmpty()) {
            final BigDecimal unitCost =
                    current.getInboundUnitPrice() != null ? current.getInboundUnitPrice() : BigDecimal.ZERO;
            final BigDecimal totalCost = accQty.multiply(unitCost);

            caches.add(BomCostCache.create(
                    root.getItemId(),
                    current.getItemId(),
                    depth,
                    current.getName(),
                    current.getItemStatus().getStatus(),
                    accQty,
                    unitCost,
                    totalCost));
            return totalCost;
        }

        // 내부 노드
        BigDecimal totalCost = BigDecimal.ZERO;
        for (final Bom childBom : children) {
            final Item child = childBom.getChildItem();
            final BigDecimal newAccQty = accQty.multiply(childBom.getQty());
            totalCost = totalCost.add(dfsBuild(root, child, newAccQty, depth + 1, caches));
        }

        caches.add(BomCostCache.create(
                root.getItemId(),
                current.getItemId(),
                depth,
                current.getName(),
                current.getItemStatus().getStatus(),
                accQty,
                BigDecimal.ZERO,
                totalCost));

        return totalCost;
    }
}
