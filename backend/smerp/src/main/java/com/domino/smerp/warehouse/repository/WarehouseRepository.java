package com.domino.smerp.warehouse.repository;

import com.domino.smerp.warehouse.Warehouse;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long>, WarehouseQueryRepository {

    Boolean existsByName(String name);

    //  Optional<Warehouse> findByItemId(Long itemId);

    @Query("SELECT DISTINCT w.id FROM Warehouse w " + "WHERE NOT EXISTS ("
            + "  SELECT l FROM Location l "
            + "  WHERE l.warehouse = w AND l.filled = true"
            + ")")
    List<Warehouse> findWarehousesWithFilledFalseLocations();

    @Query(
            """
    SELECT DISTINCT w
    FROM Warehouse w
    JOIN w.locations l
    WHERE COALESCE(l.curQty, 0) < l.maxQty
  """)
    List<Warehouse> findAvailableWarehousesWithCurQty();

    @Query(
            """
    SELECT DISTINCT s.location.warehouse
    FROM Stock s
    WHERE s.item.itemId = :itemId
      AND s.qty > 0
  """)
    List<Warehouse> findWarehousesWithStock(@Param("itemId") Long itemId);

    Optional<Warehouse> findByName(String name);
}
