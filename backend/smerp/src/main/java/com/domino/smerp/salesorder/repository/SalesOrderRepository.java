package com.domino.smerp.salesorder.repository;

import com.domino.smerp.order.Order;
import com.domino.smerp.salesorder.SalesOrder;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SalesOrderRepository extends JpaRepository<SalesOrder, Long>, SalesOrderQueryRepository {

    boolean existsByOrderAndIsDeletedFalse(Order order);

    @Query(
            value = "SELECT MAX(CAST(SUBSTRING_INDEX(s.document_no, '-', -1) AS UNSIGNED)) " + "FROM sales_order s "
                    + "WHERE s.document_no LIKE CONCAT(:prefix, '%')",
            nativeQuery = true)
    Optional<Integer> findMaxSequenceByPrefix(@Param("prefix") String prefix);

    @Query("SELECT so FROM SalesOrder so " + "JOIN FETCH so.order o "
            + "JOIN FETCH o.client "
            + "JOIN FETCH o.user "
            + "LEFT JOIN FETCH o.orderItems oi "
            + "LEFT JOIN FETCH oi.item "
            + "WHERE so.soId = :id")
    Optional<SalesOrder> findByIdWithDetails(@Param("id") Long id);
}
