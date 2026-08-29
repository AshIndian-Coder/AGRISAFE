package com.agro.trace.manufacturing.domain;

import com.agro.trace.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "manufacturer_lot_inputs")
@Getter
@Setter
@NoArgsConstructor
public class ManufacturerLotInput extends BaseEntity {

    @Column(name = "manufacturer_lot_id", nullable = false, length = 64)
    private String manufacturerLotId;

    @Column(name = "input_lot_id", nullable = false, length = 64)
    private String inputLotId;

    @Column(name = "input_type", length = 32)
    private String inputType; // LOT, PACKAGE

    @Column(name = "quantity")
    private java.math.BigDecimal quantity;

    @Column(name = "unit", length = 20)
    private String unit;
}