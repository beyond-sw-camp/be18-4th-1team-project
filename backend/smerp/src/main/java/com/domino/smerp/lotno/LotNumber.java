package com.domino.smerp.lotno;

import com.domino.smerp.common.BaseEntity;
import com.domino.smerp.item.Item;
import com.domino.smerp.log.audit.AuditLogEntityListener;
import com.domino.smerp.lotno.constants.LotNumberStatus;
import com.domino.smerp.lotno.dto.request.CreateLotNumberRequest;
import com.domino.smerp.lotno.dto.request.UpdateLotNumberRequest;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.envers.Audited;

@Entity
@Getter
@Builder
@ToString
@Audited
@EntityListeners(AuditLogEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "lot_number")
@SQLRestriction("is_deleted = false")
// @SoftDelete(columnName = "is_deleted")
public class LotNumber extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lot_id")
    private Long lotId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Item item;

    @Column(name = "name", nullable = false, length = 30)
    private String name;

    @Column(name = "qty", nullable = false, precision = 12, scale = 3)
    private BigDecimal qty;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private LotNumberStatus status;

    @Builder.Default
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    // Lot.No 생성
    public static LotNumber create(final CreateLotNumberRequest request, final Item item, final String name) {
        return LotNumber.builder()
                .item(item)
                .name(name)
                .qty(request.getQty())
                .status(LotNumberStatus.fromLabel(request.getStatus()))
                .build();
    }

    // Lot.No 수정
    public void updateLotNumber(final UpdateLotNumberRequest request) {
        if (request.getQty() != null) {
            this.qty = request.getQty();
        }
        if (request.getStatus() != null) {
            this.status = LotNumberStatus.fromLabel(request.getStatus());
        }
    }

    // Lot.No 삭제 (소프트딜리트)
    public void delete() {
        this.isDeleted = true;
    }
}
