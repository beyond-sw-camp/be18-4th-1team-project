package com.domino.smerp.purchase.purchaseorder.repository;

import com.domino.smerp.purchase.purchaseorder.PurchaseOrder;
import com.domino.smerp.purchase.purchaseorder.repository.query.PurchaseOrderQueryRepository;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long>, PurchaseOrderQueryRepository {

    // ✅ 상세 조회: RequestOrder + ItemRequestOrder까지 fetch join
    @Query("select purchaseOrder from PurchaseOrder purchaseOrder "
            + "join fetch purchaseOrder.requestOrder requestOrder "
            + "join fetch requestOrder.items itemRequestOrder "
            + "where purchaseOrder.poId = :poId")
    Optional<PurchaseOrder> findByIdWithRequestOrderAndItems(@Param("poId") final Long poId);

    // ✅ 구매 수정
    @Query("SELECT MAX(CAST(SUBSTRING(p.documentNo, 12) AS int)) " + "FROM PurchaseOrder p "
            + "WHERE SUBSTRING(p.documentNo, 1, 10) = :dateString")
    Optional<Integer> findLastSequenceByDate(@Param("dateString") String dateString);
}
